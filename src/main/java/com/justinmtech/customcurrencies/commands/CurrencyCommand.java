package com.justinmtech.customcurrencies.commands;

import com.justinmtech.customcurrencies.CustomCurrencies;
import org.bukkit.command.CommandSender;

public class CurrencyCommand extends CustomCommand {
    private final CommandHandler commandHandler;

    public CurrencyCommand(String name) {
        super(name);
        CustomCurrencies plugin = CustomCurrencies.instance;
        commandHandler = new CommandHandler(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        commandHandler.onCommand(sender, this, commandLabel, args);
        return true;
    }
}