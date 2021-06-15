package me.therbz.rbzautowelcome.core;

import me.therbz.rbzautowelcome.commands.CommandRegistry;
import me.therbz.rbzautowelcome.listeners.ListenerRegistry;
import me.therbz.rbzautowelcome.metrics.MetricsLite;
import me.therbz.rbzautowelcome.utils.AWUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class AutoWelcome extends JavaPlugin implements Listener {

    AutoWelcome plugin;

    public HashMap<UUID, String> wbPlayers = new HashMap<>();
    public HashMap<UUID, String> welcomePlayers = new HashMap<>();
    public ArrayList<UUID> recentlyWbdPlayers = new ArrayList<>();

    public final int CURRENT_CONFIG_VERSION = 7; // Update this as necessary

    public void onEnable() {

        plugin = this;

        // Config work
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // Listeners
        ListenerRegistry.registerListeners(this);

        // Commands
        CommandRegistry.registerCommands(this);

        // Metrics
        MetricsLite metrics = new MetricsLite(this, 9814);

        // Check that the config is up-to-date
        if (getConfig().getInt("config-version") < CURRENT_CONFIG_VERSION) {
            getLogger().warning("Your config.yml is outdated! Delete it (or rename it) and restart your server to update it!");
        }

        getLogger().info("Enabled rbzAutoWelcome v" + getDescription().getVersion() + " by therbz");

        // Support for /reload, load any missing data on startup instead of relying solely on the join listener
        for (Player p : Bukkit.getOnlinePlayers()) {
            AWUtils.attemptLoadPlayerdataIntoMap(this, p);
        }

    }

}