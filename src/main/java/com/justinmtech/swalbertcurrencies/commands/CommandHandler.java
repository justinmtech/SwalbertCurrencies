package com.justinmtech.swalbertcurrencies.commands;

import com.justinmtech.swalbertcurrencies.SwalbertCurrencies;
import com.justinmtech.swalbertcurrencies.core.Currency;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.List;

public class CommandHandler implements CommandExecutor {
    private SwalbertCurrencies plugin;

    public CommandHandler(SwalbertCurrencies plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            switch (getArgLength(args)) {
                case 0:
                    getBalance(label, sender);
                    break;
                case 1:
                    if (getPlayerFromArg(args[0]) != null) {
                        getBalance(label, sender, args);
                        break;
                    } else {
                        plugin.getMessages().playerErrorInvalidPlayer(args[0]);
                    }
                    break;
                case 2:
                    if (args[0].equalsIgnoreCase("reset") && resetArgsValid(args)) {
                        resetBalance(label, sender, args);
                        break;
                    }
                case 3:
                    if (args[0].equalsIgnoreCase("pay") && payArgsValid((Player)sender, args, label)) {
                        payBalance(label, sender, args);
                        break;
                    }

                    if (args[0].equalsIgnoreCase("set") && setArgsValid(args)) {
                        setBalance(label, sender, args);
                        break;
                    }

                    if (args[0].equalsIgnoreCase("give") && giveArgsValid(args)) {
                        giveBalance(label, sender, args);
                        break;
                    }

                    if (args[0].equalsIgnoreCase("take") && takeArgsValid(sender, args, label)) {
                        takeBalance(label, sender, args);
                        break;
                    }
            }
        }
        return true;
    }

    private void payBalance(String label, CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Currency currency = getCurrencyFromString(label);
        Player receiver = getPlayerFromArg(args[1]);
        if (player != receiver) {
            if (isStringNumeric(args[2])) {
                BigDecimal amount = BigDecimal.valueOf(Double.valueOf(String.valueOf(args[2])));
                plugin.getData().payBalance(player, receiver, currency, amount, false);
            }
        } else {
            player.sendMessage(plugin.getMessages().playerErrorSelfPay());
        }
    }

    private void setBalance(String label, CommandSender sender, String[] args) {
        Currency currency = getCurrencyFromString(label);
        Player target = getPlayerFromArg(args[1]);
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(args[2]));
        plugin.getData().setBalance(target, currency, amount, false);
        BigDecimal balance = plugin.getData().getBalance(target, currency, false);
        sender.sendMessage(plugin.getMessages().adminSuccessSetCurrency(target.getName(), currency.getName(), balance.toString()));
    }

    private void resetBalance(String label, CommandSender sender, String[] args) {
        Currency currency = getCurrencyFromString(label);
        Player player = getPlayerFromArg(args[1]);
        BigDecimal balance = plugin.getData().getBalance(player, currency, false);
        if (plugin.getData().resetBalance(player, currency, false)) {
        sender.sendMessage(plugin.getMessages().adminSuccessResetCurrency(player.getName(), currency.getName()));
        }
    }

    //Get command sender's balance
    private void getBalance(String label, CommandSender sender) {
        Currency currency = getCurrencyFromString(label);
        Player player = (Player) sender;
        BigDecimal amount = plugin.getData().getBalance(player, currency, false);
        player.sendMessage(plugin.getMessages().playerSuccessBalance(currency.getName(), amount.toString()));
    }

    //Get balance by player's name
    private void getBalance(String label, CommandSender sender, String[] args) {
        Currency currency = getCurrencyFromString(label);
        Player player = getPlayerFromArg(args[0]);
        BigDecimal amount = plugin.getData().getBalance(player, currency, false);
        sender.sendMessage(plugin.getMessages().playerSuccessOtherBalance(player.getName(), currency.getName(), amount.toString()));
    }

    private void giveBalance(String label, CommandSender sender, String[] args) {
        Currency currency = getCurrencyFromString(label);
        Player player = getPlayerFromArg(args[1]);
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(args[2]));
        plugin.getData().giveBalance(player, currency, amount, false);
        BigDecimal balance = plugin.getData().getBalance(player, currency, false);
        sender.sendMessage(plugin.getMessages().adminSuccessGiveCurrency(player.getName(), currency.getName(), amount.toString(), balance.toString()));
    }

    private void takeBalance(String label, CommandSender sender, String[] args) {
        Currency currency = getCurrencyFromString(label);
        Player player = getPlayerFromArg(args[1]);
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(args[2]));
        plugin.getData().takeBalance(player, currency, amount, false);
        BigDecimal balance = plugin.getData().getBalance(player, currency, false);
        if (balance.equals(BigDecimal.valueOf(0))) {
            sender.sendMessage(plugin.getMessages().adminErrorNoBalance(player.getName(), currency.getName()));
        } else {
            sender.sendMessage(plugin.getMessages().adminSuccessTakeCurrency(player.getName(), currency.getName(), amount.toString(), balance.toString()));
        }
    }

    private boolean payArgsValid(Player sender, String[] args, String label) {
        Currency currency = getCurrencyFromString(label);
        if (isStringNumeric(args[2])) {
            if (!hasEnoughFunds(sender, label, args[2])) {
                sender.sendMessage(plugin.getMessages().playerErrorInsufficientFunds(currency.getName(), args[2]));
                return false;
            } else if (isValueZero(args[2])) {
                sender.sendMessage(plugin.getMessages().playerErrorPayZero());
                return false;
            }
        } else {
            sender.sendMessage(plugin.getMessages().playerErrorInvalidNumber(args[2]));
            return false;
        }
        return true;
    }

    private boolean resetArgsValid(String[] args) {
        if (getPlayerFromArg(args[1]) != null) {
            return true;
        }
        plugin.getMessages().adminErrorInvalidPlayer();
        return false;
    }

    private boolean giveArgsValid(String[] args) {
        Player player = getPlayerFromArg(args[1]);
        if (player != null) {
            if (isStringNumeric(args[2])) {
                return true;
            } else {
                plugin.getMessages().adminErrorInvalidNumber(args[2]);
                return false;
            }
        } else {
            plugin.getMessages().adminErrorInvalidPlayer();
            return false;
        }
    }

    private boolean setArgsValid(String[] args) {
        Player player = getPlayerFromArg(args[1]);
        if (player != null) {
            if (isStringNumeric(args[2])) {
                plugin.getMessages().adminErrorInvalidNumber(args[2]);
                return true;
            }
        } else {
            plugin.getMessages().adminErrorInvalidPlayer();
            return false;
        }
        return false;
    }

    private boolean takeArgsValid(CommandSender sender, String[] args, String label) {
        Player commandSender = (Player) sender;
        Currency currency = getCurrencyFromString(label);
        Player player = getPlayerFromArg(args[1]);
        if (player != null) {
            if (isStringNumeric(args[2])) {
                if (!isValueZero(args[2])) {
                    if (hasEnoughFunds(player, label, args[2])) {
                        return true;
                    } else {
                        commandSender.sendMessage(plugin.getMessages().adminErrorInsufficientBalance(player.getName()));
                        return false;
                    }
                } else {
                    commandSender.sendMessage(plugin.getMessages().adminErrorNoBalance(player.getName(), currency.getName()));
                    return false;
                }
            } else {
                commandSender.sendMessage(plugin.getMessages().adminErrorInvalidNumber(args[2]));
                return false;
            }
        } else {
            commandSender.sendMessage(plugin.getMessages().adminErrorInvalidPlayer());
            return false;
        }
    }

    private boolean hasDecimal(String arg) {
        return arg.contains(".");
    }

    private boolean currencyIsPayable(Currency currency) {
        return currency.isAllowPay();
    }

    private int getArgLength(String[] args) {
        return args.length;
    }

    private boolean isStringNumeric(String string) {
        if (string == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(string);
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isValueZero(String value) {
        BigDecimal bd = BigDecimal.valueOf(Double.valueOf(value));
        BigDecimal bdZero = BigDecimal.ZERO;
        int compare = bd.compareTo(BigDecimal.ZERO);
        return compare == 0;
    }

    private boolean hasEnoughFunds(Player sender, String label, String amount) {
        Currency currency = getCurrencyFromString(label);
        BigDecimal transactionAmount = BigDecimal.valueOf(Double.parseDouble(amount));
        BigDecimal playerBalance = plugin.getData().getBalance(sender, currency, false);
        int returnValue = transactionAmount.compareTo(playerBalance);
        if (returnValue == 0 || returnValue == -1) {
            return true;
        } else {
            return false;
        }
    }

    private Currency getCurrencyFromString(String currencyName) {
        List<Currency> currencies = plugin.getData().getCurrencies();
        for (Currency currency : currencies) {
            if (currency.getName().equalsIgnoreCase(currencyName)) {
                return currency;
            }
        }
        return null;
    }

    private Player getPlayerFromArg(String arg) {
        Player player = Bukkit.getPlayer(arg);
        if (Bukkit.getPlayer(arg) != null) {
            return player;
        } else {
        return null;
        }
    }
}
