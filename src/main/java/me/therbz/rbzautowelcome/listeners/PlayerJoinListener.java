package me.therbz.rbzautowelcome.listeners;

import me.therbz.rbzautowelcome.AutoWelcome;
import me.therbz.rbzautowelcome.AutoWelcomeUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class PlayerJoinListener implements Listener {
    private final JavaPlugin plugin = AutoWelcome.getPlugin(AutoWelcome.class);

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player joiningPlayer = event.getPlayer();

        // read from playerdata file
        File file = new File(plugin.getDataFolder() + "/data/", joiningPlayer.getUniqueId().toString() + ".yml");
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
            FileConfiguration fileData = YamlConfiguration.loadConfiguration(file);
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

        // If joiningPlayer is returning, set playersCopy to WB
        HashMap<UUID, String> playerMessages;
        if (joiningPlayer.hasPlayedBefore()) { playerMessages = AutoWelcome.copyOfWBPlayers(); }
        else { playerMessages = AutoWelcome.copyOfWelcomePlayers(); }

        // Make players in HashMap (as long as not-null) say their wb/welcome
        AutoWelcomeUtils utils = new AutoWelcomeUtils();
        utils.welcomeLoop(playerMessages, joiningPlayer);
    }
}
