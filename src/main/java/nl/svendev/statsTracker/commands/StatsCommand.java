package nl.svendev.statsTracker.commands;

import nl.svendev.statsTracker.database.StatsDAO;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class StatsCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final StatsDAO statsDAO;

    public StatsCommand(JavaPlugin plugin, StatsDAO statsDAO) {
        this.plugin = plugin;
        this.statsDAO = statsDAO;
        plugin.getCommand("stats").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            UUID targetUuid;

            if (args.length == 0) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cConsole must specify a player: /stats <player>");
                    return true;
                }
                targetUuid = ((Player) sender).getUniqueId();
            } else {
                String target = args[0];
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(target);

                if (!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline()) {
                    sender.sendMessage("§cPlayer not found: " + target);
                    return true;
                }
                targetUuid = offlinePlayer.getUniqueId();
            }

            String stats = statsDAO.getPlayerStats(targetUuid);
            if (stats != null) {
                sender.sendMessage(stats.split("\n"));
            } else {
                sender.sendMessage("§cNo stats found for that player.");
            }
        } catch (Exception e) {
            sender.sendMessage("§cError retrieving stats: " + e.getMessage());
            plugin.getLogger().warning("Error retrieving stats: " + e.getMessage());
            e.printStackTrace();
        }
        return true;
    }
}