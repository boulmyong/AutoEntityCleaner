package com.example;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AutoEntityCleaner extends JavaPlugin {

    private int cleanIntervalSeconds;
    private List<Class<? extends Entity>> entitiesToClean;
    private boolean warningsEnabled;
    private int countdown;

    // Message fields
    private String msgManualCleanStart;
    private String msgManualCleanFinish;
    private String msgNoPermission;
    private String msgWarning;

    private static final List<Integer> WARNING_TIMES = Arrays.asList(10, 5, 4, 3, 2, 1);

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();

        if (cleanIntervalSeconds > 0) {
            // Run the countdown timer every second (20 ticks)
            Bukkit.getScheduler().runTaskTimer(this, this::countdownTick, 0L, 20L);
            getLogger().info("Auto-cleaning of entities scheduled every " + cleanIntervalSeconds + " seconds.");
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
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msgManualCleanStart));
                int cleanedCount = cleanEntities();
                String finalMessage = msgManualCleanFinish.replace("{count}", String.valueOf(cleanedCount));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', finalMessage));
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msgNoPermission));
            }
            return true;
        }
        return false;
    }

    private void loadConfigValues() {
        FileConfiguration config = getConfig();
        // Interval is in ticks, convert to seconds for our logic
        this.cleanIntervalSeconds = config.getInt("clean-interval", 6000) / 20;
        this.countdown = this.cleanIntervalSeconds;

        List<String> entityNames = config.getStringList("entities-to-clean");
        this.entitiesToClean = entityNames.stream()
                .map(name -> {
                    try {
                        EntityType type = EntityType.valueOf(name.toUpperCase());
                        return type.getEntityClass();
                    } catch (IllegalArgumentException e) {
                        getLogger().warning("Invalid entity type in config.yml: " + name);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        this.warningsEnabled = config.getBoolean("warnings.enabled", true);

        // Load messages based on language setting
        String lang = config.getInt("language", 1) == 1 ? "ko" : "en";
        this.msgManualCleanStart = config.getString("messages.manual_clean_start." + lang);
        this.msgManualCleanFinish = config.getString("messages.manual_clean_finish." + lang);
        this.msgNoPermission = config.getString("messages.no_permission." + lang);
        this.msgWarning = config.getString("messages.warning." + lang);
    }

    private void countdownTick() {
        if (warningsEnabled && WARNING_TIMES.contains(countdown)) {
            String message = msgWarning.replace("{time}", String.valueOf(countdown));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

        if (countdown <= 0) {
            runCleaningTask();
            this.countdown = this.cleanIntervalSeconds; // Reset countdown
        } else {
            countdown--;
        }
    }

    private void runCleaningTask() {
        getLogger().info("Starting automatic entity cleaning task...");
        long startTime = System.currentTimeMillis();
        int cleanedCount = cleanEntities();
        long endTime = System.currentTimeMillis();
        getLogger().info("Finished automatic entity cleaning task. Cleaned " + cleanedCount + " entities in " + (endTime - startTime) + "ms.");
    }

    private int cleanEntities() {
        int totalCleanedCount = 0;
        if (entitiesToClean.isEmpty()) {
            return 0;
        }

        for (World world : Bukkit.getWorlds()) {
            int worldCleanedCount = 0;
            @SuppressWarnings("unchecked")
            Class<? extends Entity>[] entityClasses = entitiesToClean.toArray(new Class[0]);
            Collection<Entity> entities = world.getEntitiesByClasses(entityClasses);

            for (Entity entity : entities) {
                entity.remove();
                worldCleanedCount++;
            }

            if (worldCleanedCount > 0) {
                getLogger().info("Cleaned " + worldCleanedCount + " entities from world: " + world.getName());
            }
            totalCleanedCount += worldCleanedCount;
        }
        return totalCleanedCount;
    }
}
