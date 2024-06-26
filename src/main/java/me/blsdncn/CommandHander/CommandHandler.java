package me.blsdncn.CommandHander;

import me.blsdncn.mcsearching.AStarSearch;
import me.blsdncn.mcsearching.Node;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;

public class CommandHandler implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {
        if(label.equalsIgnoreCase("PathFind")){
            if(commandSender instanceof Player){
                Player player = (Player) commandSender;
                int destX = Integer.parseInt(args[0]);
                int destY = Integer.parseInt(args[1]);
                int destZ = Integer.parseInt(args[2]);
                AStarSearch pathfinder = new AStarSearch();
                Location startLoc = player.getLocation();
                startLoc.setY(startLoc.getY()-1);
                Location destLoc = new Location(player.getWorld(), destX, destY, destZ);
                player.sendMessage("Pathfinding from " + startLoc.getX() + " " + startLoc.getY() + " " + startLoc.getZ() +  " to " + destX + " " + destY + " " + destZ);
                List<Node> path = pathfinder.findPath(startLoc,destLoc);
                World world = player.getWorld();
                if(path != null && !path.isEmpty()){
                    path.forEach(node -> {
                        Location loc = new Location(world, node.getX(), node.getY(), node.getZ());
                        world.getBlockAt(loc).setType(Material.EMERALD_BLOCK);//world.spawnParticle(Particle.NOTE,loc,10)
                    });
                } else {
                    player.sendMessage("No path found");
                }
        }
    return true;
    }
        return false;
    }
}
