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

        JRadioButton adminRadio = new JRadioButton("Admin");
        JRadioButton instructorRadio = new JRadioButton("Instructor");
        JRadioButton studentRadio = new JRadioButton("Student");
        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(adminRadio);
        roleGroup.add(instructorRadio);
        roleGroup.add(studentRadio);
        adminRadio.setSelected(true);

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
        rolePanel.add(adminRadio);
        rolePanel.add(instructorRadio);
        rolePanel.add(studentRadio);
        panel.add(rolePanel, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(btnSignup, gbc);
        gbc.gridy = 5;
        panel.add(btnBack, gbc);

        add(panel);

        usernameField.addActionListener(e -> emailField.requestFocusInWindow());
        emailField.addActionListener(e -> passField.requestFocusInWindow());
        passField.addActionListener(e -> btnSignup.doClick());

        btnSignup.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword());
            String role = studentRadio.isSelected() ? "student" : instructorRadio.isSelected() ? "instructor" : "admin";

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
            else if ("instructor".equals(role)) success = auth.signupInstructor(username, email, password);
            else success = auth.signupAdmin(username, email, password);

            if (!success) {
                JOptionPane.showMessageDialog(this, "Email is already registered.");
            } else {
                JOptionPane.showMessageDialog(this, "Account created! Please login.");
                this.setVisible(false);
                new LoginFrame(db).setVisible(true);
            }
        });

        btnBack.addActionListener(e -> {
            this.setVisible(false);
            new LoginFrame(db).setVisible(true);
        });
    }
}
