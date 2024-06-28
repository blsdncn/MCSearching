package me.blsdncn.CommandHander;

import me.blsdncn.Tasks.PathFindingTask;
import me.blsdncn.mcsearching.AStarSearch;
import me.blsdncn.mcsearching.MCSearching;
import me.blsdncn.mcsearching.Node;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;

public class CommandHandler implements CommandExecutor{
    private Plugin plugin;
    public CommandHandler(Plugin plugin) {
       this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {
        if(label.equalsIgnoreCase("PathFind")){
            if(commandSender instanceof Player){
                Player player = (Player) commandSender;
                int destX = Integer.parseInt(args[0]);
                int destY = Integer.parseInt(args[1]);
                int destZ = Integer.parseInt(args[2]);
                World world = player.getWorld();
                AStarSearch pathfinder = new AStarSearch(plugin);
                Location startLoc = player.getLocation();
                startLoc.setY(startLoc.getY()-1);
                Location destLoc = new Location(world, destX, destY, destZ);
                player.sendMessage("Pathfinding from " + startLoc.getBlockX() + " " + startLoc.getBlockY() + " " + startLoc.getBlockZ() +  " to " + destX + " " + destY + " " + destZ);
                PathFindingTask task = new PathFindingTask(startLoc,destLoc, world,plugin);
                BukkitScheduler scheduler = plugin.getServer().getScheduler();
                task.runTaskAsynchronously(plugin);
                // Wait on task to complete. Will probably have a 15 second timer, and if path is empty, then timeout the task. But is this thread safe? Probably not :(
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        List<Node> path = task.getPath();
                        if(path != null && !path.isEmpty()){
                            path.forEach(node -> {
                                Location loc = new Location(world, node.getX(), node.getY(), node.getZ());
                                loc.setY(node.getY() - .5);
                                loc.setX(node.getX()+.5);
                                loc.setZ(node.getZ()+.5);
                                world.spawnParticle(Particle.NOTE, loc, 1);
                                node.getArmorStand().setHelmet(new ItemStack(Material.EMERALD_BLOCK));
                            });
                            player.sendMessage("Pathfinding complete.");
                        } else {
                            player.sendMessage("Timed out waiting for pathfinding");
                            if(!task.isCancelled()){
                                task.cancel();
                            }
                        }
                    }
                }.runTaskLater(plugin,500L);

            }
        return true;
        }
        return false;
    }
}
