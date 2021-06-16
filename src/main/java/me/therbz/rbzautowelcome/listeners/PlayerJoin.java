package me.therbz.rbzautowelcome.listeners;

import me.therbz.rbzautowelcome.core.AutoWelcome;
import me.therbz.rbzautowelcome.core.WelcomeLoop;
import me.therbz.rbzautowelcome.utils.AWUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;

public class PlayerJoin implements Listener {

    AutoWelcome main;

    public PlayerJoin(AutoWelcome instance) {
        this.main = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();

        AWUtils.attemptLoadPlayerdataIntoMap(main, p);

        // Make players in HashMap say their wb/welcome
        WelcomeLoop.run(main, p);
    }

}