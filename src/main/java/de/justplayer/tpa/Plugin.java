package de.justplayer.tpa;

import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
    public FileConfiguration config;

    public Plugin() {
        config = new Config(this).getConfig();
    }

    @Override
    public void onEnable() {

        if (config.getBoolean("bStats.enabled")) {
            new Metrics(this, 18743);
            getLogger().info("bStats enabled");
        }

        getLogger().info("JustTPA initialized");
    }

    @Override
    public void onDisable() {
        getLogger().info("JustTPA disabled");
    }
}
