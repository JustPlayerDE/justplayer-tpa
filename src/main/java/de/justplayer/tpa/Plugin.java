package de.justplayer.tpa;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

    @Override
    public void onEnable() {
        new Metrics(this, 18743);
        getLogger().info("JustTPA initialized");
    }

    @Override
    public void onDisable() {
        getLogger().info("JustTPA disabled");
    }
}
