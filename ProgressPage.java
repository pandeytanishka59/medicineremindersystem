import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Progress Report Page for MediCare 💊
 * Visualizes health adherence using charts and metrics.
 */
public class ProgressPage extends JFrame {

    private int totalMeds, takenMeds, missedMeds;
    private int adherenceRate;

    public ProgressPage(int total, int taken, int missed) {
        this.totalMeds = total;
        this.takenMeds = taken;
        this.missedMeds = missed;
        this.adherenceRate = (total > 0) ? (taken * 100 / total) : 0;

        setTitle("MediCare - Progress Report");
        setSize(800, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Header
        JLabel header = new JLabel("Your Health Progress 📊", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.setForeground(new Color(33, 37, 41));
        mainPanel.add(header, BorderLayout.NORTH);

        // Content Area (Grid)
        JPanel content = new JPanel(new GridLayout(1, 2, 30, 0));
        content.setOpaque(false);

        // Left Side: Circular Progress & Stats
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        CircularProgress circularProgress = new CircularProgress(adherenceRate);
        circularProgress.setPreferredSize(new Dimension(250, 250));
        circularProgress.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel statText = new JLabel("Current Adherence: " + adherenceRate + "%", SwingConstants.CENTER);
        statText.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statText.setAlignmentX(Component.CENTER_ALIGNMENT);
        statText.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        leftPanel.add(circularProgress);
        leftPanel.add(statText);
        content.add(leftPanel);

        // Right Side: Bar Chart & Insights
        JPanel rightPanel = new JPanel(new BorderLayout(0, 20));
        rightPanel.setOpaque(false);

        BarChart barChart = new BarChart();
        barChart.setPreferredSize(new Dimension(300, 250));
        rightPanel.add(barChart, BorderLayout.CENTER);

        JPanel insights = new JPanel(new GridLayout(1, 1)); // Changed from 2 to 1
        insights.setOpaque(false);
        insights.add(createInsightBox("Overall Status", getStatusMessage(), new Color(230, 242, 255)));
        rightPanel.add(insights, BorderLayout.SOUTH);

        content.add(rightPanel);
        mainPanel.add(content, BorderLayout.CENTER);

        // Footer
        JButton closeBtn = new JButton("Back to Dashboard");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeBtn.addActionListener(e -> dispose());
        mainPanel.add(closeBtn, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private String getStatusMessage() {
        if (adherenceRate >= 90) return "🌟 Excellent! Keep up the great work.";
        if (adherenceRate >= 70) return "👍 Good progress! Stay consistent.";
        return "⚠️ Aim for higher consistency for better health.";
    }

    private JPanel createInsightBox(String title, String desc, Color bg) {
        JPanel box = new JPanel(new BorderLayout(5, 5));
        box.setBackground(bg);
        box.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel d = new JLabel("<html>" + desc + "</html>");
        d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        box.add(t, BorderLayout.NORTH);
        box.add(d, BorderLayout.CENTER);
        return box;
    }

    // --- CUSTOM COMPONENTS ---

    class CircularProgress extends JComponent {
        int progress;
        public CircularProgress(int p) { this.progress = p; }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int size = Math.min(getWidth(), getHeight()) - 20;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            // Background Circle
            g2.setColor(new Color(230, 230, 230));
            g2.setStroke(new BasicStroke(15, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawOval(x, y, size, size);

            // Progress Arc
            g2.setColor(new Color(0, 123, 255));
            int angle = (int) (progress * 3.6);
            g2.drawArc(x, y, size, size, 90, -angle);

            // Text in middle
            g2.setFont(new Font("Segoe UI", Font.BOLD, 40));
            String txt = progress + "%";
            FontMetrics fm = g2.getFontMetrics();
            int tx = (getWidth() - fm.stringWidth(txt)) / 2;
            int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(txt, tx, ty);
        }
    }

    class BarChart extends JComponent {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight() - 30;
            int[] values = {80, 100, 90, 70, 85, 95, adherenceRate}; // Last one is dynamic
            String[] days = {"M", "T", "W", "T", "F", "S", "S"};
            
            int barWidth = (w / values.length) - 10;
            for (int i = 0; i < values.length; i++) {
                int barHeight = (values[i] * h) / 100;
                int x = i * (barWidth + 10) + 5;
                int y = h - barHeight;

                g2.setColor(new Color(186, 224, 255));
                if (i == values.length - 1) g2.setColor(new Color(0, 123, 255)); // Highlight today
                
                g2.fillRoundRect(x, y, barWidth, barHeight, 10, 10);
                
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.drawString(days[i], x + (barWidth / 2) - 4, h + 15);
            }
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
