package nl.svendev.statsTracker.database;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.*;
import java.util.UUID;

public class StatsDAO {
    private final HikariDataSource dataSource;

    public StatsDAO(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createTables() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS player_stats (" +
                    "uuid VARCHAR(36) PRIMARY KEY," +
                    "username VARCHAR(16) NOT NULL," +
                    "total_blocks_broken INT DEFAULT 0," +
                    "total_mobs_killed INT DEFAULT 0," +
                    "total_animals_bred INT DEFAULT 0," +
                    "last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)");
        }
    }

    public void updatePlayerInfo(UUID uuid, String username) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO player_stats (uuid, username) VALUES (?, ?) " +
                             "ON DUPLICATE KEY UPDATE username = ?")) {

            ps.setString(1, uuid.toString());
            ps.setString(2, username);
            ps.setString(3, username);
            ps.executeUpdate();
        }
    }

    public void incrementBlocksBroken(UUID uuid) throws SQLException {
        updateStat(uuid, "total_blocks_broken");
    }

    public void incrementMobsKilled(UUID uuid) throws SQLException {
        updateStat(uuid, "total_mobs_killed");
    }

    public void incrementAnimalsBred(UUID uuid) throws SQLException {
        updateStat(uuid, "total_animals_bred");
    }

    private void updateStat(UUID uuid, String column) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE player_stats SET " + column + " = " + column + " + 1 WHERE uuid = ?")) {

            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        }
    }

    public String getPlayerStats(UUID uuid) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT username, total_blocks_broken, total_mobs_killed, total_animals_bred " +
                             "FROM player_stats WHERE uuid = ?")) {

            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                return "§6Stats for §e" + rs.getString("username") + "§6:\n" +
                        "§bTotal Blocks Broken: §a" + rs.getInt("total_blocks_broken") + "\n" +
                        "§bTotal Mobs Killed: §a" + rs.getInt("total_mobs_killed") + "\n" +
                        "§bTotal Animals Bred: §a" + rs.getInt("total_animals_bred");
            }
        }
    }
}