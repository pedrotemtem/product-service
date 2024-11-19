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
            
            // Create the products table if it doesn't exist
            stmt.execute("CREATE TABLE IF NOT EXISTS products (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255), " +
                    "price DECIMAL, " +
                    "description VARCHAR(255), " +
                    "image VARCHAR(255)" +
                    ");");
    
            // Insert all the products
            stmt.execute("INSERT INTO products (id, name, price, description, image) " +
                    "SELECT 1, 'Contoso Catnip''s Friend', 9.99, 'Watch your feline friend embark on a fishing adventure with Contoso Catnip''s Friend toy. Packed with irresistible catnip and dangling fish lure.', '/catnip.jpg' " +
                    "WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = 1);" +
                    "INSERT INTO products (id, name, price, description, image) " +
                    "SELECT 2, 'Salty Sailor''s Squeaky Squid', 6.99, 'Let your dog set sail with the Salty Sailor''s Squeaky Squid. This interactive toy provides hours of fun, featuring multiple squeakers and crinkle tentacles.', '/squid.jpg' " +
                    "WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = 2);" +
                    "INSERT INTO products (id, name, price, description, image) " +
                    "SELECT 3, 'Mermaid''s Mice Trio', 12.99, 'Entertain your kitty with the Mermaid''s Mice Trio. These adorable plush mice are dressed as mermaids and filled with catnip to captivate their curiosity.', '/mermaid.jpg' " +
                    "WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = 3);" +
                    "INSERT INTO products (id, name, price, description, image) " +
                    "SELECT 4, 'Ocean Explorer''s Puzzle Ball', 11.99, 'Challenge your pet''s problem-solving skills with the Ocean Explorer''s Puzzle Ball. This interactive toy features hidden compartments and treats, providing mental stimulation and entertainment.', '/ocean.jpg' " +
                    "WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = 4);" +
                    "INSERT INTO products (id, name, price, description, image) " +
                    "SELECT 5, 'Pirate Parrot Teaser Wand', 8.99, 'Engage your cat in a playful pursuit with the Pirate Parrot Teaser Wand. The colorful feathers and jingling bells mimic the mischievous charm of a pirate''s parrot.', '/pirate.jpg' " +
                    "WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = 5);" +
                    "INSERT INTO products (id, name, price, description, image) " +
                    "SELECT 6, 'Seafarer''s Tug Rope', 14.99, 'Tug-of-war meets nautical adventure with the Seafarer''s Tug Rope. Made from marine-grade rope, it''s perfect for interactive play and promoting dental health in dogs.', '/tug.jpg' " +
                    "WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = 6);" +
                    "INSERT INTO products (id, name, price, description, image) " +
                    "SELECT 7, 'Seashell Snuggle Bed', 19.99, 'Give your furry friend a cozy spot to curl up with the Seashell Snuggle Bed. Shaped like a seashell, this plush bed provides comfort and relaxation for cats and small dogs.', '/bed.jpg' " +
                    "WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = 7);" +
                    "INSERT INTO products (id, name, price, description, image) " +
                    "SELECT 8, 'Nautical Knot Ball', 7.99, 'Unleash your dog''s inner sailor with the Nautical Knot Ball. Made from sturdy ropes, it''s perfect for fetching, tugging, and satisfying their chewing needs.', '/knot.jpg' " +
                    "WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = 8);" +
                    "INSERT INTO products (id, name, price, description, image) " +
                    "SELECT 9, 'Contoso Claw''s Crabby Cat Toy', 3.99, 'Watch your cat go crazy for Contoso Claw''s Crabby Cat Toy. This crinkly and catnip-filled toy will awaken their hunting instincts and provide endless entertainment.', '/crabby.jpg' " +
                    "WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = 9);" +
                    "INSERT INTO products (id, name, price, description, image) " +
                    "SELECT 10, 'Ahoy Doggy Life Jacket', 5.99, 'Ensure your furry friend stays safe during water adventures with the Ahoy Doggy Life Jacket. Designed for dogs, this flotation device offers buoyancy and visibility in style.', '/lifejacket.jpg' " +
                    "WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = 10);");
        }
    }
}