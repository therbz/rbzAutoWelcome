package me.therbz.rbzautowelcome;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.getPlayer;

public class AutoWelcomeUtils {
    private final JavaPlugin plugin = AutoWelcome.getPlugin(AutoWelcome.class);

    public void welcomeLoop(HashMap<UUID, String> playerMessages, Player player) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            System.out.println("welcomeLoop FIRED: " + playerMessages.toString());
            for (Map.Entry<UUID, String> mapElement : playerMessages.entrySet()) {
                UUID sendingPlayerUuid = mapElement.getKey();
                String msg = mapElement.getValue();
                Player messagingPlayer = getPlayer(sendingPlayerUuid);

                boolean essHookEnabled = plugin.getConfig().getBoolean("hooks.essentials.enabled");
                boolean essHookPreventAfkMessaging = plugin.getConfig().getBoolean("hooks.essentials.prevent-afk-messaging");
                Plugin ess = Bukkit.getPluginManager().getPlugin("Essentials");
                boolean senderIsAFK = false;
                if (ess != null && essHookEnabled && essHookPreventAfkMessaging) {
                    Essentials essentials = (Essentials) ess;
                    senderIsAFK = essentials.getUser(messagingPlayer).isAfk();
                }

                if (player != messagingPlayer && messagingPlayer.hasPermission("rbzaw.set") && !senderIsAFK && msg != null) {
                    messagingPlayer.chat(msg);
                }
            }
        }, plugin.getConfig().getLong("message-delay"));
    }
}
