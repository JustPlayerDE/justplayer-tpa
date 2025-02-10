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
        config.addDefault("tpa.return-timeout", 60);
        config.setComments("tpa.return-timeout", List.of("Time in seconds until the return command times out. Put 0 to make it usable indefinitely."));

        config.addDefault("tpa.wait", 0);
        config.setComments("tpa.wait", List.of(
                "Time in seconds you have to wait before you teleport (after teleport request was accepted)",
                "Any movement will cancel the teleport, 0 to disable",
                "Note: The requesting player has to wait for teleport in both tpahere and tpa cases."
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

        config.addDefault("tpa.verbose", false);
        config.setComments("tpa.verbose", List.of(
                "Makes the plugin spam the console with maybe useful information (more verbose)"
        ));

        // We use modrinth to check for updates
        config.addDefault("check-for-updates", true);
        config.setComments("check-for-updates", List.of(
                "Check for updates on startup, this uses the modrinth api to get the newest version for your server."
        ));

        config.addDefault("messages.prefix", "§8[§6JustTPA§8] §7");
        config.addDefault("messages.reloaded", "Configuration has been reloaded.");
        config.addDefault("messages.usages.tpa", "Usage: /tpa <player>");
        config.addDefault("messages.usages.tpahere", "Usage: /tpahere <player>");
        config.addDefault("messages.usages.tpaccept", "Usage: /tpaccept to accept the last teleport request, or /tpaccept <player> to accept a specific teleport request");

        config.addDefault("messages.errors.player-required", "You must be a player to use this command.");
        config.addDefault("messages.errors.player-not-found", "Player not found.");
        config.addDefault("messages.errors.player-self-request", "You can't send a teleport request to yourself.");
        config.addDefault("messages.errors.request-pending", "You already have a pending request.");
        config.addDefault("messages.errors.request-not-found", "You have no pending request.");
        config.addDefault("messages.errors.return-not-found", "You have no place to return to, or it is timed out.");
        config.addDefault("messages.errors.request-not-found-by", "You have no pending request from %playername%.");
        config.addDefault("messages.errors.cooldown", "You have to wait %seconds% seconds before you can send another teleport request.");

        config.addDefault("messages.request.sent", "Teleport request sent to %playername% they have %seconds% seconds to accept it.");
        config.addDefault("messages.request.return", "You will be returned to your last location.");
        config.addDefault("messages.request.received", "You have received a teleport request from %playername% which expires in %seconds% seconds.");
        config.addDefault("messages.request.accept", "Type /tpaccept to accept the request.");
        config.addDefault("messages.request.deny", "Type /tpadeny to deny the request.");
        config.addDefault("messages.request.denied-by", "Teleportation Denied by %playername%.");
        config.addDefault("messages.request.denied", "You have denied the Teleportation request from %playername%.");
        config.addDefault("messages.request.warning-tpa-here", "Warning: if you accept this request, you will be teleported to %playername%.");
        config.addDefault("messages.request.canceled", "Teleportation Canceled.");
        config.addDefault("messages.request.canceled-by", "Teleportation Canceled by %playername%.");
        config.addDefault("messages.request.accepted", "You accepted the teleport request from %playername%.");
        config.addDefault("messages.request.accepted-by", "Your teleport request has been accepted by %playername%.");
        config.addDefault("messages.request.timeout-to", "Your teleport request to %playername% has timed out.");
        config.addDefault("messages.request.timeout-from", "Your teleport request from %playername% has timed out.");
        config.addDefault("messages.request.teleported-to", "You have been teleported to %playername%.");
        config.addDefault("messages.request.teleported-from", "%playername% has been teleported to you.");
        config.addDefault("messages.request.moved-return", "Teleportation has been cancelled because you have moved.");
        config.addDefault("messages.request.moved-to", "Teleportation to %playername% has been cancelled because you have moved.");
        config.addDefault("messages.request.moved-from", "Teleportation cancelled because %playername% has moved.");
        config.addDefault("messages.request.wait-to", "Please stand still for %time% seconds to get teleported to %playername%.");
        config.addDefault("messages.request.wait-to-here", "Please stand still for %time% seconds to get %playername% teleported to you.");
        config.addDefault("messages.request.wait-from", "%playername% will be teleported in %time% seconds.");

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
