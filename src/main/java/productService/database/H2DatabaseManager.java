package productService.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

import java.sql.Statement;

public
class H2DatabaseManager {
    //private static final String JDBC_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String JDBC_URL = "jdbc:h2:file:./data/testdb;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private final HikariDataSource dataSource;

    public H2DatabaseManager() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(JDBC_URL);
        config.setUsername(USER);
        config.setPassword(PASSWORD);
        config.setMaximumPoolSize(10);
        // Adjust as needed
        config.setMinimumIdle(5);
        config.setConnectionTimeout(10000); // 10 seconds

        dataSource = new HikariDataSource(config);

        try {
            initializeDatabase();
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing database", e);
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private void initializeDatabase() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS products (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255), " +
                    "price DECIMAL, " +
                    "description VARCHAR(255), " +
                    "image VARCHAR(255)" +
                    ");");
        }
    }
}