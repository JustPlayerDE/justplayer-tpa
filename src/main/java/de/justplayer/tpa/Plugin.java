package de.justplayer.tpa;

import de.justplayer.tpa.listeners.PlayerLeaveListener;
import de.justplayer.tpa.utils.CooldownManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
    public FileConfiguration config;
    public CooldownManager cooldownManager;

    public Plugin() {
        config = new Config(this).getConfig();
        cooldownManager = new CooldownManager();
    }

    @Override
    public void onEnable() {

        if (config.getBoolean("bStats.enabled")) {
            new Metrics(this, 18743);
            getLogger().info("bStats enabled");
        }

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerLeaveListener(this), this);


        // Register commands
        // TODO

        getLogger().info("JustTPA initialized");
    }

    @Override
    public void onDisable() {
        getLogger().info("JustTPA disabled");
    }
}
