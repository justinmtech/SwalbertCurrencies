package com.justinmtech.swalbertcurrencies;

import com.justinmtech.swalbertcurrencies.commands.CommandHandler;
import com.justinmtech.swalbertcurrencies.commands.CurrencyCommand;
import com.justinmtech.swalbertcurrencies.commands.CustomCommand;
import com.justinmtech.swalbertcurrencies.configuration.ConfigManager;
import com.justinmtech.swalbertcurrencies.configuration.Messages;
import com.justinmtech.swalbertcurrencies.core.Currency;
import com.justinmtech.swalbertcurrencies.core.PlayerModel;
import com.justinmtech.swalbertcurrencies.listeners.PlayerJoinListener;
import com.justinmtech.swalbertcurrencies.listeners.PlayerQuitListener;
import com.justinmtech.swalbertcurrencies.persistence.FlatfileDataHandler;
import com.justinmtech.swalbertcurrencies.persistence.ManageData;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public final class SwalbertCurrencies extends JavaPlugin {
    private ManageData data;
    private ConfigManager configManager;
    private Messages messages;
    private static SwalbertCurrencies instance;
    private SimplePluginManager spm;
    private static SimpleCommandMap scm;

    @Override
    public void onEnable() {
        instance = this;
        setupSimpleCommandMap();
        initialSetup();
        registerEvents();

        List<Currency> currencies = data.getCurrencies();
        for (Currency currency : currencies) {
            registerCommand(new CurrencyCommand(currency.getName(), currency.getName() + " ", "/" + currency.getName(), currency.getAliasNames()));
        }
        System.out.println("SwalbertCurrencies enabled!");
    }

    @Override
    public void onDisable() {
        savePlayers();
        System.out.println("SwalbertCurrencies disabled.");
    }

    private void registerEvents() {
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
    }

    public ManageData getData() {
        return data;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public Messages getMessages() {
        return messages;
    }

    private void initialSetup() {
        ConfigurationSerialization.registerClass(Currency.class);
        ConfigurationSerialization.registerClass(PlayerModel.class);
        configManager = new ConfigManager(this);
        data = new FlatfileDataHandler(this, configManager);
        configManager.generateConfigIfNoneExists();
        configManager.generateMessagesConfigIfNoneExists();
        messages = new Messages(this);
        data.initialSetup();

        initializePlayers();
    }

    private void initializePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            data.loadPlayer(player);
            data.savePlayer(player);
        }
    }

    private void savePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            data.savePlayer(player);
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

    public static SimpleCommandMap getCommandMap() {
        return scm;
    }

    public static SwalbertCurrencies getInstance() {
        return instance;
    }
}
