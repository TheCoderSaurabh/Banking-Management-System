import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import components.*;

public class BankSystem extends JFrame implements ActionListener {
    private JPanel mainPanel;
    private JLabel adminLabel;
    private CardLayout cardLayout;
    private JTextField nameField;
    private JButton submitButton, backButton, createAccountBtn, depositBtn, withdrawBtn, deleteAccountBtn, viewAccountsBtn, viewDetailsBtn;
    private Connection conn;
    String adminName;

    public BankSystem() {
        initializeDatabase();
        setTitle("Bank Management System");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // CardLayout setup
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        //creating panels
        CreateAccount createAccountPanel = new CreateAccount(cardLayout, mainPanel, conn);

        // Adding different panels
        mainPanel.add(createHomePanel(), "Home");
        mainPanel.add(createAdminPanel(), "AdminHome");
        mainPanel.add(createAccountPanel, "CreateAccount");

        add(mainPanel);
        setVisible(true);
    }

    private void initializeDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "root", "bestfriend123");
            System.out.println("Successfully connected to database...");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // === Add Image at the Top ===
        // === Add Image at the Top ===
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across both columns
        // ImageIcon icon = new ImageIcon("C:/Users/Administrator/Desktop/Projects/jdbc/BankManagementSystem/src/images/bank_logo.png"); // Change to your image path
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/bank_logo.png"));

        Image image = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH); // Resize
        JLabel imageLabel = new JLabel(new ImageIcon(image));
        panel.add(imageLabel, gbc);


        // === Add Title Below Image ===
        gbc.gridy = 1;
        JLabel titleLabel = new JLabel("Admin Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, gbc);

        // === Labels and Input Fields ===
        gbc.gridwidth = 1; // Reset to default
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("User ID:"), gbc);

        gbc.gridx = 1;
        JTextField adminId = new JTextField(15);
        panel.add(adminId, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        JPasswordField adminPass = new JPasswordField(15);
        panel.add(adminPass, gbc);

        // === Login Button ===
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JButton adminSubmit = new JButton("Login");
        adminSubmit.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(adminSubmit, gbc);

        // === Action Listener ===
        adminSubmit.addActionListener(e -> {
            String enteredId = adminId.getText();
            String enteredPass = new String(adminPass.getPassword()); // Correct way to get password
            try {
                // Query to check user credentials
                String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, enteredId);
                pstmt.setString(2, enteredPass);
    
                // Execute query
                ResultSet rs = pstmt.executeQuery();
    
                if (rs.next()) {
                    adminName=enteredId;
                    adminLabel.setText("Admin ID: " + adminName);

                    System.out.println("Login successful!" );
                    cardLayout.show(mainPanel, "AdminHome");
                } else {
                    System.out.println("Invalid username or password.");
                    JOptionPane.showMessageDialog(panel, "Invalid Credentials!", "Error", JOptionPane.ERROR_MESSAGE);
                }
    
                // Close resources
                rs.close();
                pstmt.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            // if (enteredId.equals(adminIdValue) && enteredPass.equals(adminPassword)) {
            //     cardLayout.show(mainPanel, "AdminHome");
            // } else {
            //     JOptionPane.showMessageDialog(panel, "Invalid Credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            // }
        });
        

        return panel;
    }

    private JPanel createAdminPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 1, 10, 10));

        // Display Admin ID
        adminLabel = new JLabel("Admin ID: ", SwingConstants.CENTER);

        panel.add(adminLabel);

        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        // Initialize Buttons
        createAccountBtn = new JButton("Create Account");
        depositBtn = new JButton("Deposit");
        withdrawBtn = new JButton("Withdraw");
        deleteAccountBtn = new JButton("Delete Account");
        viewAccountsBtn = new JButton("View Accounts");
        viewDetailsBtn = new JButton("View Account Details");
        backButton = new JButton("Back");

        // Add Buttons to Panel
        panel.add(createAccountBtn);
        panel.add(depositBtn);
        panel.add(withdrawBtn);
        panel.add(deleteAccountBtn);
        panel.add(viewAccountsBtn);
        panel.add(viewDetailsBtn);
        panel.add(backButton);

        // Add Action Listeners in a separate method
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Home"));
        createAccountBtn.addActionListener(e -> cardLayout.show(mainPanel, "CreateAccount"));

        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Button actions handled inside each panel
        if (e.getSource() == submitButton) {
            // Get text from the JTextField
            String name = nameField.getText();
            JOptionPane.showMessageDialog(this, "You entered: " + name);
        }
    }

    public static void main(String[] args) {
        new BankSystem();
    }
}

