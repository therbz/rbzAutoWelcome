package me.therbz.rbzautowelcome.core;

import me.therbz.rbzautowelcome.utils.AWUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.bukkit.Bukkit.getPlayer;

public class WelcomeLoop {

    private static final Random r = new Random();

    public static void run(AutoWelcome main, Player joiningPlayer, boolean essentialsUnafk) {

        if(joiningPlayer.hasPermission("rbzaw.exempt")) return;

        HashMap<UUID, String> messages = joiningPlayer.hasPlayedBefore() ? main.wbPlayers : main.welcomePlayers;
        if(essentialsUnafk) messages = main.wbPlayers;

        boolean success = false;

        for (Map.Entry<UUID, String> map : messages.entrySet()) {

            UUID sendingPlayerUuid = map.getKey();
            Player sendingPlayer = getPlayer(sendingPlayerUuid);

            if(AWUtils.isEssAfk(main, sendingPlayer)) continue;

            String msg = map.getValue().replace(main.getConfig().getString("placeholders.joining-player-name"), joiningPlayer.getName()).replace(main.getConfig().getString("placeholders.joining-player-displayname"), ChatColor.stripColor(joiningPlayer.getDisplayName()));

            if (joiningPlayer != sendingPlayer && sendingPlayer.hasPermission("rbzaw.set") && !AWUtils.isEssAfk(main, sendingPlayer)) {

                // Don't wb if sending player already wbd the joining player in the last 15 seconds
                if (main.getConfig().getBoolean("prevent-same-player-wb-spam") && main.recentlyWbdPlayers.contains(joiningPlayer.getUniqueId()) && joiningPlayer.hasPlayedBefore()) break;

                queueMsgSend(main, sendingPlayer, msg, (messages != main.wbPlayers));

                success = true;

            }

        }

        if (success && joiningPlayer.hasPlayedBefore()) {
            addToAndScheduleRemoveFromRecents(main, joiningPlayer);
        }

    }

    private static void queueMsgSend(AutoWelcome main, Player p, String msg, boolean isWelcome) {
        long delay = r.nextInt(main.getConfig().getInt("message-delay-max") + 1 - main.getConfig().getInt("message-delay-min")) + main.getConfig().getInt("message-delay-min");
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, () -> {
            p.chat(msg);
        }, isWelcome ? (delay * main.getConfig().getInt("welcome-message-delay-multiplier")) : delay);
    }

    private static void addToAndScheduleRemoveFromRecents(AutoWelcome main, Player welcomed) {

        // Don't add the same player twice
        if (main.recentlyWbdPlayers.contains(welcomed.getUniqueId())) return;

        main.recentlyWbdPlayers.add(welcomed.getUniqueId());

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, () -> {
            if (main.recentlyWbdPlayers.contains(welcomed.getUniqueId())) main.recentlyWbdPlayers.remove(welcomed.getUniqueId());
        }, main.getConfig().getInt("min-message-delay"));

    }

}