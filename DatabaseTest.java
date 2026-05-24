import java.sql.*;

/**
 * Diagnostic tool to check if the MySQL database connection is working properly.
 * Compiles and runs locally to verify connection status and table existence.
 */
public class DatabaseTest {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("🔍  DIAGNOSTIC TEST: DATABASE CONNECTION STATUS");
        System.out.println("=================================================\n");

        // 1. Initialize schemas
        try {
            System.out.println("⚙️  Running DatabaseConnection.initializeDatabase()...");
            DatabaseConnection.initializeDatabase();
            System.out.println("✅ Schema initialization complete.");
        } catch (Exception e) {
            System.err.println("\n❌ Schema Initialization Failed!");
            e.printStackTrace();
            return;
        }

        System.out.println();

        // 2. Test Connection and Query Tables
        String[] tables = {"users", "medicines", "reminders", "leftovers", "progress_reports"};
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("🎉  SUCCESS: Connected to MySQL database!");
            System.out.println("    Database Product Name:    " + meta.getDatabaseProductName());
            System.out.println("    Database Product Version: " + meta.getDatabaseProductVersion());
            System.out.println("    Connection URL:           " + meta.getURL());
            System.out.println("\n📊  VERIFYING TABLES:");

            for (String table : tables) {
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table)) {
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        System.out.println("    ✔️  Table '" + table + "' is active (Rows: " + count + ")");
                    }
                } catch (SQLException e) {
                    System.err.println("    ❌  Table '" + table + "' is missing or broken: " + e.getMessage());
                }
            }
            System.out.println("\n🚀  YOUR JDBC DATABASE CONTEXT IS WORKING PERFECTLY!");

        } catch (SQLException e) {
            System.err.println("\n❌  CONNECTION FAILED!");
            System.err.println("    Error Message: " + e.getMessage());
            System.err.println("    Error Code:    " + e.getErrorCode());
            System.err.println("    SQL State:     " + e.getSQLState());
            System.err.println("\n💡  TROUBLESHOOTING TIPS:");
            System.err.println("    1. Verify your MySQL server (XAMPP, WAMP, MySQL Installer) is active.");
            System.err.println("    2. Check if port 3306 is open and matching the configuration.");
            System.err.println("    3. Ensure you have placed the mysql-connector-j driver JAR in the project folder.");
        }
        System.out.println("\n=================================================");
    }
}
