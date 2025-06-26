package de.justplayer.tpa.commands;

import de.justplayer.tpa.Plugin;
import de.justplayer.tpa.TeleportRequest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class tpacancelCommand implements CommandExecutor {
    private final Plugin plugin;

    public tpacancelCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        Player player;
        if (sender instanceof Player) {
            player = (Player) sender;

            if(!plugin.isFeatureEnabled("tpa") && !plugin.isFeatureEnabled("tpa-here")) {
                player.sendMessage(plugin.translate("messages.prefix") + plugin.translate("messages.errors.feature-disabled"));
                return true;
            }
        } else {
            sender.sendMessage(plugin.translate("messages.prefix") + plugin.translate("messages.errors.player-required"));
            return true;
        }

        TeleportRequest request = plugin.teleportRequestManager.getRequestBySender(player.getUniqueId());

        if (request == null) {
            player.sendMessage(plugin.translate("messages.prefix") + plugin.translate("messages.errors.request-not-found"));
            return true;
        }

        plugin.teleportRequestManager.cancelRequest(request,
                plugin.translate("messages.request.canceled"),
                plugin.translate("messages.request.canceled-by", Map.of("playername", player.getName()))
        );

        return true;
    }
}
