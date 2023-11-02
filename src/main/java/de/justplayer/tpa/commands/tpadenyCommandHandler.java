package de.justplayer.tpa.commands;

import de.justplayer.tpa.Plugin;
import de.justplayer.tpa.Request;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class tpadenyCommandHandler implements CommandExecutor {
    private final Plugin plugin;

    public tpadenyCommandHandler(Plugin plugin) {
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

        if(args.length > 0) {
            Player requestSender = plugin.getServer().getPlayer(args[0]);

            if (requestSender == null) {
                player.sendMessage(plugin.config.getString("messages.prefix") + "Player not found");
                return true;
            }

            Request request = plugin.teleportRequestManager.getRequest(requestSender.getUniqueId(), player.getUniqueId());

            if(request == null) {
                player.sendMessage(plugin.config.getString("messages.prefix") + "You have no pending request from that player");
                return true;
            }

            plugin.teleportRequestManager.cancelRequest(request, "Teleportation Denied by player");
            player.sendMessage(plugin.config.getString("messages.prefix") + "You have denied the Teleportation request from " + requestSender.getName());
            return true;
        }

        List<Request> requests = plugin.teleportRequestManager.getRequestsForPlayer(player.getUniqueId());
        if (requests.isEmpty()) {
            player.sendMessage(plugin.config.getString("messages.prefix") + "You have no pending request");
            return true;
        }

        Request request = requests.get(0);
        Player requestSender = plugin.getServer().getPlayer(request.getSender());

        plugin.teleportRequestManager.cancelRequest(requests.get(0), "Teleportation Denied by player");
        player.sendMessage(plugin.config.getString("messages.prefix") + "You have denied the Teleportation request from " + (requestSender != null ? requestSender.getName() : "Unknown Player"));

        return true;
    }
}
