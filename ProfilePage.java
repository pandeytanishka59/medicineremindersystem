import javax.swing.*;
import java.awt.*;

/**
 * Profile Page for MediCare 💊
 * Displays user information and health metrics.
 */
public class ProfilePage extends JFrame {

    private String userName;

    public ProfilePage(String name) {
        this.userName = name;

        setTitle("MediCare - User Profile");
        setSize(600, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Main Container
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));

        // --- TOP SECTION: Avatar & Name ---
        JPanel topSection = new JPanel();
        topSection.setOpaque(false);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));

        JLabel avatarLabel = new JLabel("👤", SwingConstants.CENTER);
        avatarLabel.setFont(new Font("Segoe UI", Font.PLAIN, 80));
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel nameLabel = new JLabel(userName, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setForeground(new Color(33, 37, 41));

        JLabel emailLabel = new JLabel(userName.toLowerCase() + "@medicare.com", SwingConstants.CENTER);
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailLabel.setForeground(new Color(108, 117, 125));

        topSection.add(avatarLabel);
        topSection.add(Box.createVerticalStrut(10));
        topSection.add(nameLabel);
        topSection.add(emailLabel);
        topSection.add(Box.createVerticalStrut(30));

        mainPanel.add(topSection, BorderLayout.NORTH);

        // --- CENTER SECTION: Info Cards ---
        JPanel infoGrid = new JPanel(new GridLayout(3, 2, 15, 15));
        infoGrid.setOpaque(false);

        infoGrid.add(createDetailCard("Age", "21 Years", "🎂"));
        infoGrid.add(createDetailCard("Blood Group", "B+", "🩸"));
        infoGrid.add(createDetailCard("Weight", "62 Kg", "⚖️"));
        infoGrid.add(createDetailCard("Height", "165 cm", "📏"));
        infoGrid.add(createDetailCard("Health Goal", "Daily Meds", "🎯"));
        infoGrid.add(createDetailCard("Meds Taken", "145 Total", "💊"));

        mainPanel.add(infoGrid, BorderLayout.CENTER);

        // --- BOTTOM SECTION: Actions ---
        JPanel bottomSection = new JPanel(new GridLayout(2, 1, 0, 10));
        bottomSection.setOpaque(false);
        bottomSection.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        JButton editBtn = createStyledButton("Edit Profile Info", new Color(0, 123, 255));
        JButton logoutBtn = createStyledButton("Logout Account", new Color(220, 53, 69));
        
        logoutBtn.addActionListener(e -> {
            Window[] windows = Window.getWindows();
            for (Window window : windows) {
                window.dispose();
            }
            new WelcomePage().setVisible(true);
        });

        bottomSection.add(editBtn);
        bottomSection.add(logoutBtn);

        mainPanel.add(bottomSection, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createDetailCard(String title, String value, String icon) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLbl.setForeground(new Color(108, 117, 125));
        
        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueLbl.setForeground(new Color(33, 37, 41));

        textPanel.add(titleLbl);
        textPanel.add(valueLbl);

        card.add(iconLbl, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        return btn;
    }

    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, new Color(230, 242, 255), 0, getHeight(), Color.WHITE);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
