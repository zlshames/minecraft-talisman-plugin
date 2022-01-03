package com.zlshames.minecrafttalismanplugin.utils;

import com.zlshames.minecrafttalismanplugin.MinecraftTalismanPlugin;
import com.zlshames.minecrafttalismanplugin.database.models.PlayerStat;
import com.zlshames.minecrafttalismanplugin.exceptions.StatError;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

public class FighterContext {

    Logger logger;

    Player player;

    Map<String, Object> stats = new HashMap<>();

    ItemStack[] inventory;

    ItemStack[] armour;

    ItemStack[] extraItems;

    float startingExp;

    boolean restored;

    boolean isDead;

    public FighterContext(Player player) {
        this.logger = JavaPlugin.getPlugin(MinecraftTalismanPlugin.class).getLogger();
        this.player = player;
        this.restored = false;
        this.isDead = false;

        PlayerInventory inventory = player.getInventory();
        this.inventory = inventory.getStorageContents();
        this.armour = inventory.getArmorContents();
        this.extraItems = inventory.getExtraContents();
        this.startingExp = player.getExp();
        initStats();
    }

    public Player getPlayer() {
        return player;
    }

    public void pseudoKill(boolean restoreInventory) {
        // Mark player as dead
        this.isDead = true;

        Bukkit.getServer().broadcast(Component.text(player.getName() + " has died in the snowball fight!"));
        player.sendMessage("Oh dear, it seems you've died! Your items will be restored when the fight is over.");

        // Clear their inventory
        player.getInventory().clear();

        // Restore all the player values
        restorePlayer(restoreInventory);

        // Set the player's loss
        incrementStat(FighterStat.SNOWBALL_LOSSES);
    }

    public void restorePlayer(boolean restoreInventory) {
        // Give back their health
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        player.setHealth(attribute.getBaseValue());

        // Give back food
        player.setFoodLevel(20);

        // Give back inventory
        if (restoreInventory) this.restoreInventory();

        // Give back original XP
        player.setExp(startingExp);
    }

    public boolean didDie() {
        return this.isDead;
    }

    public Map<String, Object> getStats() {
        return stats;
    }

    public void initStats() {
        for (String stat : FighterStat.all()) {
            setStat(stat, null);
        }
    }

    public void restoreInventory() {
        PlayerInventory inventory = player.getInventory();
        inventory.setStorageContents(this.inventory);
        inventory.setArmorContents(this.armour);
        inventory.setExtraContents(this.extraItems);
        this.restored = true;
    }

    public void setStat(String type, Object value) {
        stats.put(type, value);
    }

    public Integer getStatInt(String type) throws StatError {
        if (!stats.containsKey(type)) return null;
        Object value = stats.get(type);
        if (value == null) return null;
        if (!Utils.isStringInt(value.toString())) {
            throw new StatError("Stat is not an integer!");
        }

        return Integer.parseInt(value.toString());
    }

    public String getStatStr(String type) {
        if (!stats.containsKey(type)) return null;
        Object output = stats.get(type);
        return (output == null) ? null : output.toString();
    }

    public void saveStats() {
        String uuid = player.getUniqueId().toString();
        for (String stat : FighterStat.all()) {
            try {
                boolean override = false;
                boolean increment = false;

                // If the stat doesn't exist, create it
                Integer currentValue = null;
                if (!PlayerStat.statExists(uuid, stat)) {
                    override = true;
                } else {
                    // If the stat exists, get its value
                    PlayerStat statItem = PlayerStat.getPlayerStat(uuid, stat);
                    // If the value is null, just set the value
                    if (statItem == null) {
                        override = true;
                    } else if (Utils.isStringInt(statItem.value)) {
                        increment = true;
                        currentValue = Integer.parseInt(statItem.value);
                    } else {
                        override = true;
                    }
                }

                try {
                    if (increment && currentValue != null) {
                        Integer val = getStatInt(stat);
                        if (val == null) val = 0;
                        PlayerStat.setPlayerStat(uuid, stat, String.valueOf(val + currentValue));
                    }
                } catch (StatError se) {
                    override = true;
                }

                if (override) {
                    PlayerStat.setPlayerStat(uuid, stat, getStatStr(stat));
                }
            } catch (SQLException se) {
                this.logger.log(Level.WARNING, "Failed to save stat: " + se.getMessage());
            }
        }
    }

    public void incrementStat(String type) {
        Integer value;

        try {
            value = getStatInt(type);
            if (value == null) value = 0;
        } catch (StatError se) {
            value = 0;
        }

        stats.put(type, String.valueOf(value + 1));
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Fight Stats:").append("\n");

        // Getting an iterator
        Iterator iter = stats.entrySet().iterator();

        // Iterating through the map and build the output
        while (iter.hasNext()) {
            Map.Entry<String, Object> mapElement = (Map.Entry) iter.next();
            if (mapElement.getValue() == null) continue;
            builder.append(mapElement.getKey()).append(": ").append(mapElement.getValue().toString());
            builder.append("\n");
        }

        return builder.toString();
    }
}
