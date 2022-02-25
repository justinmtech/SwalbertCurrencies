package com.justinmtech.customcurrencies.persistence;

import com.justinmtech.customcurrencies.CustomCurrencies;
import com.justinmtech.customcurrencies.currencies.Currency;
import com.justinmtech.customcurrencies.currencies.PlayerModel;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileManager implements ManageData {
    private final CustomCurrencies plugin;
    private final Map<String, PlayerModel> data;

    public FileManager(CustomCurrencies plugin) {
        this.plugin = plugin;
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

    private FileConfiguration getFileConfiguration(OfflinePlayer player) {
        File file = new File("plugins//SwalbertCurrencies//data//" + player.getUniqueId().toString() + ".yml");
        return YamlConfiguration.loadConfiguration(file);
    }

    private File getPlayerDataFile(Player player) {
        return new File("plugins//SwalbertCurrencies//data//" + player.getUniqueId().toString() + ".yml");
    }

    private File getPlayerDataFile(OfflinePlayer player) {
        return new File("plugins//SwalbertCurrencies//data//" + player.getUniqueId().toString() + ".yml");
    }

    private boolean updateOfflinePlayer(OfflinePlayer player, Currency currency, String type, BigDecimal amount) {
        BigDecimal prevBalance = plugin.getData().getBalance(player, currency);
        BigDecimal newBalance;
        switch (type) {
            case "give":
                newBalance = prevBalance.add(amount);
                modifyDataFile(player, currency, newBalance);
                return true;
            case "take":
                newBalance = prevBalance.subtract(amount);
                modifyDataFile(player, currency, newBalance);
                return true;
            case "set":
                modifyDataFile(player, currency, amount);
                return true;
            case "reset":
                modifyDataFile(player, currency, BigDecimal.ZERO);
                return true;
        }
        return false;
    }

    private boolean modifyDataFile(OfflinePlayer player, Currency currency, BigDecimal amount) {
        PlayerModel pm = (PlayerModel) getFileConfiguration(player).get("data");
        pm.setUuid(player.getUniqueId().toString());
        pm.getCurrencies().replace(currency.getName(), amount);
        plugin.getData().saveOfflinePlayer(player, pm);
        return true;
    }

    private boolean updateOnlinePlayer(Player player, Currency currency, String type, BigDecimal amount) {
        BigDecimal prevBalance = plugin.getData().getBalance(player, currency);
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
    public BigDecimal getBalance(OfflinePlayer player, Currency currency) {
        if (!player.isOnline()) {
            FileConfiguration playerFile = getFileConfiguration(player);
            PlayerModel pm = (PlayerModel) playerFile.get("data");
            return pm.getCurrencies().get(currency.getName());
        } else {
            return data.get(player.getUniqueId().toString()).getCurrencies().get(currency.getName());
        }
    }

    @Override
    public boolean setBalance(OfflinePlayer player, Currency currency, BigDecimal amount) {
        if (!player.isOnline()) {
            return updateOfflinePlayer(player, currency, "set", amount);
        } else {
            updateOnlinePlayer(player.getPlayer(), currency, "set", amount);
            player.getPlayer().sendMessage(plugin.getMessages().adminSuccessSetCurrency(player.getName(), currency.getName(), amount.toString()));
            return true;
        }
    }

    @Override
    public boolean payBalance(OfflinePlayer sender, OfflinePlayer receiver, Currency currency, BigDecimal amount) {
        boolean transactionA;
        boolean transactionB;
        BigDecimal senderNewBalance;
        BigDecimal receiverNewBalance;

        transactionA = updateOnlinePlayer(sender.getPlayer(), currency, "take", amount);
        senderNewBalance = plugin.getData().getBalance(sender, currency);

        if (!receiver.isOnline()) {
            transactionB = updateOfflinePlayer(receiver, currency, "give", amount);
        } else {
            transactionB = updateOnlinePlayer(receiver.getPlayer(), currency, "give", amount);
        }
        receiverNewBalance = plugin.getData().getBalance(receiver, currency);
        if (transactionA && transactionB) {
            if (currency.isAllowDecimals()) {
                sender.getPlayer().sendMessage(plugin.getMessages().playerSuccessPay(receiver.getName(), currency.getName(), amount.toString(), senderNewBalance.toString()));
                if (receiver.isOnline()) {
                receiver.getPlayer().sendMessage(plugin.getMessages().playerSuccessPayReceive(sender.getName(), currency.getName(), amount.toString(), receiverNewBalance.toString()));
                }
            } else {
                sender.getPlayer().sendMessage(plugin.getMessages().playerSuccessPay(receiver.getName(), currency.getName(), amount.toBigInteger().toString(), senderNewBalance.toBigInteger().toString()));
                if (receiver.isOnline()) {
                receiver.getPlayer().sendMessage(plugin.getMessages().playerSuccessPayReceive(sender.getName(), currency.getName(), amount.toBigInteger().toString(), receiverNewBalance.toBigInteger().toString()));
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean takeBalance(OfflinePlayer player, Currency currency, BigDecimal amount) {
        if (!player.isOnline()) {
            return updateOfflinePlayer(player, currency, "take", amount);
        } else {
            return updateOnlinePlayer(player.getPlayer(), currency, "take", amount);
        }
    }

    @Override
    public boolean giveBalance(OfflinePlayer player, Currency currency, BigDecimal amount) {
        if (!player.isOnline()) {
            return updateOfflinePlayer(player, currency, "give", amount);
        } else {
            return updateOnlinePlayer(player.getPlayer(), currency, "give", amount);
        }
    }

    @Override
    public boolean resetBalance(OfflinePlayer player, Currency currency) {
        if (!player.isOnline()) {
            return updateOfflinePlayer(player, currency, "reset", null);
        } else {
            return updateOnlinePlayer(player.getPlayer(), currency, "reset", BigDecimal.ZERO);
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
    public boolean saveOfflinePlayer(OfflinePlayer player, PlayerModel playerModel) {
        FileConfiguration playerFile = getFileConfiguration(player);
        playerFile.set("data", playerModel);
        try {
            playerFile.save(getPlayerDataFile(player));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
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
        List<Currency> currencies = plugin.getConfigManager().getCurrencies();
        for (Currency currency : currencies) {
            pm.getCurrencies().put(currency.getName(), BigDecimal.valueOf(100));
        }
        return pm;
    }
}
