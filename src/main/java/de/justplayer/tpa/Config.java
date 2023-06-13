package de.justplayer.tpa;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    private final FileConfiguration config;

    public Config(Plugin plugin) {
        config = plugin.getConfig();

        // Time in seconds you have to accept a teleport request
        config.addDefault("tpa.timeout", 60);
        // Time in seconds you have to wait before you can send another teleport request
        config.addDefault("tpa.cooldowns.tpa", 60);
        // Time in seconds you have to wait before you can send another tpa here request
        config.addDefault("tpa.cooldowns.tpaHere", 60);

        config.addDefault("bStats.enabled", true);

        config.addDefault("messages.prefix", "§8[§6JustTPA§8] §7");
        // TODO: Add more messages

        config.options().copyDefaults(true);
        plugin.saveConfig();
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
