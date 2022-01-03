package com.zlshames.minecrafttalismanplugin;

import com.zlshames.minecrafttalismanplugin.commands.*;
import com.zlshames.minecrafttalismanplugin.database.DatabaseConnection;
import com.zlshames.minecrafttalismanplugin.listeners.PlayerDeath;
import com.zlshames.minecrafttalismanplugin.listeners.PlayerJoin;
import com.zlshames.minecrafttalismanplugin.listeners.SnowballFightEvents;
import com.zlshames.minecrafttalismanplugin.utils.JsonFile;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;

public final class MinecraftTalismanPlugin extends JavaPlugin {

    private static MinecraftTalismanPlugin instance;
    public static MinecraftTalismanPlugin getInstance() {
        return instance;
    }

    public DatabaseConnection db = new DatabaseConnection();
    private JsonFile jsonFile = new JsonFile(
            this.getDataFolder().getAbsolutePath() + File.separator + "config.json");
    private SnowballFight fight;

    @Override
    public void onEnable() {
        instance = this;

        // Connect to the database
        db.connect();

        // Load settings from the config file
        JSONObject settings = this.loadConfig();

        // Listen for events
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);

        // Listen for snowball fight related things
        fight = new SnowballFight(settings);
        Bukkit.getPluginManager().registerEvents(new SnowballFightEvents(fight), this);

        // Register slash commands
        this.registerCommands();
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    private void registerCommands() {
        RollD6 rd6Instance = new RollD6();
        VillageTrader traderInstance = new VillageTrader();
        DespawnTrader despawnTraderInstance = new DespawnTrader();
        LocationSaver locationSaverInstance = new LocationSaver();
        DeathLogger deathLogger = new DeathLogger();
        this.getCommand("trade").setExecutor(traderInstance);
        this.getCommand("trade-finished").setExecutor(despawnTraderInstance);
        this.getCommand("rd6").setExecutor(rd6Instance);
        this.getCommand("rolld6").setExecutor(rd6Instance);

        // Death stuff
        this.getCommand("last-death").setExecutor(deathLogger);

        // Location stuff
        this.getCommand("save-location").setExecutor(locationSaverInstance);
        this.getCommand("get-location").setExecutor(locationSaverInstance);
        this.getCommand("get-locations").setExecutor(locationSaverInstance);
        this.getCommand("remove-location").setExecutor(locationSaverInstance);
        this.getCommand("get-locations-global").setExecutor(locationSaverInstance);
        this.getCommand("save-location-global").setExecutor(locationSaverInstance);
        this.getCommand("remove-location-global").setExecutor(locationSaverInstance);

        // Snowball fight stuff
        this.getCommand("snowball-fight-all").setExecutor(this.fight);
        this.getCommand("snowball-fight-random").setExecutor(this.fight);
        this.getCommand("snowball-fight-select").setExecutor(this.fight);
        this.getCommand("start-snowball-fight").setExecutor(this.fight);
        this.getCommand("stop-snowball-fight").setExecutor(this.fight);
        this.getCommand("snowball-fight-set-damage").setExecutor(this.fight);
        this.getCommand("snowball-fight-set-health-regen").setExecutor(this.fight);
        this.getCommand("snowball-fight-set-prize").setExecutor(this.fight);
    }

    private JSONObject loadConfig() {
        JSONObject settings = new JSONObject();

        try {
            settings = this.jsonFile.readJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return settings;
    }
}
