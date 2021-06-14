package me.therbz.rbzautowelcome.listeners;

import me.therbz.rbzautowelcome.AutoWelcome;
import me.therbz.rbzautowelcome.AutoWelcomeUtils;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class EssentialsAfkListener implements Listener {
    private final AutoWelcome plugin;

    public EssentialsAfkListener(AutoWelcome plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerUnAfk(AfkStatusChangeEvent event) {
        // AfkStatusChangeEvent#getValue returns true if AFK enabled or false if AFK disabled
        // Therefore, we're checking if the player disabled AFK

        // If essentials hook enabled and essentials wb-returning-afk enables and the player is going UN-afk and the cause ISN'T quitting
        if (plugin.getConfig().getBoolean("hooks.essentials.enabled") && plugin.getConfig().getBoolean("hooks.essentials.wb-returning-afk") && !event.getValue() && event.getCause()!=AfkStatusChangeEvent.Cause.QUIT) {

            HashMap<UUID, String> playerMessages = AutoWelcome.copyOfWBPlayers();
            Player player = event.getAffected().getBase();

            AutoWelcomeUtils utils = new AutoWelcomeUtils(plugin);
            utils.welcomeLoop(playerMessages, player);
        }
    }
}
