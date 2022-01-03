package com.zlshames.minecrafttalismanplugin.listeners;

import com.zlshames.minecrafttalismanplugin.commands.SnowballFight;
import com.zlshames.minecrafttalismanplugin.utils.FighterContext;
import com.zlshames.minecrafttalismanplugin.utils.FighterStat;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

public class SnowballFightEvents implements Listener {
    private SnowballFight snowballFight;

    public SnowballFightEvents(SnowballFight fight) {
        this.snowballFight = fight;
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent projectileHitEvent) {
        // If it's not enabled, just don't do anything
        if (!snowballFight.active) return;
        Projectile projectile = projectileHitEvent.getEntity();
        if (projectile == null || !(projectile instanceof Snowball)) return;

        Entity entity = projectileHitEvent.getHitEntity();
        if (entity == null || entity.getType() != EntityType.PLAYER) return;

        Player player = (Player) entity;
        // Prevent players who aren't part of the fight from being hit
        if (!snowballFight.fighters.contains(player)) return;
        if (player.getGameMode() == GameMode.CREATIVE) return;

        FighterContext ctx = snowballFight.getStatsForFighter(player);
        ProjectileSource thrower = projectile.getShooter();
        FighterContext throwerCtx = (thrower != null && (thrower instanceof Player)) ?
                snowballFight.getStatsForFighter((Player) thrower) :
                null;

        boolean shouldTakeDamage = true;

        // If the damage will do more than 1 hearts worth, don't allow the damage, and "kill" the player
        if (player.getHealth() - this.snowballFight.snowballDamage <= 2.0) {
            ctx.pseudoKill(false);

            // Give the fighter a kill
            if (throwerCtx != null) {
                throwerCtx.incrementStat(FighterStat.SNOWBALL_KILLS);
            }

            shouldTakeDamage = false;
        }

        // If the subject is already dead, they shouldn't take any more damage
        if (ctx.didDie()) {
            shouldTakeDamage = false;

            // Send a message to the thrower saying they're already dead
            if (throwerCtx != null) {
                throwerCtx.getPlayer().sendMessage(
                        Component.text("Stop attacking " + player.getName() + ". They're already dead!"));
            }
        }

        // Check if the shot was a headshot
        boolean isHeadshot = this.snowballFight.headshot && player.getLocation().getY() - projectile.getLocation().getY() <= -1.45;

        // Increment stats for the thrower/shooter
        if (throwerCtx != null && shouldTakeDamage) {
            // Give the thrower a headshot
            if (isHeadshot) {
                throwerCtx.incrementStat(FighterStat.SNOWBALL_HEADSHOTS);
            }

            // Give the thrower a hit
            throwerCtx.incrementStat(FighterStat.SNOWBALL_HITS);
        }

        // Check if the snowball fight should end based on currently alive players
        if (snowballFight.shouldFightEnd()) {
            snowballFight.stopSnowballFight(player, "We have a winner!");
            shouldTakeDamage = false;
        }

        // If we don't want to take damage, return out
        if (!shouldTakeDamage) return;

        // Handle when we want to take damage
        player.damage(this.snowballFight.snowballDamage, projectile);
        player.setVelocity(projectile.getVelocity().multiply(this.snowballFight.velocityFactor));
        player.getWorld().spawnParticle(Particle.SNOWBALL, player.getLocation(), 150);

        // Handle headshot code
        if (isHeadshot) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 25, 1, true));
        }
    }

    // This is so fighters can't kill themselves on accident by falling or drowning.
    // Instead, they will be marked as dead and health is restored
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        // If it's not enabled, just don't do anything
        Player player = e.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;

        // Choose how we want to handle the death
        if (snowballFight.active && snowballFight.fighters.contains(player)) {
            // Cancel the event to the player doesn't actually die
            e.setCancelled(true);

            // Set the player as dead for the fight
            handleSnowballFighterDeath(player);
        }
    }

    // This is so fighters can't kill themselves on accident by falling or drowning.
    // Instead, they will be marked as dead and health is restored
    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        if (entity == null || entity.getType() != EntityType.PLAYER) return;
        Player player = (Player) entity;
        if (player.getGameMode() == GameMode.CREATIVE) return;

        // If it's not enabled, just don't do anything
        if (snowballFight.active && snowballFight.fighters.contains(player)) {
            double damage = e.getFinalDamage();
            if (player.getHealth() - damage <= 2.0) {
                // Cancel the event to the player doesn't actually take the damage.
                // Instead, they "die" in the snowball fight
                e.setCancelled(true);

                // Set the player as dead for the fight
                handleSnowballFighterDeath(player);
            }
        }
    }

    // This is so non-fighters or other entities can't damage a fighter while in a snowball fight.
    // The event is cancelled and no damage is taken
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        if (entity == null || entity.getType() != EntityType.PLAYER) return;
        Player player = (Player) entity;
        if (player.getGameMode() == GameMode.CREATIVE) return;

        // If there is an active snowball fight, and the entity is a fighter
        if (snowballFight.active && snowballFight.fighters.contains(player)) {
            // Make sure that no other players can damage the fighter, except other fighters
            Entity damager = e.getDamager();
            if (damager.getType() != EntityType.PLAYER || !snowballFight.fighters.contains((Player) damager)) {
                // Cancel the damage so the fighter doesn't take non-snowball damage
                e.setCancelled(true);
            }
        }
    }

    // A player should not be able to regain health during the fight
    @EventHandler
    public void onPlayerRegainHealth(EntityRegainHealthEvent e) {
        Entity entity = e.getEntity();
        if (entity == null || entity.getType() != EntityType.PLAYER) return;
        Player player = (Player) entity;

        if (snowballFight.active && snowballFight.fighters.contains(player) && !snowballFight.allowHealthRegen) {
            e.setCancelled(true);
        }
    }

    // If a player leaves mid-fight, they should be marked dead
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // If there is an active snowball fight, and the entity is a fighter
        if (snowballFight.active && snowballFight.fighters.contains(player)) {
            handleSnowballFighterDeath(player);
        }
    }

    private void handleSnowballFighterDeath(Player player) {
        FighterContext ctx = snowballFight.getStatsForFighter(player);
        ctx.pseudoKill(false);
    }
}
