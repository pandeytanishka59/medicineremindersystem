import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Unified Stock & Leftovers Cabinet Page for MediCare 📦
 * Shows both active prescription stocks (with refill option) and leftover cabinet.
 */
public class StockPage extends JFrame {

    private JTabbedPane tabbedPane;
    
    // Tab 1: Active Stocks
    private JTable activeTable;
    private DefaultTableModel activeTableModel;
    private JButton refillBtn;

    // Tab 2: Leftovers Cabinet
    private JTable leftoverTable;
    private DefaultTableModel leftoverTableModel;
    private JButton updateLocBtn;

    public StockPage() {
        setTitle("MediCare - Medicine Stock Box");
        setSize(850, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Section
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Medicine Stock Box 📦", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(33, 37, 41));
        
        JLabel subtitleLabel = new JLabel("Monitor active prescription stock levels, refill quantities, or check bachi hui (leftover) medicines.", SwingConstants.LEFT);
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        subtitleLabel.setForeground(new Color(100, 110, 120));
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane Setup
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Create Tabs
        tabbedPane.addTab("💊 Active Prescription Stocks", createActiveStockTab());
        tabbedPane.addTab("📦 Leftovers Cabinet", createLeftoverTab());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Footer close button
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        JButton closeBtn = createStyledButton("Close Cabinet", new Color(108, 117, 125));
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);
        mainPanel.add(footer, BorderLayout.SOUTH);

        add(mainPanel);

        // Initial Data Load from Database
        StockManager.loadFromDatabase();
        refreshActiveTable();
        refreshLeftoverTable();
    }

    private JPanel createActiveStockTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Medicine Name", "Stock Remaining", "Daily Dosage", "Estimated Days Left", "Type"};
        activeTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        activeTable = new JTable(activeTableModel);
        activeTable.setRowHeight(35);
        activeTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        activeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        activeTable.setSelectionBackground(new Color(230, 242, 255));

        // Center renderers
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        activeTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        activeTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        // Custom renderer for Days Remaining column (Index 3) to highlight low stocks (<= 2 days)
        activeTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                try {
                    String valStr = (String) value;
                    if (valStr != null && !valStr.equals("N/A") && !valStr.contains("Unlimited")) {
                        double days = Double.parseDouble(valStr.split(" ")[0]);
                        if (days <= 2.0) {
                            c.setForeground(new Color(220, 53, 69)); // Dark Red warning
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        } else if (days <= 5.0) {
                            c.setForeground(new Color(230, 130, 0)); // Orange alert
                        } else {
                            c.setForeground(new Color(40, 167, 69)); // Green okay
                        }
                    } else {
                        c.setForeground(Color.BLACK);
                    }
                } catch (Exception ex) {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(activeTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Controls
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setOpaque(false);
        refillBtn = createStyledButton("Refill Stock ➕", new Color(40, 167, 69));
        refillBtn.addActionListener(e -> refillStock());
        btnPanel.add(refillBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createLeftoverTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Medicine Name", "Quantity Available", "Storage Location"};
        leftoverTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        leftoverTable = new JTable(leftoverTableModel);
        leftoverTable.setRowHeight(35);
        leftoverTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        leftoverTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        leftoverTable.setSelectionBackground(new Color(230, 242, 255));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        leftoverTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(leftoverTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setOpaque(false);
        updateLocBtn = createStyledButton("Update Storage Location 📍", new Color(0, 123, 255));
        updateLocBtn.addActionListener(e -> updateStorageLocation());
        btnPanel.add(updateLocBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshActiveTable() {
        activeTableModel.setRowCount(0);
        for (String medName : StockManager.tabletsRemaining.keySet()) {
            double remaining = StockManager.tabletsRemaining.get(medName);
            double daily = StockManager.dailyDosage.getOrDefault(medName, 1.0);
            String unit = StockManager.medicineUnit.getOrDefault(medName, "tablets");
            String type = StockManager.medicineType.getOrDefault(medName, "Long-Term Medicine");

            double daysLeftDouble = StockManager.getDaysRemaining(medName);
            String daysLeftStr;
            if (daysLeftDouble >= 999.0) {
                daysLeftStr = "Unlimited";
            } else {
                daysLeftStr = String.format("%.1f days", daysLeftDouble);
            }

            activeTableModel.addRow(new Object[]{
                medName,
                formatQty(remaining, unit),
                formatQty(daily, unit) + "/day",
                daysLeftStr,
                type
            });
        }
    }

    private void refreshLeftoverTable() {
        leftoverTableModel.setRowCount(0);
        for (StockManager.LeftoverMed med : StockManager.leftoverList) {
            leftoverTableModel.addRow(new Object[]{
                med.name,
                formatQty(med.quantity, med.unit),
                med.location
            });
        }
    }

    private void refillStock() {
        int selectedRow = activeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medicine from the active stock table first!", "No Row Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String medName = (String) activeTableModel.getValueAt(selectedRow, 0);
        String unit = StockManager.medicineUnit.getOrDefault(medName, "tablets");

        String amountStr = JOptionPane.showInputDialog(this, 
                "Enter quantity to add to " + medName + " (" + unit + "):",
                "Refill Stock", JOptionPane.QUESTION_MESSAGE);

        if (amountStr != null && !amountStr.trim().isEmpty()) {
            try {
                String cleanStr = amountStr.replaceAll("[^0-9.]", "");
                if (cleanStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                double amount = Double.parseDouble(cleanStr);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Refill quantity must be greater than 0!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double currentStock = StockManager.tabletsRemaining.getOrDefault(medName, 0.0);
                double newStock = currentStock + amount;
                StockManager.updateActiveStock(medName, newStock);

                refreshActiveTable();
                
                JOptionPane.showMessageDialog(this, "Stock refilled successfully!", "Refill Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateStorageLocation() {
        int selectedRow = leftoverTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medicine row from the leftovers table first!", "No Row Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String medName = (String) leftoverTableModel.getValueAt(selectedRow, 0);
        String currentLocation = (String) leftoverTableModel.getValueAt(selectedRow, 2);

        String newLocation = JOptionPane.showInputDialog(this,
                "Update storage location for " + medName + ":",
                currentLocation);

        if (newLocation != null && !newLocation.trim().isEmpty()) {
            StockManager.updateLeftoverLocation(medName, newLocation.trim());

            refreshLeftoverTable();
            JOptionPane.showMessageDialog(this, "Storage location updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String formatQty(double qty, String unit) {
        String unitStr = unit != null ? unit.toLowerCase() : "tablets";
        if (qty == (long) qty) {
            return String.format("%d %s", (long) qty, unitStr);
        } else {
            return String.format("%.1f %s", qty, unitStr);
        }
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(new Color(33, 37, 41)); // Dark text
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.darker()),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        return btn;
    }

    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, new Color(240, 248, 255), 0, getHeight(), Color.WHITE);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
