package de.justplayer.tpa.commands;

import de.justplayer.tpa.Plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class tpareloadCommand implements CommandExecutor {

    private final Plugin plugin;

    public tpareloadCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        this.plugin.reloadConfig();
        sender.sendMessage(plugin.translate("messages.prefix") + plugin.translate("messages.reloaded"));
        return true;
    }
}
