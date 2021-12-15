package com.justinmtech.swalbertcurrencies.commands;

import com.justinmtech.swalbertcurrencies.SwalbertCurrencies;
import com.justinmtech.swalbertcurrencies.core.Currency;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.List;

public class CommandHandler implements CommandExecutor {
    private final SwalbertCurrencies plugin;

    public CommandHandler(SwalbertCurrencies plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            switch (getArgLength(args)) {
                case 0:
                    if (sender.hasPermission(getCurrencyFromString(label) + ".player.check")) {
                        getBalance(label, sender);
                    } else {
                        sender.sendMessage(plugin.getMessages().playerErrorNoPermission());
                    }
                    break;
                case 1:
                    if (getPlayerIdFromArg(args[0]).hasPlayedBefore()) {
                        if (sender.hasPermission(getCurrencyFromString(label) + "player.checkother")) {
                            getBalance(label, sender, getPlayerIdFromArg(args[0]));
                        } else {
                            sender.sendMessage(plugin.getMessages().playerErrorNoPermission());
                        }
                    } else {
                        sender.sendMessage(plugin.getMessages().playerErrorInvalidPlayer(args[0]));
                    }
                    break;
                case 2:
                    if (args[0].equalsIgnoreCase("reset")) {
                        if (sender.hasPermission(getCurrencyFromString(label) + ".admin.reset")) {
                            if (resetArgsValid(args)) {
                                resetBalance(label, sender, args);
                            }
                        } else {
                            sender.sendMessage(plugin.getMessages().adminErrorNoPermission());
                        }
                        break;
                    }
                case 3:
                    if (args[0].equalsIgnoreCase("pay")) {
                        if (sender.hasPermission(getCurrencyFromString(label) + ".player.checkother")) {
                            if (payArgsValid((Player)sender, args, label)) {
                                payBalance(label, sender, args);
                            }
                        } else {
                            sender.sendMessage(plugin.getMessages().playerErrorNoPermission());
                        }
                        break;
                    }

                    if (args[0].equalsIgnoreCase("set")) {
                        if (sender.hasPermission(getCurrencyFromString(label) + ".admin.set")) {
                            if (setArgsValid(args)) {
                                setBalance(label, sender, args);
                            } else {
                            sender.sendMessage(plugin.getMessages().adminSetCurrency(label));
                            }
                        } else {
                            sender.sendMessage(plugin.getMessages().adminErrorNoPermission());
                        }
                        break;
                    }

                    if (args[0].equalsIgnoreCase("give")) {
                        if (sender.hasPermission(getCurrencyFromString(label) + ".admin.give")) {
                            if (giveArgsValid(args)) {
                                giveBalance(label, sender, args);
                            } else {
                                sender.sendMessage(plugin.getMessages().adminGiveCurrency(label));
                            }
                        } else {
                            sender.sendMessage(plugin.getMessages().adminErrorNoPermission());
                        }
                        break;
                    }

                    if (args[0].equalsIgnoreCase("take")) {
                        if (sender.hasPermission(getCurrencyFromString(label) + ".admin.take")) {
                            if (takeArgsValid(sender, args, label)) {
                                takeBalance(label, sender, args);
                                }
                            } else {
                            sender.sendMessage(plugin.getMessages().adminErrorNoPermission());
                        }
                        break;
                    }
            }
        }
        return true;
    }

    private void payBalance(String label, CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Currency currency = getCurrencyFromString(label);
        OfflinePlayer receiver = getPlayerIdFromArg(args[1]);
        if (player != receiver) {
            if (isStringNumeric(args[2])) {
                BigDecimal amount = BigDecimal.valueOf(Double.valueOf(String.valueOf(args[2])));
                plugin.getData().payBalance(player, receiver, currency, amount);
            }
        } else {
            player.sendMessage(plugin.getMessages().playerErrorSelfPay());
        }
    }

    private void setBalance(String label, CommandSender sender, String[] args) {
        Currency currency = getCurrencyFromString(label);
        OfflinePlayer target = getPlayerIdFromArg(args[1]);
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(args[2]));
        plugin.getData().setBalance(target, currency, amount);
        BigDecimal balance = plugin.getData().getBalance(target, currency);
        if (currency.isAllowDecimals()) {
            sender.sendMessage(plugin.getMessages().adminSuccessSetCurrency(target.getName(), currency.getName(), balance.toString()));
        } else {
            sender.sendMessage(plugin.getMessages().adminSuccessSetCurrency(target.getName(), currency.getName(), balance.toBigInteger().toString()));
        }
    }

    private void resetBalance(String label, CommandSender sender, String[] args) {
        Currency currency = getCurrencyFromString(label);
        OfflinePlayer player = getPlayerIdFromArg(args[1]);
        if (plugin.getData().resetBalance(player, currency)) {
        sender.sendMessage(plugin.getMessages().adminSuccessResetCurrency(player.getName(), currency.getName()));
        }
    }

    //Get command sender's balance
    private void getBalance(String label, CommandSender sender) {
        Currency currency = getCurrencyFromString(label);
        Player player = (Player) sender;
        BigDecimal amount = plugin.getData().getBalance(player, currency);
        if (currency.isAllowDecimals()) {
            sender.sendMessage(plugin.getMessages().playerSuccessOtherBalance(player.getName(), currency.getName(), amount.toString()));
        } else {
            sender.sendMessage(plugin.getMessages().playerSuccessOtherBalance(player.getName(), currency.getName(), amount.toBigInteger().toString()));
        }
    }

    //Get balance by player's name
    private void getBalance(String label, CommandSender sender, OfflinePlayer player) {
        Currency currency = getCurrencyFromString(label);
        //OfflinePlayer player = getPlayerIdFromArg(args[0]);
        BigDecimal amount = plugin.getData().getBalance(player, currency);
        if (currency.isAllowDecimals()) {
            sender.sendMessage(plugin.getMessages().playerSuccessOtherBalance(player.getName(), currency.getName(), amount.toString()));
        } else {
            sender.sendMessage(plugin.getMessages().playerSuccessOtherBalance(player.getName(), currency.getName(), amount.toBigInteger().toString()));
        }
    }

    private void giveBalance(String label, CommandSender sender, String[] args) {
        Currency currency = getCurrencyFromString(label);
        OfflinePlayer player = getPlayerIdFromArg(args[1]);
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(args[2]));
        plugin.getData().giveBalance(player, currency, amount);
        BigDecimal balance = plugin.getData().getBalance(player, currency);
        if (currency.isAllowDecimals()) {
            sender.sendMessage(plugin.getMessages().adminSuccessGiveCurrency(player.getName(), currency.getName(), amount.toString(), balance.toString()));
        } else {
            sender.sendMessage(plugin.getMessages().adminSuccessGiveCurrency(player.getName(), currency.getName(), amount.toString(), balance.toBigInteger().toString()));
        }
    }

    private void takeBalance(String label, CommandSender sender, String[] args) {
        Currency currency = getCurrencyFromString(label);
        OfflinePlayer player = getPlayerIdFromArg(args[1]);
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(args[2]));
        plugin.getData().takeBalance(player, currency, amount);
        BigDecimal balance = plugin.getData().getBalance(player, currency);
        if (balance.equals(BigDecimal.ZERO)) {
            sender.sendMessage(plugin.getMessages().adminErrorNoBalance(player.getName(), currency.getName()));
        } else {
            sender.sendMessage(plugin.getMessages().adminSuccessTakeCurrency(player.getName(), currency.getName(), amount.toString(), balance.toString()));
        }
    }

    private boolean payArgsValid(Player sender, String[] args, String label) {
        Currency currency = getCurrencyFromString(label);
        if (currencyIsPayable(currency)) {
            if (isStringNumeric(args[2])) {
                if (!currency.isAllowDecimals()) {
                    if (hasDecimal(args[2])) {
                        sender.sendMessage(plugin.getMessages().playerErrorOnlyIntegers());
                        return false;
                    }
                }
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
        } else {
            sender.sendMessage(plugin.getMessages().playerErrorCannotPay(currency.getName()));
            return false;
        }
        return true;
    }

    private boolean resetArgsValid(String[] args) {
        if (getPlayerIdFromArg(args[1]) != null) {
            return true;
        } else {
            plugin.getMessages().adminErrorInvalidPlayer();
            return false;
        }
    }

    private boolean giveArgsValid(String[] args) {
        OfflinePlayer player = getPlayerIdFromArg(args[1]);
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
        OfflinePlayer player = getPlayerIdFromArg(args[1]);
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
        OfflinePlayer player = getPlayerIdFromArg(args[1]);
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

    private boolean hasPlayedBefore(OfflinePlayer offlinePlayer) {
        return offlinePlayer.hasPlayedBefore();
    }

    private String getUUID(OfflinePlayer offlinePlayer) {
        return offlinePlayer.getUniqueId().toString();
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

    private boolean hasEnoughFunds(OfflinePlayer sender, String label, String amount) {
        Currency currency = getCurrencyFromString(label);
        BigDecimal transactionAmount = BigDecimal.valueOf(Double.parseDouble(amount));
        BigDecimal playerBalance = plugin.getData().getBalance(sender, currency);
        int returnValue = transactionAmount.compareTo(playerBalance);
        if (returnValue == 0 || returnValue == -1) {
            return true;
        } else {
            return false;
        }
    }

    private Currency getCurrencyFromString(String currencyName) {
        List<Currency> currencies = plugin.getConfigManager().getCurrencies();
        for (Currency currency : currencies) {
            if (currency.getName().equalsIgnoreCase(currencyName)) {
                return currency;
            } else if (!currency.getName().equalsIgnoreCase(currencyName)) {
                for (String alias : currency.getAliasNames()) {
                    if (alias.equalsIgnoreCase(currencyName)) {
                        return currency;
                    }
                }
            } else {
                return null;
            }
        }
        return null;
    }

    private OfflinePlayer getPlayerIdFromArg(String arg) {
        if (playerPlayedBefore(arg)) {
            return Bukkit.getOfflinePlayer(arg);
        } else {
            return null;
        }
    }

    private boolean playerPlayedBefore(String arg) {
        try {
            OfflinePlayer offlinePlayer2 = Bukkit.getOfflinePlayer(arg);
            return offlinePlayer2.hasPlayedBefore();
        } catch (NullPointerException e) {
            return false;
        }
    }
}
