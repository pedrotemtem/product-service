package productService.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class H2DatabaseManager {
    private static final String JDBC_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private Connection connection;

    public H2DatabaseManager() throws SQLException {
        connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        initializeDatabase();
    }

    private void initializeDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(
            "CREATE TABLE IF NOT EXISTS products (\n" +
                    "    id INT PRIMARY KEY,\n" +
                    "    name VARCHAR(255),\n" +
                    "    price DECIMAL,\n" +
                    "    description VARCHAR(255),\n" +
                    "    image VARCHAR(255)\n" +
                    ");"
            );
        }
    }

    public Connection getConnection() {
        return connection;
    }
}