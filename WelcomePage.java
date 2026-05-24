import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Modern Welcome Page for Medicine Reminder System
 * Designed for a professional college project aesthetic.
 */
public class WelcomePage extends JFrame {

    private JLabel timeLabel;
    private JLabel dateLabel;

    public WelcomePage() {
        // Basic Frame Setup
        setTitle("Medicine Reminder System - Welcome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);

        // Main Container with Gradient Background
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // --- TOP SECTION (Title and Logo) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        // A simple healthcare icon using text (cross)
        JLabel logoLabel = new JLabel("✚", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        logoLabel.setForeground(new Color(0, 102, 204));
        topPanel.add(logoLabel, BorderLayout.NORTH);

        JLabel titleLabel = new JLabel("MEDICINE REMINDER SYSTEM", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(new Color(33, 37, 41));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        JLabel subTitleLabel = new JLabel("Your personal healthcare companion for timely medication and schedules.",
                SwingConstants.CENTER);
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subTitleLabel.setForeground(new Color(73, 80, 87));
        topPanel.add(subTitleLabel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // --- CENTER SECTION (Buttons and Features) ---
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Action Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        JButton loginBtn = createStyledButton("Login", new Color(186, 224, 255)); // Light Blue
        JButton signupBtn = createStyledButton("Create Account", new Color(212, 237, 218)); // Light Green
        JButton exitBtn = createStyledButton("Exit", new Color(248, 215, 218)); // Light Red

        buttonPanel.add(loginBtn);
        buttonPanel.add(signupBtn);
        buttonPanel.add(exitBtn);

        // Navigation Actions
        loginBtn.addActionListener(e -> {
            new LoginPage().setVisible(true);
            this.dispose();
        });

        signupBtn.addActionListener(e -> {
            new RegisterPage().setVisible(true);
            this.dispose();
        });

        exitBtn.addActionListener(e -> System.exit(0));

        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(buttonPanel, gbc);

        // Features Section
        JPanel featuresPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        featuresPanel.setOpaque(false);
        featuresPanel.add(createFeatureCard("Medicine Alerts", "🔔"));
        featuresPanel.add(createFeatureCard("Daily Tracking", "📅"));
        featuresPanel.add(createFeatureCard("Notifications", "🚀"));
        featuresPanel.add(createFeatureCard("Med History", "📜"));

        gbc.gridy = 1;
        gbc.insets = new Insets(40, 0, 0, 0);
        centerPanel.add(featuresPanel, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // --- BOTTOM SECTION (Quote, Clock, Footer) ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        // Quote
        JLabel quoteLabel = new JLabel("\"Health is the greatest wealth.\"", SwingConstants.CENTER);
        quoteLabel.setFont(new Font("Segoe UI", Font.ITALIC, 18));
        quoteLabel.setForeground(new Color(108, 117, 125));
        bottomPanel.add(quoteLabel, BorderLayout.NORTH);

        // Clock and Footer Container
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Live Clock
        JPanel clockPanel = new JPanel(new GridLayout(2, 1));
        clockPanel.setOpaque(false);
        dateLabel = new JLabel("", SwingConstants.LEFT);
        timeLabel = new JLabel("", SwingConstants.LEFT);
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        clockPanel.add(dateLabel);
        clockPanel.add(timeLabel);
        infoPanel.add(clockPanel, BorderLayout.WEST);

        // Footer Text
        JLabel footerLabel = new JLabel(
                "<html><div style='text-align: right;'>Developed By Tanishka Pandey<br>© 2026 Medicine Reminder System</div></html>",
                SwingConstants.RIGHT);
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(108, 117, 125));
        infoPanel.add(footerLabel, BorderLayout.EAST);

        bottomPanel.add(infoPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Start Clock Timer
        startClock();

        // Exit Action (Functional)
        exitBtn.addActionListener(e -> System.exit(0));
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(new Color(33, 37, 41)); // Dark Gray Text
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1),
                BorderFactory.createEmptyBorder(12, 25, 12, 25)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover Effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg);
            }
        });

        return btn;
    }

    private JPanel createFeatureCard(String title, String icon) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(new Color(255, 255, 255, 180)); // Semi-transparent white
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 123, 255, 50), 1),
                BorderFactory.createEmptyBorder(15, 10, 15, 10)));

        JLabel iconLbl = new JLabel(icon, SwingConstants.CENTER);
        iconLbl.setFont(new Font("Segoe UI", Font.PLAIN, 30));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLbl.setForeground(new Color(52, 58, 64));

        card.add(iconLbl, BorderLayout.CENTER);
        card.add(titleLbl, BorderLayout.SOUTH);

        return card;
    }

    private void startClock() {
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
                // Adding 12 hours offset to simulate 16 May as requested
                long twelveHoursInMs = 12 * 60 * 60 * 1000;
                Date simulatedDate = new Date(System.currentTimeMillis() + twelveHoursInMs);

                timeLabel.setText(timeFormat.format(simulatedDate));
                dateLabel.setText(dateFormat.format(simulatedDate));
            }
        });
        timer.start();
    }

    // Custom Panel for Gradient Background
    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w = getWidth();
            int h = getHeight();
            Color color1 = new Color(230, 242, 255); // Very Light Blue
            Color color2 = Color.WHITE;
            GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);
        }
    }

    public static void main(String[] args) {
        // Explicitly initialize database tables first
        DatabaseConnection.initializeDatabase();

        // Set Look and Feel to System Default for better appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            new WelcomePage().setVisible(true);
        });
    }
}
