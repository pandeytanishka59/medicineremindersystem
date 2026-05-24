import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Add Medicine Page for MediCare 💊
 * Allows users to input new medication details, type, and stock limits.
 */
public class AddMedicinePage extends JFrame {

    private JTextField nameField, dosageField, timeField;
    private JComboBox<String> freqCombo;
    private JComboBox<String> typeCombo;
    private JComboBox<String> unitCombo;
    private JLabel purchasedLabel;
    private JLabel dailyDosageLabel;
    private JTextField purchasedField;
    private JTextField dailyDosageField;
    private JComboBox<String> scheduleDateCombo;
    private JSpinner scheduleDateSpinner;
    private DashboardPage parentDashboard;

    public AddMedicinePage(DashboardPage dashboard) {
        this.parentDashboard = dashboard;

        setTitle("MediCare - Add New Medicine");
        setSize(520, 820); // Slightly increased size to fit new fields beautifully
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Main Panel with Gradient
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

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
        gbc.insets = new Insets(6, 0, 4, 0);
        gbc.gridx = 0;

        // 1. Medicine Name
        addLabel(formPanel, "Medicine Name:", 0);
        nameField = createStyledTextField("e.g. Paracetamol");
        gbc.gridy = 1; formPanel.add(nameField, gbc);

        // 2. Medicine Form / Unit
        addLabel(formPanel, "Medicine Form / Unit:", 2);
        String[] units = {"Tablets", "Capsules", "Syrup (ml)", "Injection (ml)", "Other"};
        unitCombo = new JComboBox<>(units);
        unitCombo.setPreferredSize(new Dimension(0, 35));
        unitCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 3; formPanel.add(unitCombo, gbc);

        // 3. Dosage
        addLabel(formPanel, "Dosage (e.g. 500mg or 5ml):", 4);
        dosageField = createStyledTextField("e.g. 1 tablet or 5ml");
        gbc.gridy = 5; formPanel.add(dosageField, gbc);

        // 4. Reminder Time
        addLabel(formPanel, "Reminder Time (HH:mm AM/PM):", 6);
        timeField = createStyledTextField("e.g. 08:30 AM");
        gbc.gridy = 7; formPanel.add(timeField, gbc);

        // 5. Frequency
        addLabel(formPanel, "Frequency:", 8);
        String[] frequencies = {"Daily", "Twice a Day", "Weekly", "Only Once"};
        freqCombo = new JComboBox<>(frequencies);
        freqCombo.setPreferredSize(new Dimension(0, 35));
        freqCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 9; formPanel.add(freqCombo, gbc);

        // 6. Medicine Type
        addLabel(formPanel, "Medicine Type:", 10);
        String[] types = {"Long-Term Medicine", "Short-Term Medicine"};
        typeCombo = new JComboBox<>(types);
        typeCombo.setPreferredSize(new Dimension(0, 35));
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 11; formPanel.add(typeCombo, gbc);

        // 7. Purchased Quantity Label & Field
        purchasedLabel = new JLabel("Tablets Purchased Quantity:");
        purchasedLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        GridBagConstraints lblGbc1 = new GridBagConstraints();
        lblGbc1.gridx = 0; lblGbc1.gridy = 12;
        lblGbc1.anchor = GridBagConstraints.WEST;
        lblGbc1.insets = new Insets(8, 0, 2, 0);
        formPanel.add(purchasedLabel, lblGbc1);

        purchasedField = createStyledTextField("e.g. 10");
        purchasedField.setText("10"); // Default value
        gbc.gridy = 13; formPanel.add(purchasedField, gbc);

        // 8. Daily Dosage Label & Field
        dailyDosageLabel = new JLabel("Daily Dosage (Tablets per Day):");
        dailyDosageLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        GridBagConstraints lblGbc2 = new GridBagConstraints();
        lblGbc2.gridx = 0; lblGbc2.gridy = 14;
        lblGbc2.anchor = GridBagConstraints.WEST;
        lblGbc2.insets = new Insets(8, 0, 2, 0);
        formPanel.add(dailyDosageLabel, lblGbc2);

        dailyDosageField = createStyledTextField("e.g. 1");
        dailyDosageField.setText("1"); // Default value
        gbc.gridy = 15; formPanel.add(dailyDosageField, gbc);

        // 9. Reminder Date / Calendar option
        addLabel(formPanel, "Schedule / Reminder Date:", 16);
        JPanel schedulePanel = new JPanel(new BorderLayout(10, 0));
        schedulePanel.setOpaque(false);

        scheduleDateCombo = new JComboBox<>(new String[]{"Every Day", "Today Only", "Tomorrow Only", "Specific Date"});
        scheduleDateCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        scheduleDateCombo.setPreferredSize(new Dimension(160, 35));

        scheduleDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(scheduleDateSpinner, "yyyy-MM-dd");
        scheduleDateSpinner.setEditor(dateEditor);
        scheduleDateSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        scheduleDateSpinner.setPreferredSize(new Dimension(160, 35));
        scheduleDateSpinner.setVisible(false);

        schedulePanel.add(scheduleDateCombo, BorderLayout.WEST);
        schedulePanel.add(scheduleDateSpinner, BorderLayout.CENTER);
        gbc.gridy = 17; formPanel.add(schedulePanel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Footer Buttons
        JPanel footer = new JPanel(new GridLayout(1, 2, 15, 0));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton addBtn = createStyledButton("Add Medicine", new Color(40, 167, 69));
        JButton cancelBtn = createStyledButton("Cancel", new Color(220, 53, 69));

        footer.add(addBtn);
        footer.add(cancelBtn);
        mainPanel.add(footer, BorderLayout.SOUTH);

        add(mainPanel);

        // --- Action Listeners ---
        
        // Dynamic labels based on Selected Form / Unit
        unitCombo.addActionListener(e -> {
            String selected = (String) unitCombo.getSelectedItem();
            if (selected.contains("(ml)")) {
                purchasedLabel.setText("Volume Purchased (ml):");
                dailyDosageLabel.setText("Daily Dosage (ml per Day):");
            } else if ("Capsules".equals(selected)) {
                purchasedLabel.setText("Capsules Purchased Quantity:");
                dailyDosageLabel.setText("Daily Dosage (Capsules per Day):");
            } else if ("Tablets".equals(selected)) {
                purchasedLabel.setText("Tablets Purchased Quantity:");
                dailyDosageLabel.setText("Daily Dosage (Tablets per Day):");
            } else {
                purchasedLabel.setText("Quantity Purchased:");
                dailyDosageLabel.setText("Daily Dosage (per Day):");
            }
        });

        // Show date spinner only for "Specific Date"
        scheduleDateCombo.addActionListener(e -> {
            boolean isSpecific = "Specific Date".equals(scheduleDateCombo.getSelectedItem());
            scheduleDateSpinner.setVisible(isSpecific);
            schedulePanel.revalidate();
            schedulePanel.repaint();
        });

        addBtn.addActionListener(e -> saveMedicine());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void addLabel(JPanel panel, String text, int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 0, 2, 0);
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
        String name = nameField.getText().trim();
        String dosage = dosageField.getText().trim();
        String time = timeField.getText().trim();
        String type = (String) typeCombo.getSelectedItem();
        String purchasedStr = purchasedField.getText().trim();
        String dailyDosageStr = dailyDosageField.getText().trim();
        String selectedUnit = (String) unitCombo.getSelectedItem();
        
        if (name.isEmpty() || dosage.isEmpty() || time.isEmpty() || purchasedStr.isEmpty() || dailyDosageStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Parse double values safely by stripping any unit string (e.g. "ml", "tablets")
        double purchased = 10.0;
        double dailyDosage = 1.0;
        try {
            String cleanPurchased = purchasedStr.replaceAll("[^0-9.]", "");
            String cleanDaily = dailyDosageStr.replaceAll("[^0-9.]", "");

            if (cleanPurchased.isEmpty() || cleanDaily.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter numeric values for stock and dosage!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }

            purchased = Double.parseDouble(cleanPurchased);
            dailyDosage = Double.parseDouble(cleanDaily);
            if (purchased <= 0 || dailyDosage <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity and dosage must be greater than 0!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter numeric values for stock quantity and daily dosage!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Determine unit string for storage
        String unitStr = "tablets";
        if (selectedUnit.contains("(ml)")) {
            unitStr = "ml";
        } else if ("Capsules".equals(selectedUnit)) {
            unitStr = "capsules";
        } else if ("Other".equals(selectedUnit)) {
            unitStr = "units";
        }

        // Calculate schedule date string
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ENGLISH);
        Date simDate = parentDashboard.getSimulatedDate();
        String scheduleDateText = "Every Day";
        String scheduleType = (String) scheduleDateCombo.getSelectedItem();

        if ("Today Only".equals(scheduleType)) {
            scheduleDateText = "Today (" + sdf.format(simDate) + ")";
        } else if ("Tomorrow Only".equals(scheduleType)) {
            Date tomorrow = new Date(simDate.getTime() + 24 * 60 * 60 * 1000);
            scheduleDateText = "Tomorrow (" + sdf.format(tomorrow) + ")";
        } else if ("Specific Date".equals(scheduleType)) {
            Date selectedDate = (Date) scheduleDateSpinner.getValue();
            scheduleDateText = sdf.format(selectedDate);
        }

        String fullName = name + " (" + dosage + ")";

        // Add to dashboard table (now includes Schedule Date)
        parentDashboard.addMedicineToTable(fullName, scheduleDateText, time);

        // Register in StockManager
        StockManager.registerActiveStock(fullName, type, purchased, dailyDosage, unitStr, (String) freqCombo.getSelectedItem());
        
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
