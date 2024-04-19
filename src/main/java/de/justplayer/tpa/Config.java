package de.justplayer.tpa;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    private final Plugin plugin;

    // Make sure the configuration is initialized
    public Config(Plugin plugin) {
        this.plugin = plugin;
        FileConfiguration config = this.plugin.getConfig();


        // Time in seconds you have to accept a teleport request
        config.addDefault("tpa.timeout", 60);
        // Time in seconds you have to wait before you teleport (after teleport request was accepted)
        // Any movement will cancel the teleport, 0 to disable (not implemented yet)
        config.addDefault("tpa.wait", 5);
        // Time in seconds you have to wait before you can send another teleport request
        config.addDefault("tpa.cooldowns.tpa", 60);
        // Time in seconds you have to wait before you can send another tpa here request
        config.addDefault("tpa.cooldowns.tpaHere", 60);

        config.addDefault("bStats.enabled", true);

        // We use modrinth to check for updates
        config.addDefault("check-for-updates", true);

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
