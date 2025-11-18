package ui.frames;

import model.AuthManager;
import model.JsonDatabaseManager;
import model.User;
import model.Student;
import model.Instructor;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private AuthManager auth;
    private JsonDatabaseManager db;

    public LoginFrame(JsonDatabaseManager db) {
        this.db = db;
        this.auth = new AuthManager(db);
        setTitle("SkillForge - Login");
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

        JTextField emailField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JButton btnLogin = new JButton("Login");
        JButton btnSignup = new JButton("Signup");

        JRadioButton studentRadio = new JRadioButton("Student");
        JRadioButton instructorRadio = new JRadioButton("Instructor");
        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(studentRadio);
        roleGroup.add(instructorRadio);
        studentRadio.setSelected(true);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Login as:"), gbc);
        gbc.gridx = 1;
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        rolePanel.add(studentRadio);
        rolePanel.add(instructorRadio);
        panel.add(rolePanel, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(btnLogin, gbc);
        gbc.gridy = 4;
        panel.add(btnSignup, gbc);

        add(panel);

        emailField.addActionListener(e -> passField.requestFocusInWindow());
        passField.addActionListener(e -> btnLogin.doClick());

        btnLogin.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword());
            String selectedRole = studentRadio.isSelected() ? "student" : "instructor";

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fill email and password");
                return;
            }

            java.util.Optional<User> opt = auth.login(email, password);

            if (opt.isPresent() && selectedRole.equals(opt.get().getRole())) {
                User user = opt.get();
                JOptionPane.showMessageDialog(this, "Welcome, " + user.getUsername());
                this.setVisible(false);

                if ("student".equals(user.getRole())) {
                    new StudentDashboardFrame(db, (Student) user).setVisible(true);
                } else {
                    new InstructorDashboardFrame(db, (Instructor) user).setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        });

        btnSignup.addActionListener(e -> {
            new SignupFrame(db).setVisible(true);
            this.setVisible(false);
        });
    }
}
