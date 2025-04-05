package de.justplayer.tpa.listeners;

import de.justplayer.tpa.Plugin;
import de.justplayer.tpa.TeleportRequest;
import de.justplayer.tpa.ReturnRequest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Map;
import java.util.Objects;

public class PlayerMoveListener implements Listener {
    final Plugin plugin;
    public PlayerMoveListener(Plugin plugin) {
        this.plugin = plugin;
    }

    // This could get very expensive, so let's ignore cancelled events and put it on monitor priority (last called).
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        long requestWarmUpTime = plugin.getConfig().getInt("tpa.wait");

        // we only care about position changes
        if(
                Objects.requireNonNull(event.getFrom()).getBlockX() == Objects.requireNonNull(event.getTo()).getBlockX()
                        && event.getFrom().getBlockZ() == event.getTo().getBlockZ()
                        && event.getFrom().getBlockY() == event.getTo().getBlockY()
        ) {
            return;
        }

        TeleportRequest teleportRequest = this.plugin.teleportRequestManager.getRequestBySender(event.getPlayer().getUniqueId());
        if( teleportRequest != null && teleportRequest.isTeleporting()) {
            plugin.log("Player " + event.getPlayer().getUniqueId() + " has moved while being teleported.", "Debug");
            Player receiver = plugin.getServer().getPlayer(teleportRequest.getReceiver());

            plugin.teleportRequestManager.cancelRequest(
                    teleportRequest,
                    "messages.request.moved-to",
                    Map.of("playername", receiver != null ? receiver.getName() : "[Offline]"),
                    "messages.request.moved-from",
                    Map.of("playername", event.getPlayer().getName())
            );
        }

        ReturnRequest returnRequest = this.plugin.teleportRequestManager.getPlayerReturnRequest(event.getPlayer().getUniqueId());
        if(returnRequest != null && returnRequest.isTeleporting()) {
            plugin.log("Player " + event.getPlayer().getUniqueId() + " has moved while being returned.", "Debug");

            plugin.teleportRequestManager.cancelRequest(
                    returnRequest,
                    "messages.request.moved-return"
            );
        }
    }
}
