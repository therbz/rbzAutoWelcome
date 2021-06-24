package me.therbz.rbzautowelcome.listeners;

import me.therbz.rbzautowelcome.core.AutoWelcome;
import me.therbz.rbzautowelcome.core.WelcomeLoop;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AfkStatusChange implements Listener {

    AutoWelcome main;

    public AfkStatusChange(AutoWelcome instance) {
        this.main = instance;
    }

    @EventHandler
    public void onPlayerUnAfk(AfkStatusChangeEvent e) {
        // AfkStatusChangeEvent#getValue returns true if AFK enabled or false if AFK disabled
        // Therefore, we're checking if the player disabled AFK

        // If ess hook & wb-returning-afk is enabled, and the player is going un-afk for any reason other than quitting
        if (main.getConfig().getBoolean("hooks.essentials.enabled") && main.getConfig().getBoolean("hooks.essentials.wb-returning-afk") && !e.getValue() && e.getCause() != AfkStatusChangeEvent.Cause.QUIT) WelcomeLoop.run(main, e.getAffected().getBase(), true);

    }

}