import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Simple Chatbot Page for MediCare 💊
 * Built with standard Java Swing for easy viva explanations.
 */
public class ChatbotPage extends JFrame {

    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendBtn;

    public ChatbotPage() {
        // Frame Setup
        setTitle("MediCare - Chatbot Assistant");
        setSize(550, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Closes only this window

        // Main Panel with Gradient Background
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- HEADER SECTION ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("MediCare Assistant 🤖", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 102, 204));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JLabel subLabel = new JLabel("Ask me anything about common medicines or app features!", SwingConstants.CENTER);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(new Color(108, 117, 125));
        headerPanel.add(subLabel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- CHAT DISPLAY AREA ---
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chatArea.setBackground(new Color(255, 255, 255));
        chatArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Scroll pane for chat history
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Welcome message from the bot
        chatArea.append("Bot: Hello! I am your personal MediCare assistant. 👋\n");
        chatArea.append("How can I help you today? You can try asking me about:\n");
        chatArea.append("- fever / headache / pain / cold\n");
        chatArea.append("- missed dose\n");
        chatArea.append("- how to add medicine\n");
        chatArea.append("- general health tips\n");
        chatArea.append("---------------------------------------------------------------------------\n\n");

        // --- INPUT SECTION ---
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setOpaque(false);

        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        inputPanel.add(inputField, BorderLayout.CENTER);

        sendBtn = new JButton("Send 🚀");
        sendBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendBtn.setBackground(new Color(186, 224, 255)); // Light Blue
        sendBtn.setFocusPainted(false);
        sendBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        inputPanel.add(sendBtn, BorderLayout.EAST);

        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        // --- EVENT HANDLING (Action Listeners) ---
        ActionListener sendAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processMessage();
            }
        };

        sendBtn.addActionListener(sendAction);
        inputField.addActionListener(sendAction); // Allows pressing "Enter" key

        add(mainPanel);
    }

    private void processMessage() {
        String userText = inputField.getText().trim();
        if (userText.isEmpty()) {
            return;
        }

        // Show User Message
        chatArea.append("You: " + userText + "\n");
        inputField.setText(""); // Clear input box

        // Convert query to lower case for easy keyword checking
        String query = userText.toLowerCase();
        String botResponse = "";

        // --- BASIC IF-ELSE CHATBOT LOGIC ---
        if (query.contains("hello") || query.contains("hi") || query.contains("hey")) {
            botResponse = "Bot: Hello there! I am your MediCare Health Assistant. How can I help you today? 😊";
        } 
        else if (query.contains("fever") || query.contains("paracetamol") || query.contains("headache") || query.contains("pain")) {
            botResponse = "Bot: 💊 Paracetamol is commonly used for fever and mild pain. Take it after meals to prevent acidity. Seek immediate medical attention if fever exceeds 101°F or lasts more than 3 days.";
        } 
        else if (query.contains("cough") || query.contains("cold") || query.contains("throat")) {
            botResponse = "Bot: 🍵 For a cold or cough, stay hydrated with warm water. Steam inhalation, ginger tea, or honey can provide quick natural relief. Avoid cold food and drinks.";
        } 
        else if (query.contains("acidity") || query.contains("stomach") || query.contains("gas")) {
            botResponse = "Bot: 🥛 Antacids are best taken in the morning on an empty stomach or 1 hour after meals. Avoid spicy food, carbonated drinks, or lying down right after eating.";
        } 
        else if (query.contains("vitamin") || query.contains("supplement")) {
            botResponse = "Bot: 🍎 Multivitamins and health supplements are best absorbed by the body when taken in the morning with a healthy breakfast!";
        } 
        else if (query.contains("missed") || query.contains("forget") || query.contains("skip")) {
            botResponse = "Bot: ⚠️ If you forget to take a pill, take it as soon as you remember. However, if it's already close to your next scheduled dose, skip the missed one. Never take a double dose to make up for a missed one.";
        } 
        else if (query.contains("add") || query.contains("new medicine")) {
            botResponse = "Bot: ➕ To add a new medicine:\n1. Click the 'Add Medicine' button on your sidebar.\n2. Enter the medicine name and schedule time.\n3. Click 'Add Medicine' to save it.";
        } 
        else if (query.contains("progress") || query.contains("score") || query.contains("streak")) {
            botResponse = "Bot: 📊 Your Adherence Score in the Progress Report tracks your medication schedule efficiency. Keep taking your medicines on time to maintain your fire 🔥 streak!";
        } 
        else if (query.contains("water") || query.contains("hydration")) {
            botResponse = "Bot: 💧 Drinking 8-10 glasses of water daily helps keep your kidneys clean and processes medications safely inside your body.";
        } 
        else if (query.contains("exercise") || query.contains("walk")) {
            botResponse = "Bot: 🏃 A simple 20-30 minute walk every day boosts blood circulation, increases energy, and keeps your heart strong.";
        } 
        else {
            botResponse = "Bot: 🔍 I'm sorry, I couldn't find a direct answer for that. Please try asking me about:\n- fever or paracetamol\n- missed dose\n- general health tips\n- how to add a medicine";
        }

        // Show Bot Response
        chatArea.append(botResponse + "\n");
        chatArea.append("---------------------------------------------------------------------------\n\n");
        
        // Auto-scroll to the bottom of the chat
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    // Custom Panel for Gradient Background
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
        SwingUtilities.invokeLater(() -> new ChatbotPage().setVisible(true));
    }
}
