package com.justinmtech.swalbertcurrencies.listeners;

import com.justinmtech.swalbertcurrencies.SwalbertCurrencies;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private SwalbertCurrencies plugin;

    public PlayerQuitListener(SwalbertCurrencies plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        plugin.getData().savePlayer(getPlayer(e));
    }

    private Player getPlayer(PlayerQuitEvent e) {
        return e.getPlayer();
    }

    private void savePlayer(Player player) {
        plugin.getData().savePlayer(player);
    }
}
