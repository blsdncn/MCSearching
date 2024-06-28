package me.blsdncn.Tasks;
import me.blsdncn.mcsearching.Node;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

public class TraversabilityCheckTask extends BukkitRunnable {
    private final World world;
    private final CountDownLatch latch;
    private final ConcurrentLinkedQueue<Node> nodesToCheck;
    private final Plugin plugin;

    public TraversabilityCheckTask(ConcurrentLinkedQueue<Node> nodesToCheck, World world, CountDownLatch latch, Plugin plugin) {
        this.nodesToCheck = nodesToCheck;
        this.world = world;
        this.latch = latch;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        String output = "|";
        while (!nodesToCheck.isEmpty()) {
            Node node = nodesToCheck.poll();
            Block block = world.getBlockAt(node.getX(), node.getY(), node.getZ());
            if (block.getType() == Material.AIR || block.isPassable()) {
                node.setTraversable(false);
            }
            Location noteLocation = block.getLocation();
            noteLocation.setY(node.getY() - .5);
            noteLocation.setX(node.getX()+.5);
            noteLocation.setZ(node.getZ()+.5);
            ArmorStand a = (ArmorStand) world.spawnEntity(noteLocation, EntityType.ARMOR_STAND);
            a.setInvisible(true);
            a.setGravity(false);
            node.setArmorStand(a);
            if (block.getRelative(0, 1, 0).getType() != Material.AIR || block.getRelative(0, 2, 0).getType() != Material.AIR) {
                node.setTraversable(false);
                world.spawnParticle(Particle.NOTE, noteLocation, 1);
                a.setHelmet(new ItemStack(Material.REDSTONE_BLOCK));
            } else {
                node.setTraversable(true);
                output = output.concat(node.getX()+","+node.getY()+","+node.getZ()+"|");
                world.spawnParticle(Particle.NOTE, noteLocation, 1);
                a.setHelmet(new ItemStack(Material.QUARTZ_BLOCK));
            }
        }
        plugin.getLogger().info(output);
        latch.countDown(); // Signal that the check is complete
    }
}