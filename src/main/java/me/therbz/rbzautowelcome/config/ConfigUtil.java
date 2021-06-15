package me.therbz.rbzautowelcome.config;

import me.therbz.rbzautowelcome.core.AutoWelcome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ConfigUtil {

    // Save player data if necessary
    public static void attemptSavePlayerdata(AutoWelcome main, Player p) {

        UUID playerUUID = p.getUniqueId();

        if (main.wbPlayers.containsKey(playerUUID) || main.welcomePlayers.containsKey(playerUUID)) {

            File userFile = new File(main.getDataFolder() + "/data/", playerUUID.toString() + ".yml");

            try {

                FileConfiguration userFileYAML = YamlConfiguration.loadConfiguration(userFile);

                if(main.wbPlayers.containsKey(playerUUID)) userFileYAML.set("wb", main.wbPlayers.get(playerUUID));
                if(main.welcomePlayers.containsKey(playerUUID)) userFileYAML.set("welcome", main.welcomePlayers.get(playerUUID));

                userFileYAML.save(userFile);

            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Clean maps if null
            if(main.wbPlayers.containsKey(playerUUID) && main.wbPlayers.get(playerUUID) == null) main.wbPlayers.remove(playerUUID);
            if(main.welcomePlayers.containsKey(playerUUID) && main.welcomePlayers.get(playerUUID) == null) main.welcomePlayers.remove(playerUUID);

        }

    }

}