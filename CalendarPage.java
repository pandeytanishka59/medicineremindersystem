import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Calendar Page for MediCare 💊
 * Visualizes medication history and future schedules.
 */
public class CalendarPage extends JFrame {

    private JLabel monthLabel;
    private JPanel calendarGrid;
    private JLabel selectedDateLabel;
    private JList<String> dailyMedList;
    private DefaultListModel<String> medListModel;
    private javax.swing.table.DefaultTableModel dashboardData;

    public CalendarPage(javax.swing.table.DefaultTableModel data) {
        this.dashboardData = data;
        setTitle("MediCare - Health Calendar");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // --- LEFT: CALENDAR GRID ---
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setOpaque(false);

        // Month Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        monthLabel = new JLabel("May 2026", SwingConstants.CENTER);
        monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        
        JButton prevBtn = new JButton("◀");
        JButton nextBtn = new JButton("▶");
        header.add(prevBtn, BorderLayout.WEST);
        header.add(monthLabel, BorderLayout.CENTER);
        header.add(nextBtn, BorderLayout.EAST);
        leftPanel.add(header, BorderLayout.NORTH);

        // Calendar Grid
        calendarGrid = new JPanel(new GridLayout(0, 7, 5, 5));
        calendarGrid.setOpaque(false);
        renderCalendar();
        leftPanel.add(calendarGrid, BorderLayout.CENTER);

        mainPanel.add(leftPanel, BorderLayout.CENTER);

        // --- RIGHT: DETAILS PANEL ---
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setPreferredSize(new Dimension(300, 0));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        selectedDateLabel = new JLabel("Schedule for May 12", SwingConstants.CENTER);
        selectedDateLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        rightPanel.add(selectedDateLabel, BorderLayout.NORTH);

        medListModel = new DefaultListModel<>();
        updateMedList(12); // Default to today
        
        dailyMedList = new JList<>(medListModel);
        dailyMedList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dailyMedList.setFixedCellHeight(40);
        rightPanel.add(new JScrollPane(dailyMedList), BorderLayout.CENTER);

        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setForeground(new Color(33, 37, 41)); // Dark Text
        backBtn.addActionListener(e -> dispose());
        rightPanel.add(backBtn, BorderLayout.SOUTH);

        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel);
    }

    private void renderCalendar() {
        calendarGrid.removeAll();
        
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : days) {
            JLabel lbl = new JLabel(day, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lbl.setForeground(new Color(0, 102, 204));
            calendarGrid.add(lbl);
        }

        // Dummy May 2026 starting Friday
        for (int i = 0; i < 5; i++) calendarGrid.add(new JLabel("")); // Empty slots

        for (int i = 1; i <= 31; i++) {
            JPanel dayCard = new JPanel(new BorderLayout());
            dayCard.setBackground(Color.WHITE);
            dayCard.setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240)));
            
            JLabel num = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            num.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            dayCard.add(num, BorderLayout.CENTER);

            // Add dots for mock history
            if (i < 12) {
                JLabel dot = new JLabel("●", SwingConstants.CENTER);
                dot.setForeground(new Color(40, 167, 69)); // Green dot for taken
                dot.setFont(new Font("Arial", Font.BOLD, 10));
                dayCard.add(dot, BorderLayout.SOUTH);
            } else if (i == 12) {
                dayCard.setBackground(new Color(230, 242, 255)); // Highlight today
            }

            final int dayNum = i;
            dayCard.setCursor(new Cursor(Cursor.HAND_CURSOR));
            dayCard.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    selectedDateLabel.setText("Schedule for May " + dayNum);
                    updateMedList(dayNum);
                }
            });

            calendarGrid.add(dayCard);
        }
    }

    private void updateMedList(int day) {
        medListModel.clear();
        if (day == 12) { // Logic for "Today"
            if (dashboardData.getRowCount() == 0) {
                medListModel.addElement("No medicines added for today.");
            } else {
                for (int i = 0; i < dashboardData.getRowCount(); i++) {
                    String name = (String) dashboardData.getValueAt(i, 0);
                    String time = (String) dashboardData.getValueAt(i, 1);
                    String status = (String) dashboardData.getValueAt(i, 2);
                    medListModel.addElement("💊 " + name + " (" + time + ") [" + status + "]");
                }
            }
        } else if (day < 12) {
            medListModel.addElement("✅ All medicines were taken.");
            medListModel.addElement("📅 History log archived.");
        } else {
            medListModel.addElement("⏳ Schedule pending...");
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
}
