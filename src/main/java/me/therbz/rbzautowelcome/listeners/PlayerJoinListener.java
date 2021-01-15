package me.therbz.rbzautowelcome.listeners;

import me.therbz.rbzautowelcome.AutoWelcome;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import static org.bukkit.Bukkit.getPlayer;

public class PlayerJoinListener implements Listener {
    private JavaPlugin plugin = AutoWelcome.getPlugin(AutoWelcome.class);
    private File file;
    private FileConfiguration fileData;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player joiningPlayer = event.getPlayer();

        // read from playerdata file
        file = new File(plugin.getDataFolder() + "/data/", joiningPlayer.getUniqueId().toString() + ".yml");
        /*if (!file.exists()) {
            try {
                fileData = YamlConfiguration.loadConfiguration(file);
                fileData.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        // Only load if the file exists, don't create a new file for every player
        if (file.exists()) {
            fileData = YamlConfiguration.loadConfiguration(file);
            if (fileData.contains("wb")) {
                String wbMessage = fileData.getString("wb");
                AutoWelcome.setPlayerWB(joiningPlayer.getUniqueId(), wbMessage);
                //wbPlayers.put(joiningPlayer.getUniqueId(), wbMessage);
            }
            if (fileData.contains("welcome")) {
                String welcomeMessage = fileData.getString("welcome");
                AutoWelcome.setPlayerWelcome(joiningPlayer.getUniqueId(), welcomeMessage);
                //welcomePlayers.put(joiningPlayer.getUniqueId(), welcomeMessage);
            }
        }

        // Make players in HashMap say their wb/welcome
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                // wb
                if (joiningPlayer.hasPlayedBefore()) {
                    HashMap<UUID, String> wbPlayersCopy = AutoWelcome.copyOfWBPlayers();
                    wbPlayersCopy.entrySet().forEach(mapElement -> {
                        UUID sendingPlayerUuid = mapElement.getKey();
                        String msg = mapElement.getValue();
                        Player sendingPlayer = getPlayer(sendingPlayerUuid);
                        // && !msg.equals("off")
                        if (joiningPlayer!=sendingPlayer && sendingPlayer.hasPermission("rbzaw.set")) { sendingPlayer.chat(msg); }
                    });
                } else {
                    // welcome
                    HashMap<UUID, String> welcomePlayersCopy = AutoWelcome.copyOfWelcomePlayers();
                    welcomePlayersCopy.entrySet().forEach(mapElement -> {
                        UUID sendingPlayerUuid = mapElement.getKey();
                        String msg = mapElement.getValue();
                        Player sendingPlayer = getPlayer(sendingPlayerUuid);
                        if (joiningPlayer!=sendingPlayer && sendingPlayer.hasPermission("rbzaw.set")) { sendingPlayer.chat(msg); }
                    });
                }
            }
        }, plugin.getConfig().getLong("message-delay"));
    }
}
