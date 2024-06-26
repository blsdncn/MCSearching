package me.blsdncn.mcsearching;
import me.blsdncn.mcsearching.Node;
import org.bukkit.*;
import org.bukkit.block.Block;

import java.util.*;

import java.util.PriorityQueue;

public class AStarSearch {

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

    private List<Node> reconstructPath(Node node) {
        List<Node> path = new ArrayList<>();
        while (node != null) {
            path.add(node);
            node = node.getParent();
        }
        Collections.reverse(path);
        return path;
    }


    public List<Node> findPath(Location start, Location end){
        World world = start.getWorld();
        PriorityQueue<Node> openList = new PriorityQueue<Node>(Comparator.comparingDouble(Node::getFCost));
        Set<Node> openSet = new HashSet<>();
        Set<Node> closedList = new HashSet<>();
        int[] startCoords = new int[]{start.getBlockX(),start.getBlockY(),start.getBlockZ()};
        int[] endCoords = new int[]{end.getBlockX(),end.getBlockY(),end.getBlockZ()};
        Node startNode = new Node(startCoords[0],startCoords[1],startCoords[2],0,calculateHValue(startCoords[0],startCoords[1],startCoords[2],endCoords),null);
        openList.add(startNode);
        openSet.add(startNode);


        while(!openList.isEmpty()){
            Node currentNode = openList.poll();
            openSet.remove(currentNode);
            if(isDestination(currentNode.getX(),currentNode.getY(),currentNode.getZ(),endCoords)){
                return reconstructPath(currentNode);
            }
            closedList.add(currentNode);
            for(int[] direction : DIRECTIONS){
                Node neighbor = new Node(currentNode.getX()+direction[0],
                                        currentNode.getY()+direction[1],
                                        currentNode.getZ()+direction[2],
                                    currentNode.getgCost()+1,
                                        calculateHValue(currentNode.getX()+direction[0], currentNode.getY()+direction[1], currentNode.getZ()+direction[2],endCoords),
                                        currentNode
                                        );
                if (!isTraversable(neighbor, world)) continue;
                if (closedList.contains(neighbor)) continue;

                if(!openSet.contains(neighbor) || neighbor.getgCost() < currentNode.getgCost()){
                    //world.spawnParticle(Particle.NOTE, neighbor.getLocation(world),1);
                    openList.add(neighbor);
                    openSet.add(neighbor);
                }
            }

        }
        return null;
    }

}
