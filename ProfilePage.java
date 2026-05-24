import javax.swing.*;
import java.awt.*;
import java.sql.*;

/**
 * Profile Page for MediCare 💊
 * Dynamically loads user details from MySQL database.
 * Supports inline editing and saves changes back to DB.
 */
public class ProfilePage extends JFrame {

    private String userEmail;
    private StockManager.User userData;

    // Labels updated after edit
    private JLabel nameLabel;
    private JLabel emailLabel;
    private JLabel ageLbl;
    private JLabel bloodLbl;
    private JLabel weightLbl;
    private JLabel heightLbl;
    private JLabel genderLbl;

    private DashboardPage parentDashboard;

    public ProfilePage(String fallbackName, String email, DashboardPage parent) {
        this.parentDashboard = parent;
        this.userEmail = (email != null && !email.trim().isEmpty()) ? email.trim() : "guest@medicare.com";

        // Fetch real user data from MySQL
        this.userData = StockManager.getUserByEmail(userEmail);

        // Resolve display values — use DB data if available, otherwise show "N/A"
        String displayName   = (userData != null) ? userData.fullname   : fallbackName;
        String displayEmail  = (userData != null) ? userData.email      : userEmail;
        String displayAge    = (userData != null) ? userData.age + " Years"  : "N/A";
        String displayBlood  = (userData != null && userData.bloodGroup != null
                                && !userData.bloodGroup.equals("Select")) ? userData.bloodGroup : "N/A";
        String displayWeight = (userData != null && userData.weight != null
                                && !userData.weight.isEmpty()) ? userData.weight + " Kg" : "N/A";
        String displayHeight = (userData != null && userData.height != null
                                && !userData.height.isEmpty()) ? userData.height + " cm" : "N/A";
        String displayGender = (userData != null && userData.gender != null) ? userData.gender : "N/A";

        setTitle("MediCare - User Profile");
        setSize(600, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Main Container with Gradient Background
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));

        // --- TOP SECTION: Avatar, Name, Email ---
        JPanel topSection = new JPanel();
        topSection.setOpaque(false);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));

        JLabel avatarLabel = new JLabel("👤", SwingConstants.CENTER);
        avatarLabel.setFont(new Font("Segoe UI", Font.PLAIN, 80));
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        nameLabel = new JLabel(displayName, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setForeground(new Color(33, 37, 41));

        emailLabel = new JLabel(displayEmail, SwingConstants.CENTER);
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailLabel.setForeground(new Color(108, 117, 125));

        topSection.add(avatarLabel);
        topSection.add(Box.createVerticalStrut(10));
        topSection.add(nameLabel);
        topSection.add(emailLabel);
        topSection.add(Box.createVerticalStrut(30));

        mainPanel.add(topSection, BorderLayout.NORTH);

        // --- CENTER SECTION: Info Cards (Dynamic) ---
        JPanel infoGrid = new JPanel(new GridLayout(3, 2, 15, 15));
        infoGrid.setOpaque(false);

        ageLbl    = makeValueLabel(displayAge);
        bloodLbl  = makeValueLabel(displayBlood);
        weightLbl = makeValueLabel(displayWeight);
        heightLbl = makeValueLabel(displayHeight);
        genderLbl = makeValueLabel(displayGender);

        infoGrid.add(createDetailCard("Age",         ageLbl,    "🎂"));
        infoGrid.add(createDetailCard("Blood Group", bloodLbl,  "🩸"));
        infoGrid.add(createDetailCard("Weight",      weightLbl, "⚖️"));
        infoGrid.add(createDetailCard("Height",      heightLbl, "📏"));
        infoGrid.add(createDetailCard("Gender",      genderLbl, "👫"));
        infoGrid.add(createDetailCard("Email",       makeValueLabel(displayEmail), "📧"));

        mainPanel.add(infoGrid, BorderLayout.CENTER);

        // --- BOTTOM SECTION: Buttons ---
        JPanel bottomSection = new JPanel(new GridLayout(2, 1, 0, 10));
        bottomSection.setOpaque(false);
        bottomSection.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        JButton editBtn = createStyledButton("✏️  Edit Profile Info", new Color(0, 123, 255));
        editBtn.addActionListener(e -> openEditDialog());

        JButton logoutBtn = createStyledButton("🚪  Logout Account", new Color(220, 53, 69));
        logoutBtn.addActionListener(e -> {
            // Dispose ALL windows (including hidden Dashboard) then show WelcomePage
            for (Window w : Window.getWindows()) {
                if (w != null) w.dispose();
            }
            new WelcomePage().setVisible(true);
        });

        bottomSection.add(editBtn);
        bottomSection.add(logoutBtn);
        mainPanel.add(bottomSection, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // -------------------------------------------------------
    // EDIT PROFILE DIALOG
    // -------------------------------------------------------
    private void openEditDialog() {
        JDialog dialog = new JDialog(this, "Edit Profile Info", true);
        dialog.setSize(460, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JLabel dlgTitle = new JLabel("Update Your Profile", SwingConstants.CENTER);
        dlgTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        dlgTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        dialog.add(dlgTitle, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridLayout(6, 2, 10, 14));
        form.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

        JTextField nameField   = new JTextField(userData != null ? userData.fullname : "");
        JTextField ageField    = new JTextField(userData != null ? String.valueOf(userData.age) : "");
        String[] bloodGroups   = {"Select", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        JComboBox<String> bloodCombo = new JComboBox<>(bloodGroups);
        if (userData != null && userData.bloodGroup != null)
            bloodCombo.setSelectedItem(userData.bloodGroup);
        JTextField heightField = new JTextField(userData != null ? userData.height : "");
        JTextField weightField = new JTextField(userData != null ? userData.weight : "");

        // Gender Selection Components
        JRadioButton maleRadio = new JRadioButton("Male");
        JRadioButton femaleRadio = new JRadioButton("Female");
        maleRadio.setFont(fieldFont);
        femaleRadio.setFont(fieldFont);
        maleRadio.setOpaque(false);
        femaleRadio.setOpaque(false);
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        genderPanel.setOpaque(false);
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        
        if (userData != null && "Female".equalsIgnoreCase(userData.gender)) {
            femaleRadio.setSelected(true);
        } else {
            maleRadio.setSelected(true);
        }

        nameField.setFont(fieldFont);
        ageField.setFont(fieldFont);
        bloodCombo.setFont(fieldFont);
        heightField.setFont(fieldFont);
        weightField.setFont(fieldFont);

        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        JLabel l1 = new JLabel("Full Name:");  l1.setFont(labelFont);
        JLabel l2 = new JLabel("Age:");         l2.setFont(labelFont);
        JLabel l3 = new JLabel("Blood Group:"); l3.setFont(labelFont);
        JLabel l4 = new JLabel("Height (cm):"); l4.setFont(labelFont);
        JLabel l5 = new JLabel("Weight (kg):"); l5.setFont(labelFont);
        JLabel l6 = new JLabel("Gender:");       l6.setFont(labelFont);

        form.add(l1); form.add(nameField);
        form.add(l2); form.add(ageField);
        form.add(l3); form.add(bloodCombo);
        form.add(l4); form.add(heightField);
        form.add(l5); form.add(weightField);
        form.add(l6); form.add(genderPanel);

        dialog.add(form, BorderLayout.CENTER);

        // Save button
        JButton saveBtn = new JButton("💾  Save Changes");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveBtn.setBackground(new Color(40, 167, 69));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        saveBtn.addActionListener(e -> {
            String newName   = nameField.getText().trim();
            String newBlood  = (String) bloodCombo.getSelectedItem();
            String newHeight = heightField.getText().trim();
            String newWeight = weightField.getText().trim();
            String newGender = maleRadio.isSelected() ? "Male" : "Female";
            int newAge;
            try {
                newAge = Integer.parseInt(ageField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter a valid age (number only).", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Full Name cannot be empty.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Save to MySQL
            if (updateUserInDB(newName, newAge, newBlood, newHeight, newWeight, newGender)) {
                // Re-fetch database data to get fresh state
                this.userData = StockManager.getUserByEmail(userEmail);
                
                // Refresh on-screen labels instantly
                if (this.userData != null) {
                    nameLabel.setText(this.userData.fullname);
                    ageLbl.setText(this.userData.age + " Years");
                    bloodLbl.setText(this.userData.bloodGroup != null && !this.userData.bloodGroup.equals("Select") ? this.userData.bloodGroup : "N/A");
                    heightLbl.setText(this.userData.height + " cm");
                    weightLbl.setText(this.userData.weight + " Kg");
                    genderLbl.setText(this.userData.gender);
                    
                    // Live sync with dashboard
                    if (parentDashboard != null) {
                        parentDashboard.setUserName(this.userData.fullname);
                    }
                }
                
                dialog.dispose();
                JOptionPane.showMessageDialog(this,
                        "✅ Profile updated successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        btnPanel.add(saveBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // -------------------------------------------------------
    // SQL UPDATE — saves edited profile to users table
    // -------------------------------------------------------
    private boolean updateUserInDB(String name, int age, String blood, String height, String weight, String gender) {
        String sql = "UPDATE users SET fullname=?, age=?, bloodGroup=?, height=?, weight=?, gender=? WHERE email=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, blood);
            pstmt.setString(4, height);
            pstmt.setString(5, weight);
            pstmt.setString(6, gender);
            pstmt.setString(7, userEmail);
            pstmt.executeUpdate();

            // Also update in-memory userData cache
            if (userData != null) {
                userData.fullname   = name;
                userData.age        = age;
                userData.bloodGroup = blood;
                userData.height     = height;
                userData.weight     = weight;
                userData.gender     = gender;
            }
            System.out.println("✅ Profile updated in DB for: " + userEmail);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "❌ Database error while saving profile.", "DB Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // -------------------------------------------------------
    // HELPER UI BUILDERS
    // -------------------------------------------------------
    private JLabel makeValueLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(new Color(33, 37, 41));
        return lbl;
    }

    private JPanel createDetailCard(String title, JLabel valueLbl, String icon) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLbl.setForeground(new Color(108, 117, 125));

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
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(bg); }
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
}
