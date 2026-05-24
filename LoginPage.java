import javax.swing.*;
import java.awt.*;

/**
 * Login Page for Medicine Reminder System
 * Features a modern centered login card with email and password fields.
 */
public class LoginPage extends JFrame {

    public LoginPage() {
        // Safety net database initialization
        DatabaseConnection.initializeDatabase();

        // Basic Frame Setup
        setTitle("Medicare - Login");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main Container with Gradient Background
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new GridBagLayout()); // To center the login card

        // --- LOGIN CARD ---
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(450, 520)); // Increased size
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout(20, 20));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 123, 255, 100), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40) // More padding
        ));

        // Card Header
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 5));
        header.setOpaque(false);
        JLabel iconLabel = new JLabel("🔑", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 45));
        JLabel titleLabel = new JLabel("User Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        header.add(iconLabel);
        header.add(titleLabel);
        card.add(header, BorderLayout.NORTH);

        // Card Body (Form)
        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 5, 0);

        gbc.gridy = 0;
        JLabel emailLabel = new JLabel("Email Address:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        body.add(emailLabel, gbc);
        
        gbc.gridy = 1;
        JTextField emailField = new JTextField(20);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setPreferredSize(new Dimension(300, 35)); // Set height explicitly
        body.add(emailField, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(15, 0, 5, 0);
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        body.add(passLabel, gbc);
        
        gbc.gridy = 3;
        JPasswordField passField = new JPasswordField(20);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passField.setPreferredSize(new Dimension(300, 35)); // Set height explicitly
        body.add(passField, gbc);

        card.add(body, BorderLayout.CENTER);

        // Card Footer (Buttons)
        JPanel footer = new JPanel(new GridLayout(3, 1, 0, 10));
        footer.setOpaque(false);

        JButton loginBtn = createStyledButton("Login", new Color(0, 123, 255));
        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            
            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter email and password!", "MediCare", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // SQL-backed authentication via StockManager
            StockManager.User user = StockManager.getUserByEmail(email);
            if (user != null && user.password != null && user.password.equals(password)) {
                JOptionPane.showMessageDialog(this, "Login Successful! Welcome, " + user.fullname, "MediCare", JOptionPane.INFORMATION_MESSAGE);
                
                DashboardPage dashboard = new DashboardPage(user.fullname);
                dashboard.setUserEmail(email);
                dashboard.loadUserRemindersFromDB();
                dashboard.setVisible(true);
                
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Email or Password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
        footer.add(loginBtn);

        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        linkPanel.setOpaque(false);
        JLabel noAcc = new JLabel("New user?");
        JButton regLink = new JButton("Register Now");
        regLink.setForeground(new Color(0, 123, 255));
        regLink.setBorderPainted(false);
        regLink.setContentAreaFilled(false);
        regLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        regLink.addActionListener(e -> {
            new RegisterPage().setVisible(true);
            this.dispose();
        });
        linkPanel.add(noAcc);
        linkPanel.add(regLink);
        footer.add(linkPanel);

        JButton backBtn = new JButton("← Back to Home");
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            new WelcomePage().setVisible(true);
            this.dispose();
        });
        footer.add(backBtn);

        card.add(footer, BorderLayout.SOUTH);

        mainPanel.add(card); // Centered in GridBagLayout
        add(mainPanel);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(new Color(33, 37, 41)); // Dark Text
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(bg.darker()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(bg); }
        });
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

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}
