package com.justinmtech.swalbertcurrencies.persistence;

import com.justinmtech.swalbertcurrencies.SwalbertCurrencies;
import com.justinmtech.swalbertcurrencies.configuration.ConfigManager;
import com.justinmtech.swalbertcurrencies.core.Currency;
import com.justinmtech.swalbertcurrencies.core.PlayerModel;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

public class FlatfileDataHandler implements ManageData {
    private SwalbertCurrencies plugin;
    private ConfigManager configManager;
    private Map<String, PlayerModel> data;

    public FlatfileDataHandler(SwalbertCurrencies plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        data = new HashMap<>();
    }

    private boolean createNewPluginFolder() {
        try {
            File file = new File("plugins//SwalbertCurrencies");
            if (!file.exists()) {
                if (file.mkdir()) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("SwalbertCurrencies Error: Failed to create data folder");
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private boolean createNewDataFolder() {
        try {
            File file = new File("plugins//SwalbertCurrencies//data");
            if (!file.exists()) {
                if (file.mkdir()) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("SwalbertCurrencies Error: Failed to create data folder");
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private boolean createNewDataFile(Player player) {
        try {
            File file = new File("plugins//SwalbertCurrencies//data//" + player.getUniqueId());
            if (!file.exists()) {
                return file.createNewFile();
            }
        } catch (Exception e) {
            System.out.println("SwalbertCurrencies Error: File creation failed for player " + player.getName());
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private FileConfiguration getFileConfiguration(Player player) {
        File file = new File("plugins//SwalbertCurrencies//data//" + player.getUniqueId().toString() + ".yml");
        return YamlConfiguration.loadConfiguration(file);
    }

    private File getPlayerDataFile(Player player) {
        return new File("plugins//SwalbertCurrencies//data//" + player.getUniqueId().toString() + ".yml");
    }

    private boolean updateOfflinePlayer(Player player, String currencyName, String type, BigDecimal amount) {
        BigDecimal prevBalance = (BigDecimal) getFileConfiguration(player).get("currencies." + currencyName + ".amount");;
        BigDecimal newBalance;
        switch (type) {
            case "give":
                newBalance = prevBalance.add(amount);
                getFileConfiguration(player).set("currencies." + currencyName + ".amount", newBalance);
                return true;
            case "take":
                newBalance = prevBalance.subtract(amount);
                getFileConfiguration(player).set("currencies." + currencyName + ".amount", newBalance);
                return true;
            case "set":
                getFileConfiguration(player).set("currencies." + currencyName + ".amount", amount);
                return true;
            case "reset":
                getFileConfiguration(player).set("currencies." + currencyName + ".amount", BigDecimal.ZERO);
                return true;
        }
        return false;
    }

    private boolean updateOnlinePlayer(Player player, Currency currency, String type, BigDecimal amount) {
        BigDecimal prevBalance = plugin.getData().getBalance(player, currency, false);
        BigDecimal newBalance;
        switch (type) {
            case "give":
                newBalance = prevBalance.add(amount);
                data.get(player.getUniqueId().toString()).getCurrencies().replace(currency.getName(), newBalance);
                return true;
            case "take":
                newBalance = prevBalance.subtract(amount);
                data.get(player.getUniqueId().toString()).getCurrencies().replace(currency.getName(), newBalance);
                return true;
            case "set":
                data.get(player.getUniqueId().toString()).getCurrencies().replace(currency.getName(), amount);
                return true;
            case "reset":
                data.get(player.getUniqueId().toString()).getCurrencies().replace(currency.getName(), BigDecimal.ZERO);
                return true;
        }
        return false;
    }

    @Override
    public void initialSetup() {
        createNewPluginFolder();
        createNewDataFolder();
    }

    @Override
    public BigDecimal getBalance(Player player, Currency currency, boolean offline) {
        if (offline) {
        return (BigDecimal) getFileConfiguration(player).get("currencies." + currency + ".amount");
        } else {
            return data.get(player.getUniqueId().toString()).getCurrencies().get(currency.getName());
        }
    }

    @Override
    public List<Currency> getCurrencies() {
        List<Currency> currencies = new ArrayList<>();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("currencies");
        Set<String> currencyNames = plugin.getConfig().getConfigurationSection("currencies").getKeys(false);

        for (String currency : currencyNames) {
            ConfigurationSection currencySection = plugin.getConfig().getConfigurationSection("currencies." + currency);
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

    @Override
    public boolean setBalance(Player player, Currency currency, BigDecimal amount, boolean offline) {
        if (offline) {
            return updateOfflinePlayer(player, currency.getName(), "set", amount);
        } else {
            updateOnlinePlayer(player, currency, "set", amount);
            player.sendMessage(plugin.getMessages().adminSuccessSetCurrency(player.getName(), currency.getName(), amount.toString()));
            return true;
        }
    }

    @Override
    public boolean payBalance(Player sender, Player receiver, Currency currency, BigDecimal amount, boolean receiverOffline) {
        boolean transactionA;
        boolean transactionB;
        BigDecimal senderNewBalance;
        BigDecimal receiverNewBalance;

        transactionA = updateOnlinePlayer(sender, currency, "take", amount);
        senderNewBalance = plugin.getData().getBalance(sender, currency, false);

        if (receiverOffline) {
            transactionB = updateOfflinePlayer(receiver, currency.getName(), "give", amount);
        } else {
            transactionB = updateOnlinePlayer(receiver, currency, "give", amount);
        }
        receiverNewBalance = plugin.getData().getBalance(receiver, currency, false);
        if (transactionA && transactionB) {
            sender.sendMessage(plugin.getMessages().playerSuccessPay(receiver.getName(), currency.getName(), amount.toString(), senderNewBalance.toString()));
            receiver.sendMessage(plugin.getMessages().playerSuccessPayReceive(sender.getName(), currency.getName(), amount.toString(), receiverNewBalance.toString()));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean takeBalance(Player player, Currency currency, BigDecimal amount, boolean offline) {
        if (offline) {
            return updateOfflinePlayer(player, currency.getName(), "take", amount);
        } else {
            return updateOnlinePlayer(player, currency, "take", amount);
        }
    }

    @Override
    public boolean giveBalance(Player player, Currency currency, BigDecimal amount, boolean offline) {
        if (offline) {
            return updateOfflinePlayer(player, currency.getName(), "give", amount);
        } else {
            return updateOnlinePlayer(player, currency, "give", amount);
        }
    }

    @Override
    public boolean resetBalance(Player player, Currency currency, boolean offline) {
        if (offline) {
            return updateOfflinePlayer(player, currency.getName(), "reset", null);
        } else {
            return updateOnlinePlayer(player, currency, "reset", BigDecimal.ZERO);
        }
    }

    @Override
    public boolean savePlayer(Player player) {
        if (getPlayerDataFile(player).exists()) {
            try {
                PlayerModel playerModel = data.get(player.getUniqueId().toString());
                FileConfiguration playerFile = getFileConfiguration(player);
                playerFile.set("data", playerModel);
                playerFile.save(getPlayerDataFile(player));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            try {
                getPlayerDataFile(player).createNewFile();
                FileConfiguration playerFile = getFileConfiguration(player);
                playerFile.set("data", data.get(player.getUniqueId().toString()));
                playerFile.save(getPlayerDataFile(player));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }
    @Override
    public boolean savePlayers(List<Player> players) {
        try {
            for (Player player : players) {
                savePlayer(player);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean loadPlayer(Player player) {
        if (getPlayerDataFile(player).exists()) {
            try {
                FileConfiguration playerFile = getFileConfiguration(player);
                PlayerModel pm = (PlayerModel) playerFile.get("data");
                data.put(player.getUniqueId().toString(), pm);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            PlayerModel pm = buildNewPlayerModel(player);
            data.put(player.getUniqueId().toString(), pm);
            return true;
        }
    }

    private PlayerModel buildNewPlayerModel(Player player) {
        PlayerModel pm = new PlayerModel(player.getUniqueId().toString());
        List<Currency> currencies = plugin.getData().getCurrencies();
        for (Currency currency : currencies) {
            pm.getCurrencies().put(currency.getName(), BigDecimal.valueOf(100));
        }
        return pm;
    }
}
