package com.justinmtech.customcurrencies;

import com.justinmtech.customcurrencies.commands.CurrencyCommand;
import com.justinmtech.customcurrencies.commands.CustomCommand;
import com.justinmtech.customcurrencies.configuration.ConfigManager;
import com.justinmtech.customcurrencies.configuration.Messages;
import com.justinmtech.customcurrencies.currencies.Currency;
import com.justinmtech.customcurrencies.currencies.PlayerModel;
import com.justinmtech.customcurrencies.listeners.Listeners;
import com.justinmtech.customcurrencies.persistence.FileManager;
import com.justinmtech.customcurrencies.persistence.ManageData;
import com.justinmtech.customcurrencies.persistence.DatabaseManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.List;

@Getter
public final class CustomCurrencies extends JavaPlugin {
    private ManageData data;
    private ConfigManager configManager;
    private Messages messages;
    public static CustomCurrencies instance;
    private SimplePluginManager spm;
    private static SimpleCommandMap scm;

    @Override
    public void onEnable() {
        instance = this;
        ConfigurationSerialization.registerClass(Currency.class);
        ConfigurationSerialization.registerClass(PlayerModel.class);
        configManager = new ConfigManager(this);
        messages = new Messages(this);
        if (configManager.getSqlEnabled()) {
            data = new DatabaseManager(this);
        } else {
            data = new FileManager(this);
        }
        data.initialSetup();

        for (Player player : Bukkit.getOnlinePlayers()) {
            data.loadPlayer(player);
            data.savePlayer(player);
        }

        setupSimpleCommandMap();
        registerCurrencyCommands();

        this.getServer().getPluginManager().registerEvents(new Listeners(this), this);
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            data.savePlayer(player);
        }
        instance = null;
    }

    private void registerCurrencyCommands() {
        List<Currency> currencies = configManager.getCurrencies();
        for (Currency currency : currencies) {
            CustomCommand command = new CurrencyCommand(currency.getName());
            command.setAliases(currency.getAliasNames());
            registerCommand(command);
        }
    }

    private void registerCommand(CustomCommand command) {
        scm.register("SwalbertCurrencies", command);
    }

    private void setupSimpleCommandMap() {
        spm = (SimplePluginManager) this.getServer().getPluginManager();
        Field f = null;
        try {
            f = SimplePluginManager.class.getDeclaredField("commandMap");
        } catch (Exception e) {
            e.printStackTrace();
        }
        f.setAccessible(true);
        try {
            scm = (SimpleCommandMap) f.get(spm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
