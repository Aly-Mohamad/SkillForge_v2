package ui.frames;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class InstructorDashboardFrame extends JFrame {
    private JsonDatabaseManager db;
    private Instructor instructor;
    private JPanel coursesPanel;
    private JScrollPane scrollPane;

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

        // Use a panel with BoxLayout instead of JList
        coursesPanel = new JPanel();
        coursesPanel.setLayout(new BoxLayout(coursesPanel, BoxLayout.Y_AXIS));
        coursesPanel.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(coursesPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnCreate = new JButton("➕ Create Course");
        JButton btnInsights = new JButton("📊 Insights");
        JButton logoutButton = new JButton("🚪 Logout");

        buttonPanel.add(btnCreate);
        buttonPanel.add(btnInsights);
        buttonPanel.add(logoutButton);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // Load courses
        refreshCoursesPanel();

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
                refreshCoursesPanel();
            }
        });

        btnInsights.addActionListener(e -> {
            // For insights, we need to get the selected course differently
            // You can modify this to use a selection mechanism or show all courses
            List<Course> instructorCourses = getInstructorCourses();
            if (instructorCourses.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No courses available for insights.");
                return;
            }

            // Show a dialog to select a course for insights
            Course selected = showCourseSelectionDialog(instructorCourses);
            if (selected == null) return;

            java.util.Map<String, Double> avgScores = new java.util.LinkedHashMap<>();
            java.util.Map<String, Double> completionRates = new java.util.LinkedHashMap<>();

            java.util.List<Student> students = db.getStudentsByIds(selected.getStudentIds());

            for (Lesson lesson : selected.getLessons()) {
                String title = lesson.getTitle();
                avgScores.put(title, selected.getAverageScorePerLesson(lesson.getLessonId()));
                completionRates.put(title, selected.getCompletionRate(lesson.getLessonId(), students));
            }

            JPanel chartPanel = new JPanel(new GridLayout(2, 1));
            chartPanel.add(createBarChart("Average Quiz Scores", avgScores));
            chartPanel.add(createBarChart("Lesson Completion %", completionRates));

            new ChartFrame("Insights - " + selected.getTitle(), chartPanel).setVisible(true);
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

    private void refreshCoursesPanel() {
        coursesPanel.removeAll();

        List<Course> instructorCourses = getInstructorCourses();

        if (instructorCourses.isEmpty()) {
            JLabel noCoursesLabel = new JLabel("No courses created yet. Click 'Create Course' to get started!");
            noCoursesLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
            noCoursesLabel.setForeground(Color.GRAY);
            noCoursesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            noCoursesLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
            coursesPanel.add(noCoursesLabel);
        } else {
            for (Course course : instructorCourses) {
                coursesPanel.add(createCourseRow(course));
                coursesPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        coursesPanel.revalidate();
        coursesPanel.repaint();
    }

    private JPanel createCourseRow(Course course) {
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        rowPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        // Course info
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);

        int studentCount = course.getStudentIds().size();
        JLabel titleLabel = new JLabel("📘 " + course.getTitle());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel detailsLabel = new JLabel("👥 " + studentCount + " enrolled | Status: " + course.getApprovalStatus());
        detailsLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        detailsLabel.setForeground(Color.GRAY);

        infoPanel.add(titleLabel);
        infoPanel.add(detailsLabel);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonsPanel.setOpaque(false);

        // Edit button
        JButton editButton = new JButton("✏️ Edit");
        editButton.setBackground(new Color(173, 216, 230));
        editButton.setOpaque(true);
        editButton.setBorderPainted(false);
        editButton.setFocusPainted(false);

        // View Students button
        JButton studentsButton = new JButton("👥 Students");
        studentsButton.setBackground(new Color(144, 238, 144));
        studentsButton.setOpaque(true);
        studentsButton.setBorderPainted(false);
        studentsButton.setFocusPainted(false);

        // Delete button
        JButton deleteButton = new JButton("🗑️ Delete");
        deleteButton.setBackground(new Color(255, 182, 193));
        deleteButton.setOpaque(true);
        deleteButton.setBorderPainted(false);
        deleteButton.setFocusPainted(false);

        // Add action listeners
        editButton.addActionListener(e -> {
            new CourseEditorDialog(this, db, course).setVisible(true);
            refreshCoursesPanel(); // Refresh after editing
        });

        studentsButton.addActionListener(e -> {
            showEnrolledStudents(course);
        });

        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete the course: '" + course.getTitle() + "'?\n",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                // Remove from database
                db.deleteCourse(course.getCourseId());

                // Remove from instructor's created courses
                instructor.getCreatedCourses().remove(course.getCourseId());

                // Save changes
                db.save();

                // Refresh the panel
                refreshCoursesPanel();

                JOptionPane.showMessageDialog(
                        this,
                        "Course '" + course.getTitle() + "' has been deleted successfully!"
                );
            }
        });

        buttonsPanel.add(editButton);
        buttonsPanel.add(studentsButton);
        buttonsPanel.add(deleteButton);

        rowPanel.add(infoPanel, BorderLayout.CENTER);
        rowPanel.add(buttonsPanel, BorderLayout.EAST);

        return rowPanel;
    }

    private void showEnrolledStudents(Course course) {
        javax.swing.DefaultListModel<String> modelList = new javax.swing.DefaultListModel<>();
        java.util.LinkedHashSet<String> ids = new java.util.LinkedHashSet<>();
        if (course.getStudentIds() != null) ids.addAll(course.getStudentIds());

        for (String idOrUsername : ids) {
            java.util.Optional<User> opt = db.findById(idOrUsername);
            if (opt.isEmpty()) {
                opt = db.findByUsername(idOrUsername);
            }

            if (opt.isPresent() && opt.get() instanceof Student s) {
                String uname = s.getUsername() != null ? s.getUsername() : "(unknown)";
                String email = s.getEmail() != null ? s.getEmail() : "(no email)";
                modelList.addElement(uname + " (" + email + ")");
            } else {
                modelList.addElement(idOrUsername + " (email unknown)");
            }
        }

        if (modelList.isEmpty()) {
            modelList.addElement("No enrolled students yet.");
        }

        javax.swing.JList<String> jList = new javax.swing.JList<>(modelList);
        jList.setVisibleRowCount(10);
        javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(jList);
        scroll.setPreferredSize(new java.awt.Dimension(420, 240));

        JOptionPane.showMessageDialog(
                this,
                scroll,
                "Enrolled students - " + course.getTitle(),
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private List<Course> getInstructorCourses() {
        List<Course> instructorCourses = new ArrayList<>();
        for (Course c : db.getAllCourses()) {
            if (c.getInstructorId().equals(instructor.getUserId())) {
                instructorCourses.add(c);
            }
        }
        return instructorCourses;
    }

    private Course showCourseSelectionDialog(List<Course> courses) {
        if (courses.size() == 1) {
            return courses.get(0);
        }

        JComboBox<Course> courseCombo = new JComboBox<>(courses.toArray(new Course[0]));
        courseCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Course) {
                    setText(((Course) value).getTitle());
                }
                return this;
            }
        });

        int result = JOptionPane.showConfirmDialog(
                this,
                courseCombo,
                "Select Course for Insights",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            return (Course) courseCombo.getSelectedItem();
        }
        return null;
    }

    private JPanel createBarChart(String title, java.util.Map<String, Double> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (var entry : data.entrySet()) {
            dataset.addValue(entry.getValue(), "Value", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                title,
                "Lesson",
                "Value",
                dataset
        );

        var plot = chart.getCategoryPlot();
        var renderer = (org.jfree.chart.renderer.category.BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(137, 207, 240));

        int itemCount = dataset.getColumnCount();
        if (itemCount == 1) {
            renderer.setMaximumBarWidth(0.1);
        } else {
            renderer.setMaximumBarWidth(0.2);
        }

        return new ChartPanel(chart);
    }
}