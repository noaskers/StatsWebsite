package nl.svendev.statsTracker.listeners;

import nl.svendev.statsTracker.database.StatsDAO;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AnimalBreedListener implements Listener {
    private final StatsDAO statsDAO;

    public AnimalBreedListener(JavaPlugin plugin, StatsDAO statsDAO) {
        this.statsDAO = statsDAO;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onAnimalBreed(EntityBreedEvent event) {
        if (event.getBreeder() instanceof Player player) {
            try {
                statsDAO.updatePlayerInfo(player.getUniqueId(), player.getName());
                statsDAO.incrementAnimalsBred(player.getUniqueId());
            } catch (Exception e) {
                player.getServer().getLogger().warning("Error tracking animal breed: " + e.getMessage());
                player.sendMessage("§cError tracking animal breed: " + e.getMessage());
            }
        }
    }
}