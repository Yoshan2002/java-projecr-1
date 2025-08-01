import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ManageBooking extends JFrame {

    private JTextField txtBookingID, txtFirstName, txtLastName, txtRoomChoice;
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    public int bid;
    public String fname;
    public String lname;
    public String roomc;
    public Connection conn = null; 

    public ManageBooking() {
        setTitle("Booking Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(Color.PINK);
        
        Font defaultFont = new Font("SansSerif", Font.PLAIN, 16);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("Table.font", defaultFont);
        UIManager.put("TableHeader.font", defaultFont);

        JLabel lblTitle = new JLabel("Manage Bookings", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setBounds(0, 0, 900, 40);
        lblTitle.setOpaque(true);
        lblTitle.setBackground(Color.DARK_GRAY);
        lblTitle.setForeground(Color.WHITE);
        add(lblTitle);

        // Labels
        String[] labels = {"Booking ID:", "First Name:", "Last Name:", "Room Choice:"};
        int y = 70;
        for (String label : labels) {
            JLabel lbl = new JLabel(label);
            lbl.setBounds(50, y, 100, 25);
            add(lbl);
            y += 40;
        }

        // Inputs
        txtBookingID = new JTextField();
        txtFirstName = new JTextField();
        txtLastName = new JTextField();
        txtRoomChoice = new JTextField();

        Component[] inputs = {txtBookingID, txtFirstName, txtLastName, txtRoomChoice};
        y = 70;
        for (Component c : inputs) {
            c.setBounds(160, y, 300, 25);
            add(c);
            y += 40;
        }

        // Buttons
        JButton btnSave = new JButton("Save");
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");

        int btnY = 250;
        btnSave.setBounds(100, btnY, 100, 30);
        btnAdd.setBounds(210, btnY, 100, 30);
        btnUpdate.setBounds(320, btnY, 100, 30);
        btnDelete.setBounds(430, btnY, 100, 30);

        add(btnSave);
        add(btnAdd);
        add(btnUpdate);
        add(btnDelete);

        // Table setup
        String[] columns = {"Booking ID", "First Name", "Last Name", "Room Choice"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class; // Booking ID is integer
                return String.class; // All other columns are strings
            }
        };
        
        bookingsTable = new JTable(tableModel);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingsTable.setAutoCreateRowSorter(true); // Enable sorting
        
        // Set column widths
        bookingsTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID
        bookingsTable.getColumnModel().getColumn(1).setPreferredWidth(150); // First Name
        bookingsTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Last Name
        bookingsTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Room Choice
        
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBounds(50, 300, 800, 250); // Adjusted size
        add(scrollPane);

        // Load data when form opens
        loadBookingData();

        // Add Booking
        btnAdd.addActionListener(e -> {
            String id = txtBookingID.getText().trim();
            String first = txtFirstName.getText().trim();
            String last = txtLastName.getText().trim();
            String room = txtRoomChoice.getText().trim();

            if (id.isEmpty() || first.isEmpty() || last.isEmpty() || room.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check for duplicate ID
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).toString().equals(id)) {
                    JOptionPane.showMessageDialog(this, "Booking ID already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            tableModel.addRow(new Object[]{Integer.parseInt(id), first, last, room});
            clearFields();
        });

        // Save to Database
        btnSave.addActionListener(e -> saveBooking());

        // Update Booking
        btnUpdate.addActionListener(e -> updateBooking());

        // Delete Booking
        btnDelete.addActionListener(e -> deleteBooking());

        // Table row click event
        bookingsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = bookingsTable.getSelectedRow();
                if (row >= 0) {
                    txtBookingID.setText(tableModel.getValueAt(row, 0).toString());
                    txtFirstName.setText(tableModel.getValueAt(row, 1).toString());
                    txtLastName.setText(tableModel.getValueAt(row, 2).toString());
                    txtRoomChoice.setText(tableModel.getValueAt(row, 3).toString());
                }
            }
        });
    }

    private void loadBookingData() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            // Clear existing data
            tableModel.setRowCount(0);
            
            // Load the MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "yoshan");
            
            // Create statement
            stmt = conn.createStatement();
            
            // Execute query
            String query = "SELECT * FROM bookings ORDER BY booking_id";
            rs = stmt.executeQuery(query);
            
            // Add rows to table model
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("booking_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("room_choice")
                });
            }
            
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error closing resources: " + e.getMessage());
            }
        }
    }

    private void saveBooking() {
        try {
            bid = Integer.parseInt(txtBookingID.getText());
            fname = txtFirstName.getText();
            lname = txtLastName.getText();
            roomc = txtRoomChoice.getText();
            
            try {
                // Load the MySQL driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Connect to the database
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "yoshan");
                
                // Create prepared statement to prevent SQL injection
                String query = "INSERT INTO bookings (booking_id, first_name, last_name, room_choice) VALUES (?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(query);
                
                // Set parameters
                pstmt.setInt(1, bid);
                pstmt.setString(2, fname);
                pstmt.setString(3, lname);
                pstmt.setString(4, roomc);
                
                // Execute the query
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Booking saved successfully!");
                    loadBookingData(); // Refresh the table
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save booking");
                }
                
                // Close resources
                pstmt.close();
                
            } catch (ClassNotFoundException h) {
                JOptionPane.showMessageDialog(this, "MySQL JDBC Driver not found: " + h.getMessage());
            } catch (SQLException j) {
                JOptionPane.showMessageDialog(this, "Database error: " + j.getMessage());
                j.printStackTrace();
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
            JOptionPane.showMessageDialog(this, "Please enter a valid number for Booking ID");
        }
    }

    private void updateBooking() {
        // Validate input fields first
        if (txtBookingID.getText().isEmpty() || txtFirstName.getText().isEmpty() || 
            txtLastName.getText().isEmpty() || txtRoomChoice.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            int bid = Integer.parseInt(txtBookingID.getText());
            String fname = txtFirstName.getText();
            String lname = txtLastName.getText();
            String roomc = txtRoomChoice.getText();

            // Load the MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "yoshan");
            
            // Create prepared statement for update
            String updateQuery = "UPDATE bookings SET first_name = ?, last_name = ?, room_choice = ? WHERE booking_id = ?";
            pstmt = conn.prepareStatement(updateQuery);
            
            // Set parameters
            pstmt.setString(1, fname);
            pstmt.setString(2, lname);
            pstmt.setString(3, roomc);
            pstmt.setInt(4, bid);
            
            // Execute the update
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Booking updated successfully!");
                loadBookingData(); // Refresh the table
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "No booking found with ID: " + bid + 
                    "\nOr no changes were made to the existing data.");
            }
            
        } catch (NumberFormatException f) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for Booking ID");
        } catch (ClassNotFoundException h) {
            JOptionPane.showMessageDialog(this, "Database driver error: " + h.getMessage());
        } catch (SQLException j) {
            JOptionPane.showMessageDialog(this, "Database operation failed: " + j.getMessage());
            j.printStackTrace();
        } finally {
            // Close resources in finally block
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException d) {
                JOptionPane.showMessageDialog(this, "Error closing database resources: " + d.getMessage());
            }
        }
    }

    private void deleteBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the booking ID from the selected row
        String bookingIdStr = tableModel.getValueAt(selectedRow, 0).toString();
        
        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to delete booking ID: " + bookingIdStr + "?", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            int bid = Integer.parseInt(bookingIdStr);
            
            // Load the MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "yoshan");
            
            // Create prepared statement for delete
            String deleteQuery = "DELETE FROM bookings WHERE booking_id = ?";
            pstmt = conn.prepareStatement(deleteQuery);
            pstmt.setInt(1, bid);
            
            // Execute the delete
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Remove from table model only after successful database deletion
                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Booking deleted successfully!");
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "No booking found with ID: " + bid);
            }
            
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid Booking ID format");
        } catch (ClassNotFoundException cnfe) {
            JOptionPane.showMessageDialog(this, "Database driver error: " + cnfe.getMessage());
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this, "Database error: " + se.getMessage());
            se.printStackTrace();
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
    }

    private void clearFields() {
        txtBookingID.setText("");
        txtFirstName.setText("");
        txtLastName.setText("");
        txtRoomChoice.setText("");
        bookingsTable.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ManageBooking frame = new ManageBooking();
            frame.setVisible(true);
        });
    }
}