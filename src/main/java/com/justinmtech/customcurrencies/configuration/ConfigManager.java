package com.justinmtech.customcurrencies.configuration;

import com.justinmtech.customcurrencies.CustomCurrencies;
import com.justinmtech.customcurrencies.currencies.Currency;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigManager {
    private CustomCurrencies plugin;

    public ConfigManager(CustomCurrencies plugin) {
        this.plugin = plugin;
        generatePluginFolderIfNoneExists();
        generateConfigIfNoneExists();
        generateMessagesConfigIfNoneExists();
    }

    public CustomCurrencies getPlugin() {
        return plugin;
    }

    public void setPlugin(CustomCurrencies plugin) {
        this.plugin = plugin;
    }

    public boolean generatePluginFolderIfNoneExists() {
        if (!getConfigFolder().exists()) {
                return getConfigFolder().mkdir();
        }
        return false;
    }

    public boolean generateConfigIfNoneExists() {
        if (!getConfigFile().exists()) {
            try {
                getConfigFile().createNewFile();
                    setDefaultConfig();
                    return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean generateMessagesConfigIfNoneExists() {
        if (!getMessagesConfigFile().exists()) {
            try {
                getMessagesConfigFile().createNewFile();
                setDefaultMessagesConfig();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private void setDefaultConfig() {
        FileConfiguration config = getConfig();
        config.set("sql.enabled", false);
        config.set("sql.host", "localhost");
        config.set("sql.port", 3306);
        config.set("sql.username", "user");
        config.set("sql.password", "password");
        config.set("sql.database", "dbName");
        config.set("sql.table", "table");
        List<String> aliases = new ArrayList<>(Arrays.asList("credit", "c"));
        List<String> currencies = new ArrayList<>(Arrays.asList("credit"));
        config.set("currencies", currencies);
        config.set("currencies.credit.alias-names", aliases);
        config.set("currencies.credit.local-storage", true);
        config.set("currencies.credit.language.singular", "credit has");
        config.set("currencies.credit.language.plural", "credits have");
        config.set("currencies.credit.allow-decimals", true);
        config.set("currencies.credit.allow-pay", true);
        try {
            config.save(getConfigFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    public FileConfiguration getMessagesConfig() {
        return YamlConfiguration.loadConfiguration(getMessagesConfigFile());
    }

    private void setDefaultMessagesConfig() {
        FileConfiguration messages = getMessagesConfig();
        messages.set("admin.usage.reload", "&8[&c&l!&8] &eUsage: &7/%currency% reload");
        messages.set("admin.usage.set-currency", "&8[&c&l!&8] &eUsage: &7/%currency% set <player> <amount>");
        messages.set("admin.usage.reset-currency", "&8[&c&l!&8] &eUsage: &7/%currency% reset <player>");
        messages.set("admin.usage.give-currency", "&8[&c&l!&8] &eUsage: &7/%currency% give <player> <amount>");
        messages.set("admin.usage.take-currency", "&8[&c&l!&8] &eUsage: &7/%currency% take <player> <amount>");
        messages.set("admin.error.no-permission", "&8[&c&l!&8] &7You do not have permission to do that.");
        messages.set("admin.error.only-integers", "&8[&c&l!&8] &You can only use integers for this currency.");
        messages.set("admin.error.invalid-number", "&8[&c&l!&8] &e%input% &7is not a valid number.");
        messages.set("admin.error.invalid-player", "&8[&c&l!&8] &e%input% &7has never joined before.");
        messages.set("admin.error.no-balance", "&8[&c&l!&8] &e%player%'s &7%currency% balance is currently zero.");
        messages.set("admin.error.insufficient-balance", "&8[&c&l!&8] &e%player% does not have that much in their balance!");
        messages.set("admin.success.reload", "&8[&e&l!&8] &7Configuration files reloaded.");
        messages.set("admin.success.set-currency", "&8[&e&l!&8] &e%player%'s &7%currency% balance is now &e%balance%&7.");
        messages.set("admin.success.reset-currency", "&8[&e&l!&8] &e%player%'s &7%currency% balance has been reset.");
        messages.set("admin.success.give-currency", "&8[&e&l!&8] &e%amount% &7%currency% been given to &e%player%&7. Their balance is now &e%balance%&7.");
        messages.set("admin.success.take-currency", "&8[&e&l!&8] &e%amount% &7%currency% been taken from &e%player%&7. Their balance is now &e%balance%&7.");

        messages.set("player.usage.check-other", "&8[&c&l!&8] &eUsage: &7/%currency% <player>");
        messages.set("player.usage.general", "&8[&c&l!&8] &eUsage: &7/%currency% <player, pay> <amount>");
        messages.set("player.usage.pay", "&8[&c&l!&8] &eUsage: &7/%currency% pay <player> <amount>");
        messages.set("player.error.no-permission", "&8[&c&l!&8] &7You do not have permission to do that.");
        messages.set("player.error.only-integers", "&8[&c&l!&8] &7You can only use integers for this currency.");
        messages.set("player.error.invalid-number", "&8[&c&l!&8] &e%input% &7is not a valid number.");
        messages.set("player.error.invalid-player", "&8[&c&l!&8] &e%input% &7has never joined before.");
        messages.set("player.error.insufficient-funds", "&8[&c&l!&8] &7You do not have &e%amount% &7%currency%.");
        messages.set("player.error.cannot-pay", "&8[&c&l!&8] &7You cannot pay other players %currency%.");
        messages.set("player.error.self-pay", "&8[&c&l!&8] &7You cannot pay yourself!");
        messages.set("player.error.pay-zero", "&8[&c&l!&8] &7You must enter a value greater than zero!");
        messages.set("player.success.balance", "&8[&e&l!&8] &7You have &e%balance% &7%currency%.");
        messages.set("player.success.other-balance", "&8[&e&l!&8] &e%player% &7has &e%balance% &7%currency%.");
        messages.set("player.success.pay", "&8[&e&l!&8] &7You have paid &e%player% &7%amount% %currency%. New balance: %balance% %currency%");
        messages.set("player.success.pay-receive", "&8[&e&l!&8] &7You have received &7%amount% %currency% &7from %player%. New balance: %balance% %currency%");
        try {
            messages.save(getMessagesConfigFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getMessagesConfigFile() {
        return new File("plugins//SwalbertCurrencies//messages.yml");
    }

    public File getConfigFile() {
        return new File("plugins//SwalbertCurrencies//config.yml");
    }

    public File getConfigFolder() {
        return new File("plugins//SwalbertCurrencies");
    }

    public File getPlayerDataFile(Player player) {
        return new File("plugins//SwalbertCurrencies//data//" + player.getUniqueId().toString() + ".yml");
    }
    public boolean getSqlEnabled() {return getConfig().getBoolean("sql.enabled");}

    public String getSqlHost() {
        return getConfig().getString("sql.host");
    }

    public int getSqlPort() {
        return getConfig().getInt("sql.port");
    }

    public String getSqlTable() {return getConfig().getString("sql.table");}

    public String getSqlUsername() {
        return getConfig().getString("sql.username");
    }

    public String getSqlPassword() {
        return getConfig().getString("sql.password");
    }

    public String getSqlDatabase() {
        return getConfig().getString("sql.database");
    }

    public List<Currency> getCurrencies() {
        List<Currency> currencies = new ArrayList<>();
        ConfigurationSection section = getConfig().getConfigurationSection("currencies");
        Set<String> currencyNames = getConfig().getConfigurationSection("currencies").getKeys(false);

        for (String currency : currencyNames) {
            ConfigurationSection currencySection = getConfig().getConfigurationSection("currencies." + currency);
            Set<String> keys = currencySection.getKeys(true);
            HashMap<String, Object> map = new HashMap<>();
            for (String key : keys) {
                Object object = currencySection.get(key);
                map.put(key, object);
            }
            Currency currencyObject = new Currency(currency, map);
            currencies.add(currencyObject);
        }
        return currencies;
    }
}
