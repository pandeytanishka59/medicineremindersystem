import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Shared Data Manager for Medicine Stock & Leftovers.
 * Completely refactored to utilize standard MySQL JDBC connectivity!
 * Integrates PreparedStatements and clean SQL operations for database queries.
 */
public class StockManager {

    // --- INNER DATA MODELS (Keep identical for front-end compatibility) ---

    public static class LeftoverMed {
        public String name;
        public double quantity;
        public String location;
        public String unit;
        public String email;

        public LeftoverMed(String name, double quantity, String location, String unit) {
            this.name = name;
            this.quantity = quantity;
            this.location = location;
            this.unit = unit != null ? unit.toLowerCase() : "tablets";
            this.email = "guest@medicare.com";
        }

        public LeftoverMed(String name, double quantity, String location, String unit, String email) {
            this(name, quantity, location, unit);
            this.email = email;
        }
    }

    public static class User {
        public String fullname;
        public int age;
        public String gender;
        public String bloodGroup;
        public String height;
        public String weight;
        public String email;
        public String password;

        public User(String fullname, int age, String gender, String bloodGroup, String height, String weight, String email, String password) {
            this.fullname = fullname;
            this.age = age;
            this.gender = gender;
            this.bloodGroup = bloodGroup;
            this.height = height;
            this.weight = weight;
            this.email = email;
            this.password = password;
        }
    }

    public static class MedicineRecord {
        public String name;
        public String type;
        public double remainingStock;
        public double dailyDosage;
        public String unit;
        public String frequency;
        public String email;

        public MedicineRecord(String name, String type, double remainingStock, double dailyDosage, String unit, String frequency, String email) {
            this.name = name;
            this.type = type;
            this.remainingStock = remainingStock;
            this.dailyDosage = dailyDosage;
            this.unit = unit;
            this.frequency = frequency;
            this.email = email;
        }
    }

    public static class Reminder {
        public String medicineName;
        public String scheduleDate;
        public String reminderTime;
        public String status;
        public String email;

        public Reminder(String medicineName, String scheduleDate, String reminderTime, String status, String email) {
            this.medicineName = medicineName;
            this.scheduleDate = scheduleDate;
            this.reminderTime = reminderTime;
            this.status = status;
            this.email = email;
        }
    }

    // --- STATIC DATA CACHES (Keeps existing Swing pages working seamlessly!) ---
    public static HashMap<String, Double> tabletsRemaining = new HashMap<>();
    public static HashMap<String, Double> dailyDosage = new HashMap<>();
    public static HashMap<String, String> medicineType = new HashMap<>();
    public static HashMap<String, String> medicineUnit = new HashMap<>();
    public static HashMap<String, String> medicineFrequency = new HashMap<>();
    
    public static ArrayList<LeftoverMed> leftoverList = new ArrayList<>();
    
    // Kept for backward compatibility references in StockPage/DashboardPage
    public static ArrayList<MedicineRecord> medicineDb = new ArrayList<>();
    public static ArrayList<LeftoverMed> leftoverDb = new ArrayList<>();
    public static ArrayList<Reminder> reminderList = new ArrayList<>();
    
    public static HashMap<String, User> registeredUsers = new HashMap<>();
    
    public static String currentUserEmail = "guest@medicare.com";

    // --- INITIALIZATION ---
    static {
        // Automatically create database and tables at startup
        DatabaseConnection.initializeDatabase();
    }

    // --- USER CRUD OPERATIONS ---

    /**
     * SQL SELECT: Fetches a user record by email from the database.
     */
    public static User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getString("fullname"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("bloodGroup"),
                        rs.getString("height"),
                        rs.getString("weight"),
                        rs.getString("email"),
                        rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching user by email: " + email);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * SQL INSERT: Inserts a new user record into the database.
     */
    public static boolean registerUser(User user) {
        String sql = "INSERT INTO users (email, fullname, age, gender, bloodGroup, height, weight, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.email);
            pstmt.setString(2, user.fullname);
            pstmt.setInt(3, user.age);
            pstmt.setString(4, user.gender);
            pstmt.setString(5, user.bloodGroup);
            pstmt.setString(6, user.height);
            pstmt.setString(7, user.weight);
            pstmt.setString(8, user.password);
            
            pstmt.executeUpdate();
            System.out.println("✅ User registered in database: " + user.email);
            
            // Also populate cache map
            registeredUsers.put(user.email, user);
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Error registering user in database: " + user.email);
            e.printStackTrace();
            return false;
        }
    }

    // --- LOADING SYSTEM SESSION ---

    /**
     * SQL SELECT: Loads all active records (medicines, leftovers, reminders) for the current user session.
     */
    public static void loadFromDatabase() {
        if (currentUserEmail == null || currentUserEmail.trim().isEmpty()) {
            currentUserEmail = "guest@medicare.com";
        }
        System.out.println("🔄 Loading user data from SQL database for session: " + currentUserEmail);
        
        tabletsRemaining.clear();
        dailyDosage.clear();
        medicineType.clear();
        medicineUnit.clear();
        medicineFrequency.clear();
        leftoverList.clear();
        
        medicineDb.clear();
        leftoverDb.clear();
        reminderList.clear();

        // 1. Fetch active medicines from SQL
        String medSql = "SELECT * FROM medicines WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(medSql)) {
            
            pstmt.setString(1, currentUserEmail);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    String type = rs.getString("type");
                    double stock = rs.getDouble("remainingStock");
                    double dosage = rs.getDouble("dailyDosage");
                    String unit = rs.getString("unit");
                    String freq = rs.getString("frequency");

                    // Populate caches
                    medicineType.put(name, type);
                    tabletsRemaining.put(name, stock);
                    dailyDosage.put(name, dosage);
                    medicineUnit.put(name, unit);
                    medicineFrequency.put(name, freq);

                    medicineDb.add(new MedicineRecord(name, type, stock, dosage, unit, freq, currentUserEmail));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error loading medicines from SQL database.");
            e.printStackTrace();
        }

        // 2. Fetch leftovers from SQL
        String leftoverSql = "SELECT * FROM leftovers WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(leftoverSql)) {
            
            pstmt.setString(1, currentUserEmail);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LeftoverMed med = new LeftoverMed(
                        rs.getString("name"),
                        rs.getDouble("quantity"),
                        rs.getString("location"),
                        rs.getString("unit"),
                        rs.getString("email")
                    );
                    leftoverList.add(med);
                    leftoverDb.add(med);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error loading leftovers from SQL database.");
            e.printStackTrace();
        }

        // 3. Fetch reminders from SQL
        String reminderSql = "SELECT * FROM reminders WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(reminderSql)) {
            
            pstmt.setString(1, currentUserEmail);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Reminder rem = new Reminder(
                        rs.getString("medicineName"),
                        rs.getString("scheduleDate"),
                        rs.getString("reminderTime"),
                        rs.getString("status"),
                        rs.getString("email")
                    );
                    reminderList.add(rem);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error loading reminders from SQL database.");
            e.printStackTrace();
        }
    }

    // --- ACTIVE STOCK CRUD OPERATIONS ---

    /**
     * SQL INSERT/UPDATE: Registers active medicine stock and saves to database.
     */
    public static void registerActiveStock(String medName, String type, double initialStock, double dosage, String unit, String frequency) {
        // Sync local cache
        medicineType.put(medName, type);
        tabletsRemaining.put(medName, initialStock);
        dailyDosage.put(medName, dosage <= 0 ? 1.0 : dosage);
        medicineUnit.put(medName, unit != null ? unit.toLowerCase() : "tablets");
        medicineFrequency.put(medName, frequency != null ? frequency : "Daily");

        // Sync Database: check if exists
        String checkSql = "SELECT id FROM medicines WHERE name = ? AND email = ?";
        boolean exists = false;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            pstmt.setString(1, medName);
            pstmt.setString(2, currentUserEmail);
            try (ResultSet rs = pstmt.executeQuery()) {
                exists = rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (exists) {
            // SQL UPDATE
            String updateSql = "UPDATE medicines SET type = ?, remainingStock = ?, dailyDosage = ?, unit = ?, frequency = ? WHERE name = ? AND email = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setString(1, type);
                pstmt.setDouble(2, initialStock);
                pstmt.setDouble(3, dosage <= 0 ? 1.0 : dosage);
                pstmt.setString(4, unit != null ? unit.toLowerCase() : "tablets");
                pstmt.setString(5, frequency != null ? frequency : "Daily");
                pstmt.setString(6, medName);
                pstmt.setString(7, currentUserEmail);
                pstmt.executeUpdate();
                System.out.println("✅ Medicine stock updated in SQL: " + medName);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // SQL INSERT
            String insertSql = "INSERT INTO medicines (name, type, remainingStock, dailyDosage, unit, frequency, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setString(1, medName);
                pstmt.setString(2, type);
                pstmt.setDouble(3, initialStock);
                pstmt.setDouble(4, dosage <= 0 ? 1.0 : dosage);
                pstmt.setString(5, unit != null ? unit.toLowerCase() : "tablets");
                pstmt.setString(6, frequency != null ? frequency : "Daily");
                pstmt.setString(7, currentUserEmail);
                pstmt.executeUpdate();
                System.out.println("✅ Medicine stock inserted in SQL: " + medName);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Reload local list cache
        loadFromDatabase();
    }

    /**
     * Refills active medicine stock in DB and Cache (helper for Refill Button).
     */
    public static void updateActiveStock(String medName, double newStock) {
        tabletsRemaining.put(medName, newStock);
        
        String sql = "UPDATE medicines SET remainingStock = ? WHERE name = ? AND email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newStock);
            pstmt.setString(2, medName);
            pstmt.setString(3, currentUserEmail);
            pstmt.executeUpdate();
            System.out.println("✅ Medicine stock refilled in SQL: " + medName + " -> " + newStock);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Sync local cache lists
        loadFromDatabase();
    }

    /**
     * Helper to get days remaining.
     */
    public static double getDaysRemaining(String medName) {
        if (!tabletsRemaining.containsKey(medName)) {
            return 999.0;
        }
        double remaining = tabletsRemaining.get(medName);
        double dosage = dailyDosage.getOrDefault(medName, 1.0);
        return remaining / dosage;
    }

    /**
     * SQL UPDATE: Deducts dosage when user takes medicine.
     */
    public static void takeDose(String medName) {
        if (tabletsRemaining.containsKey(medName)) {
            double current = tabletsRemaining.get(medName);
            double daily = dailyDosage.getOrDefault(medName, 1.0);
            String freq = medicineFrequency.getOrDefault(medName, "Daily");

            double divisor = 1.0;
            if ("Twice a Day".equalsIgnoreCase(freq)) {
                divisor = 2.0;
            }
            double dose = daily / divisor;
            double newStock = current >= dose ? current - dose : 0.0;
            
            // Sync cache and database
            updateActiveStock(medName, newStock);
        }
    }

    // --- LEFTOVERS CRUD OPERATIONS ---

    /**
     * SQL DELETE & INSERT transaction: Moves completed medicine stock to leftovers.
     */
    public static void moveToLeftovers(String medName, double quantity, String location, String unit) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Transaction begins

            // 1. Delete from active medicines table
            String deleteSql = "DELETE FROM medicines WHERE name = ? AND email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                pstmt.setString(1, medName);
                pstmt.setString(2, currentUserEmail);
                pstmt.executeUpdate();
            }

            // 2. Check if leftover record already exists in leftovers table
            String checkSql = "SELECT quantity FROM leftovers WHERE name = ? AND email = ?";
            double existingQty = 0.0;
            boolean exists = false;
            try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
                pstmt.setString(1, medName);
                pstmt.setString(2, currentUserEmail);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        existingQty = rs.getDouble("quantity");
                        exists = true;
                    }
                }
            }

            if (exists) {
                // UPDATE quantity
                String updateSql = "UPDATE leftovers SET quantity = ?, location = ? WHERE name = ? AND email = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    pstmt.setDouble(1, existingQty + quantity);
                    pstmt.setString(2, location);
                    pstmt.setString(3, medName);
                    pstmt.setString(4, currentUserEmail);
                    pstmt.executeUpdate();
                }
            } else {
                // INSERT new leftover
                String insertSql = "INSERT INTO leftovers (name, quantity, location, unit, email) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setString(1, medName);
                    pstmt.setDouble(2, quantity);
                    pstmt.setString(3, location);
                    pstmt.setString(4, unit != null ? unit.toLowerCase() : "tablets");
                    pstmt.setString(5, currentUserEmail);
                    pstmt.executeUpdate();
                }
            }

            conn.commit(); // Transaction succeeds
            System.out.println("✅ Medicine successfully moved to SQL leftovers: " + medName);

        } catch (SQLException e) {
            System.err.println("❌ Transaction failed moving medicine to leftovers.");
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        // Reload data to reflect in UI
        loadFromDatabase();
    }

    /**
     * SQL UPDATE: Modifies storage location of a leftover cabinet item (helper for Update Storage Location button).
     */
    public static void updateLeftoverLocation(String medName, String newLocation) {
        String sql = "UPDATE leftovers SET location = ? WHERE name = ? AND email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newLocation);
            pstmt.setString(2, medName);
            pstmt.setString(3, currentUserEmail);
            pstmt.executeUpdate();
            System.out.println("✅ Storage location updated in SQL: " + medName + " -> " + newLocation);

        } catch (SQLException e) {
            System.err.println("❌ Error updating leftover location.");
            e.printStackTrace();
        }

        // Sync caches
        loadFromDatabase();
    }

    // --- REMINDER CRUD OPERATIONS ---

    /**
     * SQL INSERT: Adds a reminder schedule.
     */
    public static void addReminder(String name, String date, String time, String status) {
        String sql = "INSERT INTO reminders (medicineName, scheduleDate, reminderTime, status, email) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, date);
            pstmt.setString(3, time);
            pstmt.setString(4, status);
            pstmt.setString(5, currentUserEmail);
            pstmt.executeUpdate();
            System.out.println("✅ Reminder inserted in SQL database: " + name);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Refresh cache
        loadFromDatabase();
    }

    /**
     * SQL UPDATE: Toggles the taken status of a reminder.
     */
    public static void updateReminderStatus(String name, String date, String time, String status) {
        String sql = "UPDATE reminders SET status = ? WHERE medicineName = ? AND scheduleDate = ? AND reminderTime = ? AND email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setString(2, name);
            pstmt.setString(3, date);
            pstmt.setString(4, time);
            pstmt.setString(5, currentUserEmail);
            pstmt.executeUpdate();
            System.out.println("✅ Reminder status updated in SQL to " + status + ": " + name);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Refresh cache
        loadFromDatabase();
    }

    /**
     * SQL DELETE: Deletes a reminder.
     */
    public static void deleteReminder(String name, String date, String time) {
        String sql = "DELETE FROM reminders WHERE medicineName = ? AND scheduleDate = ? AND reminderTime = ? AND email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, date);
            pstmt.setString(3, time);
            pstmt.setString(4, currentUserEmail);
            pstmt.executeUpdate();
            System.out.println("✅ Reminder deleted from SQL: " + name);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Refresh cache
        loadFromDatabase();
    }

    // --- PROGRESS LOG OPERATIONS ---

    /**
     * SQL INSERT or UPDATE: Logs daily progress report.
     */
    public static void saveProgressReport(String reportDate, int totalMeds, int takenMeds, int missedMeds, int rate) {
        // MySQL specific query using ON DUPLICATE KEY UPDATE for unique (email, reportDate) index
        String sql = "INSERT INTO progress_reports (email, reportDate, totalMeds, takenMeds, missedMeds, adherenceRate) " +
                     "VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE totalMeds = ?, takenMeds = ?, missedMeds = ?, adherenceRate = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, currentUserEmail);
            pstmt.setString(2, reportDate);
            pstmt.setInt(3, totalMeds);
            pstmt.setInt(4, takenMeds);
            pstmt.setInt(5, missedMeds);
            pstmt.setInt(6, rate);
            
            // Updates
            pstmt.setInt(7, totalMeds);
            pstmt.setInt(8, takenMeds);
            pstmt.setInt(9, missedMeds);
            pstmt.setInt(10, rate);

            pstmt.executeUpdate();
            System.out.println("✅ Progress Report logged in database for date: " + reportDate);

        } catch (SQLException e) {
            System.err.println("❌ Failed to save progress report.");
            e.printStackTrace();
        }
    }
    // --- STREAK MANAGEMENT ---

    /**
     * SQL SELECT: Returns current streak count for the given user.
     */
    public static int getStreak(String email) {
        if (email == null || email.isEmpty()) return 0;
        String sql = "SELECT streak FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("streak");
            }
        } catch (SQLException e) {
            System.err.println("\u274c Error fetching streak for: " + email);
        }
        return 0;
    }

    /**
     * SQL SELECT: Returns the last date user completed all medicines (for same-day protection).
     */
    public static String getLastTakenDate(String email) {
        if (email == null || email.isEmpty()) return null;
        String sql = "SELECT last_taken_date FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getString("last_taken_date");
            }
        } catch (SQLException e) {
            System.err.println("\u274c Error fetching last_taken_date for: " + email);
        }
        return null;
    }

    /**
     * SQL UPDATE: Increments streak by 1 and records today as last_taken_date.
     */
    public static void incrementStreak(String email, String todayDate) {
        if (email == null || email.isEmpty()) return;
        String sql = "UPDATE users SET streak = streak + 1, last_taken_date = ? WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, todayDate);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
            System.out.println("\u2705 Streak incremented for: " + email + " on " + todayDate);
        } catch (SQLException e) {
            System.err.println("\u274c Error incrementing streak for: " + email);
            e.printStackTrace();
        }
    }

    /**
     * SQL UPDATE: Resets streak to 0 and clears last_taken_date.
     */
    public static void resetStreak(String email) {
        if (email == null || email.isEmpty()) return;
        String sql = "UPDATE users SET streak = 0, last_taken_date = NULL WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.executeUpdate();
            System.out.println("🔄 Streak reset to 0 for: " + email);
        } catch (SQLException e) {
            System.err.println("❌ Error resetting streak for: " + email);
            e.printStackTrace();
        }
    }
}
