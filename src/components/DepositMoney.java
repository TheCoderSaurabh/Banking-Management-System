package components;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class DepositMoney extends JPanel implements ActionListener {
    private JTextField accountNumberField, phoneNumberField, amountField;
    private JButton goBackBtn, depositBtn;
    private Connection conn;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public DepositMoney(CardLayout cardLayout, JPanel mainPanel, Connection conn) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.conn = conn;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
                "Deposit Money", 
                TitledBorder.CENTER, 
                TitledBorder.TOP, 
                new Font("Arial", Font.BOLD, 14), 
                Color.BLUE
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
        
        // Amount Field
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Deposit Amount:"), gbc);
        gbc.gridx = 1;
        amountField = new JTextField(20);
        add(amountField, gbc);

        // "Go Back" Button
        gbc.gridx = 0; gbc.gridy = 3;
        goBackBtn = new JButton("Go Back");
        goBackBtn.addActionListener(e -> cardLayout.show(mainPanel, "AdminHome")); 
        add(goBackBtn, gbc);

        // "Deposit Money" Button
        gbc.gridx = 1;
        depositBtn = new JButton("Deposit Money");
        depositBtn.setBackground(Color.GREEN);
        depositBtn.setForeground(Color.WHITE);
        depositBtn.addActionListener(this);
        add(depositBtn, gbc);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == depositBtn) {
            depositMoney();
        }
    }

    private void depositMoney() {
        String accountNumber = accountNumberField.getText().trim();
        String phone = phoneNumberField.getText().trim();
        String amountText = amountField.getText().trim();

        // Validation: Ensure all fields are filled
        if (accountNumber.isEmpty() || phone.isEmpty() || amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Convert input values
            int accNumber = Integer.parseInt(accountNumber);
            long phoneNum = Long.parseLong(phone);
            double amount = Double.parseDouble(amountText);

            // Ensure the deposit amount is valid
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than zero!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Confirm deposit action
            int confirm = JOptionPane.showConfirmDialog(this, "Confirm Deposit of ₹" + amount + "?", "Confirm Deposit", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // SQL Query to Update Balance
            String updateQuery = "UPDATE accounts SET balance = balance + ? WHERE account_number = ? AND phone = ?";
            PreparedStatement pst = conn.prepareStatement(updateQuery);
            pst.setDouble(1, amount);
            pst.setInt(2, accNumber);
            pst.setLong(3, phoneNum);

            int rowsUpdated = pst.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "₹" + amount + " Deposited Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(mainPanel, "AdminHome");
            } else {
                JOptionPane.showMessageDialog(this, "Account Not Found! Please check details.", "Error", JOptionPane.ERROR_MESSAGE);
                cardLayout.show(mainPanel, "AdminHome");

            }

            pst.close();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input! Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error Depositing Amount: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
