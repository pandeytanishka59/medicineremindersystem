import javax.swing.*;
import java.awt.*;

/**
 * Add Medicine Page for MediCare 💊
 * Allows users to input new medication details.
 */
public class AddMedicinePage extends JFrame {

    private JTextField nameField, dosageField, timeField;
    private JComboBox<String> freqCombo;
    private DashboardPage parentDashboard;

    public AddMedicinePage(DashboardPage dashboard) {
        this.parentDashboard = dashboard;

        setTitle("MediCare - Add New Medicine");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Main Panel with Gradient
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Header
        JLabel headerLabel = new JLabel("Add New Medicine 💊", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(new Color(33, 37, 41));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 5, 0);
        gbc.gridx = 0;

        // Fields
        addLabel(formPanel, "Medicine Name:", 0);
        nameField = createStyledTextField("e.g. Paracetamol");
        gbc.gridy = 1; formPanel.add(nameField, gbc);

        addLabel(formPanel, "Dosage (e.g. 500mg):", 2);
        dosageField = createStyledTextField("e.g. 1 Tablet");
        gbc.gridy = 3; formPanel.add(dosageField, gbc);

        addLabel(formPanel, "Reminder Time (HH:mm AM/PM):", 4);
        timeField = createStyledTextField("e.g. 08:30 AM");
        gbc.gridy = 5; formPanel.add(timeField, gbc);

        addLabel(formPanel, "Frequency:", 6);
        String[] frequencies = {"Daily", "Twice a Day", "Weekly", "Only Once"};
        freqCombo = new JComboBox<>(frequencies);
        freqCombo.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 7; formPanel.add(freqCombo, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Footer Buttons
        JPanel footer = new JPanel(new GridLayout(1, 2, 15, 0));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton addBtn = createStyledButton("Add Medicine", new Color(40, 167, 69));
        JButton cancelBtn = createStyledButton("Cancel", new Color(220, 53, 69));

        footer.add(addBtn);
        footer.add(cancelBtn);
        mainPanel.add(footer, BorderLayout.SOUTH);

        add(mainPanel);

        // Actions
        addBtn.addActionListener(e -> saveMedicine());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void addLabel(JPanel panel, String text, int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 2, 0);
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(label, gbc);
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(20);
        field.setPreferredSize(new Dimension(0, 35));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(new Color(33, 37, 41)); // Dark Text
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return btn;
    }

    private void saveMedicine() {
        String name = nameField.getText();
        String dosage = dosageField.getText();
        String time = timeField.getText();
        
        if (name.isEmpty() || dosage.isEmpty() || time.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Add to dashboard table
        parentDashboard.addMedicineToTable(name + " (" + dosage + ")", time);
        
        JOptionPane.showMessageDialog(this, "Medicine Added Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
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
