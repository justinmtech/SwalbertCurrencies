package com.justinmtech.swalbertcurrencies.commands;

import com.justinmtech.swalbertcurrencies.SwalbertCurrencies;
import org.bukkit.command.CommandSender;

public class CurrencyCommand extends CustomCommand {
    private final SwalbertCurrencies plugin = SwalbertCurrencies.getInstance();
    private final CommandHandler commandHandler;

    public CurrencyCommand(String name) {
        super(name);
        commandHandler = new CommandHandler(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        commandHandler.onCommand(sender, this, commandLabel, args);
        return true;
    }
}