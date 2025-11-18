package ui.frames;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class InstructorDashboardFrame extends JFrame {
    private JsonDatabaseManager db;
    private Instructor instructor;

    public InstructorDashboardFrame(JsonDatabaseManager db, Instructor ins) {
        this.db = db;
        this.instructor = ins;
        setTitle("Instructor Dashboard - " + ins.getUsername());
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
            if (c.getInstructorId().equals(instructor.getUserId())) {
                model.addElement(c);
            }
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnCreate = new JButton("➕ Create Course");
        JButton btnEdit = new JButton("✏️ Edit Course");
        JButton btnViewStudents = new JButton("👥 View Enrolled Students");
        JButton logoutButton = new JButton("🚪 Logout");

        buttonPanel.add(btnCreate);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnViewStudents);
        buttonPanel.add(logoutButton);

        mainPanel.add(new JScrollPane(list), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        btnCreate.addActionListener(e -> {
            JPanel inputPanel = new JPanel(new GridLayout(4, 1, 5, 5));
            JTextField titleField = new JTextField();
            JTextArea descArea = new JTextArea(3, 20);
            descArea.setLineWrap(true);
            descArea.setWrapStyleWord(true);
            JScrollPane descScroll = new JScrollPane(descArea);

            inputPanel.add(new JLabel("Course Title:"));
            inputPanel.add(titleField);
            inputPanel.add(new JLabel("Description:"));
            inputPanel.add(descScroll);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    inputPanel,
                    "Create New Course",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                String title = titleField.getText().trim();
                String desc = descArea.getText().trim();
                if (title.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Title cannot be empty.");
                    return;
                }
                Course c = new Course(title, desc, instructor.getUserId());
                db.addCourse(c);
                instructor.addCourse(c.getCourseId());
                db.save();
                model.addElement(c);
            }
        });

        btnEdit.addActionListener(e -> {
            Course c = list.getSelectedValue();
            if (c == null) {
                JOptionPane.showMessageDialog(this, "Select a course");
                return;
            }
            new CourseEditorDialog(this, db, c).setVisible(true);
        });

        btnViewStudents.addActionListener(e -> {
            Course c = list.getSelectedValue();
            if (c == null) {
                JOptionPane.showMessageDialog(this, "Select a course");
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (String sid : c.getStudents()) {
                db.findById(sid).ifPresent(u -> sb.append(u.getUsername()).append(" (").append(u.getEmail()).append(")\n"));
            }
            if (sb.length() == 0) sb.append("No enrolled students yet.");
            JOptionPane.showMessageDialog(this, sb.toString(), "Enrolled students", JOptionPane.INFORMATION_MESSAGE);
        });

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