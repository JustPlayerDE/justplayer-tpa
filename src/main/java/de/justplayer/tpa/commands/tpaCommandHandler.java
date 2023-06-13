package de.justplayer.tpa.commands;

import de.justplayer.tpa.Plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class tpaCommandHandler implements CommandExecutor {

    private final Plugin plugin;

    public tpaCommandHandler(Plugin plugin) {
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

        if (args.length != 1) {
            player.sendMessage(plugin.config.getString("messages.prefix") + "Usage: /tpa <player>");
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(plugin.config.getString("messages.prefix") + "Player not found");
            return true;
        }

        if (target == player) {
            player.sendMessage(plugin.config.getString("messages.prefix") + "You can't send a teleport request to yourself");
            return true;
        }

        if (plugin.cooldownManager.isOnCooldown(player.getUniqueId(), "tpa")) {
            player.sendMessage(plugin.config.getString("messages.prefix") + "You have to wait " + plugin.cooldownManager.getCooldown(player.getUniqueId(), "tpa") + " seconds before you can send another teleport request");
            return true;
        }

        if(plugin.teleportRequestManager.getRequestByPlayer(player.getUniqueId()) != null) {
            player.sendMessage(plugin.config.getString("messages.prefix") + "You already have a pending request");
            return true;
        }

        plugin.cooldownManager.addCooldown(player.getUniqueId(), "tpa", plugin.config.getInt("tpa.cooldowns.tpa"));

        plugin.teleportRequestManager.createRequest(
                player.getUniqueId(),
                target.getUniqueId(),
                false
        );

        player.sendMessage(plugin.config.getString("messages.prefix") + "Teleport request sent to " + target.getName() + ", they have " + plugin.config.getInt("tpa.timeout") + " seconds to accept it");
        target.sendMessage(plugin.config.getString("messages.prefix") + "You have received a teleport request from " + player.getName());
        target.sendMessage(plugin.config.getString("messages.prefix") + "This request will expire in " + plugin.config.getInt("tpa.timeout") + " seconds");
        target.sendMessage(plugin.config.getString("messages.prefix") + "Type /tpaccept to accept the request");
        target.sendMessage(plugin.config.getString("messages.prefix") + "Type /tpdeny to deny the request");


        return true;
    }
}
