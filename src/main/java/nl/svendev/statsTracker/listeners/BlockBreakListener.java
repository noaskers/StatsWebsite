package nl.svendev.statsTracker.listeners;

import nl.svendev.statsTracker.database.StatsDAO;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockBreakListener implements Listener {
    private final StatsDAO statsDAO;

    public BlockBreakListener(JavaPlugin plugin, StatsDAO statsDAO) {
        this.statsDAO = statsDAO;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        try {
            statsDAO.updatePlayerInfo(event.getPlayer().getUniqueId(), event.getPlayer().getName());
            statsDAO.incrementBlocksBroken(event.getPlayer().getUniqueId());
        } catch (Exception e) {
            event.getPlayer().getServer().getLogger().warning("Error tracking block break: " + e.getMessage());
            event.getPlayer().sendMessage("§cError tracking block break: " + e.getMessage());
        }
    }
}