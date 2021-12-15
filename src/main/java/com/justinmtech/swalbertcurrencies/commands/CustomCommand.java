package com.justinmtech.swalbertcurrencies.commands;

import com.justinmtech.swalbertcurrencies.SwalbertCurrencies;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

public abstract class CustomCommand extends Command implements PluginIdentifiableCommand {
    private static final SwalbertCurrencies plugin = SwalbertCurrencies.getInstance();

    public CustomCommand(String name) {
        super(name);
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }
}
