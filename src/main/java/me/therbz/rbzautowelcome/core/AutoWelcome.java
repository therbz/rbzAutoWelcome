package me.therbz.rbzautowelcome.core;

import me.therbz.rbzautowelcome.commands.AutoWelcomeCommand;
import me.therbz.rbzautowelcome.listeners.AfkStatusChange;
import me.therbz.rbzautowelcome.listeners.PlayerJoin;
import me.therbz.rbzautowelcome.listeners.PlayerQuit;
import me.therbz.rbzautowelcome.metrics.MetricsLite;
import me.therbz.rbzautowelcome.utils.AWUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class AutoWelcome extends JavaPlugin implements Listener {

    public HashMap<UUID, String> wbPlayers = new HashMap<>();
    public HashMap<UUID, String> welcomePlayers = new HashMap<>();
    public ArrayList<UUID> recentlyWbdPlayers = new ArrayList<>();

    public final int CURRENT_CONFIG_VERSION = 9; // Update this as necessary

    public void onEnable() {

        // Config work
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // Listeners
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoin(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerQuit(this), this);
        if (Bukkit.getPluginManager().getPlugin("Essentials") != null) Bukkit.getServer().getPluginManager().registerEvents(new AfkStatusChange(this), this);

        // Commands
        Objects.requireNonNull(this.getCommand("autowb")).setExecutor(new AutoWelcomeCommand(this));

        // Metrics
        MetricsLite metrics = new MetricsLite(this, 9814);

        // Check that the config is up-to-date
        if (getConfig().getInt("config-version") < CURRENT_CONFIG_VERSION) {
            getLogger().warning("Your config.yml is outdated! Delete it (or rename it) and restart your server to update it!");
        }

        // Support for /reload, load any missing data on startup instead of relying solely on the join listener
        for (Player p : Bukkit.getOnlinePlayers()) {
            AWUtils.attemptLoadPlayerdataIntoMap(this, p);
        }

    }

}