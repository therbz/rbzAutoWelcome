package me.therbz.rbzautowelcome.commands;

import me.therbz.rbzautowelcome.core.AutoWelcome;

import java.util.Objects;

public class CommandRegistry {

    public static void registerCommands(AutoWelcome main) {
        Objects.requireNonNull(main.getCommand("autowb")).setExecutor(new AutoWelcomeCommand(main));
    }

}