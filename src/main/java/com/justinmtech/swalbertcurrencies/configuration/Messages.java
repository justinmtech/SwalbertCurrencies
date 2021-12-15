package com.justinmtech.swalbertcurrencies.configuration;

import com.justinmtech.swalbertcurrencies.SwalbertCurrencies;
import org.bukkit.ChatColor;

public class Messages {
    private SwalbertCurrencies plugin;

    public Messages(SwalbertCurrencies plugin) {
        this.plugin = plugin;
    }

    private String getStringFromPath(String path) {
        return ChatColor.translateAlternateColorCodes('&', (String) plugin.getConfigManager().getMessagesConfig().get(path));
    }

    public String adminReload(String currencyName) {
        String str = getStringFromPath("admin.usage.reload");
        str = str.replace("%currency%", currencyName);
        return str;
    }

    public String adminSetCurrency(String currencyName) {
        String str = getStringFromPath("admin.usage.set-currency");
        str = str.replace("%currency%", currencyName);
        return str;
    }

    public String adminResetCurrency(String currencyName) {
        String str = getStringFromPath("admin.usage.reset-currency");
        str = str.replace("%currency%", currencyName);
        return str;
    }

    public String adminGiveCurrency(String currencyName) {
        String str = getStringFromPath("admin.usage.give-currency");
        str = str.replace("%currency%", currencyName);
        return str;
    }

    public String adminTakeCurrency(String currencyName) {
        String str = getStringFromPath("admin.usage.take-currency");
        str = str.replace("%currency%", currencyName);
        return str;
    }

    public String adminUsageGeneral() {
        return getStringFromPath("admin.usage.general");
    }

    public String adminErrorNoPermission() {
        return getStringFromPath("admin.error.no-permission");
    }

    public String adminErrorOnlyIntegers() {
        return getStringFromPath("admin.error.only-integers");
    }

    public String adminErrorInvalidNumber(String input) {
        String str = getStringFromPath("admin.error.invalid-number");
        str = str.replace("%input%", input);
        return str;
    }

    public String adminErrorInvalidPlayer() {
        return getStringFromPath("admin.error.invalid-player");
    }

    public String adminErrorNoBalance(String playerName, String currencyName) {
        String str = getStringFromPath("admin.error.no-balance");
        str = str.replace("%player%", playerName);
        str = str.replace("%currency%", currencyName);
        return str;
    }

    public String adminErrorInsufficientBalance(String playerName) {
        String str = getStringFromPath("admin.error.insufficient-balance");
        str = str.replace("%player%", playerName);
        return str;
    }

    public String adminSuccessReload() {
        return getStringFromPath("admin.success.reload");
    }

    public String adminSuccessSetCurrency(String playerName, String currencyName, String balance) {
        String str = getStringFromPath("admin.success.set-currency");
        str = str.replace("%player%", playerName);
        str = str.replace("%currency%", currencyName);
        str = str.replace("%balance%", balance);
        return str;
    }

    public String adminSuccessResetCurrency(String playerName, String currencyName) {
        String str = getStringFromPath("admin.success.reset-currency");
        str = str.replace("%player%", playerName);
        str = str.replace("%currency%", currencyName);
        return str;
    }

    public String adminSuccessGiveCurrency(String playerName, String currencyName, String amount, String balance) {
        String str = getStringFromPath("admin.success.give-currency");
        str = str.replace("%player%", playerName);
        str = str.replace("%currency%", currencyName);
        str = str.replace("%amount%", amount);
        str = str.replace("%balance%", balance);
        return str;
    }

    public String adminSuccessTakeCurrency(String playerName, String currencyName, String amount, String balance) {
        String str = getStringFromPath("admin.success.take-currency");
        str = str.replace("%player%", playerName);
        str = str.replace("%currency%", currencyName);
        str = str.replace("%amount%", amount);
        str = str.replace("%balance%", balance);
        return str;
    }

    public String playerCheckOther(String currencyName) {
        String str = getStringFromPath("player.usage.check-other");
        str = str.replace("%currency%", currencyName);
        return str;
    }

    public String playerPay(String currencyName) {
        String str = getStringFromPath("player.usage.pay");
        str = str.replace("%currency%", currencyName);
        return str;
    }

    public String playerUsageGeneral() {
        return getStringFromPath("player.usage.general");
    }

    public String playerErrorNoPermission() {
        return getStringFromPath("player.error.no-permission");
    }

    public String playerErrorOnlyIntegers() {
        return getStringFromPath("player.error.only-integers");
    }

    public String playerErrorInvalidNumber(String input) {
        String str = getStringFromPath("player.error.invalid-number");
        str = str.replace("%input%", input);
        return str;
    }

    public String playerErrorInvalidPlayer(String input) {
        String str = getStringFromPath("player.error.invalid-player");
        str = str.replace("%input%", input);
        return str;
    }

    public String playerErrorInsufficientFunds(String currencyName, String amount) {
        String str = getStringFromPath("player.error.insufficient-funds");
        str = str.replace("%amount%", amount);
        str = str.replace("%currency%", currencyName);
        return str;
    }

    public String playerErrorCannotPay(String currencyName) {
        String str = getStringFromPath("player.error.cannot-pay");
        str = str.replace("%currency%", currencyName);
        return str;
    }

    public String playerErrorPayZero() {
        return getStringFromPath("player.error.pay-zero");
    }

    public String playerErrorSelfPay() {
        return getStringFromPath("player.error.self-pay");
    }

    public String playerSuccessBalance(String currencyName, String balance) {
        String str = getStringFromPath("player.success.balance");
        str = str.replace("%currency%", currencyName);
        str = str.replace("%balance%", balance);
        return str;
    }

    public String playerSuccessOtherBalance(String playerName, String currencyName, String balance) {
        String str = getStringFromPath("player.success.other-balance");
        str = str.replace("%player%", playerName);
        str = str.replace("%currency%", currencyName);
        str = str.replace("%balance%", balance);
        return str;
    }

    public String playerSuccessPay(String receiverName, String currencyName, String amount, String balance) {
        String str = getStringFromPath("player.success.pay");
        str = str.replace("%player%", receiverName);
        str = str.replace("%currency%", currencyName);
        str = str.replace("%amount%", amount);
        str = str.replace("%balance%", balance);

        return str;
    }
    public String playerSuccessPayReceive(String senderName, String currencyName, String amount, String newBalance) {
        String str = getStringFromPath("player.success.pay-receive");
        str = str.replace("%player%", senderName);
        str = str.replace("%amount%", amount);
        str = str.replace("%currency%", currencyName);
        str = str.replace("%balance%", newBalance);
        return str;
    }
}
