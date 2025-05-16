package nl.svendev.statsTracker.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import nl.svendev.statsTracker.StatsTracker;
import java.sql.SQLException;

public class DatabaseManager {
    private final StatsTracker plugin;
    private HikariDataSource dataSource;
    private StatsDAO statsDAO;

    public DatabaseManager(StatsTracker plugin) {
        this.plugin = plugin;
    }

    public boolean initialize() {
        String dbHost = plugin.getConfig().getString("database.host");
        int dbPort = plugin.getConfig().getInt("database.port");
        String dbName = plugin.getConfig().getString("database.name");
        String dbUser = plugin.getConfig().getString("database.user");
        String dbPass = plugin.getConfig().getString("database.password");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName);
        config.setUsername(dbUser);
        config.setPassword(dbPass);

        // HikariCP optimization settings
        config.setMaximumPoolSize(plugin.getConfig().getInt("connection-pool.maximum-pool-size", 10));
        config.setMinimumIdle(plugin.getConfig().getInt("connection-pool.minimum-idle", 5));
        config.setConnectionTimeout(plugin.getConfig().getLong("connection-pool.connection-timeout", 30000));
        config.setIdleTimeout(plugin.getConfig().getLong("connection-pool.idle-timeout", 600000));
        config.setMaxLifetime(plugin.getConfig().getLong("connection-pool.max-lifetime", 1800000));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");

        try {
            dataSource = new HikariDataSource(config);
            statsDAO = new StatsDAO(dataSource);
            statsDAO.createTables();

            plugin.getLogger().info("HikariCP connection pool initialized successfully!");
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to initialize HikariCP: " + e.getMessage());
            return false;
        }
    }

    public StatsDAO getStatsDAO() {
        return statsDAO;
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            plugin.getLogger().info("Database connection pool closed successfully");
        }
    }
}