package com.zlshames.minecrafttalismanplugin.commands;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RollD6 implements CommandExecutor {

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        StringBuilder message = new StringBuilder();
        Boolean success = false;

        try {
            Bukkit.getLogger().info("Rolling a six-sided dice...");

            // Generate the random number
            int randomNum = ThreadLocalRandom.current().nextInt(1, 6 + 1);
            Bukkit.getLogger().info("Roll: ".format(String.valueOf(randomNum)));

            // Set the message to show
            message.append(sender.getName()).append(" rolled a ").append(randomNum).append("!");
            success = true;
        } catch (Exception ex) {
            // Log the error to the console
            Bukkit.getLogger().log(Level.SEVERE, "An error has occurred");
            Bukkit.getLogger().log(Level.SEVERE, ex.getMessage());

            // Set the message to show
            message.append("Failed to run command! Please contact the server administrator.");
            success = false;
        }

        // Dispatch the message back to the chat
        String msg = message.toString();
        if (success) {
            Bukkit.getServer().broadcast(Component.text(msg));
            return true;
        }

        // If the command failed, dispatch the failure back to the player
        sender.sendMessage(msg);
        return false;
    }
}
