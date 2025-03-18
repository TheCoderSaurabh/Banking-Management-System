package components;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class WithdrawMoney extends JPanel implements ActionListener {
    private JTextField accountNumberField, phoneNumberField, amountField;
    private JButton goBackBtn, withdrawBtn;
    private Connection conn;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public WithdrawMoney(CardLayout cardLayout, JPanel mainPanel, Connection conn) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.conn = conn;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
                "Withdraw Money",
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
        add(new JLabel("Withdraw Amount:"), gbc);
        gbc.gridx = 1;
        amountField = new JTextField(20);
        add(amountField, gbc);

        // "Go Back" Button
        gbc.gridx = 0; gbc.gridy = 3;
        goBackBtn = new JButton("Go Back");
        goBackBtn.addActionListener(e -> cardLayout.show(mainPanel, "AdminHome"));
        add(goBackBtn, gbc);

        // "Withdraw Money" Button
        gbc.gridx = 1;
        withdrawBtn = new JButton("Withdraw Money");
        withdrawBtn.setBackground(Color.RED);
        withdrawBtn.setForeground(Color.WHITE);
        withdrawBtn.addActionListener(this);
        add(withdrawBtn, gbc);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == withdrawBtn) {
            withdrawMoney();
        }
    }

    private void withdrawMoney() {
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

            // Ensure the withdrawal amount is valid
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than zero!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // **Check Current Balance**
            String balanceQuery = "SELECT balance FROM accounts WHERE account_number = ? AND phone = ?";
            PreparedStatement balanceStmt = conn.prepareStatement(balanceQuery);
            balanceStmt.setInt(1, accNumber);
            balanceStmt.setLong(2, phoneNum);
            ResultSet rs = balanceStmt.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Account Not Found! Please check details.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double currentBalance = rs.getDouble("balance");

            // **Check if balance is sufficient**
            if (currentBalance < amount) {
                JOptionPane.showMessageDialog(this, "Insufficient Balance! Your current balance is ₹" + currentBalance, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Confirm withdrawal action
            int confirm = JOptionPane.showConfirmDialog(this, "Confirm Withdraw of ₹" + amount + "?", "Confirm Withdraw", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // **Update Balance**
            String updateQuery = "UPDATE accounts SET balance = balance - ? WHERE account_number = ? AND phone = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setDouble(1, amount);
            updateStmt.setInt(2, accNumber);
            updateStmt.setLong(3, phoneNum);

            int rowsUpdated = updateStmt.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "₹" + amount + " Withdrawn Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(mainPanel, "AdminHome");
            } else {
                JOptionPane.showMessageDialog(this, "Error Processing Withdrawal.", "Error", JOptionPane.ERROR_MESSAGE);
                cardLayout.show(mainPanel, "AdminHome");
            }

            // Close Statements
            balanceStmt.close();
            updateStmt.close();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input! Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error Withdrawing Amount: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
