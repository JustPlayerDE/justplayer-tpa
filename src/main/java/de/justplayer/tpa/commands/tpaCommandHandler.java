package de.justplayer.tpa.commands;

import de.justplayer.tpa.Plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class tpaCommandHandler implements CommandExecutor {

    private final Plugin plugin;

    public tpaCommandHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        // TODO
        return true;
    }
}
