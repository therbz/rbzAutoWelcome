package me.therbz.rbzautowelcome;

import me.therbz.rbzautowelcome.listeners.PlayerJoinListener;
import me.therbz.rbzautowelcome.listeners.PlayerLeaveListener;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.UUID;
import java.util.HashMap;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class AutoWelcome extends JavaPlugin implements Listener {
    private static HashMap<UUID, String> wbPlayers = new HashMap<>();
    private static HashMap<UUID, String> welcomePlayers = new HashMap<>();

    @Override
    public void onEnable() {
        // Get configuration, and save the default if there is no file
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // Register listeners
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerLeaveListener(), this);

        // Register commands
        Objects.requireNonNull(getCommand("autowb")).setExecutor(new AutoWelcomeCommand());

        // Set up bStats metrics
        final int BSTATS_PLUGIN_ID = 9814;
        MetricsLite metrics = new MetricsLite(this, BSTATS_PLUGIN_ID);

        // Check that the config is up-to-date
        final int CURRENT_CONFIG_VERSION = 2; // Update this as necessary
        int config_version = getConfig().getInt("config-version");

        if(config_version < CURRENT_CONFIG_VERSION) {
            getLogger().warning("Your config.yml is outdated! Delete it (or rename it) and restart your server to update it!");
        }

        // If all is good so far, send an enabled message
        getLogger().info("Enabled rbzAutoWelcome v" + getDescription().getVersion() + " by therbz");
    }

    public static void messageSender(CommandSender sender, String message) {
        sender.sendMessage(translateAlternateColorCodes('&', message));
    }

    // WB access
    /* DEPRECATED - use getPlayerWB!=null
    public static boolean playerHasSetWB(UUID uuid) { return wbPlayers.containsKey(uuid); }*/
    public static String getPlayerWB(UUID uuid) { return wbPlayers.get(uuid); }
    public static void setPlayerWB(UUID uuid, String string) {
        wbPlayers.put(uuid, string);
    }
    public static void removePlayerWB(UUID uuid) {
        wbPlayers.remove(uuid);
    }
    public static HashMap<UUID, String> copyOfWBPlayers() {
        return wbPlayers;
    }

    // Welcome access
    /* DEPRECATED - use getPlayerWelcome!=null
    public static boolean playerHasSetWelcome(UUID uuid) {
        String msg = welcomePlayers.get(uuid);
        if (!msg.equals("")) { return welcomePlayers.containsKey(uuid); }
        else { return false; }
    }*/
    public static String getPlayerWelcome(UUID uuid) { return welcomePlayers.get(uuid); }
    public static void setPlayerWelcome(UUID uuid, String string) {
        welcomePlayers.put(uuid, string);
    }
    public static void removePlayerWelcome(UUID uuid) {
        welcomePlayers.remove(uuid);
    }
    public static HashMap<UUID, String> copyOfWelcomePlayers() {
        return welcomePlayers;
    }
}