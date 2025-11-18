package ui.frames;

import model.JsonDatabaseManager;
import model.AuthManager;

import javax.swing.*;
import java.awt.*;

public class SignupFrame extends JFrame {
    private JsonDatabaseManager db;
    private AuthManager auth;

    public SignupFrame(JsonDatabaseManager db) {
        this.db = db;
        this.auth = new AuthManager(db);
        setTitle("SkillForge - Signup");
        setSize(400, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        init();
    }

    private void init() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField usernameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JButton btnSignup = new JButton("Create Account");
        JButton btnBack = new JButton("Back");

        // Role selection
        JRadioButton studentRadio = new JRadioButton("Student");
        JRadioButton instructorRadio = new JRadioButton("Instructor");
        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(studentRadio);
        roleGroup.add(instructorRadio);
        studentRadio.setSelected(true);

        // Layout components
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        rolePanel.add(studentRadio);
        rolePanel.add(instructorRadio);
        panel.add(rolePanel, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(btnSignup, gbc);
        gbc.gridy = 5;
        panel.add(btnBack, gbc);

        add(panel);

        // Focus order
        usernameField.addActionListener(e -> emailField.requestFocusInWindow());
        emailField.addActionListener(e -> passField.requestFocusInWindow());
        passField.addActionListener(e -> btnSignup.doClick());

        // Signup logic
        btnSignup.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword());
            String role = studentRadio.isSelected() ? "student" : "instructor";

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.");
                return;
            }
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                JOptionPane.showMessageDialog(this, "Invalid email format.");
                return;
            }

            boolean success;
            if ("student".equals(role)) success = auth.signupStudent(username, email, password);
            else success = auth.signupInstructor(username, email, password);

            if (!success) {
                JOptionPane.showMessageDialog(this, "Email is already registered.");
            } else {
                JOptionPane.showMessageDialog(this, "Account created! Please login.");
                this.setVisible(false);
                new LoginFrame(db).setVisible(true);
            }
        });

        // Back button
        btnBack.addActionListener(e -> {
            this.setVisible(false);
            new LoginFrame(db).setVisible(true);
        });
    }
}
