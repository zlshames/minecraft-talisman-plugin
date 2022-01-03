package com.zlshames.minecrafttalismanplugin.commands;

import com.zlshames.minecrafttalismanplugin.exceptions.AdminError;
import com.zlshames.minecrafttalismanplugin.utils.FighterContext;
import com.zlshames.minecrafttalismanplugin.utils.FighterStat;
import com.zlshames.minecrafttalismanplugin.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SnowballFight implements CommandExecutor {

    public boolean active = false;
    public List<Player> fighters = new ArrayList();
    public List<FighterContext> fighterStats = new ArrayList<>();

    public double velocityFactor;
    public double snowballDamage;
    public boolean headshot;
    public boolean allowHealthRegen;
    public ItemStack prize;
    public Integer prizeExp;

    public SnowballFight(JSONObject settings) {
        try {
            this.velocityFactor = (double) settings.get("velocity_factor");
            this.headshot = (boolean) settings.get("headshot");
            this.snowballDamage = (double) settings.get("snowball_damage");
        } catch (Exception e) {
            this.velocityFactor = 0.3;
            this.headshot = true;
            this.snowballDamage = 0.5;
            System.out.println("[SnowballFight] Problem found in config file! Using default settings.");
        }

        this.allowHealthRegen = false;
        this.prize = null;
        this.prizeExp = 0;
    }

    public double getVelocityFactor() {
        return velocityFactor;
    }

    public void setVelocityFactor(double velocityFactor) {
        this.velocityFactor = velocityFactor;
    }

    public void setSnowballDamage(double damage) {
        this.snowballDamage = snowballDamage;
    }

    public boolean isHeadshot() {
        return headshot;
    }

    public void setHeadshot(boolean headshot) {
        this.headshot = headshot;
    }

    public void setAllowHealthRegen(boolean allowHealthRegen) {
        this.allowHealthRegen = allowHealthRegen;
    }

    public void setPrize(String input, int amount) {
        String saniInput = input.toLowerCase();
        if (saniInput.equals("exp") || saniInput.equals("experience") || saniInput.equals("xp")) {
            this.prizeExp = amount;
        } else {
            Material m = Material.matchMaterial(input);
            this.prize = new ItemStack(m, amount);
        }
    }

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        if (!sender.isOp()) return false;

        StringBuilder message = new StringBuilder();
        boolean success = false;

        try {
            switch (command.getName()) {
                case "snowball-fight-all":
                    pickEveryone((Player) sender);
                    message.append("Selected the following fighters: ").append(this.getFighterNames());
                    break;
                case "snowball-fight-random":
                    pickRandom((Player) sender, args.length > 0 ? Integer.parseInt(args[0]) : 2);
                    message.append("Selected the following fighters: ").append(this.getFighterNames());
                    break;
                case "snowball-fight-select":
                    setFighters((Player) sender, args);
                    message.append("Selected the following fighters: ").append(this.getFighterNames());
                    break;
                case "snowball-fight-reset":
                    this.fighters = new ArrayList();
                    if (this.active) {
                        this.active = false;
                    }

                    message.append("Snowball fight participants have been cleared");
                    break;
                case "snowball-fight-set-damage":
                    double newDamage = Double.parseDouble(args[0]);
                    newDamage = newDamage <= 0 ? 0.5 : newDamage;
                    this.setSnowballDamage(newDamage);

                    message.append("Snowball damage set to: ").append(newDamage);
                    break;
                case "snowball-fight-set-health-regen":
                    boolean regenPolicy = Boolean.parseBoolean(args[0]);
                    this.setAllowHealthRegen(regenPolicy);

                    message.append(
                            "Snowball fight health regeneration is now: " + ((regenPolicy) ? "enabled" : "disabled"));
                    break;
                case "snowball-fight-set-prize":
                    this.prize = null;
                    this.prizeExp = 0;

                    String item = args[0];
                    Integer amount = 0;
                    if (args.length > 1 && Utils.isStringInt(args[1])) {
                        amount = Integer.parseInt(args[1]);
                    }

                    setPrize(item, amount);

                    if (prize != null) {
                        message.append("Snowball fight prize item: ").append(prize.getType().toString());
                        message.append(" (").append("Amount: ").append(prize.getAmount()).append(")");
                    } else if (prizeExp != null && prizeExp != 0.0) {
                        message.append("Snowball fight prize XP: ").append(prizeExp);
                    }
                    break;
                case "start-snowball-fight":
                    if (this.active) {
                        throw new AdminError("A snowball fight is already active!");
                    } else if (this.fighters.size() == 0) {
                        throw new AdminError("No fighters have been selected to take part in the snowball fight!");
                    }
//                    } else if (this.fighters.size() == 1) {
//                        throw new AdminError("You can't just have a snowball fight with one fighter!");
//                    }

                    beginSnowballFight((Player) sender);
                    message.append("Let the snowball fight begin!");
                    break;
                case "stop-snowball-fight":
                    if (!this.active) {
                        throw new AdminError("Sorry, I can't stop a snowball fight that hasn't been started!");
                    }

                    stopSnowballFight((Player) sender, null);
                    message.append("The snowball fight has concluded...");
                    break;
            }

            success = true;
        } catch (AdminError ex) {
            message.append(ex.getMessage());
        } catch (Exception ex) {
            // Log the error to the console
            Bukkit.getLogger().log(Level.SEVERE, "An error has occurred");
            Bukkit.getLogger().log(Level.SEVERE, ex.getMessage());

            // Set the message to show
            message.append("Failed to run command! " + ex.getMessage());
        }

        // Dispatch the message back to the chat
        String msg = message.toString();
        if (success) {
            if (!msg.isEmpty()) {
                Bukkit.getServer().broadcast(Component.text(msg));
            }

            return true;
        }

        // If the command failed, dispatch the failure back to the player
        sender.sendMessage(msg);
        return false;
    }

    public void beginSnowballFight(Player sender) {
        // Make sure it's not active while we initialize it
        this.active = false;

        // Clear player stats
        fighterStats.clear();

        for (Player p : this.fighters) {
            // Set everyone's health to 100
            setPlayerHealth(p, null);

            // Set everyone's food to 1 less than max (to prevent healing)
            setPlayerFood(p, 18);

            // Init all the player stats
            // This includes saving the current inventory
            fighterStats.add(new FighterContext(p));

            // Clear inventory
            p.getInventory().clear();

            // Give snowballs
            int base = (64 * 9);
            float amount = Utils.clamp(base * fighters.size() - 1, base, base * 4);
            p.getInventory().addItem(new ItemStack(Material.SNOWBALL, (int) amount));
        }

        // Start the fight
        this.active = true;
    }

    public void stopSnowballFight(Player sender, String reason) {
        // Stop the fight
        this.active = false;

        // Compute winners
        List<FighterContext> winners = new ArrayList<>();

        // Restore everything
        boolean legitFighters = fighters.size() > 1;
        for (FighterContext ctx : fighterStats) {
            Player player = ctx.getPlayer();

            // Add them to the winners if they didn't die
            if (!ctx.didDie() && legitFighters) {
                winners.add(ctx);

                // Increment player wins
                try {
                    ctx.incrementStat(FighterStat.SNOWBALL_WINS);
                } catch (Exception ex) {
                    // Don't do anything
                }

                // Give prize to winner
                if (prize != null) {
                    player.getInventory().addItem(prize);
                }

                if (prizeExp != null && prizeExp != 0.0) {
                    player.giveExp(prizeExp);
                }
            }

            // Clear inventory
            player.getInventory().clear();

            // Restore player items & xp
            ctx.restorePlayer(true);
        }

        StringBuilder output = new StringBuilder();
        output.append("Snowball fight has ended!");
        if (reason != null) {
            output.append(" Reason: ").append(reason);
        }

        // Let everyone know the fight is over
        Bukkit.getServer().broadcast(Component.text(output.toString()));

        // List all the winners
        if (winners.size() > 0) {
            List<String> winnerList = new ArrayList<>();
            for (FighterContext fc : winners) {
                Object value = fc.getStats().get(FighterStat.SNOWBALL_KILLS);
                int kills = 0;
                if (value != null && Utils.isStringInt(value.toString())) {
                    kills = Integer.parseInt(value.toString());
                }

                StringBuilder substr = new StringBuilder();
                substr.append(fc.getPlayer().getName());
                substr.append(" (Kills: ").append(kills);
                substr.append(")");

                winnerList.add(substr.toString());
            }

            String winnerStr = winnerList.stream().collect(Collectors.joining(", "));
            Bukkit.getServer().broadcast(Component.text("Winner(s): " + winnerStr));

            String earnings = null;
            if (prize != null) {
                earnings = String.valueOf(prize.getAmount()) + " " + prize.getType();
            } else if (prizeExp != null && prizeExp != 0) {
                earnings = String.valueOf(prizeExp) + " Experience";
            }

            if (earnings != null) {
                Bukkit.getServer().broadcast(Component.text("Winners received: " + earnings));
            }
        } else {
            Bukkit.getServer().broadcast(Component.text("Tie! Everyone died!"));
        }

        // Tell everyone how they did
        for (FighterContext ctx : fighterStats) {
            Player player = ctx.getPlayer();
            player.sendMessage(Component.text(ctx.toString()));

            // Also, save everyone's stats
            ctx.saveStats();
        }
    }

    public boolean shouldFightEnd() {
        if (!active) return true;

        int totalFighters = fighterStats.size();
        for (FighterContext ctx : fighterStats) {
            // Add them to the winners if they didn't die
            if (ctx.didDie()) {
                totalFighters -= 1;
            }
        }

        return totalFighters <= 1;

    }

    public FighterContext getStatsForFighter(Player player) {
        for (FighterContext stats : fighterStats) {
            if (player.getUniqueId().toString().equals(stats.getPlayer().getUniqueId().toString())) {
                return stats;
            }
        }

        return null;
    }

    public void setPlayerHealth(Player player, Double value) {
        if (value != null) {
            player.setHealth(value);
        } else {
            AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            player.setHealth(attribute.getBaseValue());
        }
    }

    public void setPlayerFood(Player player, Integer value) {
        if (value != null) {
            player.setFoodLevel(value);
        } else {
            player.setFoodLevel(20);
        }
    }

    public void pickEveryone(Player sender) {
        this.fighters = sender.getWorld().getPlayers();
    }

    public void pickRandom(Player sender, Integer count) throws Exception {
        List<Player> players = getPlayers(sender);
        if (players.size() < count) {
            throw new Exception("There are not enough players on the server to pick fighters!");
        }

        // Pick random integers
        List<Integer> used = new ArrayList();
        while (used.size() < count) {
            Random rand = new Random();
            Integer randIdx = rand.nextInt(players.size());

            // If the index is already chosen
            if (!used.contains(randIdx)) {
                used.add(randIdx);
            }
        }

        // Set the fighters based on selected indexes
        this.fighters = IntStream.range(0, players.size())
                .filter(i -> used.contains(i))
                .mapToObj(players::get)
                .collect(Collectors.toList());
    }

    public void setFighters(Player sender, String[] args) {
        List<Player> players = getPlayers(sender);
        List<String> selected = Arrays.asList(args);
        players = players.stream()
                .filter(p -> selected.contains(p.getName())).collect(Collectors.toList());
        this.fighters = players;
    }

    public List<Player> getPlayers(Player sender) {
        return sender.getWorld().getPlayers();
    }

    public Player getPlayerByName(Player sender, String name) {
        List<Player> players = getPlayers(sender);
        players = players.stream()
                .filter(p -> p.getName().toLowerCase().equals(name.toLowerCase())).collect(Collectors.toList());
        return (players.size() > 0) ? players.get(0) : null;
    }

    public String getFighterNames() {
        return this.fighters.stream().map(HumanEntity::getName)
                .collect(Collectors.joining(", "));
    }
}
