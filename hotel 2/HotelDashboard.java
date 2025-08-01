import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HotelDashboard extends JFrame {
    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hms";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "yoshan";

    // Labels to display counts
    private JLabel guestCountLabel;
    private JLabel roomCountLabel;
    private JLabel bookingCountLabel;
    private JLabel popularRoomLabel;

    public HotelDashboard() {
        setTitle("Hotel Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top bar
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.YELLOW);
        topPanel.setPreferredSize(new Dimension(0, 40));
        topPanel.add(new JLabel("Hotel Management System"));
        add(topPanel, BorderLayout.NORTH);

        // Sidebar menu
        JPanel sidePanel = new JPanel();
        sidePanel.setBackground(new Color(173, 216, 230));
        sidePanel.setPreferredSize(new Dimension(120, 0));
        sidePanel.setLayout(new GridLayout(6, 1, 5, 5));

        String[] buttons = {"Guest", "Room", "Booking", "Payment", "Dashboard"};
        for (String name : buttons) {
            JButton button = new JButton(name);
            sidePanel.add(button);
        }
        add(sidePanel, BorderLayout.WEST);

        // Load background image
        ImageIcon backgroundIcon = new ImageIcon("photo-1517840901100-8179e982acb7.JPEG");
        Image backgroundImage = backgroundIcon.getImage();

        // Dashboard content with background image
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(null);

        JLabel title = new JLabel("Hotel Green Dashboard");
        title.setBounds(30, 10, 300, 30);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        mainPanel.add(title);

        // Admin panel image
        JLabel adminImage = new JLabel();
        adminImage.setIcon(new ImageIcon("global-admin-icon-color-outline-vector.JPG"));
        adminImage.setBounds(0, 60, 200, 160);
        mainPanel.add(adminImage);

        JLabel adminLabel = new JLabel("Admin Panel");
        adminLabel.setBounds(40, 165, 100, 20);
        mainPanel.add(adminLabel);

        // Dashboard metrics panel
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(1, 3, 10, 10));
        statsPanel.setBounds(210, 60, 600, 100);
        statsPanel.setOpaque(false);

        // Create panels for each metric
        JPanel guestPanel = createMetricPanel("Total Guests");
        JPanel roomPanel = createMetricPanel("Total Rooms");
        JPanel bookingPanel = createMetricPanel("Total Bookings");

        // Get references to the count labels
        guestCountLabel = (JLabel) guestPanel.getComponent(1);
        roomCountLabel = (JLabel) roomPanel.getComponent(1);
        bookingCountLabel = (JLabel) bookingPanel.getComponent(1);

        statsPanel.add(guestPanel);
        statsPanel.add(roomPanel);
        statsPanel.add(bookingPanel);
        mainPanel.add(statsPanel);

        // Popular Room section
        JPanel popularRoomPanel = new JPanel();
        popularRoomPanel.setBackground(new Color(255, 255, 255, 150)); // Semi-transparent
        popularRoomPanel.setBounds(210, 180, 600, 100);
        popularRoomLabel = new JLabel("Loading popular room...");
        popularRoomLabel.setFont(new Font("Arial", Font.BOLD, 14));
        popularRoomPanel.add(popularRoomLabel);
        mainPanel.add(popularRoomPanel);

        add(mainPanel, BorderLayout.CENTER);

        // Load data when window is shown
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                loadDatabaseCounts();
                loadPopularRoom();
            }
        });

        // Refresh button
        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.setBounds(210, 300, 150, 30);
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDatabaseCounts();
                loadPopularRoom();
            }
        });
        mainPanel.add(refreshButton);

        setVisible(true);
    }

    private JPanel createMetricPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 255, 255, 150)); // Semi-transparent
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel countLabel = new JLabel("Loading...");
        countLabel.setFont(new Font("Arial", Font.BOLD, 18));
        countLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(countLabel);
        
        return panel;
    }

    private void loadDatabaseCounts() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            // Get guest count
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM guests");
            if (rs.next()) {
                guestCountLabel.setText(String.valueOf(rs.getInt("count")));
            }
            
            // Get room count
            rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM rooms");
            if (rs.next()) {
                roomCountLabel.setText(String.valueOf(rs.getInt("count")));
            }
            
            // Get booking count
            rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM bookings");
            if (rs.next()) {
                bookingCountLabel.setText(String.valueOf(rs.getInt("count")));
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Database Error: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPopularRoom() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "yoshan");
             Statement stmt = conn.createStatement()) {
            
            // Query to find the most popular room type
            String query = "SELECT room_type, COUNT(*) as booking_count " +
                          "FROM bookings b JOIN rooms r ON b.room_id = r.room_id " +
                          "GROUP BY room_type ORDER BY booking_count DESC LIMIT 1";
            
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                String roomType = rs.getString("room_type");
                int count = rs.getInt("booking_count");
                popularRoomLabel.setText("Most Popular: " + roomType + " (" + count + " bookings)");
            } else {
                popularRoomLabel.setText("No booking data available");
            }
            
        } catch (SQLException ex) {
            popularRoomLabel.setText("Error loading popular room");
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Load MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, 
                "MySQL JDBC Driver not found!", 
                "Driver Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            new HotelDashboard();
        });
    }
}
