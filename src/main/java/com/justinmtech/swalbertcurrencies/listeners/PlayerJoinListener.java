package com.justinmtech.swalbertcurrencies.listeners;

import com.justinmtech.swalbertcurrencies.SwalbertCurrencies;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private SwalbertCurrencies plugin;

    public PlayerJoinListener(SwalbertCurrencies plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        plugin.getData().loadPlayer(e.getPlayer());
    }
}

