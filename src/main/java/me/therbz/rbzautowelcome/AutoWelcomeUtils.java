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

    public void welcomeLoop(HashMap<UUID, String> playerMessages, Player joiningPlayer) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            for (Map.Entry<UUID, String> mapElement : playerMessages.entrySet()) {
                UUID sendingPlayerUuid = mapElement.getKey();
                String joiningPlayerName = joiningPlayer.getName();
                String playerPlaceholder = plugin.getConfig().getString("placeholders.joining-player");
                String msg = mapElement.getValue().replaceAll(playerPlaceholder, joiningPlayerName);
                Player sendingPlayer = getPlayer(sendingPlayerUuid);

                boolean essHookEnabled = plugin.getConfig().getBoolean("hooks.essentials.enabled");
                boolean essHookPreventAfkMessaging = plugin.getConfig().getBoolean("hooks.essentials.prevent-afk-messaging");
                Plugin ess = Bukkit.getPluginManager().getPlugin("Essentials");
                boolean senderIsAFK = false;
                if (ess != null && essHookEnabled && essHookPreventAfkMessaging) {
                    Essentials essentials = (Essentials) ess;
                    senderIsAFK = essentials.getUser(sendingPlayer).isAfk();
                }

                if (joiningPlayer != sendingPlayer && sendingPlayer.hasPermission("rbzaw.set") && !senderIsAFK && msg != null) {
                    sendingPlayer.chat(msg);
                }
            }
        }, plugin.getConfig().getLong("message-delay"));
    }
}
