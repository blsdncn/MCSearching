package me.blsdncn.mcsearching;
import org.bukkit.*;
import org.bukkit.plugin.Plugin;
import me.blsdncn.Tasks.TraversabilityCheckTask;

import java.util.*;

import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class AStarSearch {
    private Plugin plugin;
    public AStarSearch(Plugin  plugin) {
        this.plugin = plugin;
    }

    private final int[][] DIRECTIONS = {
            // Cardinal directions on same Y level
            {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1},
            // Diagonal Directions on same Y level
            {1, 0, 1}, {1, 0, -1}, {-1, 0, 1}, {-1, 0, -1},
            // Up and down in cardinal directions
            {1, 1, 0}, {1, -1, 0}, {-1, 1, 0}, {-1, -1, 0},
            {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1},
            // Up and down in diagonal directions
            {1, 1, 1}, {1, -1, -1}, {-1, 1, 1}, {-1, -1, -1},
            {1, 1, -1}, {1, -1, 1}, {-1, 1, -1}, {-1, -1, 1}
    };

    private static boolean isDestination(int x, int y, int z, int[] dest) {
        return dest[0] == x && dest[1] == y && dest[2] == z;
    }

    private static double calculateHValue(int x, int y, int z, int[] dest) {
        return Math.sqrt(Math.pow(x - dest[0], 2) + Math.pow(y - dest[1], 2) + Math.pow(z - dest[2], 2));
    }

    private static double calculateHValue(Node current, int[] dest) {
        return Math.sqrt(Math.pow(current.getX(), 2) + Math.pow(current.getY(), 2) + Math.pow(current.getZ(), 2));
    }

    /*
    private boolean isTraversable(Node node, World world){
        if(node == null) return false;
        if(node.isTraversable()) return true;
        Block block = world.getBlockAt(node.getLocation(world));
        if(block.getType() == Material.AIR) {
            return false;
        }
        if(block.getRelative(0,1,0).getType() != Material.AIR || block.getRelative(0,2,0).getType() != Material.AIR) {
            return false;
        }
        Bukkit.getLogger().info("Block at " + node.getX() + " " + node.getY() + " " + node.getZ() + " is traversable: "+ block.getType() + " | G: "+node.getgCost()+" | H: " +node.getHCost() + " | F: "+ node.getFCost());
        node.setTraversable(true);
        return true;
    }
    */

    private List<Node> reconstructPath(Node node) {
        List<Node> path = new ArrayList<>();
        while (node != null) {
            path.add(node);
            node = node.getParent();
        }
        Collections.reverse(path);
        return path;
    }


    public List<Node> findPath(Location start, Location end, ConcurrentLinkedQueue<Node> toCheckTraversability, World world, AtomicBoolean cancelled){
        PriorityQueue<Node> openList = new PriorityQueue<Node>(Comparator.comparingDouble(Node::getFCost)); // A priority queue based on estimated cost
        Set<Node> openSet = new HashSet<>(); // A set used for quick access to nodes for contains calls
        Set<Node> closedList = new HashSet<>();
        int[] startCoords = new int[]{start.getBlockX(),start.getBlockY(),start.getBlockZ()};
        int[] endCoords = new int[]{end.getBlockX(),end.getBlockY(),end.getBlockZ()};
        Node startNode = new Node(startCoords[0],startCoords[1],startCoords[2],0,calculateHValue(startCoords[0],startCoords[1],startCoords[2],endCoords),null);
        openList.add(startNode);
        openSet.add(startNode);

        while(!openList.isEmpty()){
            if(cancelled.get()){
                return null;
            }
            Node currentNode = openList.poll();
            plugin.getLogger().info(currentNode.getX()+","+currentNode.getY()+","+currentNode.getZ());
            openSet.remove(currentNode);
            if(isDestination(currentNode.getX(),currentNode.getY(),currentNode.getZ(),endCoords)){
                plugin.getLogger().info("Done!");
                return reconstructPath(currentNode);
            }
            closedList.add(currentNode);
            List<Node> tempNodes = new ArrayList<>();
            for(int[] direction : DIRECTIONS) {
                Node neighbor = new Node(currentNode.getX() + direction[0],
                        currentNode.getY() + direction[1],
                        currentNode.getZ() + direction[2],
                        currentNode.getgCost() + 1,
                        calculateHValue(currentNode.getX() + direction[0], currentNode.getY() + direction[1], currentNode.getZ() + direction[2], endCoords),
                        currentNode);
                //plugin.getLogger().info("Adding node at " + neighbor.getX() + "," + neighbor.getY() + "," + neighbor.getZ() + "\n_____________________________________________________");
                if (closedList.contains(neighbor)) continue;
                tempNodes.add(neighbor);
                toCheckTraversability.add(neighbor);
            }
            CountDownLatch latch = new CountDownLatch(1);
            TraversabilityCheckTask checkTask = new TraversabilityCheckTask(toCheckTraversability, world, latch, plugin);
            checkTask.runTask(plugin);
            try {
                //plugin.getLogger().info("Checking traversability of node");
                latch.await(); // Wait for the synchronous task to complete
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //plugin.getLogger().info("Signaled");
            for (Node neighbor : tempNodes) {
                if (!neighbor.isTraversable()) {
                    closedList.add(neighbor);
                    continue;
                }

                if (!openSet.contains(neighbor) || neighbor.getgCost() < currentNode.getgCost() + 1) {
                    neighbor.setgCost(currentNode.getgCost() + 1);
                    neighbor.setParent(currentNode);
                    if (!openSet.contains(neighbor)) {
                        openList.add(neighbor);
                        openSet.add(neighbor);
                    } else {
                        // Remove and re-add to update the priority
                        openList.remove(neighbor);
                        openList.add(neighbor);
                    }
                }
            }
        }

        return null;
    }
}
