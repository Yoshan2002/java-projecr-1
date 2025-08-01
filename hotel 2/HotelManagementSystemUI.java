import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class HotelManagementSystemUI extends JFrame {

    public HotelManagementSystemUI() {
        setTitle("Hotel Management System");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel (Header)
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(255, 204, 0));
        headerPanel.setPreferredSize(new Dimension(0, 50));
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("Hotel Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.RED);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Left panel (Navigation)
        JPanel navPanel = new JPanel();
        navPanel.setBackground(new Color(150, 140, 255));
        navPanel.setPreferredSize(new Dimension(180, 0));
        navPanel.setLayout(new GridLayout(6, 1, 10, 10));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Create buttons with individual action listeners
        JButton guestButton = createNavButton("Guest", "guest.PNG");
        JButton roomButton = createNavButton("Room", "room.JPEG");
        JButton bookingButton = createNavButton("Booking", "booking.PNG");
        JButton paymentButton = createNavButton("Payment", "payment.PNG");
        JButton dashboardButton = createNavButton("Dashboard", "dashboard.PNG");

        // Add action listeners to each button
        guestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Guest button clicked");
                // Add your guest-related functionality here
            }
        });

        roomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Room button clicked");
                // Add your room-related functionality here
            }
        });

        bookingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Booking button clicked");
                // Add your booking-related functionality here
            }
        });

         guestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ProcessBuilder pb = new ProcessBuilder("java", "ManageGuests");
                    pb.start();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(HotelManagementSystemUI.this, 
                        "Error launching program: " + ex.getMessage());
                }
            }
        });

            roomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ProcessBuilder pb = new ProcessBuilder("java", "ManageRooms");
                    pb.start();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(HotelManagementSystemUI.this, 
                        "Error launching program: " + ex.getMessage());
                }
            }
        });

            bookingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ProcessBuilder pb = new ProcessBuilder("java", "ManageBooking");
                    pb.start();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(HotelManagementSystemUI.this, 
                        "Error launching program: " + ex.getMessage());
                }
            }
        });

        paymentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ProcessBuilder pb = new ProcessBuilder("java", "ManagePayments");
                    pb.start();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(HotelManagementSystemUI.this, 
                        "Error launching program: " + ex.getMessage());
                }
            }
        });

        dashboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ProcessBuilder pb = new ProcessBuilder("java", "HotelDashboard");
                    pb.start();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(HotelManagementSystemUI.this, 
                        "Error launching program: " + ex.getMessage());
                }
            }
        });

        // Add buttons to the panel
        navPanel.add(guestButton);
        navPanel.add(roomButton);
        navPanel.add(bookingButton);
        navPanel.add(paymentButton);
        navPanel.add(dashboardButton);

        add(navPanel, BorderLayout.WEST);

        // Background image panel
        JLabel backgroundLabel = new JLabel();
        backgroundLabel.setLayout(new BorderLayout());
        ImageIcon imageIcon = new ImageIcon("photo-1517840901100-8179e982acb7.JPEG");
        Image scaledImage = imageIcon.getImage().getScaledInstance(1020, 650, Image.SCALE_SMOOTH);
        backgroundLabel.setIcon(new ImageIcon(scaledImage));
        add(backgroundLabel, BorderLayout.CENTER);
    }

    // Helper method to create styled navigation buttons
    private JButton createNavButton(String name, String iconFile) {
        JButton button = new JButton(name);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setBackground(new Color(180, 255, 200));
        button.setFocusPainted(false);

        // Load and set icon
        try {
            ImageIcon icon = new ImageIcon(iconFile);
            Image img = icon.getImage().getScaledInstance(34, 34, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setIconTextGap(10);
        } catch (Exception e) {
            System.err.println("Error loading icon: " + iconFile);
        }

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HotelManagementSystemUI ui = new HotelManagementSystemUI();
            ui.setVisible(true);
        });
    }
}