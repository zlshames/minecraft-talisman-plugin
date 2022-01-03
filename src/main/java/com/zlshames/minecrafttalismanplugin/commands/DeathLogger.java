package com.zlshames.minecrafttalismanplugin.commands;

import com.zlshames.minecrafttalismanplugin.database.models.DeathLog;
import com.zlshames.minecrafttalismanplugin.database.models.Location;
import com.zlshames.minecrafttalismanplugin.utils.MsgTextColor;
import com.zlshames.minecrafttalismanplugin.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class DeathLogger implements CommandExecutor {

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        StringBuilder message = new StringBuilder();
        Boolean success;

        try {
            Player player = (Player) sender;
            DeathLog log = DeathLog.find(player.getUniqueId().toString());
            if (log == null) {
                message.append("You do not have any recent deaths!");
            } else {
                message.append(log);
            }

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
            sender.sendMessage(Component.text(msg, MsgTextColor.GREEN));
            return true;
        }

        // If the command failed, dispatch the failure back to the player
        sender.sendMessage(Component.text(msg, MsgTextColor.RED));
        return false;
    }
}
