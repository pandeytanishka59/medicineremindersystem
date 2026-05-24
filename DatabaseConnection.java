import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Centered Database Connection Helper for MediCare 💊
 * Operates standard MySQL JDBC connections and auto-creates schemas at startup.
 * Beginner-friendly, with complete annotations for viva explanations.
 */
public class DatabaseConnection {

    // --- DATABASE CONFIGURATION ---
    // Change these details to match your MySQL Server credentials
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DB_NAME = "medicare_db";
    private static final String USER = "root";
    private static final String PASSWORD = "root123"; // MySQL root password

    // JDBC connection URLs
    private static final String SERVER_URL = "jdbc:mysql://" + HOST + ":" + PORT
            + "/?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DB_URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME
            + "?useSSL=false&allowPublicKeyRetrieval=true";

    static {
        // Explicitly register MySQL JDBC Driver for compatibility with older
        // environments
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                System.err.println("⚠️ MySQL JDBC Driver not found in Classpath! Please add mysql-connector jar.");
            }
        }
    }

    /**
     * Gets a connection to the 'medicare_db' MySQL database.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }

    /**
     * Auto-initializes the MySQL server, creates 'medicare_db' if absent,
     * and sets up the tables matching our application data models.
     */
    public static void initializeDatabase() {
        System.out.println("🔄 Initializing MySQL Database Connection...");

        // 1. First connect to the server without a database to create it if it doesn't
        // exist
        try (Connection conn = DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
                Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            System.out.println("✅ Database '" + DB_NAME + "' confirmed or created successfully.");

        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to MySQL Server or create database.");
            System.err.println("👉 Make sure your MySQL Server (e.g. XAMPP, WAMP, or standalone MySQL) is running!");
            e.printStackTrace();
            return;
        }

        // 2. Connect to the database and create tables
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {

            // Table 1: Users
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "email VARCHAR(100) PRIMARY KEY, " +
                    "fullname VARCHAR(100) NOT NULL, " +
                    "age INT, " +
                    "gender VARCHAR(15), " +
                    "bloodGroup VARCHAR(15), " +
                    "height VARCHAR(10), " +
                    "weight VARCHAR(10), " +
                    "password VARCHAR(100) NOT NULL" +
                    ")");

            // Safely modify bloodGroup column size if table already exists
            try {
                stmt.executeUpdate("ALTER TABLE users MODIFY COLUMN bloodGroup VARCHAR(15)");
            } catch (SQLException ignored) {
            }

            // Safely add streak tracking columns (ALTER ignored if columns already exist)
            try {
                stmt.executeUpdate("ALTER TABLE users ADD COLUMN streak INT DEFAULT 0");
            } catch (SQLException ignored) {
            }
            try {
                stmt.executeUpdate("ALTER TABLE users ADD COLUMN last_taken_date VARCHAR(20) DEFAULT NULL");
            } catch (SQLException ignored) {
            }

            // Table 2: Medicines (Active stock)
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS medicines (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "type VARCHAR(50), " +
                    "remainingStock DOUBLE, " +
                    "dailyDosage DOUBLE, " +
                    "unit VARCHAR(20), " +
                    "frequency VARCHAR(50), " +
                    "email VARCHAR(100), " +
                    "FOREIGN KEY (email) REFERENCES users(email) ON DELETE CASCADE" +
                    ")");

            // Table 3: Reminders (Schedules)
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS reminders (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "medicineName VARCHAR(100) NOT NULL, " +
                    "scheduleDate VARCHAR(50), " +
                    "reminderTime VARCHAR(20), " +
                    "status VARCHAR(20), " +
                    "email VARCHAR(100), " +
                    "FOREIGN KEY (email) REFERENCES users(email) ON DELETE CASCADE" +
                    ")");

            // Table 4: Leftover Cabinet
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS leftovers (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "quantity DOUBLE, " +
                    "location VARCHAR(100), " +
                    "unit VARCHAR(20), " +
                    "email VARCHAR(100), " +
                    "FOREIGN KEY (email) REFERENCES users(email) ON DELETE CASCADE" +
                    ")");

            // Table 5: Daily Progress Reports
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS progress_reports (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "email VARCHAR(100), " +
                    "reportDate VARCHAR(30), " +
                    "totalMeds INT, " +
                    "takenMeds INT, " +
                    "missedMeds INT, " +
                    "adherenceRate INT, " +
                    "FOREIGN KEY (email) REFERENCES users(email) ON DELETE CASCADE, " +
                    "UNIQUE KEY unique_user_date (email, reportDate)" +
                    ")");

            System.out.println("✅ All SQL tables created/verified successfully.");

        } catch (SQLException e) {
            System.err.println("❌ Error creating database tables.");
            e.printStackTrace();
        }
    }

    /**
     * Helper method to test database connection.
     * Returns true if connection is successful, false otherwise.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
