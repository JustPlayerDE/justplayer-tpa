package de.justplayer.tpa;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.List;

public class Config {
    private final Plugin plugin;

    // Make sure the configuration is initialized
    public Config(Plugin plugin) {
        this.plugin = plugin;
        FileConfiguration config = this.plugin.getConfig();

        config.addDefault("tpa.timeout", 60);
        config.setComments("tpa.timeout", List.of("Time in seconds you have to accept a teleport request"));

        config.addDefault("tpa.wait", 5);
        config.setComments("tpa.wait", List.of(
                "Time in seconds you have to wait before you teleport (after teleport request was accepted)",
                "Any movement will cancel the teleport, 0 to disable",
                "Note: this feature is not implemented yet, teleports are currently instant."
        ));

        config.addDefault("tpa.cooldowns.tpa", 60);
        config.setComments("tpa.cooldowns.tpa", List.of(
                "Time in seconds you have to wait before you can send another teleport request"
        ));

        config.addDefault("tpa.cooldowns.tpaHere", 60);
        config.setComments("tpa.cooldowns.tpaHere", List.of(
                "Time in seconds you have to wait before you can send another tpa here request"
        ));

        config.addDefault("bStats.enabled", true);
        config.setComments("bStats.enabled", List.of(
                "Enable bStats for this plugin"
        ));

        // We use modrinth to check for updates
        config.addDefault("check-for-updates", true);
        config.setComments("check-for-updates", List.of(
                "Check for updates on startup, this uses the modrinth api to get the newest version for your server."
        ));

        config.addDefault("messages.prefix", "§8[§6JustTPA§8] §7");
        // TODO: Add more messages

        config.options().copyDefaults(true);
        this.plugin.saveConfig();
        this.plugin.reloadConfig();
    }

    public String getString(String key) {
        return this.plugin.getConfig().getString(key);
    }
    public String getString(String key, String defaultValue) {
        return this.plugin.getConfig().getString(key, defaultValue);
    }

    public int getInt(String key) {
        return this.plugin.getConfig().getInt(key);
    }
    public int getInt(String key, int defaultValue) {
        return this.plugin.getConfig().getInt(key, defaultValue);
    }

    public boolean getBoolean(String key) {
        return this.plugin.getConfig().getBoolean(key);
    }
    public boolean getBoolean(String key, boolean defaultValue) {
        return this.plugin.getConfig().getBoolean(key, defaultValue);
    }
}
