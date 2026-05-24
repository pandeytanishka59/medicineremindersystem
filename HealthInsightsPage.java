import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Insights Page for MediCare 💊
 * Provides a clean, extremely simple, and robust daily summary page
 * utilizing a standard GridLayout to ensure perfect layout display on all screens.
 */
public class HealthInsightsPage extends JFrame {

    private DashboardPage parentDashboard;

    // Daily summary metrics
    private int activeMeds = 0;
    private int leftoversCount = 0;
    private int takenDoses = 0;
    private int missedDoses = 0;
    private int currentStreak = 5;

    private String mostConsistentMed = "None";
    private String mostMissedMed = "None";

    private int morningTotal = 0;
    private int morningTaken = 0;
    private int eveningTotal = 0;
    private int eveningTaken = 0;

    public HealthInsightsPage(DashboardPage parentDashboard) {
        this.parentDashboard = parentDashboard;

        // Fetch and calculate summary values from parent dashboard table model
        calculateSummary();

        // Frame Setup
        setTitle("MediCare - Daily Health Summary");
        setSize(680, 520);
        setLocationRelativeTo(parentDashboard);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true); // Allow resizing to ensure it displays perfectly under any OS scaling

        // Gradient Background Panel
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- HEADER SECTION ---
        JPanel headerPanel = new JPanel(new BorderLayout(2, 2));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Daily Health Summary 📈");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(33, 37, 41));

        JLabel subtitleLabel = new JLabel("A simple daily status report for your medication habits.");
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        subtitleLabel.setForeground(new Color(90, 95, 100));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- CENTER SECTION (Grid Layout for Perfect Display) ---
        // A single-column grid ensures every card gets equal height and fits perfectly.
        JPanel bodyPanel = new JPanel(new GridLayout(3, 1, 15, 15));
        bodyPanel.setOpaque(false);

        // Card 1: Daily Active Status
        bodyPanel.add(createCard("📋 Daily Active Status", new String[][]{
                {"Active Medicines Scheduled:", String.valueOf(activeMeds) + " medicines"},
                {"Streak Counter:", "🔥 " + currentStreak + " Days"},
                {"Leftovers in Cabinet:", String.valueOf(leftoversCount) + " medicines"}
        }));

        // Card 2: Today's Compliance
        bodyPanel.add(createCard("✅ Today's Compliance", new String[][]{
                {"Doses Taken Today:", String.valueOf(takenDoses) + " doses"},
                {"Doses Missed Today:", String.valueOf(missedDoses) + " doses"}
        }));

        // Card 3: Medicine Performance
        bodyPanel.add(createCard("✨ Medicine Performance", new String[][]{
                {"Most Consistent Med:", mostConsistentMed},
                {"Most Missed Medicine:", mostMissedMed}
        }));

        mainPanel.add(bodyPanel, BorderLayout.CENTER);

        // --- FOOTER SECTION ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setOpaque(false);

        JButton closeBtn = new JButton("Back to Dashboard");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        closeBtn.setBackground(new Color(235, 235, 235));
        closeBtn.setForeground(new Color(33, 37, 41)); // Dark charcoal text
        closeBtn.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.setPreferredSize(new Dimension(160, 38));
        closeBtn.addActionListener(e -> dispose());
        footerPanel.add(closeBtn);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Reads schedule table state and stock manager to calculate daily totals.
     */
    private void calculateSummary() {
        if (parentDashboard == null) return;

        DefaultTableModel model = parentDashboard.getTableModel();
        int totalRows = model.getRowCount();

        activeMeds = totalRows;
        leftoversCount = StockManager.leftoverList.size();

        HashMap<String, Integer> takenCounts = new HashMap<>();
        HashMap<String, Integer> missedCounts = new HashMap<>();

        Date simDate = parentDashboard.getSimulatedDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ENGLISH);
        String todayStr = sdf.format(simDate);

        int totalToday = 0;
        int takenToday = 0;

        for (int i = 0; i < totalRows; i++) {
            String medName = (String) model.getValueAt(i, 0);
            String scheduleDate = (String) model.getValueAt(i, 1);
            String timeStr = (String) model.getValueAt(i, 2);
            String status = (String) model.getValueAt(i, 3);

            if ("Taken".equals(status)) {
                takenDoses++;
                takenCounts.put(medName, takenCounts.getOrDefault(medName, 0) + 1);
            } else if ("Missed".equals(status)) {
                missedDoses++;
                missedCounts.put(medName, missedCounts.getOrDefault(medName, 0) + 1);
            }

            if (scheduleDate.equalsIgnoreCase("Every Day") || scheduleDate.contains(todayStr)) {
                totalToday++;
                if ("Taken".equals(status)) {
                    takenToday++;
                }
            }

            boolean isPm = timeStr.toUpperCase().contains("PM");
            if (!isPm) {
                morningTotal++;
                if ("Taken".equals(status)) {
                    morningTaken++;
                }
            } else {
                eveningTotal++;
                if ("Taken".equals(status)) {
                    eveningTaken++;
                }
            }
        }

        int baseStreak = parentDashboard.getBaseStreak();
        currentStreak = (totalToday > 0 && takenToday == totalToday) ? (baseStreak + 1) : baseStreak;

        int maxTaken = 0;
        for (Map.Entry<String, Integer> entry : takenCounts.entrySet()) {
            if (entry.getValue() > maxTaken) {
                maxTaken = entry.getValue();
                mostConsistentMed = entry.getKey();
            }
        }

        int maxMissed = 0;
        for (Map.Entry<String, Integer> entry : missedCounts.entrySet()) {
            if (entry.getValue() > maxMissed) {
                maxMissed = entry.getValue();
                mostMissedMed = entry.getKey();
            }
        }

        if (mostConsistentMed == null || mostConsistentMed.equals("None") || maxTaken == 0) {
            mostConsistentMed = "No doses taken today yet";
        }
        if (mostMissedMed == null || mostMissedMed.equals("None") || maxMissed == 0) {
            mostMissedMed = "None! Great job! 🎉";
        }
    }

    /**
     * Dynamically builds a simple JCard panel showing labels and values.
     */
    private JPanel createCard(String title, String[][] items) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));

        JLabel cardTitle = new JLabel(title);
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cardTitle.setForeground(new Color(0, 102, 204));
        card.add(cardTitle, BorderLayout.NORTH);

        // Simple row layout inside the card
        JPanel rowsPanel = new JPanel(new GridLayout(items.length, 1, 6, 6));
        rowsPanel.setOpaque(false);
        rowsPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        for (String[] item : items) {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);

            JLabel label = new JLabel(item[0]);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setForeground(new Color(80, 80, 80));

            JLabel val = new JLabel(item[1]);
            val.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            val.setForeground(new Color(33, 37, 41));

            // Accent color highlighting for streaking and streaks
            if (item[1].startsWith("🔥")) {
                val.setFont(new Font("Segoe UI", Font.BOLD, 12));
                val.setForeground(new Color(230, 110, 0));
            } else if (item[1].contains("doses") && !item[1].startsWith("0")) {
                val.setFont(new Font("Segoe UI", Font.BOLD, 12));
                if (item[0].contains("Taken")) {
                    val.setForeground(new Color(40, 167, 69));
                } else if (item[0].contains("Missed")) {
                    val.setForeground(new Color(220, 53, 69));
                }
            }

            row.add(label, BorderLayout.WEST);
            row.add(val, BorderLayout.EAST);
            rowsPanel.add(row);
        }

        card.add(rowsPanel, BorderLayout.CENTER);
        return card;
    }

    /**
     * Standard Gradient Panel for background styling
     */
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
}
