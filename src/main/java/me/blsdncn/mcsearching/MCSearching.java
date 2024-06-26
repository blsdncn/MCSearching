package me.blsdncn.mcsearching;

import org.bukkit.plugin.java.JavaPlugin;

public final class MCSearching extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Starting up");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Shutting down");
    }
}
