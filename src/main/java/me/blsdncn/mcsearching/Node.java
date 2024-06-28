package me.blsdncn.mcsearching;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;

import java.util.Objects;

public class Node {
    private int x, y, z;
    private double gCost; // Cost from start to this node
    private double hCost; // Heuristic cost to goal
    private Node parent;
    private boolean traversable;
    private ArmorStand armorStand;

    public Node(int x, int y, int z, double gCost, double hCost, Node parent) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.gCost = gCost;
        this.hCost = hCost;
        this.parent = parent;
        this.traversable = false;
        this.armorStand = null;
    }

    public double getgCost() {
        return gCost;
    }
    public double getFCost() {
        return gCost + hCost;
    }

    public Node getParent() {
        return parent;
    }

    public double getHCost() {
        return hCost;
    }

    public int getZ() {
        return z;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public Location getLocation(World world){
        return new Location(world, x, y, z);
    }

    public boolean isTraversable() {
        return traversable;
    }

    public void setTraversable(boolean traversable) {
        this.traversable = traversable;
    }

    public void setgCost(double gCost) {
        this.gCost = gCost;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }

    public void setArmorStand(ArmorStand armorStand) {
        this.armorStand = armorStand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return x == node.x && y == node.y && z == node.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

}
