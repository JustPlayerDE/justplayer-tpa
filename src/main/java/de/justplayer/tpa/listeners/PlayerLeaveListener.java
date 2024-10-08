package de.justplayer.tpa.listeners;

import de.justplayer.tpa.Plugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

    private final Plugin plugin;

    public PlayerLeaveListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        plugin.log("Removing cooldowns and requests for " + event.getPlayer().getUniqueId() + " because the player left the server.", "Debug");
        // May be exploitable if the player leaves and rejoins the server before the cooldown ends
        // But I don't think that's a big problem
        // Memory footprint is a bit more important than making sure that they can never bypass the cooldown
        this.plugin.cooldownManager.removeCooldowns(event.getPlayer().getUniqueId());

        // Remove all pending requests
        this.plugin.teleportRequestManager.removeRequests(event.getPlayer().getUniqueId());
    }
}
