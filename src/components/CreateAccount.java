package components;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class CreateAccount extends JPanel implements ActionListener {
    private JTextField fullNameField, uniqueIdField, phoneNumberField, ageField;
    private JButton goBackBtn, createAccountBtn;
    private Connection conn;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public CreateAccount(CardLayout cardLayout, JPanel mainPanel, Connection conn) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.conn = conn;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), // Border Style
                "Account Details", // Title Text
                TitledBorder.CENTER, // Title Alignment (CENTER)
                TitledBorder.TOP, // Title Position (TOP)
                new Font("Arial", Font.BOLD, 14), // Title Font
                Color.BLUE // Title Color
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Full Name
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        fullNameField = new JTextField(20);
        add(fullNameField, gbc);

        // Aadhar Number
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Unique ID (Aadhar):"), gbc);
        gbc.gridx = 1;
        uniqueIdField = new JTextField(20);
        add(uniqueIdField, gbc);

        // Mobile Number
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Mobile No.:"), gbc);
        gbc.gridx = 1;
        phoneNumberField = new JTextField(20);
        add(phoneNumberField, gbc);

        // Age
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Age:"), gbc);
        gbc.gridx = 1;
        ageField = new JTextField(5);
        add(ageField, gbc);

        // ✅ "Go Back" Button
        gbc.gridx = 0; gbc.gridy = 4;
        goBackBtn = new JButton("Go Back");
        goBackBtn.addActionListener(e -> cardLayout.show(mainPanel, "AdminHome")); // ✅ Navigate Back
        add(goBackBtn, gbc);

        // ✅ "Create Account" Button
        gbc.gridx = 1;
        createAccountBtn = new JButton("Create Account");
        createAccountBtn.addActionListener(this);
        add(createAccountBtn, gbc);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == createAccountBtn) {
            createAccount();
        }
    }

    private void createAccount() {
        String fullName = fullNameField.getText().trim();
        String uniqueId = uniqueIdField.getText().trim();
        String phone = phoneNumberField.getText().trim();
        String ageText = ageField.getText().trim();

        if (fullName.isEmpty() || uniqueId.isEmpty() || phone.isEmpty() || ageText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int age = Integer.parseInt(ageText);
            conn.setAutoCommit(false); //used to stop creating new account even if error occurs
            
            String insertQuery = "INSERT INTO accounts (account_holder_name, aadhar, phone, age) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, fullName);
            pst.setString(2, uniqueId);
            pst.setString(3, phone);
            pst.setInt(4, age);

            int rowsInserted = pst.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    int accountNumber = rs.getInt(1);
                    JOptionPane.showMessageDialog(this, "Account Created Successfully!\nAccount No: " + accountNumber, "Success", JOptionPane.INFORMATION_MESSAGE);
                    cardLayout.show(mainPanel, "AdminHome");
                }
            }
            pst.close();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
            try {
                conn.rollback();  // 🔄 Rollback transaction on error
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error Creating Account: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            try {
                conn.rollback();  // 🔄 Rollback transaction on error
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            try {
                conn.setAutoCommit(true);  // 🔄 Reset auto-commit mode
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    
        // catch (NumberFormatException ex) {
        //     JOptionPane.showMessageDialog(this, "Age must be a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
        // } catch (SQLException ex) {
        //     JOptionPane.showMessageDialog(this, "Error Creating Account: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        // }
    }
}
