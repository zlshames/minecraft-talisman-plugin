package com.zlshames.minecrafttalismanplugin.commands;

import com.zlshames.minecrafttalismanplugin.database.models.Location;
import com.zlshames.minecrafttalismanplugin.utils.MsgTextColor;
import com.zlshames.minecrafttalismanplugin.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.swing.text.Style;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class LocationSaver implements CommandExecutor {

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        StringBuilder message = new StringBuilder();
        Boolean success = false;
        Boolean respondGlobal = false;

        try {
            switch (command.getName()) {
                case "save-location":
                    if (args.length < 1) {
                        message.append("Failed to save location! No name provided!");
                        success = false;
                    } else {
                        // Save location to the database for the user
                        String n = Utils.stringListToSingleString(args);
                        Player p = (Player) sender;
                        org.bukkit.@NotNull Location local = p.getLocation();
                        Location.createNew(p.getUniqueId().toString(), n, local.getX(), local.getY(), local.getZ());

                        // Set the message to show
                        message.append("Successfully saved location!");
                        success = true;
                    }
                    break;
                case "get-location":
                    if (args.length < 1) {
                        message.append("Failed to get location! No name provided!");
                        success = false;
                    } else {
                        // Save location to the database for the user
                        String name = Utils.stringListToSingleString(args);
                        Player p1 = (Player) sender;
                        Location local = Location.findByName(p1.getUniqueId().toString(), name);
                        message.append(local.toString());
                        success = true;
                    }
                    break;
                case "get-locations":
                    // Get all locations for the user
                    Player p2 = (Player) sender;
                    List<Location> locals = Location.findAllForUser(p2.getUniqueId().toString());
                    if (locals.size() == 0) {
                        message.append("You have no saved locations!");
                    } else {
                        String msg = locals.stream().map(Location::toString).collect(Collectors.joining("\n"));
                        message.append("Here are your saved locations:\n");
                        message.append(msg);
                    }
                    success = true;
                    break;
                case "get-locations-global":
                    // Get all locations for the user
                    List<Location> locals2 = Location.findAllGlobal();
                    if (locals2.size() == 0) {
                        message.append("There are no globally saved locations!");
                    } else {
                        String msg = locals2.stream().map(Location::toString).collect(Collectors.joining("\n"));
                        message.append("Here are the globally saved locations:\n");
                        message.append(msg);
                    }
                    success = true;
                    break;
                case "save-location-global":
                    if (args.length < 1) {
                        message.append("Failed to save global location! No name provided!");
                        success = false;
                    } else {
                        // Save location to the database for the user
                        String n = Utils.stringListToSingleString(args);
                        Player p = (Player) sender;
                        org.bukkit.@NotNull Location local = p.getLocation();
                        Location.createNewGlobal(n, local.getX(), local.getY(), local.getZ());
                        Location newLocal = new Location(n, local.getX(), local.getY(), local.getZ());

                        // Set the message to show
                        message.append(p.getName()).append(" saved a global location! \n    -> ").append(newLocal);
                        respondGlobal = true;
                        success = true;
                    }
                    break;
                case "remove-location":
                    if (args.length < 1) {
                        message.append("Failed to remove location! No name provided!");
                        success = false;
                    } else {
                        // Delete the location for the user
                        String name2 = Utils.stringListToSingleString(args);
                        Player p3 = (Player) sender;
                        Location.remove(p3.getUniqueId().toString(), name2);
                        message.append("Successfully removed location!");
                        success = true;
                    }
                    break;
                case "remove-location-global":
                    Player p3 = (Player) sender;
                    if (!p3.isOp()) {
                        message.append("Only admins can remove a global location! Please contact them to remove it.");
                        success = false;
                    } else if (args.length < 1) {
                        message.append("Failed to remove global location! No name provided!");
                        success = false;
                    } else {
                        // Delete the location for the user
                        String name2 = Utils.stringListToSingleString(args);
                        Location.removeGlobal(name2);
                        message.append(p3.getName()).append(" removed a global location!\n    -> ").append(name2);
                        success = true;
                        respondGlobal = true;
                    }
                    break;
            }
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
            if (respondGlobal) {
                Bukkit.getServer().broadcast(Component.text(msg, MsgTextColor.ORANGE));
            } else {
                sender.sendMessage(Component.text(msg, MsgTextColor.GREEN));
            }

            return true;
        }

        // If the command failed, dispatch the failure back to the player
        sender.sendMessage(Component.text(msg, MsgTextColor.RED));
        return false;
    }
}
