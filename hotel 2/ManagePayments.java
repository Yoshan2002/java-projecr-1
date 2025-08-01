import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ManagePayments extends JFrame {

    private JTextField txtID, txtFirstName, txtLastName, txtAmount, txtPaymentMethod;
    private JTable paymentsTable;  // Changed from 'table' to 'paymentsTable' to match variable name
    private DefaultTableModel tableModel;
    public int pid;
    public String fname;
    public String lname;
    public int amount;
    public String method;
    public Connection conn = null; 

    public ManagePayments() {
        setTitle("Manage Payments");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.PINK);
        
        Font defaultFont = new Font("SansSerif", Font.PLAIN, 16);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("Table.font", defaultFont);
        UIManager.put("TableHeader.font", defaultFont);

        JLabel lblTitle = new JLabel("Manage Payments", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setOpaque(true);
        lblTitle.setBackground(Color.DARK_GRAY);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 0, 800, 40);
        add(lblTitle);

        // Labels
        String[] labelNames = {"ID:", "First Name:", "Last Name:", "Amount:", "Payment Method:"};
        int y = 70;
        for (String text : labelNames) {
            JLabel lbl = new JLabel(text);
            lbl.setBounds(50, y, 100, 25);
            add(lbl);
            y += 40;
        }

        // Text fields
        txtID = new JTextField();
        txtFirstName = new JTextField();
        txtLastName = new JTextField();
        txtAmount = new JTextField();
        txtPaymentMethod = new JTextField();

        JTextField[] fields = {txtID, txtFirstName, txtLastName, txtAmount, txtPaymentMethod};
        y = 70;
        for (JTextField field : fields) {
            field.setBounds(180, y, 300, 25);
            add(field);
            y += 40;
        }

        // Buttons
        JButton btnSave = new JButton("Save");
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");

        btnSave.setBounds(100, 270, 100, 30);
        btnAdd.setBounds(210, 270, 100, 30);
        btnUpdate.setBounds(320, 270, 100, 30);
        btnDelete.setBounds(430, 270, 100, 30);

        add(btnSave);
        add(btnAdd);
        add(btnUpdate);
        add(btnDelete);

        // Table setup
        String[] columnNames = {"ID", "First Name", "Last Name", "Amount", "Payment Method"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        paymentsTable = new JTable(tableModel);  // Fixed variable name
        JScrollPane scrollPane = new JScrollPane(paymentsTable);  // Fixed variable name
        scrollPane.setBounds(50, 320, 680, 120);
        panel.add(scrollPane);

        // Button Actions
        btnAdd.addActionListener(e -> {
            String[] rowData = {
                    txtID.getText(),
                    txtFirstName.getText(),
                    txtLastName.getText(),
                    txtAmount.getText(),
                    txtPaymentMethod.getText()
            };
            tableModel.addRow(rowData);
        });

        btnSave.addActionListener(e -> {
            try {
                pid = Integer.parseInt(txtID.getText());
                fname = txtFirstName.getText();
                lname = txtLastName.getText();
                amount = Integer.parseInt(txtAmount.getText());
                method = txtPaymentMethod.getText();
                
                try {
                    // Load the MySQL driver
                    Class.forName("com.mysql.cj.jdbc.Driver");

                    // Connect to the database
                    conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "yoshan");
                    
                    // Create prepared statement to prevent SQL injection
                    String query = "INSERT INTO payments (p_id, first_name, last_name, amount, payment_method) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    
                    // Set parameters
                    pstmt.setInt(1, pid);
                    pstmt.setString(2, fname);
                    pstmt.setString(3, lname);
                    pstmt.setInt(4, amount);
                    pstmt.setString(5, method);  // Fixed variable name from 'telephone' to 'method'
                    
                    // Execute the query
                    int rowsAffected = pstmt.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Payment added successfully!");
                        clearFields(); // Clear fields after successful save
                        loadPaymentsData(); // Refresh the table data
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to add payment");
                    }
                    
                    // Close resources
                    pstmt.close();
                    
                } catch (ClassNotFoundException h) {
                    JOptionPane.showMessageDialog(this, "MySQL JDBC Driver not found: " + h.getMessage());
                } catch (SQLException j) {
                    JOptionPane.showMessageDialog(this, "Database error: " + j.getMessage());
                    j.printStackTrace(); // Added for debugging
                } finally {
                    // Close connection in finally block
                    if (conn != null) {
                        try {
                            conn.close();
                        } catch (SQLException d) {
                            JOptionPane.showMessageDialog(this, "Error closing connection: " + d.getMessage());
                        }
                    }
                }
                
            } catch (NumberFormatException f) {
                JOptionPane.showMessageDialog(this, "There is mismatch with data types: " + f.getMessage());
            }
        });

        btnUpdate.addActionListener(e -> {
            // Validate input fields first
            if (txtID.getText().isEmpty() || txtFirstName.getText().isEmpty() || 
                txtLastName.getText().isEmpty() || txtAmount.getText().isEmpty() || 
                txtPaymentMethod.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields");
                return;
            }

            Connection conn = null;
            PreparedStatement pstmt = null;
            
            try {
                // Parse values from text fields
                int pid = Integer.parseInt(txtID.getText());
                String fname = txtFirstName.getText();
                String lname = txtLastName.getText();
                int amount = Integer.parseInt(txtAmount.getText());
                String method = txtPaymentMethod.getText();

                // Load the MySQL driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Connect to the database
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "yoshan");
                
                // Create prepared statement for update
                String updateQuery = "UPDATE payments SET first_name = ?, last_name = ?, amount = ?, payment_method = ? WHERE p_id = ?";
                pstmt = conn.prepareStatement(updateQuery);
                
                // Set parameters
                pstmt.setString(1, fname);
                pstmt.setString(2, lname);
                pstmt.setInt(3, amount);
                pstmt.setString(4, method);
                pstmt.setInt(5, pid);
                
                // Execute the update
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Payment updated successfully!");
                    loadPaymentsData(); // Refresh the table data
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "No payment found with ID: " + pid + 
                        "\nOr no changes were made to the existing data.");
                }
                
            } catch (ClassNotFoundException h) {
                JOptionPane.showMessageDialog(this, "Database driver error: " + h.getMessage());
            } catch (SQLException j) {
                JOptionPane.showMessageDialog(this, "Database operation failed: " + j.getMessage());
                j.printStackTrace();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for ID and Amount");
            } finally {
                // Close resources in finally block
                try {
                    if (pstmt != null) pstmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException d) {
                    JOptionPane.showMessageDialog(this, "Error closing database resources: " + d.getMessage());
                }
            }
        });

        btnDelete.addActionListener(e -> {
            int selectedRow = paymentsTable.getSelectedRow();  // Fixed variable name
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a payment to delete");
                return;
            }

            String paymentIdStr = tableModel.getValueAt(selectedRow, 0).toString();
            
            try {
                int paymentId = Integer.parseInt(paymentIdStr);
                
                int confirm = JOptionPane.showConfirmDialog(
                    this, 
                    "Are you sure you want to delete payment ID: " + paymentId + "?", 
                    "Confirm Deletion", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }

                // Load the MySQL driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Connect to the database
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "yoshan");
                
                // Create prepared statement for delete
                String deleteQuery = "DELETE FROM payments WHERE p_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(deleteQuery);
                pstmt.setInt(1, paymentId);
                
                // Execute the delete
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Payment deleted successfully!");
                    loadPaymentsData(); // Refresh the table data
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "No payment found with ID: " + paymentId);
                }
                
                pstmt.close();
                
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Invalid Payment ID format");
            } catch (ClassNotFoundException cnfe) {
                JOptionPane.showMessageDialog(this, "Database driver error: " + cnfe.getMessage());
            } catch (SQLException se) {
                JOptionPane.showMessageDialog(this, "Database error: " + se.getMessage());
            } finally {
                // Close connection
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException se) {
                        JOptionPane.showMessageDialog(this, "Error closing connection: " + se.getMessage());
                    }
                }
            }
        });

        paymentsTable.addMouseListener(new MouseAdapter() {  // Fixed variable name
            public void mouseClicked(MouseEvent e) {
                int selectedRow = paymentsTable.getSelectedRow();  // Fixed variable name
                txtID.setText(tableModel.getValueAt(selectedRow, 0).toString());
                txtFirstName.setText(tableModel.getValueAt(selectedRow, 1).toString());
                txtLastName.setText(tableModel.getValueAt(selectedRow, 2).toString());
                txtAmount.setText(tableModel.getValueAt(selectedRow, 3).toString());
                txtPaymentMethod.setText(tableModel.getValueAt(selectedRow, 4).toString());
            }
        });

        add(panel);
        setVisible(true);
        
        // Load initial data
        loadPaymentsData();
    }

    private void loadPaymentsData() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            // Clear existing table data
            tableModel.setRowCount(0);
            
            // Load the MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "yoshan");
            
            // Create statement
            stmt = conn.createStatement();
            
            // Execute query
            String query = "SELECT * FROM payments";
            rs = stmt.executeQuery(query);
            
            // Populate table with data from database
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("p_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getInt("amount"),
                    rs.getString("payment_method")
                };
                tableModel.addRow(row);
            }
            
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error closing database resources: " + e.getMessage());
            }
        }
    }

    private void clearFields() {
        txtID.setText("");
        txtFirstName.setText("");
        txtLastName.setText("");
        txtAmount.setText("");
        txtPaymentMethod.setText("");
        paymentsTable.clearSelection();  // Fixed variable name
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ManagePayments();
        });
    }
}