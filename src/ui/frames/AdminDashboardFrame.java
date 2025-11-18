package ui.frames;

import model.Admin;
import model.Course;
import model.JsonDatabaseManager;
import javax.swing.*;
import java.awt.*;

public class AdminDashboardFrame extends JFrame {
    private JsonDatabaseManager db;
    private Admin admin;

    public AdminDashboardFrame(JsonDatabaseManager db, Admin admin) {
        this.db = db;
        this.admin = admin;
        setTitle("Admin Dashboard - " + admin.getUsername());
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        init();
    }
    private void init() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        final DefaultListModel<Course> model = new DefaultListModel<>();
        final JList<Course> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFixedCellHeight(40);
        list.setCellRenderer(new ListCellRenderer<Course>() {
            public Component getListCellRendererComponent(JList<? extends Course> l, Course value, int index, boolean isSelected, boolean cellHasFocus) {
                int studentCount = value.getStudents().size();
                String display = "📘 " + value.getTitle() + "  —  👥 " + studentCount + " enrolled";
                JLabel label = new JLabel(display);
                label.setFont(new Font("SansSerif", Font.PLAIN, 14));
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                label.setOpaque(true);
                label.setBackground(isSelected ? new Color(220, 240, 255) : Color.WHITE);
                label.setForeground(isSelected ? Color.BLACK : new Color(50, 50, 50));
                return label;
            }
        });

        for (Course c : db.getAllCourses()) {
            if (c.getApprovalStatus().equals("PENDING")) {
                model.addElement(c);
            }
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton logoutButton = new JButton("🚪 Logout");
        buttonPanel.add(logoutButton);

        mainPanel.add(new JScrollPane(list), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (choice == JOptionPane.YES_OPTION) {
                this.setVisible(false);
                new LoginFrame(db).setVisible(true);
            }
        });
    }
}
