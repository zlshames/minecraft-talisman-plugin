package com.zlshames.minecrafttalismanplugin;

import org.bukkit.plugin.java.JavaPlugin;
import com.zlshames.minecrafttalismanplugin.commands.RollD6;

public final class MinecraftTalismanPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        this.registerCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerCommands() {
        RollD6 rd6Instance = new RollD6();
        this.getCommand("rd6").setExecutor(rd6Instance);
        this.getCommand("rolld6").setExecutor(rd6Instance);
    }
}
