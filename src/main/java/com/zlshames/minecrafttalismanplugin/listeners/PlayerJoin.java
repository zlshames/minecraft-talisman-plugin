package com.zlshames.minecrafttalismanplugin.listeners;

import com.zlshames.minecrafttalismanplugin.MinecraftTalismanPlugin;
import com.zlshames.minecrafttalismanplugin.database.models.TalismanPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    MinecraftTalismanPlugin plugin;

    public PlayerJoin(MinecraftTalismanPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        try {
            TalismanPlayer tPlayer = TalismanPlayer.findById(player.getUniqueId().toString());
            if (tPlayer == null) {
                System.out.println(player.getName());
                tPlayer = TalismanPlayer.createNew(player.getUniqueId().toString(), player.getName());
            }

            if (tPlayer == null) {
                System.err.println("Failed to create new player!");
            } else {
                String playerStr = tPlayer.toString();
                System.out.println("Player has joined! Player: " + playerStr);

                StringBuilder str = new StringBuilder();
                str.append("Welcome back, ").append(tPlayer.name).append("! ");
                str.append("Here are your stats: ");
                str.append(playerStr, playerStr.indexOf("("), playerStr.length() - 1);
                player.sendMessage(Component.text(str.toString()));
            }
        } catch (Exception ex) {
            System.err.println("Failed to find player by name!");
            System.err.println(ex.getMessage());
        }
    }
}
