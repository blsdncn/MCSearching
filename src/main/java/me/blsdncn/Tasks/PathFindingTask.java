package me.blsdncn.Tasks;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import me.blsdncn.mcsearching.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class PathFindingTask extends BukkitRunnable {

    private final Location start;
    private final Location end;
    private final World world;
    private List<Node> path;
    private Plugin plugin;
    private final ConcurrentLinkedQueue<Node> toCheckTraversability = new ConcurrentLinkedQueue<>();

    public PathFindingTask(Location start, Location end, World world, Plugin plugin) {
        this.start = start;
        this.end = end;
        this.world = world;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        AStarSearch pathfinder = new AStarSearch(plugin);
        path = pathfinder.findPath(start, end,toCheckTraversability, world, new AtomicBoolean(false));
    }

    public List<Node> getPath() {
        return path;
    }
}