package me.blsdncn.mcsearching;

import me.blsdncn.CommandHander.CommandHandler;
import org.bukkit.plugin.java.JavaPlugin;

public final class MCSearching extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Starting up");
        CommandHandler cmdHandler = new CommandHandler();
        this.getCommand("PathFind").setExecutor(cmdHandler);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Shutting down");
    }
}
