package com.zlshames.minecrafttalismanplugin.commands;

import com.zlshames.minecrafttalismanplugin.MinecraftTalismanPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class DespawnTrader implements CommandExecutor {


    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        StringBuilder message = new StringBuilder();
        Boolean success = false;

        try {
            Bukkit.getLogger().info("De-spawning trader...");

            // De-Spawn an entity
            int count = 0;
            for(Entity e : ((Player) sender).getWorld().getEntities()){
                String name = e.getCustomName();
                if (name != null && name.equals("Zach the Trader")) {
                    e.remove();
                    count += 1;
                }
            }

            if (count == 0) {
                message.append("No traders to de-spawn!");
            } else {
                message.append(sender.getName()).append(" de-spawned ").append(count).append(" traders!");
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
            Bukkit.getServer().broadcast(Component.text(msg));
            return true;
        }

        // If the command failed, dispatch the failure back to the player
        sender.sendMessage(msg);
        return false;
    }
}
