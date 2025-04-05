package de.justplayer.tpa.commands;

import de.justplayer.tpa.Plugin;
import de.justplayer.tpa.ReturnRequest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class tpareturnCommand implements CommandExecutor {

    private final Plugin plugin;

    public tpareturnCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        Player player;
        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            sender.sendMessage(plugin.translate("messages.prefix") + plugin.translate("messages.errors.player-required"));
            return true;
        }

        ReturnRequest returnRequest = plugin.teleportRequestManager.getPlayerReturnRequest(player.getUniqueId());

        if(returnRequest == null) {
            sender.sendMessage(plugin.translate("messages.prefix") + plugin.translate("messages.errors.return-not-found"));
            return true;
        }


        sender.sendMessage(plugin.translate("messages.prefix") + plugin.translate("messages.request.return"));


        returnRequest.setRequested();

        return true;
    }
}