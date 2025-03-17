package components;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class DeleteAccount extends JPanel implements ActionListener {
    private JTextField accountNumberField, phoneNumberField;
    private JButton goBackBtn, deleteAccountBtn;
    private Connection conn;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public DeleteAccount(CardLayout cardLayout, JPanel mainPanel, Connection conn) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.conn = conn;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
                "Delete Account", 
                TitledBorder.CENTER, 
                TitledBorder.TOP, 
                new Font("Arial", Font.BOLD, 14), 
                Color.RED
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Mobile Number Field
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Mobile No.:"), gbc);
        gbc.gridx = 1;
        phoneNumberField = new JTextField(20);
        add(phoneNumberField, gbc);

        // Account Number Field
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Account Number:"), gbc);
        gbc.gridx = 1;
        accountNumberField = new JTextField(20);
        add(accountNumberField, gbc);

        // "Go Back" Button
        gbc.gridx = 0; gbc.gridy = 2;
        goBackBtn = new JButton("Go Back");
        goBackBtn.addActionListener(e -> cardLayout.show(mainPanel, "AdminHome")); 
        add(goBackBtn, gbc);

        // "Delete Account" Button
        gbc.gridx = 1;
        deleteAccountBtn = new JButton("Delete Account");
        deleteAccountBtn.setBackground(Color.RED);
        deleteAccountBtn.setForeground(Color.WHITE);
        deleteAccountBtn.addActionListener(this);
        add(deleteAccountBtn, gbc);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == deleteAccountBtn) {
            deleteAccount();
        }
    }

    private void deleteAccount() {
        String accountNumber = accountNumberField.getText().trim();
        String phone = phoneNumberField.getText().trim();

        // Validation: Ensure both fields are filled
        if (accountNumber.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Account Number and Mobile Number!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Confirm before deletion
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this account?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // SQL Query to Delete the Account
            String deleteQuery = "DELETE FROM accounts WHERE account_number = ? AND phone = ?";
            PreparedStatement pst = conn.prepareStatement(deleteQuery);
            pst.setInt(1, Integer.parseInt(accountNumber));
            pst.setLong(2, Long.parseLong(phone));

            int rowsDeleted = pst.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Account Deleted Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Account Not Found! Please check your details.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            pst.close();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Account Number or Phone Number!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error Deleting Account: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
