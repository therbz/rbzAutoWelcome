package me.therbz.rbzautowelcome.listeners;

import me.therbz.rbzautowelcome.core.AutoWelcome;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuit implements Listener {

    AutoWelcome main;

    public PlayerQuit(AutoWelcome instance) {
        this.main = instance;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {

        UUID playerUUID = e.getPlayer().getUniqueId();

        // Clean maps
        if (main.wbPlayers.containsKey(playerUUID)) main.wbPlayers.remove(playerUUID);
        if (main.welcomePlayers.containsKey(playerUUID)) main.welcomePlayers.remove(playerUUID);

    }

}