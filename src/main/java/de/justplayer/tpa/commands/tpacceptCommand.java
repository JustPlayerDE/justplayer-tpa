package de.justplayer.tpa.commands;

import de.justplayer.tpa.Plugin;
import de.justplayer.tpa.TeleportRequest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class tpacceptCommand implements CommandExecutor {
    private final Plugin plugin;

    public tpacceptCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
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

        if (args.length > 1) {
            player.sendMessage(plugin.translate("messages.prefix") + plugin.translate("messages.usages.tpaccept"));
            return true;
        }

        if (args.length == 0) {
            List<TeleportRequest> requests = plugin.teleportRequestManager.getRequestsForPlayer(player.getUniqueId());

            if (requests.isEmpty()) {
                player.sendMessage(plugin.translate("messages.prefix") + plugin.translate("messages.errors.request-not-found"));
                return true;
            }

            TeleportRequest request = null;
            Player requestSender = null;
            for (int i = requests.size() - 1; i >= 0; i--) {
                requestSender = plugin.getServer().getPlayer(requests.get(i).getSender());
                if (requestSender != null) {
                    request = requests.get(i);
                    break; // first valid player
                }
            }

            if (request == null) {
                player.sendMessage(plugin.translate("messages.prefix") + plugin.translate("messages.errors.request-not-found"));
                return true;
            }

            plugin.teleportRequestManager.acceptRequest(request);
            player.sendMessage(plugin.translate("messages.prefix") + plugin.translate("messages.request.accepted", Map.of("playername", requestSender.getName())));
            return true;
        }

        Player requestSender = plugin.getServer().getPlayer(args[0]);

        if (requestSender == null) {
            player.sendMessage(plugin.translate("messages.prefix") + plugin.translate("messages.errors.player-not-found"));
            return true;
        }

        TeleportRequest request = plugin.teleportRequestManager.getRequest(requestSender.getUniqueId(), player.getUniqueId());

        if (request == null) {
            player.sendMessage(plugin.translate("messages.prefix") + plugin.translate("messages.errors.request-not-found-by", Map.of("playername", args[0])));
            return true;
        }

        plugin.teleportRequestManager.acceptRequest(request);

        player.sendMessage(plugin.translate("messages.prefix") + plugin.translate("messages.request.accepted", Map.of("playername", requestSender.getName())));

        return true;
    }
}
