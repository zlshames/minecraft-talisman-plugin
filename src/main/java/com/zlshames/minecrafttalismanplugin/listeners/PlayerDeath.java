package com.zlshames.minecrafttalismanplugin.listeners;

import com.zlshames.minecrafttalismanplugin.database.models.DeathLog;
import com.zlshames.minecrafttalismanplugin.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.time.Instant;
import java.util.Date;
import java.util.logging.Level;

public class PlayerDeath implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        try {
            Player player = event.getPlayer();
            Location local = player.getLocation();
            Date time = Date.from(Instant.ofEpochMilli(Utils.getEpochMillis()));
            DeathLog.add(player.getUniqueId().toString(), local.getX(), local.getY(), local.getZ(), time);
        } catch (Exception ex) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to save last player death!");
            Bukkit.getLogger().log(Level.WARNING, ex.getMessage());
        }
    }
}
