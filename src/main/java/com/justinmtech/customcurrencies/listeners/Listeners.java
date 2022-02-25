package com.justinmtech.customcurrencies.listeners;

import com.justinmtech.customcurrencies.CustomCurrencies;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public class Listeners implements Listener {
    private final CustomCurrencies plugin;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        plugin.getData().loadPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        plugin.getData().savePlayer(getPlayer(e));
    }

    private Player getPlayer(PlayerQuitEvent e) {
        return e.getPlayer();
    }
}
