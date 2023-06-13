package de.justplayer.tpa;

import de.justplayer.tpa.commands.tpaCommandHandler;
import de.justplayer.tpa.listeners.PlayerLeaveListener;
import de.justplayer.tpa.utils.CooldownManager;
import de.justplayer.tpa.utils.TeleportRequestManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Plugin extends JavaPlugin {
    public FileConfiguration config;
    public CooldownManager cooldownManager;
    public TeleportRequestManager teleportRequestManager;

    public Plugin() {
        config = new Config(this).getConfig();
        cooldownManager = new CooldownManager();
        teleportRequestManager = new TeleportRequestManager(this);
    }

    @Override
    public void onEnable() {

        if (config.getBoolean("bStats.enabled") && !getDescription().getVersion().contains("dev")) {
            new Metrics(this, 18743);
            getLogger().info("bStats enabled");
        }

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerLeaveListener(this), this);

        // Register commands
        Objects.requireNonNull(getCommand("tpa")).setExecutor(new tpaCommandHandler(this));

        // Start the teleport request manager scheduler
        teleportRequestManager.start();

        getLogger().info("JustTPA initialized");
    }

    @Override
    public void onDisable() {
        getLogger().info("JustTPA disabled");

        teleportRequestManager.stop();
    }
}
