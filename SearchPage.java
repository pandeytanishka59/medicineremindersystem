import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Medicine Search Page for MediCare 🔍
 * Allows users to search active and leftover stocks to prevent redundant purchases.
 */
public class SearchPage extends JFrame {

    private JTextField searchField;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JLabel alertLabel;
    private JPanel alertPanel;

    public SearchPage() {
        setTitle("MediCare - Search Stock & Leftovers");
        setSize(850, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main Gradient Panel
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Smart Medicine Search 🔍", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(33, 37, 41));
        
        JLabel descLabel = new JLabel("Search by name to see if you have leftover tablets or active stock at home.", SwingConstants.LEFT);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(new Color(110, 110, 110));
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(descLabel, BorderLayout.SOUTH);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Search Bar Area
        JPanel searchBarPanel = new JPanel(new BorderLayout(10, 0));
        searchBarPanel.setOpaque(false);
        searchBarPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchField.putClientProperty("JTextField.placeholderText", "Enter medicine name to search...");

        JButton searchBtn = new JButton("Search Stock 🔍");
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchBtn.setBackground(new Color(40, 167, 69));
        searchBtn.setForeground(new Color(33, 37, 41)); // Dark text
        searchBtn.setFocusPainted(false);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        searchBarPanel.add(searchField, BorderLayout.CENTER);
        searchBarPanel.add(searchBtn, BorderLayout.EAST);
        
        // Add components to layout container
        JPanel centerContainer = new JPanel(new BorderLayout(10, 10));
        centerContainer.setOpaque(false);
        centerContainer.add(searchBarPanel, BorderLayout.NORTH);

        // Smart Tip Alert Panel (Hidden by default)
        alertPanel = new JPanel(new BorderLayout());
        alertPanel.setBackground(new Color(230, 245, 233));
        alertPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(40, 167, 69), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        alertPanel.setVisible(false);

        alertLabel = new JLabel("");
        alertLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        alertLabel.setForeground(new Color(33, 100, 48));
        alertLabel.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
        alertPanel.add(alertLabel, BorderLayout.CENTER);

        centerContainer.add(alertPanel, BorderLayout.SOUTH);
        mainPanel.add(centerContainer, BorderLayout.WEST); // wait, let's keep searchbar at center top

        // Results Table
        String[] columns = {"Cabinet Section", "Medicine Name", "Stock Available", "Storage Location / Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resultsTable = new JTable(tableModel);
        resultsTable.setRowHeight(35);
        resultsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        resultsTable.setSelectionBackground(new Color(230, 242, 255));

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));
        
        JPanel resultsPanel = new JPanel(new BorderLayout(10, 10));
        resultsPanel.setOpaque(false);
        resultsPanel.add(alertPanel, BorderLayout.NORTH);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);
        
        centerContainer.add(resultsPanel, BorderLayout.CENTER);
        mainPanel.add(centerContainer, BorderLayout.CENTER);

        // Actions
        searchBtn.addActionListener(e -> performSearch());
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });

        // Initialize with all items visible
        performSearch();

        add(mainPanel);
    }

    private void performSearch() {
        String query = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        alertPanel.setVisible(false);

        boolean foundLeftover = false;
        String leftoverName = "";
        double leftoverQty = 0;
        String leftoverLoc = "";
        String leftoverUnit = "tablets";

        // 1. Search in Leftover Medicines
        for (StockManager.LeftoverMed med : StockManager.leftoverList) {
            if (query.isEmpty() || med.name.toLowerCase().contains(query)) {
                String qtyStr = formatQty(med.quantity, med.unit);
                tableModel.addRow(new Object[]{
                    "📦 Leftover Box",
                    med.name,
                    qtyStr,
                    med.location
                });

                if (!query.isEmpty() && med.name.toLowerCase().contains(query)) {
                    foundLeftover = true;
                    leftoverName = med.name;
                    leftoverQty = med.quantity;
                    leftoverLoc = med.location;
                    leftoverUnit = med.unit;
                }
            }
        }

        // 2. Search in Active Medicines
        for (String medName : StockManager.tabletsRemaining.keySet()) {
            if (query.isEmpty() || medName.toLowerCase().contains(query)) {
                double remaining = StockManager.tabletsRemaining.get(medName);
                String unit = StockManager.medicineUnit.getOrDefault(medName, "tablets");
                String type = StockManager.medicineType.getOrDefault(medName, "Regular");
                String qtyStr = formatQty(remaining, unit);
                tableModel.addRow(new Object[]{
                    "💊 Active Prescriptions",
                    medName,
                    qtyStr + " left",
                    "In Active Use (" + type + ")"
                });
            }
        }

        // 3. Display Smart Cost-Saving Tip if found in Leftovers!
        if (foundLeftover) {
            String qtyStr = formatQty(leftoverQty, leftoverUnit);
            alertLabel.setText("<html>💡 <b>Smart Saving Tip:</b> You already have <b>" + qtyStr + "</b> of <b>" + leftoverName + "</b> stored in the <b>\"" + leftoverLoc + "\"</b>! Use these instead of purchasing a new strip!</html>");
            alertPanel.setVisible(true);
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

    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, new Color(245, 250, 240), 0, getHeight(), Color.WHITE);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
