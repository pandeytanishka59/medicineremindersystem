import javax.swing.*;
import java.awt.*;

/**
 * Registration Page for Medicine Reminder System
 * Features a structured form for personal and medical details.
 */
public class RegisterPage extends JFrame {

    public RegisterPage() {
        // Basic Frame Setup
        setTitle("Medicare - Create Account");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main Container with Gradient Background
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // --- HEADER SECTION ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JButton backBtn = new JButton("← Back");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            new WelcomePage().setVisible(true);
            this.dispose();
        });
        headerPanel.add(backBtn, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Create Your Profile", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(33, 37, 41));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- FORM SECTION ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Full Name
        addFormField(formPanel, "Full Name:", new JTextField(20), gbc, 0);

        // Row 2: Age and Gender
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row2.setOpaque(false);
        JSpinner ageSpinner = new JSpinner(new SpinnerNumberModel(18, 0, 120, 1));
        row2.add(ageSpinner);
        row2.add(new JLabel("   Gender: "));
        JRadioButton male = new JRadioButton("Male");
        JRadioButton female = new JRadioButton("Female");
        male.setOpaque(false); female.setOpaque(false);
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(male); genderGroup.add(female);
        row2.add(male); row2.add(female);
        addFormField(formPanel, "Age:", row2, gbc, 1);

        // Row 3: Blood Group
        String[] bloodGroups = {"Select", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        addFormField(formPanel, "Blood Group:", new JComboBox<>(bloodGroups), gbc, 2);

        // Row 4: Height and Weight
        JPanel row4 = new JPanel(new GridLayout(1, 2, 20, 0));
        row4.setOpaque(false);
        row4.add(createInputWithLabel("Height (cm):", new JTextField()));
        row4.add(createInputWithLabel("Weight (kg):", new JTextField()));
        addFormField(formPanel, "Physical Stats:", row4, gbc, 3);

        // Row 5: Email
        addFormField(formPanel, "Email ID:", new JTextField(20), gbc, 4);

        // Row 6: Password
        addFormField(formPanel, "Password:", new JPasswordField(20), gbc, 5);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // --- FOOTER SECTION ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);

        JButton registerBtn = createStyledButton("Complete Registration", new Color(40, 167, 69));
        registerBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Account Created Successfully! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            new LoginPage().setVisible(true);
            this.dispose();
        });
        footerPanel.add(registerBtn);

        // Already have an account? Login
        JPanel loginLinkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginLinkPanel.setOpaque(false);
        JLabel existingUser = new JLabel("Already have an account?");
        JButton loginLink = new JButton("Login");
        loginLink.setForeground(new Color(0, 123, 255));
        loginLink.setBorderPainted(false);
        loginLink.setContentAreaFilled(false);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.addActionListener(e -> {
            new LoginPage().setVisible(true);
            this.dispose();
        });
        loginLinkPanel.add(existingUser);
        loginLinkPanel.add(loginLink);
        
        JPanel southPanel = new JPanel(new GridLayout(2, 1));
        southPanel.setOpaque(false);
        southPanel.add(footerPanel);
        southPanel.add(loginLinkPanel);

        mainPanel.add(southPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addFormField(JPanel panel, String labelText, JComponent component, GridBagConstraints gbc, int row) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(component, gbc);
    }

    private JPanel createInputWithLabel(String text, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(5, 0));
        p.setOpaque(false);
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(new Color(33, 37, 41));
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(bg.brighter()); }
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
        SwingUtilities.invokeLater(() -> new RegisterPage().setVisible(true));
    }
}
