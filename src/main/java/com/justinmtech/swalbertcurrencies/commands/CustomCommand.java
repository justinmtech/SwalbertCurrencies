package com.justinmtech.swalbertcurrencies.commands;

import com.justinmtech.swalbertcurrencies.SwalbertCurrencies;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

public abstract class CustomCommand extends Command implements PluginIdentifiableCommand {
    private static SwalbertCurrencies plugin = SwalbertCurrencies.getInstance();
    CommandSender sender;

    public CustomCommand(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    public abstract void run(CommandSender sender, String commandLabel, String[] arguments);//Just simpler and allows 'return;' instead of 'return true/false;'

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] arguments) {
        this.sender = sender;
        run(sender, commandLabel, arguments);//actually run the command.
        return true;
    }

    protected void sendMessage(String... messages) {
        Arrays.stream(messages)
                .forEach(message -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));//Why not have a way to send messages easily colored?
    }
    protected void sendMessage(CommandSender sender, String... messages) {
        Arrays.stream(messages)
                .forEach(message -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));//Why not have a way to send messages easily colored?
    }
}
