package me.therbz.rbzautowelcome;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

import static me.therbz.rbzautowelcome.AutoWelcome.*;
import static org.bukkit.Bukkit.getPlayer;
import static org.bukkit.ChatColor.translateAlternateColorCodes;
import static org.bukkit.ChatColor.stripColor;

public class AutoWelcomeCommand implements CommandExecutor {
    private final JavaPlugin plugin = getPlugin(AutoWelcome.class);
    private final FileConfiguration config = plugin.getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 0 args, send help menu
        if (args.length==0) {
            List<String> help = config.getStringList("messages.help");
            for (String m : help) {
                messageSender(sender, m);
            }
            return true;
        } else {
            // /autowb reload
            if (args[0].equalsIgnoreCase("reload")) {
                // Check that the sender has permission
                if (!sender.hasPermission("rbzaw.reload")) {
                    messageSender(sender, config.getString("messages.no-permission").replace("%permission%", "rbzaw.reload"));
                    return true;
                }

                // Reload
                plugin.reloadConfig();
                messageSender(sender, config.getString("messages.reload"));
                plugin.getLogger().info("Reloaded rbzAutoWelcome v" + plugin.getDescription().getVersion());
                return true;
            }

            // /autowb check <wb|welcome> [player]
            if (args[0].equalsIgnoreCase("check")) {
                // Check self
                if (args.length==2) {
                    // Check that the sender is a player
                    if (!(sender instanceof Player)) {
                        messageSender(sender, config.getString("messages.not-player"));
                        return true;
                    }
                    Player p = (Player) sender;
                    
                    // Check that sender has permission
                    if (!sender.hasPermission("rbzaw.check")) {
                        messageSender(sender, config.getString("messages.no-permission").replace("%permission%", "rbzaw.check"));
                        return true;
                    }

                    // Check self wb
                    if (args[1].equalsIgnoreCase("wb")) {
                        // Check that they have actually set it first
                        if (getPlayerWB(p.getUniqueId())==null) {
                            messageSender(sender, config.getString("messages.wb.self.check.fail"));
                            return true;
                        }

                        // Tell the player their auto-wb message
                        messageSender(sender, config.getString("messages.wb.self.check.success").replace("%msg%", getPlayerWB(p.getUniqueId())));
                        return true;
                    }

                    // Check self welcome
                    if (args[1].equalsIgnoreCase("welcome")) {
                        // Check that they have actually set it first
                        if (getPlayerWelcome(p.getUniqueId())==null) {
                            messageSender(sender, config.getString("messages.welcome.self.check.fail"));
                            return true;
                        }

                        // Tell the player their auto-wb message
                        messageSender(sender, config.getString("messages.welcome.self.check.success").replace("%msg%",  getPlayerWelcome(p.getUniqueId())));
                        return true;
                    }

                    // They didn't put "wb" or "welcome"
                    messageSender(sender, config.getString("messages.incorrect-usage").replace("%usage%","/autowb check <wb|welcome> [player]"));
                    return true;
                }

                // Check player
                else if (args.length==3) {
                    // Check that sender has permission
                    if (!sender.hasPermission("rbzaw.check.others")) {
                        messageSender(sender, config.getString("messages.no-permission").replace("%permission%", "rbzaw.check.others"));
                        return true;
                    }

                    // Check that player exists
                    if (getPlayer(args[2])==null) {
                        messageSender(sender, config.getString("messages.unknown-player").replace("%target%", args[2]));
                        return true;
                    }
                    Player p = getPlayer(args[2]);

                    if (args[1].equalsIgnoreCase("wb")) {
                        // Check that they have actually set it first
                        //if (!wbPlayers.containsKey(p.getUniqueId())) {
                        if (getPlayerWB(p.getUniqueId())==null) {
                            messageSender(sender, config.getString("messages.wb.other-player.check.fail").replace("%player%", p.getName()));
                            return true;
                        }

                        // Tell the player their auto-wb message
                        messageSender(sender, config.getString("messages.wb.other-player.check.success").replace("%msg%", getPlayerWB(p.getUniqueId())).replace("%player%", p.getName()));
                        return true;
                    }
                    else if (args[1].equalsIgnoreCase("welcome")) {
                        // Check that they have actually set it first
                        //if (!welcomePlayers.containsKey(p.getUniqueId())) {
                        if (getPlayerWelcome(p.getUniqueId())==null) {
                            messageSender(sender, config.getString("messages.welcome.other-player.check.fail").replace("%player%", p.getName()));
                            return true;
                        }

                        // Tell the player their auto-wb message
                        messageSender(sender, config.getString("messages.welcome.other-player.check.success").replace("%msg%", getPlayerWelcome(p.getUniqueId())).replace("%player%", p.getName()));
                        return true;
                    }
                }

                // Sender didn't put 2 or 3 arguments
                messageSender(sender, config.getString("messages.incorrect-usage").replace("%usage%", "/autowb check <wb|welcome> [player]"));
                return true;
            }

            // /autowb set <wb|welcome> <message>
            if (args[0].equalsIgnoreCase("set")) {
                // Set for self
                if (args.length>=3) {
                    // Check that the sender is a player
                    if (!(sender instanceof Player)) {
                        messageSender(sender, config.getString("messages.not-player"));
                        return true;
                    }
                    Player p = (Player) sender;

                    // Check that sender has permission
                    if (!sender.hasPermission("rbzaw.set")) {
                        messageSender(sender, config.getString("messages.no-permission").replace("%permission%", "rbzaw.set"));
                        return true;
                    }

                    if (args[1].equalsIgnoreCase("wb")) {
                        if (args[2].equalsIgnoreCase("off")) {
                            setPlayerWB(p.getUniqueId(), null);
                            //removePlayerWB(p.getUniqueId());
                            messageSender(sender, config.getString("messages.wb.self.disable"));
                            return true;
                        }

                        StringBuilder messageBuilder = new StringBuilder(args[2]);
                        for (int i = 3; i < args.length; i++) {
                            messageBuilder.append(" ").append(args[i]);
                        }
                        String message = messageBuilder.toString();

                        // If message length exceeds max, and sender does not have permission to go over max then cancel
                        String messageNoColour = stripColor(translateAlternateColorCodes('&', message));
                        int maxMessageLength=config.getInt("max-message-length");
                        if(messageNoColour.length()>maxMessageLength && !sender.hasPermission("rbzaw.bypass.maxlength")) {
                                messageSender(sender, config.getString("messages.exceeded-max-length").replace("%length%", String.valueOf(maxMessageLength)));
                                return true;
                        }

                        //wbPlayers.put(p.getUniqueId(), args[2]);
                        setPlayerWB(p.getUniqueId(), message);
                        messageSender(sender, config.getString("messages.wb.self.enable").replace("%msg%", message));
                        return true;
                    }
                    else if (args[1].equalsIgnoreCase("welcome")) {
                        if (args[2].equalsIgnoreCase("off")) {
                            setPlayerWelcome(p.getUniqueId(), null);
                            //removePlayerWelcome(p.getUniqueId());
                            messageSender(sender, config.getString("messages.welcome.self.disable"));
                            return true;
                        }

                        StringBuilder messageBuilder = new StringBuilder(args[2]);
                        for (int i = 3; i < args.length; i++) {
                            messageBuilder.append(" ").append(args[i]);
                        }
                        String message = messageBuilder.toString();

                        // If message length exceeds max, and sender does not have permission to go over max then cancel
                        String messageNoColour = stripColor(translateAlternateColorCodes('&', message));
                        int maxMessageLength=config.getInt("max-message-length");
                        if(messageNoColour.length()>maxMessageLength && !sender.hasPermission("rbzaw.bypass.maxlength")) {
                            messageSender(sender, config.getString("messages.exceeded-max-length").replace("%length%", String.valueOf(maxMessageLength)));
                            return true;
                        }

                        //welcomePlayers.put(p.getUniqueId(), args[2]);
                        setPlayerWelcome(p.getUniqueId(), message);
                        messageSender(sender, config.getString("messages.welcome.self.enable").replace("%msg%", message));
                        return true;
                    }
                }
                // Sender didn't put at least 3 arguments
                messageSender(sender, config.getString("messages.incorrect-usage").replace("%usage%", "/autowb set <wb|welcome> <message|off>"));
                return true;
            }
            // /autowb setplayer <wb|welcome> <player> <message>
            if (args[0].equalsIgnoreCase("setplayer")) {
                // Set for player
                if (args.length>=4) {
                    // Check that sender has permission
                    if (!sender.hasPermission("rbzaw.set.others")) {
                        messageSender(sender, config.getString("messages.no-permission").replace("%permission%", "rbzaw.set.others"));
                        return true;
                    }

                    // Check that target exists
                    if (getPlayer(args[1])==null) {
                        messageSender(sender, config.getString("messages.unknown-player").replace("%target%", args[1]));
                        return true;
                    }
                    Player p = getPlayer(args[1]);

                    if (args[2].equalsIgnoreCase("wb")) {
                        if (args[3].equalsIgnoreCase("off")) {
                            setPlayerWB(p.getUniqueId(), null);
                            //removePlayerWB(p.getUniqueId());
                            messageSender(sender, config.getString("messages.wb.other-player.disable").replace("%target%", p.getName()));
                            messageSender(p, config.getString("messages.wb.self.disable"));
                            return true;
                        }

                        StringBuilder messageBuilder = new StringBuilder(args[3]);
                        for (int i = 4; i < args.length; i++) {
                            messageBuilder.append(" ").append(args[i]);
                        }
                        String message = messageBuilder.toString();

                        // If message length exceeds max, and sender does not have permission to go over max then cancel
                        String messageNoColour = stripColor(translateAlternateColorCodes('&', message));
                        int maxMessageLength=config.getInt("max-message-length");
                        if(messageNoColour.length()>maxMessageLength && !sender.hasPermission("rbzaw.bypass.maxlength")) {
                            messageSender(sender, config.getString("messages.exceeded-max-length").replace("%length%", String.valueOf(maxMessageLength)));
                            return true;
                        }

                        //wbPlayers.put(p.getUniqueId(), args[3]);
                        setPlayerWB(p.getUniqueId(), message);
                        messageSender(sender, config.getString("messages.wb.other-player.enable").replace("%msg%", message).replace("%target%", p.getName()));
                        messageSender(p, config.getString("messages.wb.self.enable").replace("%msg%", message));
                        return true;
                    }
                    else if (args[2].equalsIgnoreCase("welcome")) {
                        if (args[3].equalsIgnoreCase("off")) {
                            setPlayerWelcome(p.getUniqueId(), null);
                            //removePlayerWelcome(p.getUniqueId());
                            messageSender(sender, config.getString("messages.welcome.other-player.disable").replace("%target%", p.getName()));
                            messageSender(p, config.getString("messages.welcome.self.disable"));
                            return true;
                        }

                        StringBuilder messageBuilder = new StringBuilder(args[3]);
                        for (int i = 4; i < args.length; i++) {
                            messageBuilder.append(" ").append(args[i]);
                        }
                        String message = messageBuilder.toString();

                        // If message length exceeds max, and sender does not have permission to go over max then cancel
                        String messageNoColour = stripColor(translateAlternateColorCodes('&', message));
                        int maxMessageLength=config.getInt("max-message-length");
                        if(messageNoColour.length()>maxMessageLength && !sender.hasPermission("rbzaw.bypass.maxlength")) {
                            messageSender(sender, config.getString("messages.exceeded-max-length").replace("%length%", String.valueOf(maxMessageLength)));
                            return true;
                        }

                        //welcomePlayers.put(p.getUniqueId(), args[3]);
                        setPlayerWelcome(p.getUniqueId(), message);
                        messageSender(sender, config.getString("messages.welcome.other-player.enable").replace("%msg%", message).replace("%target%", p.getName()));
                        messageSender(p, config.getString("messages.welcome.self.enable").replace("%msg%", message));
                        return true;
                    }
                }
                // Not 4 arguments
                messageSender(sender, config.getString("messages.incorrect-usage").replace("%usage%", "/autowb setplayer <player> <wb|welcome> <message|off>"));
                return true;
            }
        }

        // Incorrect args
        messageSender(sender, config.getString("messages.incorrect-usage").replace("%usage%","/autowb"));
        return true;
    }
}
