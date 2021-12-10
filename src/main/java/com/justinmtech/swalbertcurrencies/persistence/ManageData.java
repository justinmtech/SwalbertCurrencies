package com.justinmtech.swalbertcurrencies.persistence;

import com.justinmtech.swalbertcurrencies.core.Currency;
import com.justinmtech.swalbertcurrencies.core.PlayerModel;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.List;

public interface ManageData {

    void initialSetup();
    BigDecimal getBalance(Player player, Currency currency, boolean offline);
    List<Currency> getCurrencies();
    boolean setBalance(Player player, Currency currency, BigDecimal amount, boolean offline);
    boolean payBalance(Player player1, Player player2, Currency currency, BigDecimal amount, boolean offline);
    boolean takeBalance(Player player, Currency currency, BigDecimal amount, boolean offline);
    boolean giveBalance(Player player, Currency currency, BigDecimal amount, boolean offline);
    boolean resetBalance(Player player, Currency currency, boolean offline);

    boolean savePlayer(Player player);
    boolean savePlayers(List<Player> players);
    boolean loadPlayer(Player player);

}
