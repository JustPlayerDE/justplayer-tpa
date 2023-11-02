package de.justplayer.tpa.commands;

import de.justplayer.tpa.Plugin;
import de.justplayer.tpa.Request;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class tpacancelCommandHandler implements CommandExecutor {
    private final Plugin plugin;

    public tpacancelCommandHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        Player player;
        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            sender.sendMessage(plugin.config.getString("messages.prefix") + "You must be a player to use this command");
            return true;
        }

        Request request = plugin.teleportRequestManager.getRequestByPlayer(player.getUniqueId());

        if (request == null) {
            player.sendMessage(plugin.config.getString("messages.prefix") + "You have no pending request");
            return true;
        }

        plugin.teleportRequestManager.cancelRequest(request,
                "Teleportation Canceled",
                "Teleportation Canceled by " + player.getName()
        );

        return true;
    }
}
