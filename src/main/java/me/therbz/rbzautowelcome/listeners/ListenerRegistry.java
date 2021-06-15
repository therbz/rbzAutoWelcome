package me.therbz.rbzautowelcome.listeners;

import me.therbz.rbzautowelcome.core.AutoWelcome;
import org.bukkit.Bukkit;

public class ListenerRegistry {

    public static void registerListeners(AutoWelcome main) {
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoin(main), main);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerQuit(main), main);
        if (Bukkit.getPluginManager().getPlugin("Essentials") != null) Bukkit.getServer().getPluginManager().registerEvents(new AfkStatusChange(main), main);
    }

}