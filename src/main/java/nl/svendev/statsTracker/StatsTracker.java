package nl.svendev.statsTracker;

import nl.svendev.statsTracker.commands.StatsCommand;
import nl.svendev.statsTracker.database.DatabaseManager;
import nl.svendev.statsTracker.listeners.AnimalBreedListener;
import nl.svendev.statsTracker.listeners.BlockBreakListener;
import nl.svendev.statsTracker.listeners.MobKillListener;
import org.bukkit.plugin.java.JavaPlugin;

public class StatsTracker extends JavaPlugin {
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        databaseManager = new DatabaseManager(this);
        if (!databaseManager.initialize()) {
            getLogger().severe("Failed to initialize database! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new StatsCommand(this, databaseManager.getStatsDAO());

        new BlockBreakListener(this, databaseManager.getStatsDAO());
        new MobKillListener(this, databaseManager.getStatsDAO());
        new AnimalBreedListener(this, databaseManager.getStatsDAO());

        getLogger().info("StatsTracker has been enabled!");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
        getLogger().info("StatsTracker has been disabled!");
    }
}