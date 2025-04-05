package de.justplayer.tpa.listeners;

import de.justplayer.tpa.Plugin;
import de.justplayer.tpa.ReturnRequest;
import de.justplayer.tpa.TeleportRequest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;

public class PlayerDamageListener implements Listener {

    private final Plugin plugin;

    public PlayerDamageListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            TeleportRequest teleportRequest = this.plugin.teleportRequestManager.getRequestBySender(player.getUniqueId());
            if( teleportRequest != null && teleportRequest.isTeleporting()) {
                plugin.log("Player " + player.getUniqueId() + " received damage while being teleported.", "Debug");
                Player receiver = plugin.getServer().getPlayer(teleportRequest.getReceiver());

                plugin.teleportRequestManager.cancelRequest(
                        teleportRequest,
                        "messages.request.moved-to",
                        Map.of("playername", receiver != null ? receiver.getName() : "[Offline]"),
                        "messages.request.moved-from",
                        Map.of("playername", player.getName())
                );
            }

            ReturnRequest returnRequest = this.plugin.teleportRequestManager.getPlayerReturnRequest(player.getUniqueId());
            if(returnRequest != null && returnRequest.isTeleporting()) {
                plugin.log("Player " + player.getUniqueId() + " received damage while being returned.", "Debug");

                plugin.teleportRequestManager.cancelRequest(
                        returnRequest,
                        "messages.request.moved-return"
                );
            }
        }
    }
}
