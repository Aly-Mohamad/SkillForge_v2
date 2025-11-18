package ui.frames;

import model.*;
import javax.swing.*;
import java.awt.*;

public class StudentDashboardFrame extends JFrame {

    private JsonDatabaseManager db;
    private Student student;

    public StudentDashboardFrame(JsonDatabaseManager db, Student student) {
        this.db = db;
        this.student = student;

        setTitle("Student Dashboard - " + student.getUsername());
        setSize(820, 470);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        DefaultListModel<Course> enrolledModel = new DefaultListModel<>();
        DefaultListModel<Course> availableModel = new DefaultListModel<>();

        for (Course c : db.getAllCourses()) {
            if (student.getCourses().contains(c.getCourseId())) enrolledModel.addElement(c);
            else availableModel.addElement(c);
        }

        JList<Course> enrolledList = new JList<>(enrolledModel);
        JList<Course> availableList = new JList<>(availableModel);

        enrolledList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        availableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        enrolledList.setBorder(BorderFactory.createTitledBorder("Enrolled Courses"));
        availableList.setBorder(BorderFactory.createTitledBorder("Available Courses"));

        enrolledList.setCellRenderer(courseRenderer(true));
        availableList.setCellRenderer(courseRenderer(false));

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        centerPanel.add(new JScrollPane(enrolledList));
        centerPanel.add(new JScrollPane(availableList));

        JButton btnEnroll = new JButton("➕ Enroll");
        JButton btnView = new JButton("📖 View Lessons");
        JButton btnLogout = new JButton("🚪 Logout");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.add(btnEnroll);
        buttonPanel.add(btnView);
        buttonPanel.add(btnLogout);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // ---------------- BUTTON ACTIONS -----------------
        btnEnroll.addActionListener(e -> {
            Course selected = availableList.getSelectedValue();
            if (selected == null) { JOptionPane.showMessageDialog(this, "Select a course to enroll."); return; }
            selected.enrollStudent(student.getUserId());
            student.enroll(selected.getCourseId());
            db.updateCourse(selected);
            availableModel.removeElement(selected);
            enrolledModel.addElement(selected);
            JOptionPane.showMessageDialog(this, "Enrolled in: " + selected.getTitle());
        });

        btnView.addActionListener(e -> {
            Course selected = enrolledList.getSelectedValue();
            if (selected == null) { JOptionPane.showMessageDialog(this, "Select an enrolled course to view lessons."); return; }
            new LessonListDialog(this, db, student, selected).setVisible(true);
            enrolledList.repaint(); // refresh progress
        });

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) { this.dispose(); new LoginFrame(db).setVisible(true); }
        });
    }

    private ListCellRenderer<Course> courseRenderer(boolean showProgress) {
        return (list, course, index, isSelected, cellHasFocus) -> {
            String text;
            if (showProgress) {
                text = "📘 " + course.getTitle() + " — " + course.getCompletionPercentage(student.getUserId()) + "% completed";
            } else text = "📘 " + course.getTitle() + " — " + course.getDescription();

            JLabel lbl = new JLabel(text);
            lbl.setOpaque(true);
            lbl.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            lbl.setFont(new java.awt.Font("SansSerif", Font.PLAIN, 13));
            lbl.setBackground(isSelected ? new java.awt.Color(220, 240, 255) : Color.WHITE);
            lbl.setForeground(isSelected ? Color.BLACK : new java.awt.Color(50, 50, 50));
            return lbl;
        };
    }
}
