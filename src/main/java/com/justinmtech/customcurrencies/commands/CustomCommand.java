package com.justinmtech.customcurrencies.commands;

import com.justinmtech.customcurrencies.CustomCurrencies;
import org.bukkit.command.Command;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;

public abstract class CustomCommand extends Command implements PluginIdentifiableCommand {
    private static final CustomCurrencies plugin = CustomCurrencies.instance;

    public CustomCommand(String name) {
        super(name);
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }
}
