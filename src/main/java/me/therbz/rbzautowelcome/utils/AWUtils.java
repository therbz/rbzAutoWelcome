package me.therbz.rbzautowelcome.utils;

import com.earth2me.essentials.Essentials;
import me.therbz.rbzautowelcome.core.AutoWelcome;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class AWUtils {

    // Send a colorized message to a CommandSender
    public static void format(AutoWelcome main, boolean appendPrefix, CommandSender sender, String message) {
        sender.sendMessage(translateAlternateColorCodes('&', appendPrefix ? main.getConfig().getString("messages.prefix") + " " + message : message));
    }

    // Returns whether a player is AFK according to Essentials, otherwise returns false if ess hook is disabled
    public static boolean isEssAfk(AutoWelcome main, Player p) {

        boolean essHookEnabled = main.getConfig().getBoolean("hooks.essentials.enabled");

        Plugin ess = Bukkit.getPluginManager().getPlugin("Essentials");

        if (ess != null && essHookEnabled) {
            return ((Essentials) ess).getUser(p).isAfk();
        }

        return false;
    }

    public static void attemptLoadPlayerdataIntoMap(AutoWelcome main, Player p) {

        File userFile = new File(main.getDataFolder() + "/data/", p.getUniqueId().toString() + ".yml");

        // Only load if the file exists, don't create a new file for every player
        if (!userFile.exists()) return;

        FileConfiguration userFileYAML = YamlConfiguration.loadConfiguration(userFile);

        // Load values into maps if present
        if (userFileYAML.contains("wb")) main.wbPlayers.put(p.getUniqueId(), userFileYAML.getString("wb"));
        if (userFileYAML.contains("welcome")) main.welcomePlayers.put(p.getUniqueId(), userFileYAML.getString("welcome"));

    }

}