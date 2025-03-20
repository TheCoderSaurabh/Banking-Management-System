package components;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class ShowBankDetail extends JPanel implements ActionListener {
    private JTextField accountNumber, accHolderName;
    private JButton showDetail, goBack;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Connection conn;

    public ShowBankDetail(CardLayout cardLayout, JPanel mainPanel, Connection conn) {
        this.cardLayout = cardLayout;
        this.conn = conn;
        this.mainPanel = mainPanel;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
                "Show Account Details",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16),
                Color.BLUE));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Account Number Input
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Account Number:"), gbc);
        gbc.gridx = 1;
        accountNumber = new JTextField(20);
        add(accountNumber, gbc);

        // Account Holder Name Input
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Account Holder Name:"), gbc);
        gbc.gridx = 1;
        accHolderName = new JTextField(20);
        add(accHolderName, gbc);

        // "Go Back" Button
        gbc.gridx = 0;
        gbc.gridy = 2;
        goBack = new JButton("Go Back");
        goBack.addActionListener(e -> cardLayout.show(mainPanel, "AdminHome"));
        add(goBack, gbc);

        // "Show Detail" Button
        gbc.gridx = 1;
        showDetail = new JButton("Show Transactions");
        showDetail.setBackground(Color.GREEN);
        showDetail.setForeground(Color.WHITE);
        showDetail.addActionListener(this);
        add(showDetail, gbc);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == showDetail) {
                showDetails();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showDetails() {
        String accNumber = accountNumber.getText().trim();
        String accountHolderName = accHolderName.getText().trim();

        if (accNumber.isEmpty() || accountHolderName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int accNo = Integer.parseInt(accNumber);
            // Step 1: Fetch the Actual Account Holder Name from DB
            String fetchNameQuery = "SELECT account_holder_name FROM accounts WHERE account_number = ?";
            PreparedStatement nameStmt = conn.prepareStatement(fetchNameQuery);
            nameStmt.setInt(1, accNo);
            ResultSet nameRs = nameStmt.executeQuery();

            if (!nameRs.next()) {
                JOptionPane.showMessageDialog(this, "No account found with this account number.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String actualAccountHolderName = nameRs.getString("account_holder_name"); // Fetched Name from DB
            nameStmt.close();

            // Step 2: Compare User-Entered Name with DB Name
            if (!actualAccountHolderName.equalsIgnoreCase(accountHolderName)) {
                JOptionPane.showMessageDialog(this, "Account Holder Name does not match the account number!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // SQL Query to Fetch Transactions (Sort by Date Descending)
            String query = "SELECT transaction_date, transaction_type, amount FROM transactions WHERE account_number = ? ORDER BY transaction_date DESC";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, accNo);
            ResultSet rs = pst.executeQuery();

            // If No Transactions Exist
            if (!rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(this, "No transactions found for this account.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create JFrame to Show Transactions
            JFrame detailsFrame = new JFrame("Transaction Details");
            detailsFrame.setSize(650, 400);
            detailsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // Table Model
            String[] columnNames = {  "Date", "Type", "Amount (₹)"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            while (rs.next()) {
                String date = rs.getString("transaction_date");
                String type = rs.getString("transaction_type");
                double amount = rs.getDouble("amount");
                model.addRow(new Object[] { date, type, String.format("₹%.2f", amount) });
            }

            // Create JTable and Add to JScrollPane
            JTable table = new JTable(model);

            // **Auto Resize Column Widths for Readability**
            TableColumnModel columnModel = table.getColumnModel();
            // columnModel.getColumn(0).setPreferredWidth(100); // Transaction ID
            columnModel.getColumn(0).setPreferredWidth(200); // Date
            columnModel.getColumn(1).setPreferredWidth(150); // Type
            columnModel.getColumn(2).setPreferredWidth(100); // Amount

            JScrollPane scrollPane = new JScrollPane(table);
            detailsFrame.add(scrollPane);
            detailsFrame.setVisible(true);

            // Close Resources
            rs.close();
            pst.close();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid account number!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
