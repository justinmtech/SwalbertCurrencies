package com.justinmtech.swalbertcurrencies.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CurrencyCommand extends Command {

    public CurrencyCommand(String name) {
        setName(name);
        setLabel(name);
        setDescription()
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        return false;
    }
}
