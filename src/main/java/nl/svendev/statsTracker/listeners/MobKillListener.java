package nl.svendev.statsTracker.listeners;

import nl.svendev.statsTracker.database.StatsDAO;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MobKillListener implements Listener {
    private final StatsDAO statsDAO;

    public MobKillListener(JavaPlugin plugin, StatsDAO statsDAO) {
        this.statsDAO = statsDAO;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            try {
                statsDAO.updatePlayerInfo(event.getEntity().getKiller().getUniqueId(),
                        event.getEntity().getKiller().getName());
                statsDAO.incrementMobsKilled(event.getEntity().getKiller().getUniqueId());
            } catch (Exception e) {
                event.getEntity().getKiller().getServer().getLogger()
                        .warning("Error tracking mob kill: " + e.getMessage());
                event.getEntity().getKiller().sendMessage("§cError tracking mob kill: " + e.getMessage());
            }
        }
    }
}