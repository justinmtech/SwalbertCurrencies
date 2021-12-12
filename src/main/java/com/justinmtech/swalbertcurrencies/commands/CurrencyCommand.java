package com.justinmtech.swalbertcurrencies.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CurrencyCommand extends CustomCommand {
    public CurrencyCommand(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public void run(CommandSender s, String cl, String[] args) {

        if (!(s instanceof Player)) {
            s.sendMessage(getName());
            return;
        }
        Player player = (Player) s;
        if (!player.hasPermission("admin.main")) {
            s.sendMessage(ChatColor.RED + "You don't have permission to use this!");
            return;
        }
        sendMessage("TEST " + (args.length > 0 ? args[0] : "No args"));
    }


    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] arguments) {
        List<String> arguments1 = Arrays.asList("testing", "testing2");

        List<String> result = new ArrayList<String>();
        if (arguments.length == 1) {
            for (String argument : arguments1)
                if (argument.toLowerCase().startsWith(arguments[0].toLowerCase()))
                    result.add(argument);
            return result;
        }

        return result;
    }
}
