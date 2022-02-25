package com.justinmtech.customcurrencies.persistence;

import com.justinmtech.customcurrencies.currencies.Currency;
import com.justinmtech.customcurrencies.currencies.PlayerModel;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.List;

public interface ManageData {

    void initialSetup();
    BigDecimal getBalance(OfflinePlayer player, Currency currency);
    boolean setBalance(OfflinePlayer player, Currency currency, BigDecimal amount);
    boolean payBalance(OfflinePlayer player1, OfflinePlayer player2, Currency currency, BigDecimal amount);
    boolean takeBalance(OfflinePlayer player, Currency currency, BigDecimal amount);
    boolean giveBalance(OfflinePlayer player, Currency currency, BigDecimal amount);
    boolean resetBalance(OfflinePlayer player, Currency currency);

    boolean savePlayer(Player player);
    boolean saveOfflinePlayer(OfflinePlayer player, PlayerModel playerModel);
    boolean savePlayers(List<Player> players);
    boolean loadPlayer(Player player);

}
