package com.zlshames.minecrafttalismanplugin.commands;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.logging.Level;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public class RollD6 implements CommandExecutor {

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        try {
            Bukkit.getLogger().info("Rolling a six-sided dice...");

            // Generate the random number
            int randomNum = ThreadLocalRandom.current().nextInt(1, 6 + 1);
            Bukkit.getLogger().info("Roll: ".format(String.valueOf(randomNum)));

            // Pick out the name to use
            Bukkit.getLogger().info("before");
            String name = sender.getName();
            Bukkit.getLogger().info("after");

            // Build the message
            StringBuilder msgBuilder = new StringBuilder();
            msgBuilder.append(name).append(" rolled a ").append(randomNum).append("!");

            // Send the message
            Component message = Component.text(msgBuilder.toString());
            Bukkit.getServer().broadcast(message);
        } catch (Exception ex) {
            Bukkit.getLogger().log(Level.SEVERE, "An error has occurred");
            Bukkit.getLogger().log(Level.SEVERE, ex.getMessage());
            Bukkit.getServer().broadcast(Component.text("Failed to run command! Please check the logs..."));
            return false;
        }

        return true;
    }
}
