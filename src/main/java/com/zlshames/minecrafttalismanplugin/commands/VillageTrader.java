package com.zlshames.minecrafttalismanplugin.commands;

import com.destroystokyo.paper.block.TargetBlockInfo;
import com.destroystokyo.paper.entity.Pathfinder;
import com.destroystokyo.paper.entity.TargetEntityInfo;
import com.zlshames.minecrafttalismanplugin.MinecraftTalismanPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.*;
import org.bukkit.loot.LootTable;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class VillageTrader implements CommandExecutor {

    public static final NamespacedKey TEMPORARY_BLACKLISTED = new NamespacedKey(MinecraftTalismanPlugin.getInstance(), "talismanTemporaryBlacklist");
    public static final NamespacedKey CONFIG_NAME = new NamespacedKey(MinecraftTalismanPlugin.getInstance(), "talismanConfig");
    public static final NamespacedKey PROTECT = new NamespacedKey(MinecraftTalismanPlugin.getInstance(), "talismanProtect");

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        StringBuilder message = new StringBuilder();
        Boolean success = false;

        try {
            Bukkit.getLogger().info("Spawning trader...");
            this.summonTrader(((Player) sender).getLocation(), true);
//            ((Player) sender).openMerchant(merchant, true);
            // Set the message to show
            message.append(sender.getName()).append(" spawned a trader!");
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

    private void summonTrader(Location loc, boolean disableAI) {
        final List<MerchantRecipe> recipes = new ArrayList<MerchantRecipe>();
        recipes.add(this.createMerchantRecipe(
                new ItemStack(Material.EMERALD, 1),
                new ItemStack(Material.GOLD_INGOT, 1),
                new ItemStack(Material.DIAMOND_SHOVEL, 1)
        ));

        loc.getWorld().spawn(loc, WanderingTrader.class, wanderingTrader -> {
            wanderingTrader.resetOffers();
            wanderingTrader.setRecipes(recipes);
            wanderingTrader.setAI(!disableAI);

            final PersistentDataContainer dataContainer = wanderingTrader.getPersistentDataContainer();
            dataContainer.set(TEMPORARY_BLACKLISTED, PersistentDataType.BYTE, (byte) 1);
            dataContainer.set(CONFIG_NAME, PersistentDataType.STRING, "Zach the Trader");

            final String customName = "Zach the Trader";
            if (customName != null && !customName.isEmpty() && !customName.equalsIgnoreCase("NONE")) {
                wanderingTrader.setCustomName(customName);
                wanderingTrader.setCustomNameVisible(true);
            }

            // Make him invincible
            wanderingTrader.setInvulnerable(true);
            wanderingTrader.setRemoveWhenFarAway(false);
            wanderingTrader.setPersistent(true);
            dataContainer.set(PROTECT, PersistentDataType.STRING, "true");
        });
    }

    private MerchantRecipe createMerchantRecipe(ItemStack buyItem1, ItemStack buyItem2, ItemStack sellingItem) {
        assert sellingItem.getAmount() > 0 && buyItem1.getAmount() > 0;
        MerchantRecipe recipe = new MerchantRecipe(sellingItem, 10000); // no max-uses limit
        recipe.setExperienceReward(false); // no experience rewards
        recipe.addIngredient(buyItem1);

        if (buyItem2.getAmount() > 0) {
            recipe.addIngredient(buyItem2);
        }

        return recipe;
    }
}
