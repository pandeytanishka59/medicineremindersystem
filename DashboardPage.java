import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.sound.sampled.*;
import java.io.File;

/**
 * Modern Dashboard for MediCare 💊
 * Acts as the main control center with stats, reminders, and insights.
 */
public class DashboardPage extends JFrame {

    private JLabel timeLabel;
    private JLabel dateLabel;
    private JTable medicineTable;
    private DefaultTableModel tableModel;
    private DefaultListModel<String> activityModel;
    private JLabel totalMedLabel, takenTodayLabel, missedLabel, adherenceLabel;
    private JLabel welcomeLabel; // Class field to allow dynamic updates

    // Dynamic sidebar components
    private JLabel nMed;
    private JLabel nTime;
    private JLabel streakLbl;
    private int baseStreak = 0; // Loaded from DB on login
    private String lastStreakUpdatedDate = ""; // Prevents same-day double-increment

    private String userName = "User"; // Neutral default for demo
    private String userEmail = "guest@medicare.com";

    public DashboardPage(String name) {
        if (name != null && !name.isEmpty()) {
            this.userName = name;
        }

        // No demo data - medicines are loaded from DB per user after login

        // Basic Frame Setup
        setTitle("MediCare - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 800);
        setLocationRelativeTo(null);
        setResizable(true);

        // Main Container
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());

        // --- SIDEBAR ---
        JPanel sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        // --- CENTER CONTENT ---
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. Header (Welcome, Date, Time)
        JPanel header = createHeader();
        contentPanel.add(header, BorderLayout.NORTH);

        // 2. Scrollable Body
        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Row 1: Main Work Area
        // Left Column: Today's Medicines Table
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.7;
        gbc.weighty = 0.6;
        body.add(createTableSection(), gbc);

        // Row 2: Stats Cards
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 0.15;
        body.add(createStatsPanel(), gbc);

        // Right Column: Next Reminder & Insights
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        gbc.weighty = 0.6;
        body.add(createRightSidebar(), gbc);

        // Row 3: Motivational & Activity
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 0.25;
        body.add(createBottomSection(), gbc);

        JScrollPane scrollPane = new JScrollPane(body);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Start Clock
        startClock();
    }

    public Date getSimulatedDate() {
        // Adding 12 hours offset to simulate simulated date/PM as requested
        long twelveHoursInMs = 12 * 60 * 60 * 1000;
        return new Date(System.currentTimeMillis() + twelveHoursInMs);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel welcomeBox = new JPanel(new GridLayout(2, 1));
        welcomeBox.setOpaque(false);
        welcomeLabel = new JLabel("Welcome back, " + userName + "! 👋");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(33, 37, 41));

        JLabel subLabel = new JLabel("Your health journey is looking great today.");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subLabel.setForeground(new Color(108, 117, 125));

        welcomeBox.add(welcomeLabel);
        welcomeBox.add(subLabel);

        JPanel dateTimeBox = new JPanel(new GridLayout(2, 1));
        dateTimeBox.setOpaque(false);
        dateLabel = new JLabel("", SwingConstants.RIGHT);
        timeLabel = new JLabel("", SwingConstants.RIGHT);
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dateTimeBox.add(dateLabel);
        dateTimeBox.add(timeLabel);

        header.add(welcomeBox, BorderLayout.WEST);
        header.add(dateTimeBox, BorderLayout.EAST);
        return header;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(new Color(255, 255, 255, 200));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0, 0, 0, 30)));

        JLabel logo = new JLabel("MediCare 💊", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logo.setForeground(new Color(0, 102, 204));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(30, 0, 40, 0));
        sidebar.add(logo);

        JButton addMedBtn = createSidebarButton("Add Medicine", "➕");
        addMedBtn.addActionListener(e -> openSubPage(new AddMedicinePage(this)));
        sidebar.add(addMedBtn);

        JButton progressBtn = createSidebarButton("Progress Report", "📊");
        progressBtn.addActionListener(e -> {
            int total = tableModel.getRowCount();
            int taken = 0;
            int missed = 0;
            for (int i = 0; i < total; i++) {
                String s = (String) tableModel.getValueAt(i, 3); // Index 3 is Status
                if (s.equals("Taken"))
                    taken++;
                else if (s.equals("Missed"))
                    missed++;
            }
            openSubPage(new ProgressPage(total, taken, missed));
        });
        sidebar.add(progressBtn);

        JButton leftoverBtn = createSidebarButton("Stock Cabinet", "📦");
        leftoverBtn.addActionListener(e -> openSubPage(new StockPage()));
        sidebar.add(leftoverBtn);

        JButton searchBtn = createSidebarButton("Search Stock", "🔍");
        searchBtn.addActionListener(e -> openSubPage(new SearchPage()));
        sidebar.add(searchBtn);

        JButton insightsBtn = createSidebarButton("Health Insights", "📈");
        insightsBtn.addActionListener(e -> openSubPage(new HealthInsightsPage(this)));
        sidebar.add(insightsBtn);

        JButton aiBtn = createSidebarButton("AI Chatbot", "🤖");
        aiBtn.addActionListener(e -> openSubPage(new ChatbotPage()));
        sidebar.add(aiBtn);

        JButton profileBtn = createSidebarButton("Profile", "👤");
        profileBtn.addActionListener(e -> openSubPage(new ProfilePage(userName, userEmail, this)));
        sidebar.add(profileBtn);

        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = createSidebarButton("Logout", "🚪");
        logoutBtn.addActionListener(e -> {
            // Dispose ALL open windows, then show WelcomePage
            for (Window w : Window.getWindows()) {
                w.dispose();
            }
            new WelcomePage().setVisible(true);
        });
        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(20));

        return sidebar;
    }

    /**
     * Opens a sub-page while hiding the Dashboard.
     * When the sub-page is closed, the Dashboard reappears automatically.
     * This ensures only ONE window is visible at any time.
     */
    private void openSubPage(JFrame subPage) {
        // Hide Dashboard
        this.setVisible(false);

        // Show the sub-page
        subPage.setVisible(true);

        // When sub-page is closed (disposed), bring Dashboard back
        subPage.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                DashboardPage.this.setVisible(true);
            }
        });
    }

    private JButton createSidebarButton(String text, String icon) {
        JButton btn = new JButton(icon + "  " + text);
        btn.setMaximumSize(new Dimension(220, 50));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(new Color(33, 37, 41)); // Dark Text
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setForeground(new Color(0, 123, 255));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setForeground(new Color(73, 80, 87));
            }
        });
        return btn;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setOpaque(false);

        totalMedLabel = new JLabel("0");
        takenTodayLabel = new JLabel("0");
        missedLabel = new JLabel("0");
        adherenceLabel = new JLabel("0%");

        panel.add(createStatCard("Total Medicines", totalMedLabel, new Color(186, 224, 255)));
        panel.add(createStatCard("Taken Today", takenTodayLabel, new Color(212, 237, 218)));
        panel.add(createStatCard("Missed", missedLabel, new Color(248, 215, 218)));
        panel.add(createStatCard("Adherence", adherenceLabel, new Color(255, 243, 205)));

        updateStats(); // Initial calculation
        return panel;
    }

    private JPanel createStatCard(String title, JLabel valueLbl, Color bg) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bg);
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 24));

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createTableSection() {
        JPanel section = new JPanel(new BorderLayout(10, 10));
        section.setOpaque(false);

        // Header panel containing Title and Simple Filter dropdown
        JPanel tableHeaderPanel = new JPanel(new BorderLayout());
        tableHeaderPanel.setOpaque(false);

        JLabel title = new JLabel("Today's Medication Schedule");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableHeaderPanel.add(title, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        filterPanel.setOpaque(false);
        JLabel filterLabel = new JLabel("Filter Schedule:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JComboBox<String> filterCombo = new JComboBox<>(
                new String[] { "All Schedules", "Today Only", "Tomorrow Only" });
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterCombo.setPreferredSize(new Dimension(140, 28));
        filterCombo.addActionListener(e -> filterSchedule((String) filterCombo.getSelectedItem()));

        filterPanel.add(filterLabel);
        filterPanel.add(filterCombo);
        tableHeaderPanel.add(filterPanel, BorderLayout.EAST);

        section.add(tableHeaderPanel, BorderLayout.NORTH);

        // Empty table - reminders are loaded from DB after the user logs in
        String[] columns = { "Medicine Name", "Schedule Date", "Reminder Time", "Status", "Mark Status", "Action" };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.tableModel = model;

        medicineTable = new JTable(model);
        medicineTable.setRowHeight(40);
        medicineTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        medicineTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        medicineTable.setSelectionBackground(new Color(230, 242, 255));
        medicineTable.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Enable Row Sorter for dynamic filtering
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        medicineTable.setRowSorter(sorter);

        // --- Interactivity: Click to Mark Taken & Delete ---
        medicineTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int viewRow = medicineTable.rowAtPoint(e.getPoint());
                if (viewRow == -1)
                    return;

                // Convert view index to model index to prevent mismatch when sorted/filtered
                int row = medicineTable.convertRowIndexToModel(viewRow);
                int col = medicineTable.columnAtPoint(e.getPoint());

                // If user clicks on the "Mark Status" column (index 4)
                if (col == 4) {
                    String currentStatus = (String) tableModel.getValueAt(row, 3);
                    if (currentStatus.equals("Pending")) {
                        tableModel.setValueAt("Taken", row, 3);
                        tableModel.setValueAt("Done", row, 4);

                        // Streak increment logic: ONLY when they click "Mark as Taken"
                        SimpleDateFormat streakSdf = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ENGLISH);
                        String realToday = streakSdf.format(new java.util.Date(System.currentTimeMillis()));
                        if (userEmail != null && !userEmail.equals("guest@medicare.com")) {
                            String dbLastDate = StockManager.getLastTakenDate(userEmail);
                            if (dbLastDate == null || !dbLastDate.equals(realToday)) {
                                StockManager.incrementStreak(userEmail, realToday);
                                lastStreakUpdatedDate = realToday;
                            }
                        }

                        updateStats();

                        String medName = (String) tableModel.getValueAt(row, 0);
                        activityModel.add(0, "✅ " + medName + " marked as taken");

                        // Update status in SQL database
                        String date = (String) tableModel.getValueAt(row, 1);
                        String time = (String) tableModel.getValueAt(row, 2);
                        StockManager.updateReminderStatus(medName, date, time, "Taken");

                        // Stock deduction and calculation
                        StockManager.takeDose(medName);
                        double daysLeftDouble = StockManager.getDaysRemaining(medName);
                        int daysLeft = (int) Math.ceil(daysLeftDouble);
                        String unit = StockManager.medicineUnit.getOrDefault(medName, "tablets");

                        if (daysLeft == 0) {
                            JOptionPane.showMessageDialog(null,
                                    "🚨 Out of Stock Alert:\nYou have 0 " + unit + " left for " + medName
                                            + "!\nPlease purchase a refill immediately.",
                                    "Out of Stock", JOptionPane.ERROR_MESSAGE);
                        } else if (daysLeft <= 2) {
                            JOptionPane.showMessageDialog(null,
                                    "⚠️ Refill Alert:\nOnly " + daysLeft + " days of " + medName
                                            + " remaining in stock!",
                                    "Low Stock Alert", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }

                // If user clicks on the "Action" column (index 5) - DELETE
                if (col == 5) {
                    int confirm = JOptionPane.showConfirmDialog(null,
                            "Are you sure you want to delete this medicine?",
                            "Confirm Delete", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        String medName = (String) tableModel.getValueAt(row, 0);

                        // Check for leftover short-term medicine
                        String type = StockManager.medicineType.get(medName);
                        double remaining = StockManager.tabletsRemaining.getOrDefault(medName, 0.0);

                        if ("Short-Term Medicine".equalsIgnoreCase(type) && remaining > 0) {
                            String unit = StockManager.medicineUnit.getOrDefault(medName, "tablets");
                            String qtyStr = remaining == (long) remaining ? String.valueOf((long) remaining)
                                    : String.valueOf(remaining);

                            int leftoverConfirm = JOptionPane.showConfirmDialog(null,
                                    "This Short-Term medicine has " + qtyStr + " " + unit
                                            + " remaining.\nWas your treatment completed early?",
                                    "Treatment Completed Early?", JOptionPane.YES_NO_OPTION);

                            if (leftoverConfirm == JOptionPane.YES_OPTION) {
                                String location = JOptionPane.showInputDialog(null,
                                        "Enter the location where you stored the remaining " + qtyStr + " " + unit
                                                + ":\n(e.g., 'Kitchen Drawer', 'Study Table Shelf', 'Blue Box')",
                                        "Store Leftover Medicine", JOptionPane.QUESTION_MESSAGE);

                                if (location != null && !location.trim().isEmpty()) {
                                    StockManager.moveToLeftovers(medName, remaining, location.trim(), unit);
                                    activityModel.add(0, "📦 Archived: " + qtyStr + " " + unit + " of " + medName
                                            + " in " + location);
                                    JOptionPane.showMessageDialog(null, "Successfully archived leftovers in Stock Box!",
                                            "Archived Successfully", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    // User cancelled or entered empty location - move to leftovers anyway with
                                    // default location
                                    StockManager.moveToLeftovers(medName, remaining, "General Medicine Drawer", unit);
                                    activityModel.add(0, "📦 Archived: " + qtyStr + " " + unit + " of " + medName
                                            + " in General Medicine Drawer");
                                    JOptionPane.showMessageDialog(null,
                                            "Saved in 'General Medicine Drawer' by default.", "Archived",
                                            JOptionPane.INFORMATION_MESSAGE);
                                }
                            }
                        }

                        // Delete reminder from SQL Database
                        String date = (String) tableModel.getValueAt(row, 1);
                        String time = (String) tableModel.getValueAt(row, 2);
                        StockManager.deleteReminder(medName, date, time);

                        tableModel.removeRow(row);
                        updateStats();
                        activityModel.add(0, "🗑️ Removed medicine: " + medName);
                    }
                }
            }
        });

        // Custom Status Renderer (Column 3 is Status)
        medicineTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                String status = (String) value;
                if (status.equals("Taken"))
                    label.setForeground(new Color(40, 167, 69));
                else if (status.equals("Pending"))
                    label.setForeground(new Color(0, 123, 255));
                else if (status.equals("Missed"))
                    label.setForeground(new Color(220, 53, 69));
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                return label;
            }
        });

        JScrollPane sp = new JScrollPane(medicineTable);
        sp.getViewport().setBackground(Color.WHITE);
        section.add(sp, BorderLayout.CENTER);

        return section;
    }

    private void filterSchedule(String filterType) {
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) medicineTable.getRowSorter();
        if (sorter == null)
            return;

        if ("All Schedules".equals(filterType)) {
            sorter.setRowFilter(null);
        } else {
            Date simDate = getSimulatedDate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ENGLISH);
            String todayStr = sdf.format(simDate);

            long oneDayMs = 24 * 60 * 60 * 1000;
            String tomorrowStr = sdf.format(new Date(simDate.getTime() + oneDayMs));

            String targetDateStr = "Today Only".equals(filterType) ? todayStr : tomorrowStr;

            sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                @Override
                public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                    String scheduleDate = (String) entry.getValue(1); // column index 1 is Schedule Date
                    if (scheduleDate.equalsIgnoreCase("Every Day")) {
                        return true;
                    }
                    return scheduleDate.contains(targetDateStr);
                }
            });
        }
    }

    private JPanel createRightSidebar() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 15, 0);

        // Next Reminder Card
        gbc.gridy = 0;
        JPanel nextCard = new JPanel(new BorderLayout(10, 10));
        nextCard.setBackground(new Color(0, 123, 255));
        nextCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nTitle = new JLabel("Next Reminder 🔔");
        nTitle.setForeground(Color.WHITE);
        nTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        nMed = new JLabel("All Set! 🎉");
        nMed.setForeground(Color.WHITE);
        nMed.setFont(new Font("Segoe UI", Font.BOLD, 22));

        nTime = new JLabel("No more reminders today");
        nTime.setForeground(new Color(255, 255, 255, 200));
        nTime.setFont(new Font("Segoe UI", Font.ITALIC, 14));

        nextCard.add(nTitle, BorderLayout.NORTH);
        nextCard.add(nMed, BorderLayout.CENTER);
        nextCard.add(nTime, BorderLayout.SOUTH);
        panel.add(nextCard, gbc);

        // AI Health Insights
        gbc.gridy = 1;
        JPanel aiCard = new JPanel(new BorderLayout());
        aiCard.setBackground(Color.WHITE);
        aiCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel aiTitle = new JLabel("AI Insights 🤖");
        aiTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel aiText = new JLabel(
                "<html>\"You often miss morning medicines. Consider moving them closer to your coffee break!\"</html>");
        aiText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        aiText.setForeground(new Color(100, 100, 100));

        aiCard.add(aiTitle, BorderLayout.NORTH);
        aiCard.add(aiText, BorderLayout.CENTER);
        panel.add(aiCard, gbc);

        // Health Streak
        gbc.gridy = 2;
        JPanel streakCard = new JPanel(new BorderLayout());
        streakCard.setBackground(new Color(255, 243, 205));
        streakCard.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        streakLbl = new JLabel("🔥 0 Day Streak! (Pending today)");
        streakLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        streakCard.add(streakLbl, BorderLayout.CENTER);
        panel.add(streakCard, gbc);

        gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private JPanel createBottomSection() {
        JPanel section = new JPanel(new GridLayout(1, 2, 20, 0));
        section.setOpaque(false);

        // Motivational Section
        JPanel motivate = new JPanel(new BorderLayout());
        motivate.setBackground(new Color(212, 237, 218));
        motivate.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel mText = new JLabel("<html><div style='text-align: center;'>🎉 Great job, " + userName
                + "!<br>You are on track to a healthier life. Keep it up!</div></html>", SwingConstants.CENTER);
        mText.setFont(new Font("Segoe UI", Font.BOLD, 18));
        mText.setForeground(new Color(21, 87, 36));
        motivate.add(mText, BorderLayout.CENTER);
        section.add(motivate);

        // Recent Activity
        JPanel activity = new JPanel(new BorderLayout());
        activity.setBackground(Color.WHITE);
        activity.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel aTitle = new JLabel("Recent Activity 🕒");
        aTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));

        activityModel = new DefaultListModel<>();
        activityModel.addElement("🎉 Welcome to MediCare! Your activity will appear here.");

        JList<String> list = new JList<>(activityModel);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        list.setFixedCellHeight(30);

        activity.add(aTitle, BorderLayout.NORTH);
        activity.add(new JScrollPane(list), BorderLayout.CENTER);
        section.add(activity);

        return section;
    }

    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            // Using Locale.ENGLISH ensures "AM/PM" is consistent
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a", java.util.Locale.ENGLISH);
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", java.util.Locale.ENGLISH);

            String currentTime = timeFormat.format(getSimulatedDate());

            timeLabel.setText(currentTime);
            dateLabel.setText(dateFormat.format(getSimulatedDate()));

            // Check for reminders every second
            checkReminders(currentTime);

            // Update the dynamic next reminder card countdown
            updateNextReminder();
        });
        timer.start();
    }

    private String lastAlertedTime = "";

    private void checkReminders(String currentTime) {
        // current time is "hh:mm:ss a" (e.g., "08:04:10 AM" or "08:04:10 PM")
        // We need "hh:mm a" (e.g., "08:04 AM" or "08:04 PM")
        String timePart = currentTime.substring(0, 5).replaceFirst("^0", "");
        String amPmPart = currentTime.substring(9);
        String checkTime = timePart + " " + amPmPart;

        if (checkTime.equals(lastAlertedTime))
            return;


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ENGLISH);
        String todayStr = sdf.format(getSimulatedDate());

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String scheduleDate = (String) tableModel.getValueAt(i, 1);
            String medTime = ((String) tableModel.getValueAt(i, 2)).trim().replaceFirst("^0", "");
            String status = (String) tableModel.getValueAt(i, 3);

            // Normalize spaces for comparison
            medTime = medTime.replaceAll("\\s+", " ");
            String normalizedCheckTime = checkTime.replaceAll("\\s+", " ");

            if (medTime.equalsIgnoreCase(normalizedCheckTime) && status.equals("Pending")) {
                if (scheduleDate.equalsIgnoreCase("Every Day") || scheduleDate.contains(todayStr)) {
                    lastAlertedTime = checkTime;
                    String medName = (String) tableModel.getValueAt(i, 0);
                    triggerAlarm(medName);
                    break;
                }
            }
        }
    }

    private void updateNextReminder() {
        if (nMed == null || nTime == null)
            return;

        Date simDate = getSimulatedDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ENGLISH);
        String todayStr = sdf.format(simDate);

        // Get current total seconds of day
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(simDate);
        int curHour24 = cal.get(java.util.Calendar.HOUR_OF_DAY);
        int curMinute = cal.get(java.util.Calendar.MINUTE);
        int curSecond = cal.get(java.util.Calendar.SECOND);
        int curTotalSecs = curHour24 * 3600 + curMinute * 60 + curSecond;

        String closestMedName = null;
        int minDiffSeconds = Integer.MAX_VALUE;

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String scheduleDate = (String) tableModel.getValueAt(i, 1);
            String status = (String) tableModel.getValueAt(i, 3);
            String medTime = ((String) tableModel.getValueAt(i, 2)).trim();

            // Verify if medicine is Pending and scheduled for today
            if (status.equals("Pending")
                    && (scheduleDate.equalsIgnoreCase("Every Day") || scheduleDate.contains(todayStr))) {
                try {
                    int medTotalSecs = parseTimeToSeconds(medTime);
                    int diff = medTotalSecs - curTotalSecs;

                    // Check if it's in the future and closest
                    if (diff > 0 && diff < minDiffSeconds) {
                        minDiffSeconds = diff;
                        closestMedName = (String) tableModel.getValueAt(i, 0);
                    }
                } catch (Exception ignored) {
                }
            }
        }

        if (closestMedName != null) {
            nMed.setText(closestMedName);
            int diffMins = minDiffSeconds / 60;
            int hours = diffMins / 60;
            int mins = diffMins % 60;

            if (hours > 0) {
                nTime.setText("In " + hours + " hr " + mins + " mins");
            } else {
                nTime.setText("In " + mins + " mins");
            }
        } else {
            nMed.setText("All Set! 🎉");
            nTime.setText("No more reminders today");
        }
    }

    private int parseTimeToSeconds(String timeStr) throws Exception {
        // Simple parser supporting formats like "08:30 AM" or "8:30 PM"
        SimpleDateFormat parser = new SimpleDateFormat("hh:mm a", java.util.Locale.ENGLISH);
        Date date = parser.parse(timeStr.trim());
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(date);
        return c.get(java.util.Calendar.HOUR_OF_DAY) * 3600 + c.get(java.util.Calendar.MINUTE) * 60;
    }

    private void triggerAlarm(String medName) {
        playAlarmSound();
        JOptionPane.showMessageDialog(this,
                "⏰ REMINDER: It's time to take your " + medName + "!",
                "Medicine Alarm",
                JOptionPane.WARNING_MESSAGE);
    }

    private void playAlarmSound() {
        try {
            File soundFile = new File("alarm.wav");
            if (soundFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            } else {
                Toolkit.getDefaultToolkit().beep();
                Thread.sleep(500);
                Toolkit.getDefaultToolkit().beep();
            }
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }

    public void addMedicineToTable(String name, String scheduleDate, String time) {
        // Add to Table
        tableModel.addRow(new Object[] { name, scheduleDate, time, "Pending", "Mark Taken", "Delete" });

        // Save reminder to SQL Database
        StockManager.addReminder(name, scheduleDate, time, "Pending");

        // Add to Activity List
        activityModel.add(0, "➕ Added new medicine: " + name);

        // Update Stats real-time
        updateStats();
    }

    public DefaultTableModel getTableModel() {
        return this.tableModel;
    }

    public int getBaseStreak() {
        return this.baseStreak;
    }

    private void updateStats() {
        int total = tableModel.getRowCount();
        int taken = 0;
        int missed = 0;

        Date simDate = getSimulatedDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ENGLISH);
        String todayStr = sdf.format(simDate);

        int totalToday = 0;
        int takenToday = 0;

        for (int i = 0; i < total; i++) {
            String scheduleDate = (String) tableModel.getValueAt(i, 1);
            String status = (String) tableModel.getValueAt(i, 3);
            if (status.equals("Taken")) {
                taken++;
            } else if (status.equals("Missed")) {
                missed++;
            }

            // Count scheduled for today only
            if (scheduleDate.equalsIgnoreCase("Every Day") || scheduleDate.contains(todayStr)) {
                totalToday++;
                if (status.equals("Taken")) {
                    takenToday++;
                }
            }
        }

        totalMedLabel.setText(String.valueOf(total));
        takenTodayLabel.setText(String.valueOf(taken));
        missedLabel.setText(String.valueOf(missed));

        if (total > 0) {
            int percentage = (taken * 100) / total;
            adherenceLabel.setText(percentage + "%");
        } else {
            adherenceLabel.setText("0%");
        }

        // Update Health Streak dynamically
        if (streakLbl != null) {
            baseStreak = StockManager.getStreak(userEmail);
            if (totalToday > 0 && takenToday == totalToday) {
                streakLbl.setText("🔥 " + baseStreak + " Day Streak! (Today Complete \u2705)");
                streakLbl.getParent().setBackground(new Color(212, 237, 218));
            } else {
                streakLbl.setText("🔥 " + baseStreak + " Day Streak! (Pending today)");
                streakLbl.getParent().setBackground(new Color(255, 243, 205));
            }
        }

        // Log progress report to MySQL database
        int currentAdherenceRate = (total > 0) ? (taken * 100 / total) : 0;
        StockManager.saveProgressReport(todayStr, total, taken, missed, currentAdherenceRate);
    }

    public void setUserEmail(String email) {
        this.userEmail = email;
        StockManager.currentUserEmail = email;
    }

    public void loadUserRemindersFromDB() {
        if (tableModel == null) return;

        // Load this user's streak from DB
        baseStreak = StockManager.getStreak(userEmail);

        // Load medicines, leftovers, and reminders into StockManager cache
        StockManager.loadFromDatabase();

        // Clear table and repopulate with only this user's reminders
        tableModel.setRowCount(0);
        for (StockManager.Reminder r : StockManager.reminderList) {
            if (r.email.equalsIgnoreCase(userEmail)) {
                String markStatus = r.status.equals("Taken") ? "Done" : "Mark Taken";
                tableModel.addRow(new Object[]{r.medicineName, r.scheduleDate, r.reminderTime, r.status, markStatus, "Delete"});
            }
        }

        // New users start with an empty table - they add their own medicines
        updateStats();
    }

    public String getUserEmail() {
        return this.userEmail;
    }

    public void setUserName(String name) {
        this.userName = name;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome back, " + userName + "! 👋");
        }
    }

    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, new Color(245, 250, 255), 0, getHeight(), Color.WHITE);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new DashboardPage("Tanishka").setVisible(true));
    }
}
