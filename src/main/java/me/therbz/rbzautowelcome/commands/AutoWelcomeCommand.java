package me.therbz.rbzautowelcome.commands;

import me.therbz.rbzautowelcome.config.ConfigUtil;
import me.therbz.rbzautowelcome.core.AutoWelcome;
import me.therbz.rbzautowelcome.utils.AWUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static org.bukkit.Bukkit.getPlayer;
import static org.bukkit.ChatColor.stripColor;

public class AutoWelcomeCommand implements CommandExecutor {

    AutoWelcome main;

    public AutoWelcomeCommand(AutoWelcome instance) {
        this.main = instance;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // 0 args, send help menu
        if (args.length == 0) {
            List<String> help = main.getConfig().getStringList("messages.help");
            for (String string : help) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', string));
            }
            return true;
        }

        // /autowb reload
        if (args[0].equalsIgnoreCase("reload")) {

            // Check that the sender has permission
            if (!sender.hasPermission("rbzaw.reload")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.no-permission").replace("%permission%", "rbzaw.reload")));
                return true;
            }

            // Reload
            main.reloadConfig();

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.reload")));
            main.getLogger().info("Reloaded rbzAutoWelcome v" + main.getDescription().getVersion());

            return true;
        }

        // /autowb check <wb|welcome> [player]
        if (args[0].equalsIgnoreCase("check")) {

            // Check self
            if (args.length == 2) {

                // Check that the sender is a player
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.not-player")));
                    return true;
                }

                Player p = (Player) sender;

                // Check that sender has permission
                if (!sender.hasPermission("rbzaw.check")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.no-permission").replace("%permission%", "rbzaw.check")));
                    return true;
                }

                // Check self wb
                if (args[1].equalsIgnoreCase("wb")) {

                    // Check that they have actually set it first
                    if (!main.wbPlayers.containsKey(p.getUniqueId())) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.wb.self.check.fail")));
                        return true;
                    }

                    // Tell the player their auto-wb message
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.wb.self.check.success").replace("%msg%", main.wbPlayers.get(p.getUniqueId()))));

                    return true;
                }

                // Check self welcome
                if (args[1].equalsIgnoreCase("welcome")) {

                    // Check that they have actually set it first
                    if (!main.welcomePlayers.containsKey(p.getUniqueId())) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.welcome.self.check.fail")));
                        return true;
                    }

                    // Tell the player their auto-wb message
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.welcome.self.check.success").replace("%msg%", main.welcomePlayers.get(p.getUniqueId()))));

                    return true;
                }

                // They didn't put "wb" or "welcome"
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.incorrect-usage").replace("%usage%", "/autowb check <wb|welcome> [player]")));
                return true;
            }

            // Check player
            if (args.length == 3) {

                // Check that sender has permission
                if (!sender.hasPermission("rbzaw.check.others")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.no-permission").replace("%permission%", "rbzaw.check.others")));
                    return true;
                }

                // Check that player exists
                if (getPlayer(args[2]) == null) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.unknown-player").replace("%target%", args[2])));
                    return true;
                }

                Player p = getPlayer(args[2]);

                if (args[1].equalsIgnoreCase("wb")) {

                    // Check that they have actually set it first
                    //if (!wbPlayers.containsKey(p.getUniqueId())) {
                    if (!main.wbPlayers.containsKey(p.getUniqueId())) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.wb.other-player.check.fail").replace("%player%", p.getName())));
                        return true;
                    }

                    // Tell the player their auto-wb message
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.wb.other-player.check.success").replace("%msg%", main.wbPlayers.get(p.getUniqueId())).replace("%player%", p.getName())));
                    return true;

                } else if (args[1].equalsIgnoreCase("welcome")) {

                    // Check that they have actually set it first
                    if (!main.welcomePlayers.containsKey(p.getUniqueId())) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.welcome.other-player.check.fail").replace("%player%", p.getName())));
                        return true;
                    }

                    // Tell the player their auto-wb message
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.welcome.other-player.check.success").replace("%msg%", main.welcomePlayers.get(p.getUniqueId())).replace("%player%", p.getName())));

                    return true;
                }

            }

            // Sender didn't put 2 or 3 arguments
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.incorrect-usage").replace("%usage%", "/autowb check <wb|welcome> [player]")));

            return true;
        }

        // /autowb set <wb|welcome> <message>
        if (args[0].equalsIgnoreCase("set")) {

            // Set for self
            if (args.length >= 3) {

                // Check that the sender is a player
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.not-player")));
                    return true;
                }

                Player p = (Player) sender;

                // Check that sender has permission
                if (!sender.hasPermission("rbzaw.set")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.no-permission").replace("%permission%", "rbzaw.set")));
                    return true;
                }

                if (args[1].equalsIgnoreCase("wb")) {

                    if (args[2].equalsIgnoreCase("off")) {

                        main.wbPlayers.put(p.getUniqueId(), null);
                        ConfigUtil.attemptSavePlayerdata(main, p);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.wb.self.disable")));

                        return true;
                    }

                    StringBuilder messageBuilder = new StringBuilder(args[2]);
                    for (int i = 3; i < args.length; i++) {
                        messageBuilder.append(" ").append(args[i]);
                    }
                    String message = messageBuilder.toString().trim();

                    // If message length exceeds max, and sender does not have permission to go over max then cancel
                    int maxMessageLength = main.getConfig().getInt("max-message-length");
                    if (stripColor(message).length() > maxMessageLength && !sender.hasPermission("rbzaw.bypass.maxlength")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.exceeded-max-length").replace("%length%", String.valueOf(maxMessageLength))));
                        return true;
                    }

                    main.wbPlayers.put(p.getUniqueId(), message);
                    ConfigUtil.attemptSavePlayerdata(main, p);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.wb.self.enable").replace("%msg%", message)));

                    return true;

                } else if (args[1].equalsIgnoreCase("welcome")) {

                    if (args[2].equalsIgnoreCase("off")) {

                        main.welcomePlayers.put(p.getUniqueId(), null);
                        ConfigUtil.attemptSavePlayerdata(main, p);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.welcome.self.disable")));

                        return true;
                    }

                    StringBuilder messageBuilder = new StringBuilder(args[2]);
                    for (int i = 3; i < args.length; i++) {
                        messageBuilder.append(" ").append(args[i]);
                    }
                    String message = messageBuilder.toString().trim();

                    // If message length exceeds max, and sender does not have permission to go over max then cancel
                    int maxMessageLength = main.getConfig().getInt("max-message-length");
                    if (stripColor(message).length() > maxMessageLength && !sender.hasPermission("rbzaw.bypass.maxlength")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.exceeded-max-length").replace("%length%", String.valueOf(maxMessageLength))));
                        return true;
                    }

                    main.welcomePlayers.put(p.getUniqueId(), message);
                    ConfigUtil.attemptSavePlayerdata(main, p);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.welcome.self.enable").replace("%msg%", message)));

                    return true;

                }
            }

            // Sender didn't put at least 3 arguments
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.incorrect-usage").replace("%usage%", "/autowb set <wb|welcome> <message|off>")));
            return true;
        }

        // /autowb setplayer <wb|welcome> <player> <message>
        if (args[0].equalsIgnoreCase("setplayer")) {
            // Set for player
            if (args.length >= 4) {

                // Check that sender has permission
                if (!sender.hasPermission("rbzaw.set.others")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.no-permission").replace("%permission%", "rbzaw.set.others")));
                    return true;
                }

                // Check that target exists
                if (getPlayer(args[1]) == null) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.unknown-player").replace("%target%", args[1])));
                    return true;
                }

                Player p = getPlayer(args[1]);

                if (args[2].equalsIgnoreCase("wb")) {

                    if (args[3].equalsIgnoreCase("off")) {

                        main.wbPlayers.put(p.getUniqueId(), null);
                        ConfigUtil.attemptSavePlayerdata(main, p);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.wb.other-player.disable").replace("%target%", p.getName())));
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.wb.self.disable")));

                        return true;
                    }

                    StringBuilder messageBuilder = new StringBuilder(args[3]);
                    for (int i = 4; i < args.length; i++) {
                        messageBuilder.append(" ").append(args[i]);
                    }
                    String message = messageBuilder.toString().trim();

                    // If message length exceeds max, and sender does not have permission to go over max then cancel
                    int maxMessageLength = main.getConfig().getInt("max-message-length");
                    if (stripColor(message).length() > maxMessageLength && !sender.hasPermission("rbzaw.bypass.maxlength")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.exceeded-max-length").replace("%length%", String.valueOf(maxMessageLength))));
                        return true;
                    }

                    main.wbPlayers.put(p.getUniqueId(), message);
                    ConfigUtil.attemptSavePlayerdata(main, p);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.wb.other-player.enable").replace("%msg%", message).replace("%target%", p.getName())));
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.wb.self.enable").replace("%msg%", message)));
                    return true;

                } else if (args[2].equalsIgnoreCase("welcome")) {

                    if (args[3].equalsIgnoreCase("off")) {

                        main.welcomePlayers.put(p.getUniqueId(), null);
                        ConfigUtil.attemptSavePlayerdata(main, p);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.welcome.other-player.disable").replace("%target%", p.getName())));
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.welcome.self.disable")));

                        return true;
                    }

                    StringBuilder messageBuilder = new StringBuilder(args[3]);
                    for (int i = 4; i < args.length; i++) {
                        messageBuilder.append(" ").append(args[i]);
                    }
                    String message = messageBuilder.toString().trim();

                    // If message length exceeds max, and sender does not have permission to go over max then cancel
                    int maxMessageLength = main.getConfig().getInt("max-message-length");
                    if (stripColor(message).length() > maxMessageLength && !sender.hasPermission("rbzaw.bypass.maxlength")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.exceeded-max-length").replace("%length%", String.valueOf(maxMessageLength))));
                        return true;
                    }

                    main.welcomePlayers.put(p.getUniqueId(), null);
                    ConfigUtil.attemptSavePlayerdata(main, p);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.welcome.other-player.enable").replace("%msg%", message).replace("%target%", p.getName())));
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.welcome.self.enable").replace("%msg%", message)));
                    return true;
                }
            }

            // Not 4 arguments
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.incorrect-usage").replace("%usage%", "/autowb setplayer <player> <wb|welcome> <message|off>")));
            return true;
        }

        // Incorrect args
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.incorrect-usage").replace("%usage%", "/autowb")));
        return true;
    }

}