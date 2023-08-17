package de.justplayer.tpa.commands;

import de.justplayer.tpa.Plugin;
import de.justplayer.tpa.Request;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class tpacceptCommandHandler implements CommandExecutor {
    private final Plugin plugin;

    public tpacceptCommandHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        Player player;
        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            sender.sendMessage(plugin.config.getString("messages.prefix") + "You must be a player to use this command");
            return true;
        }

        if (args.length > 1) {
            player.sendMessage(plugin.config.getString("messages.prefix") + "Usage: /tpaccept to accept the last teleport request, or /tpaccept <player> to accept a specific teleport request");
            return true;
        }

        if (args.length == 0) {
            List<Request> requests = plugin.teleportRequestManager.getRequestsForPlayer(player.getUniqueId());

            if (requests.isEmpty()) {
                player.sendMessage(plugin.config.getString("messages.prefix") + "You have no pending requests");
                return true;
            }

            Request request = null;
            Player requestSender = null;
            for (int i = requests.size() - 1; i >= 0; i--) {
                requestSender = plugin.getServer().getPlayer(requests.get(i).getSender());
                if (requestSender != null) {
                    request = requests.get(i);
                    break; // first valid player
                }
            }

            if (request == null) {
                player.sendMessage(plugin.config.getString("messages.prefix") + "You have no pending requests");
                return true;
            }

            plugin.teleportRequestManager.acceptRequest(request);
            player.sendMessage(plugin.config.getString("messages.prefix") + "You accepted the teleport request from " + requestSender.getName());
            return true;
        }

        Player requestSender = plugin.getServer().getPlayer(args[0]);

        if (requestSender == null) {
            player.sendMessage(plugin.config.getString("messages.prefix") + "Player not found");
            return true;
        }

        Request request = plugin.teleportRequestManager.getRequestByPlayer(requestSender.getUniqueId());

        if (request == null) {
            player.sendMessage(plugin.config.getString("messages.prefix") + "No pending request from that player");
            return true;
        }

        plugin.teleportRequestManager.acceptRequest(request);

        player.sendMessage(plugin.config.getString("messages.prefix") + "You accepted the teleport request from " + requestSender.getName());

        return true;
    }
}
