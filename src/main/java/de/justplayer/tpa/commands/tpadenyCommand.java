package de.justplayer.tpa.commands;

import de.justplayer.tpa.Plugin;
import de.justplayer.tpa.TeleportRequest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class tpadenyCommand implements CommandExecutor {
    private final Plugin plugin;

    public tpadenyCommand(Plugin plugin) {
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

        List<TeleportRequest> requests = plugin.teleportRequestManager.getRequestsForPlayer(player.getUniqueId());
        if (requests.isEmpty()) {
            player.sendMessage(plugin.translate("messages.prefix") + plugin.translate("messages.errors.request-not-found"));
            return true;
        }

        if(args.length > 0) {
            Player requestSender = plugin.getServer().getPlayer(args[0]);

            if (requestSender == null) {
                player.sendMessage(plugin.translate("messages.prefix") + plugin.translate("messages.errors.player-not-found"));
                return true;
            }

            TeleportRequest request = plugin.teleportRequestManager.getRequest(requestSender.getUniqueId(), player.getUniqueId());

            if(request == null) {
                player.sendMessage(plugin.translate("messages.prefix") + plugin.translate("messages.errors.request-not-found-by",
                        Map.of("playername", args[0])
                ));
                return true;
            }

            plugin.teleportRequestManager.cancelRequest(request,"messages.request.denied-by", Map.of("playername", player.getName()));

            player.sendMessage(plugin.translate("messages.prefix") + plugin.translate("messages.request.denied",
                    Map.of("playername", player.getName())
            ));

            return true;
        }

        TeleportRequest request = requests.get(0);
        Player requestSender = plugin.getServer().getPlayer(request.getSender());
        String senderName = (requestSender != null ? requestSender.getName() : "Unknown Player");

        plugin.teleportRequestManager.cancelRequest(requests.get(0),"messages.request.denied-by", Map.of("playername", player.getName()));
        player.sendMessage(plugin.translate("messages.prefix") + plugin.translate("messages.request.denied",
                Map.of("playername", senderName)
        ));

        return true;
    }
}
