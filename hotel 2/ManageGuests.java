import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ManageGuests extends JFrame {

    private JTextField txtGuestID, txtFirstName, txtLastName, txtEmail, txtPhone;
    private JTable guestTable;
    private DefaultTableModel tableModel;
    public int uid;
    public String fname;
    public String lname;
    public String personelemail;
    public String telephone;
    public Connection conn = null; 

    public ManageGuests() {
        setTitle("Guest Management");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(Color.PINK);
        
        Font defaultFont = new Font("SansSerif", Font.PLAIN, 16);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("Table.font", defaultFont);
        UIManager.put("TableHeader.font", defaultFont);

        // Title
        JLabel lblTitle = new JLabel("Manage Guests", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setOpaque(true);
        lblTitle.setBackground(Color.DARK_GRAY);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 0, 800, 40);
        add(lblTitle);

        // Labels
        String[] labelNames = {"Guest ID:", "First Name:", "Last Name:", "Email:", "Phone:"};
        int y = 70;
        for (String text : labelNames) {
            JLabel lbl = new JLabel(text);
            lbl.setBounds(50, y, 100, 25);
            add(lbl);
            y += 40;
        }

        // Text fields
        txtGuestID = new JTextField();
        txtFirstName = new JTextField();
        txtLastName = new JTextField();
        txtEmail = new JTextField();
        txtPhone = new JTextField();

        JTextField[] fields = {txtGuestID, txtFirstName, txtLastName, txtEmail, txtPhone};
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

        // Table
        String[] columns = {"Guest ID", "First Name", "Last Name", "Email", "Phone"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                if (columnIndex == 4) return String.class;
                return String.class;
            }
        };
        
        guestTable = new JTable(tableModel);
        guestTable.setAutoCreateRowSorter(true);
        guestTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        guestTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        guestTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        guestTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        guestTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        guestTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        
        JScrollPane scrollPane = new JScrollPane(guestTable);
        scrollPane.setBounds(20, 320, 760, 220);
        add(scrollPane);

        // Load data from database when the form opens
        loadGuestData();

        // Button actions
        btnAdd.addActionListener(e -> {
            String id = txtGuestID.getText().trim();
            String first = txtFirstName.getText().trim();
            String last = txtLastName.getText().trim();
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();

            if (id.isEmpty() || first.isEmpty() || last.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).toString().equals(id)) {
                    JOptionPane.showMessageDialog(this, "Guest ID already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            tableModel.addRow(new Object[]{Integer.parseInt(id), first, last, email, phone});
            clearFields();
        });

        btnSave.addActionListener(e -> {
            try {
                uid = Integer.parseInt(txtGuestID.getText());
                fname = txtFirstName.getText();
                lname = txtLastName.getText();
                personelemail = txtEmail.getText();
                telephone = txtPhone.getText();
                
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "yoshan");
                    
                    String query = "INSERT INTO guests(guest_id, first_name, lastName, email, phone) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    
                    pstmt.setInt(1, uid);
                    pstmt.setString(2, fname);
                    pstmt.setString(3, lname);
                    pstmt.setString(4, personelemail);
                    pstmt.setString(5, telephone);
                    
                    int rowsAffected = pstmt.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Guest added successfully!");
                        clearFields();
                        loadGuestData();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to add guest");
                    }
                    
                    pstmt.close();
                    
                } catch (ClassNotFoundException h) {
                    JOptionPane.showMessageDialog(this, "MySQL JDBC Driver not found: " + h.getMessage());
                } catch (SQLException j) {
                    JOptionPane.showMessageDialog(this, "Database error: " + j.getMessage());
                } finally {
                    if (conn != null) {
                        try {
                            conn.close();
                        } catch (SQLException d) {
                            JOptionPane.showMessageDialog(this, "Error closing connection: " + d.getMessage());
                        }
                    }
                }
                
            } catch (NumberFormatException f) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for Guest ID: " + f.getMessage());
            }
        });

        btnUpdate.addActionListener(e -> updateGuest());
        btnDelete.addActionListener(e -> deleteGuest());

        guestTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = guestTable.getSelectedRow();
                if (row >= 0) {
                    txtGuestID.setText(tableModel.getValueAt(row, 0).toString());
                    txtFirstName.setText(tableModel.getValueAt(row, 1).toString());
                    txtLastName.setText(tableModel.getValueAt(row, 2).toString());
                    txtEmail.setText(tableModel.getValueAt(row, 3).toString());
                    txtPhone.setText(tableModel.getValueAt(row, 4).toString());
                }
            }
        });
    }

    private void updateGuest() {
        if (txtGuestID.getText().isEmpty() || txtFirstName.getText().isEmpty() || 
            txtLastName.getText().isEmpty() || txtEmail.getText().isEmpty() || 
            txtPhone.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            int guestId = Integer.parseInt(txtGuestID.getText());
            String firstName = txtFirstName.getText();
            String lastName = txtLastName.getText();
            String email = txtEmail.getText();
            String phone = txtPhone.getText();

            if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid email address");
                return;
            }

            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "yoshan");
            
            String updateQuery = "UPDATE guests SET first_name = ?, lastName = ?, email = ?, phone = ? WHERE guest_id = ?";
            pstmt = conn.prepareStatement(updateQuery);
            
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            pstmt.setInt(5, guestId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Guest updated successfully!");
                clearFields();
                loadGuestData();
            } else {
                JOptionPane.showMessageDialog(this, "No guest found with ID: " + guestId + 
                    "\nOr no changes were made to the existing data.");
            }
            
        } catch (NumberFormatException f) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for Guest ID: " + f.getMessage());
        } catch (ClassNotFoundException h) {
            JOptionPane.showMessageDialog(this, "Database driver error: " + h.getMessage());
        } catch (SQLException j) {
            JOptionPane.showMessageDialog(this, "Database operation failed: " + j.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException d) {
                JOptionPane.showMessageDialog(this, "Error closing database resources: " + d.getMessage());
            }
        }
    }

    private void deleteGuest() {
        int selectedRow = guestTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a guest to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String guestId = tableModel.getValueAt(selectedRow, 0).toString();
        
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to delete guest with ID: " + guestId + "?", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = null;
            PreparedStatement pstmt = null;
            
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "yoshan");
                
                String deleteQuery = "DELETE FROM guests WHERE guest_id = ?";
                pstmt = conn.prepareStatement(deleteQuery);
                
                pstmt.setInt(1, Integer.parseInt(guestId));
                
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    tableModel.removeRow(selectedRow);
                    clearFields();
                    JOptionPane.showMessageDialog(this, "Guest deleted successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete guest or guest not found.");
                }
                
            } catch (ClassNotFoundException h) {
                JOptionPane.showMessageDialog(this, "Database driver error: " + h.getMessage());
            } catch (SQLException j) {
                JOptionPane.showMessageDialog(this, "Database operation failed: " + j.getMessage());
            } finally {
                try {
                    if (pstmt != null) pstmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException d) {
                    JOptionPane.showMessageDialog(this, "Error closing database resources: " + d.getMessage());
                }
            }
        }
    }

    private void loadGuestData() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            tableModel.setRowCount(0);
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "yoshan");
            
            stmt = conn.createStatement();
            String query = "SELECT * FROM guests ORDER BY guest_id";
            rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("guest_id"),
                    rs.getString("first_name"),
                    rs.getString("lastName"),
                    rs.getString("email"),
                    rs.getString("phone")
                });
            }
            
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error closing resources: " + e.getMessage());
            }
        }
    }

    private void clearFields() {
        txtGuestID.setText("");
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        guestTable.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ManageGuests frame = new ManageGuests();
            frame.setVisible(true);
        });
    }
}