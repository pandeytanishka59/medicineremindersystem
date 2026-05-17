import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private String userName = "Tanishka"; // Default for demo

    public DashboardPage(String name) {
        if (name != null && !name.isEmpty()) {
            this.userName = name;
        }

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

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel welcomeBox = new JPanel(new GridLayout(2, 1));
        welcomeBox.setOpaque(false);
        JLabel welcomeLabel = new JLabel("Welcome back, " + userName + "! 👋");
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
        addMedBtn.addActionListener(e -> new AddMedicinePage(this).setVisible(true));
        sidebar.add(addMedBtn);

        JButton calendarBtn = createSidebarButton("Calendar", "📅");
        calendarBtn.addActionListener(e -> new CalendarPage(tableModel).setVisible(true));
        sidebar.add(calendarBtn);

        JButton progressBtn = createSidebarButton("Progress Report", "📊");
        progressBtn.addActionListener(e -> {
            int total = tableModel.getRowCount();
            int taken = 0;
            int missed = 0;
            for (int i = 0; i < total; i++) {
                String s = (String) tableModel.getValueAt(i, 2);
                if (s.equals("Taken"))
                    taken++;
                else if (s.equals("Missed"))
                    missed++;
            }
            new ProgressPage(total, taken, missed).setVisible(true);
        });
        sidebar.add(progressBtn);

        JButton aiBtn = createSidebarButton("AI Chatbot", "🤖");
        aiBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "AI Chatbot: This feature is coming soon! 🤖",
                "Coming Soon", JOptionPane.INFORMATION_MESSAGE));
        sidebar.add(aiBtn);

        JButton profileBtn = createSidebarButton("Profile", "👤");
        profileBtn.addActionListener(e -> new ProfilePage(userName).setVisible(true));
        sidebar.add(profileBtn);

        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = createSidebarButton("Logout", "🚪");
        logoutBtn.addActionListener(e -> {
            new WelcomePage().setVisible(true);
            this.dispose();
        });
        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(20));

        return sidebar;
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

        JLabel title = new JLabel("Today's Medication Schedule");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        section.add(title, BorderLayout.NORTH);

        String[] columns = { "Medicine Name", "Reminder Time", "Status", "Mark Status", "Action" };
        Object[][] data = {
                { "Paracetamol (500mg)", "08:00 AM", "Taken", "Done", "Delete" },
                { "Vitamin C", "09:30 AM", "Taken", "Done", "Delete" },
                { "Metformin", "01:00 PM", "Pending", "Mark Taken", "Delete" },
                { "Omega 3", "08:00 PM", "Pending", "Mark Taken", "Delete" },
                { "Aspirin", "10:00 PM", "Pending", "Mark Taken", "Delete" }
        };

        DefaultTableModel model = new DefaultTableModel(data, columns) {
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

        // --- Interactivity: Click to Mark Taken ---
        medicineTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = medicineTable.rowAtPoint(e.getPoint());
                int col = medicineTable.columnAtPoint(e.getPoint());

                // If user clicks on the "Mark Status" column (index 3)
                if (col == 3) {
                    String currentStatus = (String) tableModel.getValueAt(row, 2);
                    if (currentStatus.equals("Pending")) {
                        tableModel.setValueAt("Taken", row, 2);
                        tableModel.setValueAt("Done", row, 3);
                        updateStats();
                        activityModel.add(0, "✅ " + tableModel.getValueAt(row, 0) + " marked as taken");
                    }
                }

                // If user clicks on the "Action" column (index 4) - DELETE
                if (col == 4) {
                    int confirm = JOptionPane.showConfirmDialog(null,
                            "Are you sure you want to delete this medicine?",
                            "Confirm Delete", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        String medName = (String) tableModel.getValueAt(row, 0);
                        tableModel.removeRow(row);
                        updateStats();
                        activityModel.add(0, "🗑️ Removed medicine: " + medName);
                    }
                }
            }
        });

        // Custom Status Renderer
        medicineTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
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

        JLabel nMed = new JLabel("Metformin");
        nMed.setForeground(Color.WHITE);
        nMed.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel nTime = new JLabel("In 2 hours 45 mins");
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
        JLabel streakLbl = new JLabel("🔥 12 Day Health Streak!");
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
        activityModel.addElement("✅ Vitamin C marked as taken (09:35 AM)");
        activityModel.addElement("➕ New medicine 'Omega 3' added");
        activityModel.addElement("🔔 Reminder snoozed: Metformin");

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
            // Adding 12 hours offset to simulate PM as requested
            long twelveHoursInMs = 12 * 60 * 60 * 1000;
            Date simulatedDate = new Date(System.currentTimeMillis() + twelveHoursInMs);

            String currentTime = timeFormat.format(simulatedDate);

            timeLabel.setText(currentTime);
            dateLabel.setText(dateFormat.format(simulatedDate));

            // Check for reminders every second
            checkReminders(currentTime);
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

        // Debugging log (Console mein dikhega)
        System.out.println("Checking Time: " + checkTime);

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String medTime = ((String) tableModel.getValueAt(i, 1)).trim().replaceFirst("^0", "");
            String status = (String) tableModel.getValueAt(i, 2);

            // Normalize spaces for comparison
            medTime = medTime.replaceAll("\\s+", " ");
            String normalizedCheckTime = checkTime.replaceAll("\\s+", " ");

            if (medTime.equalsIgnoreCase(normalizedCheckTime) && status.equals("Pending")) {
                lastAlertedTime = checkTime;
                String medName = (String) tableModel.getValueAt(i, 0);
                triggerAlarm(medName);
                break;
            }
        }
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
            // Trying to play a local alarm.wav file
            File soundFile = new File("alarm.wav");
            if (soundFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            } else {
                // Fallback: System Beep if file not found
                Toolkit.getDefaultToolkit().beep();
                Thread.sleep(500);
                Toolkit.getDefaultToolkit().beep();
            }
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }

    public void addMedicineToTable(String name, String time) {
        // Add to Table
        tableModel.addRow(new Object[] { name, time, "Pending", "Mark Taken", "Delete" });

        // Add to Activity List
        activityModel.add(0, "➕ Added new medicine: " + name);

        // Update Stats real-time
        updateStats();
    }

    private void updateStats() {
        int total = tableModel.getRowCount();
        int taken = 0;
        int missed = 0;

        for (int i = 0; i < total; i++) {
            String status = (String) tableModel.getValueAt(i, 2);
            if (status.equals("Taken")) {
                taken++;
            } else if (status.equals("Missed")) {
                missed++;
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
