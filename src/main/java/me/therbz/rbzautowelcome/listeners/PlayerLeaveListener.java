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
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Save player data
        File file = new File(plugin.getDataFolder() + "/data/", player.getUniqueId().toString() + ".yml");
        try {
            FileConfiguration fileData = YamlConfiguration.loadConfiguration(file);
            if (AutoWelcome.playerHasSetWB(playerUUID)) {
                if (!AutoWelcome.getPlayerWB(playerUUID).equals("off")) {
                    fileData.createSection("wb");
                    fileData.set("wb", AutoWelcome.getPlayerWB(playerUUID));
                }
                else {
                    fileData.createSection("wb");
                    fileData.set("wb", null);
                }
            }
            if (AutoWelcome.playerHasSetWelcome(playerUUID)) {
                if (!AutoWelcome.getPlayerWB(playerUUID).equals("off")) {
                    fileData.createSection("welcome");
                    fileData.set("welcome", AutoWelcome.getPlayerWelcome(playerUUID));
                } else {
                    fileData.createSection("wb");
                    fileData.set("wb", null);
                }
            }
            fileData.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Clean up HashMaps
        AutoWelcome.removePlayerWB(playerUUID);
        AutoWelcome.removePlayerWelcome(playerUUID);
    }
}
