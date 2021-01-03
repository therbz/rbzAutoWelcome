package me.therbz.rbzautowelcome;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.bukkit.Bukkit.*;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class Main extends JavaPlugin implements Listener {
    public HashMap<UUID, String> autowbPlayers = new HashMap<>();
    public HashMap<UUID, String> autowelcomePlayers = new HashMap<>();
    private File file;
    private FileConfiguration fileData;

    @Override
    public void onEnable() {
        // Get configuration, and save the default if there is no file
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // Register PlayerJoin listener
        Bukkit.getServer().getPluginManager().registerEvents(this, this);

        // Set up bStats metrics
        final int BSTATS_PLUGIN_ID = 9814;
        MetricsLite metrics = new MetricsLite(this, BSTATS_PLUGIN_ID);

        // Check that the config is up-to-date
        final int CURRENT_CONFIG_VERSION = 1; // Update this as necessary
        int config_version = getConfig().getInt("config-version");

        if(config_version < CURRENT_CONFIG_VERSION) {
            getLogger().warning("Your config.yml is outdated! Delete it (or rename it) and restart your server to update it!");
        }

        // If all is good so far, send an enabled message
        getLogger().info("Enabled rbzAutoWelcome v" + getDescription().getVersion() + " by therbz");
    }

    public void messageSender(CommandSender sender, String message) {
        sender.sendMessage(translateAlternateColorCodes('&', message));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player joiningPlayer = event.getPlayer();

        // read from playerdata file
        file = new File(getDataFolder() + "/data/", joiningPlayer.getUniqueId().toString() + ".yml");
        if (!file.exists()) {
            try {
                fileData = YamlConfiguration.loadConfiguration(file);
                fileData.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fileData = YamlConfiguration.loadConfiguration(file);
        if (fileData.contains("wb")) {
            String wbMessage = fileData.getString("wb");
            autowbPlayers.put(joiningPlayer.getUniqueId(), wbMessage);
        }
        if (fileData.contains("welcome")) {
            String welcomeMessage = fileData.getString("welcome");
            autowelcomePlayers.put(joiningPlayer.getUniqueId(), welcomeMessage);
        }

        // Make players in HashMap say their wb/welcome
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                // wb
                if (joiningPlayer.hasPlayedBefore()) {
                    autowbPlayers.entrySet().forEach(mapElement -> {
                        UUID sendingPlayerUuid = mapElement.getKey();
                        String msg = mapElement.getValue();
                        Player sendingPlayer = getPlayer(sendingPlayerUuid);
                        if (joiningPlayer!=sendingPlayer && sendingPlayer.hasPermission("rbzaw.set")) { sendingPlayer.chat(msg); }
                    });
                } else {
                    // welcome
                    autowelcomePlayers.entrySet().forEach(mapElement -> {
                        UUID sendingPlayerUuid = mapElement.getKey();
                        String msg = mapElement.getValue();
                        Player sendingPlayer = getPlayer(sendingPlayerUuid);
                        if (joiningPlayer!=sendingPlayer && sendingPlayer.hasPermission("rbzaw.set")) { sendingPlayer.chat(msg); }
                    });
                }
            }
        }, getConfig().getLong("message-delay"));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Save player data
        file = new File(getDataFolder() + "/data/", player.getUniqueId().toString() + ".yml");
        try {
            fileData = YamlConfiguration.loadConfiguration(file);
            if (autowbPlayers.containsKey(playerUUID)) {
                fileData.createSection("wb");
                fileData.set("wb", autowbPlayers.get(playerUUID));
            }
            if (autowelcomePlayers.containsKey(playerUUID)) {
                fileData.createSection("welcome");
                fileData.set("welcome", autowelcomePlayers.get(playerUUID));
            }
            fileData.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Clean up HashMaps
        autowbPlayers.remove(playerUUID);
        autowelcomePlayers.remove(playerUUID);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 0 args, send help menu
        if (args.length==0) {
            List<String> help = getConfig().getStringList("messages.help");
            for (String m : help) {
                messageSender(sender, m);
            }
            return true;
        } else {
            // /autowb reload
            if (args[0].equalsIgnoreCase("reload")) {
                // Check that the sender has permission
                if (!sender.hasPermission("rbzaw.reload")) {
                    messageSender(sender, getConfig().getString("messages.no-permission").replace("%permission%", "rbzaw.reload"));
                    return true;
                }

                // Reload
                reloadConfig();
                messageSender(sender, getConfig().getString("messages.reload"));
                getLogger().info("Reloaded rbzAutoWelcome v" + getDescription().getVersion());
                return true;
            }

            // /autowb check <wb|welcome> [player]
            if (args[0].equalsIgnoreCase("check")) {
                // Check self
                if (args.length==2) {
                    // Check that the sender is a player
                    if (!(sender instanceof Player)) {
                        messageSender(sender, getConfig().getString("messages.not-player"));
                        return true;
                    }
                    Player p = (Player) sender;

                    // Check that sender has permission
                    if (!sender.hasPermission("rbzaw.check")) {
                        messageSender(sender, getConfig().getString("messages.no-permission").replace("%permission%", "rbzaw.check"));
                        return true;
                    }

                    // Check self wb
                    if (args[1].equalsIgnoreCase("wb")) {
                        // Check that they have actually set it first
                        if (!autowbPlayers.containsKey(p.getUniqueId())) {
                            messageSender(sender, getConfig().getString("messages.wb.self.check.fail"));
                            return true;
                        }

                        // Tell the player their auto-wb message
                        messageSender(sender, getConfig().getString("messages.wb.self.check.success").replace("%msg%", autowbPlayers.get(p.getUniqueId())));
                        return true;
                    }

                    // Check self welcome
                    if (args[1].equalsIgnoreCase("welcome")) {
                        // Check that they have actually set it first
                        if (!autowelcomePlayers.containsKey(p.getUniqueId())) {
                            messageSender(sender, getConfig().getString("messages.welcome.self.check.fail"));
                            return true;
                        }

                        // Tell the player their auto-wb message
                        messageSender(sender, getConfig().getString("messages.welcome.self.check.success").replace("%msg%", autowelcomePlayers.get(p.getUniqueId())));
                        return true;
                    }
                    
                    // They didn't put "wb" or "welcome"
                    messageSender(sender, getConfig().getString("messages.incorrect-usage").replace("%usage%","/autowb check <wb|welcome> [player]"));
                    return true;
                }

                // Check player
                else if (args.length==3) {
                    // Check that sender has permission
                    if (!sender.hasPermission("rbzaw.check.others")) {
                        messageSender(sender, getConfig().getString("messages.no-permission").replace("%permission%", "rbzaw.check.others"));
                        return true;
                    }

                    // Check that player exists
                    if (getPlayer(args[2])==null) {
                        messageSender(sender, getConfig().getString("messages.unknown-player").replace("%target%", args[2]));
                        return true;
                    }
                    Player p = getPlayer(args[2]);

                    if (args[1].equalsIgnoreCase("wb")) {
                        // Check that they have actually set it first
                        if (!autowbPlayers.containsKey(p.getUniqueId())) {
                            messageSender(sender, getConfig().getString("messages.wb.other-player.check.fail").replace("%player%", p.getName()));
                            return true;
                        }

                        // Tell the player their auto-wb message
                        messageSender(sender, getConfig().getString("messages.wb.other-player.check.success").replace("%msg%", autowbPlayers.get(p.getUniqueId())).replace("%player%", p.getName()));
                        return true;
                    }
                    else if (args[1].equalsIgnoreCase("welcome")) {
                        // Check that they have actually set it first
                        if (!autowelcomePlayers.containsKey(p.getUniqueId())) {
                            messageSender(sender, getConfig().getString("messages.welcome.other-player.check.fail").replace("%player%", p.getName()));
                            return true;
                        }

                        // Tell the player their auto-wb message
                        messageSender(sender, getConfig().getString("messages.welcome.other-player.check.success").replace("%msg%", autowelcomePlayers.get(p.getUniqueId())).replace("%player%", p.getName()));
                    }
                }

                // Sender didn't put 2 or 3 arguments
                messageSender(sender, getConfig().getString("messages.incorrect-usage").replace("%usage%", "/autowb check <wb|welcome> [player]"));
                return true;
            }

            // /autowb set <wb|welcome> <message>
            if (args[0].equalsIgnoreCase("set")) {
                // Set for self
                if (args.length==3) {
                    // Check that the sender is a player
                    if (!(sender instanceof Player)) {
                        messageSender(sender, getConfig().getString("messages.not-player"));
                        return true;
                    }
                    Player p = (Player) sender;

                    // Check that sender has permission
                    if (!sender.hasPermission("rbzaw.set")) {
                        messageSender(sender, getConfig().getString("messages.no-permission").replace("%permission%", "rbzaw.set"));
                        return true;
                    }

                    if (args[1].equalsIgnoreCase("wb")) {
                        autowbPlayers.put(p.getUniqueId(), args[2]);
                        messageSender(sender, getConfig().getString("messages.wb.self.enable").replace("%msg%", args[2]));
                        return true;
                    }
                    else if (args[1].equalsIgnoreCase("welcome")) {
                        autowelcomePlayers.put(p.getUniqueId(), args[2]);
                        messageSender(sender, getConfig().getString("messages.welcome.self.enable").replace("%msg%", args[2]));
                        return true;
                    }
                    // Sender didn't put wb or welcome
                    messageSender(sender, getConfig().getString("messages.incorrect-usage").replace("%usage%", "/autowb set <wb|welcome> <message>"));
                    return true;
                }
                // Sender didn't put 3 arguments
                messageSender(sender, getConfig().getString("messages.incorrect-usage").replace("%usage%", "/autowb set <wb|welcome> <message>"));
                return true;
            }
            // /autowb setplayer <wb|welcome> <player> <message>
            if (args[0].equalsIgnoreCase("setplayer")) {
                // Set for player
                if (args.length==4) {
                    // Check that sender has permission
                    if (!sender.hasPermission("rbzaw.set.others")) {
                        messageSender(sender, getConfig().getString("messages.no-permission").replace("%permission%", "rbzaw.set.others"));
                        return true;
                    }

                    // Check that target exists
                    if (getPlayer(args[2])==null) {
                        messageSender(sender, getConfig().getString("messages.not-player").replace("%target%", args[2]));
                        return true;
                    }
                    Player p = getPlayer(args[2]);

                    if (args[1].equalsIgnoreCase("wb")) {
                        autowbPlayers.put(p.getUniqueId(), args[3]);
                        messageSender(sender, getConfig().getString("messages.wb.self.enable").replace("%msg%", args[3]));
                        return true;
                    }
                    else if (args[1].equalsIgnoreCase("welcome")) {
                        autowelcomePlayers.put(p.getUniqueId(), args[3]);
                        messageSender(sender, getConfig().getString("messages.welcome.self.enable").replace("%msg%", args[3]));
                        return true;
                    }
                    messageSender(sender, getConfig().getString("messages.incorrect-usage").replace("%usage%", "/autowb set <wb|welcome> <message>"));
                    return true;
                }
                // Not 4 arguments
                messageSender(sender, getConfig().getString("incorrect-usage").replace("%usage%", "/autowb set <wb|welcome> [player]"));
                return true;
            }
        }

        // Incorrect args
        messageSender(sender, getConfig().getString("messages.incorrect-usage").replace("%usage%","/autowb"));
        return true;
    }
}

/*

    Permissions:
    rbzaw.reload - reload the plugin
    rbzaw.set(.others) - allows setting and actual auto-wb'ing
    rbzaw.check(.others) - allows checking

 */