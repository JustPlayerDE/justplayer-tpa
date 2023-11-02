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

        List<Request> requests = plugin.teleportRequestManager.getRequestsForPlayer(player.getUniqueId());
        if (requests.isEmpty()) {
            player.sendMessage(plugin.config.getString("messages.prefix") + "You have no pending request");
            return true;
        }

        // Cancel oldest request
        plugin.teleportRequestManager.cancelRequest(requests.get(0), "Teleportation Denied by player");

        return true;
    }
}
