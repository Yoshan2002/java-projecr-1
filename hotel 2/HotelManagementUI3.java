import java.awt.*;
import java.io.*;
import javax.swing.*;

public class HotelManagementUI3 {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}

// Theme colors
class UITheme {
    public static final Color BACKGROUND_COLOR = new Color(255, 254, 159); // AliceBlue
    public static final Color BUTTON_COLOR = new Color(65, 105, 225);      // RoyalBlue
    public static final Color BUTTON_TEXT_COLOR = Color.WHITE;
    public static final Color TEXT_COLOR = new Color(33, 33, 33);          // Dark Gray
    public static final Font FONT = new Font("Segoe UI", Font.PLAIN, 16);
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    public static final Color ERROR_COLOR = new Color(255, 200, 200);      // Light red for error messages
}

class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Hotel Management Login");
        setSize(600, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Title label
        JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.TITLE_FONT);
        titleLabel.setForeground(UITheme.TEXT_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0)); // Top spacing

        // Form panel (center part)
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBackground(UITheme.BACKGROUND_COLOR);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(UITheme.FONT);
        usernameLabel.setForeground(UITheme.TEXT_COLOR);
        usernameField = new JTextField();
        usernameField.setFont(UITheme.FONT);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(UITheme.FONT);
        passwordLabel.setForeground(UITheme.TEXT_COLOR);
        passwordField = new JPasswordField();
        passwordField.setFont(UITheme.FONT);

        JButton loginButton = new JButton("Login");
        styleButton(loginButton);

        JButton registerButton = new JButton("Register");
        styleButton(registerButton);

        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(loginButton);
        formPanel.add(registerButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        add(mainPanel);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            User user = UserDAO.loginUser(username, password);
            if (user != null) {
                JOptionPane.showMessageDialog(null, "Login Successful!");
                // Redirect to the main menu/home page
                try {
                    ProcessBuilder pb = new ProcessBuilder("java", "HotelManagementSystemUI");
                    pb.start();
                    dispose();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(formPanel, "Ërror occured");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid credentials. Please try again.");
            }
        });

        registerButton.addActionListener(e -> {
            dispose();
            new RegisterFrame();
        });

        setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setBackground(UITheme.BUTTON_COLOR);
        button.setForeground(UITheme.BUTTON_TEXT_COLOR);
        button.setFocusPainted(false);
        button.setFont(UITheme.FONT);
    }
}

class RegisterFrame extends JFrame {
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;

    public RegisterFrame() {
        setTitle("Hotel Management Registration");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Title label
        JLabel titleLabel = new JLabel("Register", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.TITLE_FONT);
        titleLabel.setForeground(UITheme.TEXT_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // Form panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));
        panel.setBackground(UITheme.BACKGROUND_COLOR);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(UITheme.FONT);
        usernameLabel.setForeground(UITheme.TEXT_COLOR);
        usernameField = new JTextField();
        usernameField.setFont(UITheme.FONT);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(UITheme.FONT);
        emailLabel.setForeground(UITheme.TEXT_COLOR);
        emailField = new JTextField();
        emailField.setFont(UITheme.FONT);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(UITheme.FONT);
        passwordLabel.setForeground(UITheme.TEXT_COLOR);
        passwordField = new JPasswordField();
        passwordField.setFont(UITheme.FONT);

        JButton registerButton = new JButton("Register");
        registerButton.setFont(UITheme.FONT);
        registerButton.setBackground(UITheme.BUTTON_COLOR);
        registerButton.setForeground(UITheme.BUTTON_TEXT_COLOR);
        registerButton.setFocusPainted(false);

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel()); // Empty space
        panel.add(registerButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(panel, BorderLayout.CENTER);

        add(mainPanel);

        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            // Validate inputs
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showErrorDialog("All fields are required!", this);
                return;
            }

            User user = new User(0, username, password, email);
            boolean success = UserDAO.registerUser(user);
            if (success) {
                JOptionPane.showMessageDialog(this, "Registration Successful!");
                dispose();
                new LoginFrame();
            } else {
                showErrorDialog("Registration failed. Username might already exist.", this);
            }
        });

        setVisible(true);
    }

    private void showErrorDialog(String message, Component parent) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}