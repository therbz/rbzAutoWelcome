package me.therbz.rbzautowelcome;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

import static org.bukkit.Bukkit.getPlayer;

public class AutoWelcomeCommand implements CommandExecutor {
    private JavaPlugin plugin = AutoWelcome.getPlugin(AutoWelcome.class);

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        // 0 args, send help menu
        if (args.length==0) {
            List<String> help = plugin.getConfig().getStringList("messages.help");
            for (String m : help) {
                AutoWelcome.messageSender(sender, m);
            }
            return true;
        } else {
            // /autowb reload
            if (args[0].equalsIgnoreCase("reload")) {
                // Check that the sender has permission
                if (!sender.hasPermission("rbzaw.reload")) {
                    AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.no-permission").replace("%permission%", "rbzaw.reload"));
                    return true;
                }

                // Reload
                plugin.reloadConfig();
                AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.reload"));
                plugin.getLogger().info("Reloaded rbzAutoWelcome v" + plugin.getDescription().getVersion());
                return true;
            }

            // /autowb check <wb|welcome> [player]
            if (args[0].equalsIgnoreCase("check")) {
                // Check self
                if (args.length==2) {
                    // Check that the sender is a player
                    if (!(sender instanceof Player)) {
                        AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.not-player"));
                        return true;
                    }
                    Player p = (Player) sender;

                    // Check that sender has permission
                    if (!sender.hasPermission("rbzaw.check")) {
                        AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.no-permission").replace("%permission%", "rbzaw.check"));
                        return true;
                    }

                    // Check self wb
                    if (args[1].equalsIgnoreCase("wb")) {
                        // Check that they have actually set it first
                        if (!AutoWelcome.playerHasSetWB(p.getUniqueId())) {
                            AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.wb.self.check.fail"));
                            return true;
                        }

                        // Tell the player their auto-wb message
                        AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.wb.self.check.success").replace("%msg%", AutoWelcome.getPlayerWB(p.getUniqueId())));
                        return true;
                    }

                    // Check self welcome
                    if (args[1].equalsIgnoreCase("welcome")) {
                        // Check that they have actually set it first
                        if (!AutoWelcome.playerHasSetWelcome(p.getUniqueId())) {
                            AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.welcome.self.check.fail"));
                            return true;
                        }

                        // Tell the player their auto-wb message
                        AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.welcome.self.check.success").replace("%msg%",  AutoWelcome.getPlayerWelcome(p.getUniqueId())));
                        return true;
                    }

                    // They didn't put "wb" or "welcome"
                    AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.incorrect-usage").replace("%usage%","/autowb check <wb|welcome> [player]"));
                    return true;
                }

                // Check player
                else if (args.length==3) {
                    // Check that sender has permission
                    if (!sender.hasPermission("rbzaw.check.others")) {
                        AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.no-permission").replace("%permission%", "rbzaw.check.others"));
                        return true;
                    }

                    // Check that player exists
                    if (getPlayer(args[2])==null) {
                        AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.unknown-player").replace("%target%", args[2]));
                        return true;
                    }
                    Player p = getPlayer(args[2]);

                    if (args[1].equalsIgnoreCase("wb")) {
                        // Check that they have actually set it first
                        //if (!wbPlayers.containsKey(p.getUniqueId())) {
                        if (!AutoWelcome.playerHasSetWB(p.getUniqueId())) {
                            AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.wb.other-player.check.fail").replace("%player%", p.getName()));
                            return true;
                        }

                        // Tell the player their auto-wb message
                        AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.wb.other-player.check.success").replace("%msg%", AutoWelcome.getPlayerWB(p.getUniqueId())).replace("%player%", p.getName()));
                        return true;
                    }
                    else if (args[1].equalsIgnoreCase("welcome")) {
                        // Check that they have actually set it first
                        //if (!welcomePlayers.containsKey(p.getUniqueId())) {
                        if (!AutoWelcome.playerHasSetWelcome(p.getUniqueId())) {
                            AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.welcome.other-player.check.fail").replace("%player%", p.getName()));
                            return true;
                        }

                        // Tell the player their auto-wb message
                        AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.welcome.other-player.check.success").replace("%msg%", AutoWelcome.getPlayerWelcome(p.getUniqueId())).replace("%player%", p.getName()));
                        return true;
                    }
                }

                // Sender didn't put 2 or 3 arguments
                AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.incorrect-usage").replace("%usage%", "/autowb check <wb|welcome> [player]"));
                return true;
            }

            // /autowb set <wb|welcome> <message>
            if (args[0].equalsIgnoreCase("set")) {
                // Set for self
                if (args.length>=3) {
                    // Check that the sender is a player
                    if (!(sender instanceof Player)) {
                        AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.not-player"));
                        return true;
                    }
                    Player p = (Player) sender;

                    // Check that sender has permission
                    if (!sender.hasPermission("rbzaw.set")) {
                        AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.no-permission").replace("%permission%", "rbzaw.set"));
                        return true;
                    }

                    if (args[1].equalsIgnoreCase("wb")) {
                        if (args[2].equalsIgnoreCase("off")) {
                            AutoWelcome.removePlayerWB(p.getUniqueId());
                            AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.wb.self.disable"));
                            return true;
                        }

                        String message=args[2];
                        for (int i = 3; i < args.length; i++) {
                            message = message + " " + args[i];
                        }

                        //wbPlayers.put(p.getUniqueId(), args[2]);
                        AutoWelcome.setPlayerWB(p.getUniqueId(), message);
                        AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.wb.self.enable").replace("%msg%", message));
                        return true;
                    }
                    else if (args[1].equalsIgnoreCase("welcome")) {
                        if (args[2].equalsIgnoreCase("off")) {
                            AutoWelcome.removePlayerWelcome(p.getUniqueId());
                            AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.welcome.self.disable"));
                            return true;
                        }

                        String message=args[2];
                        for (int i = 3; i < args.length; i++) {
                            message = message + " " + args[i];
                        }

                        //welcomePlayers.put(p.getUniqueId(), args[2]);
                        AutoWelcome.setPlayerWelcome(p.getUniqueId(), message);
                        AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.welcome.self.enable").replace("%msg%", message));
                        return true;
                    }
                }
                // Sender didn't put at least 3 arguments
                AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.incorrect-usage").replace("%usage%", "/autowb set <wb|welcome> <message|off>"));
                return true;
            }
            // /autowb setplayer <wb|welcome> <player> <message>
            if (args[0].equalsIgnoreCase("setplayer")) {
                // Set for player
                if (args.length>=4) {
                    // Check that sender has permission
                    if (!sender.hasPermission("rbzaw.set.others")) {
                        AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.no-permission").replace("%permission%", "rbzaw.set.others"));
                        return true;
                    }

                    // Check that target exists
                    if (getPlayer(args[2])==null) {
                        AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.not-player").replace("%target%", args[2]));
                        return true;
                    }
                    Player p = getPlayer(args[2]);

                    if (args[1].equalsIgnoreCase("wb")) {
                        if (args[3].equalsIgnoreCase("off")) {
                            AutoWelcome.removePlayerWB(p.getUniqueId());
                            AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.wb.other-player.disable").replace("%target%", p.getName()));
                            AutoWelcome.messageSender(p, plugin.getConfig().getString("messages.wb.self.disable"));
                            return true;
                        }


                        String message=args[3];
                        for (int i = 4; i < args.length; i++) {
                            message = message + " " + args[i];
                        }

                        //wbPlayers.put(p.getUniqueId(), args[3]);
                        AutoWelcome.setPlayerWB(p.getUniqueId(), message);
                        AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.wb.other-player.enable").replace("%msg%", message).replace("%target%", p.getName()));
                        AutoWelcome.messageSender(p, plugin.getConfig().getString("messages.wb.self.enable").replace("%msg%", message));
                        return true;
                    }
                    else if (args[1].equalsIgnoreCase("welcome")) {
                        if (args[3].equalsIgnoreCase("off")) {
                            AutoWelcome.removePlayerWB(p.getUniqueId());
                            AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.welcome.other-player.disable").replace("%target%", p.getName()));
                            AutoWelcome.messageSender(p, plugin.getConfig().getString("messages.welcome.self.disable"));
                            return true;
                        }

                        String message=args[3];
                        for (int i = 4; i < args.length; i++) {
                            message = message + " " + args[i];
                        }

                        //welcomePlayers.put(p.getUniqueId(), args[3]);
                        AutoWelcome.setPlayerWelcome(p.getUniqueId(), message);
                        AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.welcome.other-player.enable").replace("%msg%", message).replace("%target%", p.getName()));
                        AutoWelcome.messageSender(p, plugin.getConfig().getString("messages.welcome.self.enable").replace("%msg%", message));
                        return true;
                    }
                }
                // Not 4 arguments
                AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.incorrect-usage").replace("%usage%", "/autowb setplayer <wb|welcome> <player> <message|off>"));
                return true;
            }
        }

        // Incorrect args
        AutoWelcome.messageSender(sender, plugin.getConfig().getString("messages.incorrect-usage").replace("%usage%","/autowb"));
        return true;
    }
}
