import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ManageRooms extends JFrame {
    private JTextField txtRoomID, txtRoomNumber, txtRoomType, txtPrice, txtStatus;
    private JTable roomsTable;
    private DefaultTableModel tableModel;
    public int rid;
    public int rnumber;
    public String rtype;
    public int price;
    public String status;
    public Connection conn = null; 

    public ManageRooms() {
        setTitle("Manage Rooms");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

        JLabel lblTitle = new JLabel("Manage Rooms", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setOpaque(true);
        lblTitle.setBackground(Color.DARK_GRAY);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 0, 700, 40);
        add(lblTitle);

        // Labels
        String[] labelNames = {"RoomID:", "RoomNumber:", "RoomType:", "Price:", "Status:"};
        int y = 70;
        for (String text : labelNames) {
            JLabel lbl = new JLabel(text);
            lbl.setBounds(50, y, 100, 25);
            add(lbl);
            y += 40;
        }

        // Text fields
        txtRoomID = new JTextField();
        txtRoomNumber = new JTextField();
        txtRoomType = new JTextField();
        txtPrice = new JTextField();
        txtStatus = new JTextField();

        JTextField[] fields = {txtRoomID, txtRoomNumber, txtRoomType, txtPrice, txtStatus};
        y = 70;
        for (JTextField field : fields) {
            field.setBounds(160, y, 300, 25);
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
        String[] columnNames = {"RoomID", "RoomNumber", "RoomType", "Price", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        roomsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(roomsTable);
        scrollPane.setBounds(50, 320, 580, 120);
        panel.add(scrollPane);

        // Load data from database when application starts
        loadRoomsData();

        // Button functionality
        btnAdd.addActionListener(e -> {
            String[] rowData = {
                    txtRoomID.getText(),
                    txtRoomNumber.getText(),
                    txtRoomType.getText(),
                    txtPrice.getText(),
                    txtStatus.getText()
            };
            tableModel.addRow(rowData);
        });

        btnSave.addActionListener(e -> {
            try {
                rid = Integer.parseInt(txtRoomID.getText());
                rnumber = Integer.parseInt(txtRoomNumber.getText());
                rtype = txtRoomType.getText();
                price = Integer.parseInt(txtPrice.getText());
                status = txtStatus.getText();
                
                try {
                    // Load the MySQL driver
                    Class.forName("com.mysql.cj.jdbc.Driver");

                    // Connect to the database
                    conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "yoshan");
                    
                    // Create prepared statement to prevent SQL injection
                    String query = "INSERT INTO rooms(rooms_id, rooms_number, room_type, price, status) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    
                    // Set parameters
                    pstmt.setInt(1, rid);
                    pstmt.setInt(2, rnumber);
                    pstmt.setString(3, rtype);
                    pstmt.setInt(4, price);
                    pstmt.setString(5, status);
                    
                    // Execute the query
                    int rowsAffected = pstmt.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Rooms added successfully!");
                        clearFields(); // Clear fields after successful save
                        loadRoomsData(); // Refresh the table data
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to add rooms");
                    }
                    
                    // Close resources
                    pstmt.close();
                    
                } catch (ClassNotFoundException h) {
                    JOptionPane.showMessageDialog(this, "MySQL JDBC Driver not found: " + h.getMessage());
                } catch (SQLException j) {
                    JOptionPane.showMessageDialog(this, "Database error: " + j.getMessage());
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
            if (txtRoomID.getText().isEmpty() || txtRoomNumber.getText().isEmpty() || 
                txtRoomType.getText().isEmpty() || txtPrice.getText().isEmpty() || 
                txtStatus.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields");
                return;
            }

            Connection conn = null;
            PreparedStatement pstmt = null;
            
            try {
                // Parse values from text fields
                int rid = Integer.parseInt(txtRoomID.getText());
                int rnumber = Integer.parseInt(txtRoomNumber.getText());
                String rtype = txtRoomType.getText();
                int price = Integer.parseInt(txtPrice.getText());
                String status = txtStatus.getText();

                // Load the MySQL driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Connect to the database
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "yoshan");
                
                // Create prepared statement for update
                String updateQuery = "UPDATE rooms SET rooms_number = ?, room_type = ?, price = ?, status = ? WHERE rooms_id = ?";
                pstmt = conn.prepareStatement(updateQuery);
                
                // Set parameters in correct order (matches the SET clause order)
                pstmt.setInt(1, rnumber);       // rooms_number
                pstmt.setString(2, rtype);      // room_type
                pstmt.setInt(3, price);         // price
                pstmt.setString(4, status);     // status
                pstmt.setInt(5, rid);           // rooms_id (WHERE clause)
                
                // Execute the update
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Room updated successfully!");
                    loadRoomsData(); // Refresh the table data
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "No room found with ID: " + rid + 
                        "\nOr no changes were made to the existing data.");
                }
                
            } catch (ClassNotFoundException h) {
                JOptionPane.showMessageDialog(this, "Database driver error: " + h.getMessage());
            } catch (SQLException j) {
                JOptionPane.showMessageDialog(this, "Database operation failed: " + j.getMessage());
                j.printStackTrace(); // Add this to see detailed error
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for Room ID, Room Number, and Price");
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
            // Check if a row is selected in the table
            int selectedRow = roomsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a room to delete");
                return;
            }

            // Get the room ID from the selected row
            String roomIdStr = tableModel.getValueAt(selectedRow, 0).toString();
            
            try {
                int roomId = Integer.parseInt(roomIdStr);
                
                // Confirm deletion with user
                int confirm = JOptionPane.showConfirmDialog(
                    this, 
                    "Are you sure you want to delete room ID: " + roomId + "?", 
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
                String deleteQuery = "DELETE FROM rooms WHERE rooms_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(deleteQuery);
                pstmt.setInt(1, roomId);
                
                // Execute the delete
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Room deleted successfully!");
                    loadRoomsData(); // Refresh the table data
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "No room found with ID: " + roomId);
                }
                
                pstmt.close();
                
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Invalid Room ID format");
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

        roomsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = roomsTable.getSelectedRow();
                txtRoomID.setText(tableModel.getValueAt(selectedRow, 0).toString());
                txtRoomNumber.setText(tableModel.getValueAt(selectedRow, 1).toString());
                txtRoomType.setText(tableModel.getValueAt(selectedRow, 2).toString());
                txtPrice.setText(tableModel.getValueAt(selectedRow, 3).toString());
                txtStatus.setText(tableModel.getValueAt(selectedRow, 4).toString());
            }
        });

        add(panel);
        setVisible(true);
    }

    // Method to load rooms data from database
    private void loadRoomsData() {
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
            String query = "SELECT * FROM rooms";
            rs = stmt.executeQuery(query);
            
            // Populate table with data from database
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("rooms_id"),
                    rs.getInt("rooms_number"),
                    rs.getString("room_type"),
                    rs.getInt("price"),
                    rs.getString("status")
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

    public static void main(String[] args) {
        new ManageRooms();
    }

    private void clearFields() {
        txtRoomID.setText("");
        txtRoomNumber.setText("");
        txtRoomType.setText("");
        txtPrice.setText("");
        txtStatus.setText("");
        roomsTable.clearSelection();
    }
}