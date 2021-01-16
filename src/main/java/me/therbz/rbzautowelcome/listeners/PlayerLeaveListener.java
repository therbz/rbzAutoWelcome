package me.therbz.rbzautowelcome.listeners;

import me.therbz.rbzautowelcome.AutoWelcome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerLeaveListener implements Listener {
    private final JavaPlugin plugin = AutoWelcome.getPlugin(AutoWelcome.class);

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();

        // Save player data
        File file = new File(plugin.getDataFolder() + "/data/", playerUUID.toString() + ".yml");
        try {
            FileConfiguration fileData = YamlConfiguration.loadConfiguration(file);

            /* Do not need to perform these checks as both execute the same code regardless of null or not
            // If player has a WB enabled
            if (AutoWelcome.getPlayerWB(playerUUID)!=null) {
                fileData.createSection("wb");
                fileData.set("wb", AutoWelcome.getPlayerWB(playerUUID));
            } else {
                fileData.createSection("wb");
                fileData.set("wb", null);
            }

            // If player has a Welcome enabled
            if (AutoWelcome.getPlayerWelcome(playerUUID)!=null) {
                fileData.createSection("welcome");
                fileData.set("welcome", AutoWelcome.getPlayerWelcome(playerUUID));
            } else {
                fileData.createSection("welcome");
                fileData.set("welcome", null);
            }
            */

            String playerWBMsg=AutoWelcome.getPlayerWB(playerUUID);
            fileData.createSection("wb");
            fileData.set("wb", playerWBMsg);

            String playerWelcomeMsg=AutoWelcome.getPlayerWB(playerUUID);
            fileData.createSection("welcome");
            fileData.set("welcome", playerWelcomeMsg);

            fileData.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Remove player from HashMaps
        AutoWelcome.removePlayerWB(playerUUID);
        AutoWelcome.removePlayerWelcome(playerUUID);
    }
}
