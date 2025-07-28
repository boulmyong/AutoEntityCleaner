package com.example;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

public class AutoEntityCleaner extends JavaPlugin {

    private int cleanInterval;
    private List<EntityType> entitiesToClean;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();

        if (cleanInterval > 0) {
            Bukkit.getScheduler().runTaskTimer(this, this::cleanEntities, cleanInterval, cleanInterval);
            getLogger().info("Auto-cleaning of entities scheduled every " + (cleanInterval / 20) + " seconds.");
        } else {
            getLogger().info("Auto-cleaning of entities is disabled.");
        }

        getLogger().info("AutoEntityCleaner has been enabled!");
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        getLogger().info("AutoEntityCleaner has been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("cleanentities")) {
            if (sender.hasPermission("autocleaner.clean")) {
                int cleanedCount = cleanEntities();
                sender.sendMessage(ChatColor.GREEN + "Successfully cleaned " + cleanedCount + " entities.");
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
            return true;
        }
        return false;
    }

    private void loadConfigValues() {
        this.cleanInterval = getConfig().getInt("clean-interval", 6000);
        List<String> entityNames = getConfig().getStringList("entities-to-clean");
        this.entitiesToClean = entityNames.stream()
                .map(name -> {
                    try {
                        return EntityType.valueOf(name.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        getLogger().warning("Invalid entity type in config.yml: " + name);
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    private int cleanEntities() {
        int cleanedCount = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entitiesToClean.contains(entity.getType())) {
                    entity.remove();
                    cleanedCount++;
                }
            }
        }
        if (cleanedCount > 0) {
            getLogger().info("Cleaned " + cleanedCount + " entities.");
        }
        return cleanedCount;
    }
}
