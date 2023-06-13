package de.justplayer.tpa;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    private final FileConfiguration config;

    public Config(Plugin plugin) {
        config = plugin.getConfig();

        config.addDefault("bStats.enabled", true);

        config.addDefault("messages.prefix", "&8[&6JustTPA&8] &7");
        // TODO: Add more messages

        config.options().copyDefaults(true);
        plugin.saveConfig();
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
