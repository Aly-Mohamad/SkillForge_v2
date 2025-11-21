package ui.frames;

import model.*;
import javax.swing.*;
import java.awt.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

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
                int studentCount = value.getStudentIds().size();
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
        JButton btnInsights = new JButton("📊 Insights");
        buttonPanel.add(btnInsights);
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

            javax.swing.DefaultListModel<String> modelList = new javax.swing.DefaultListModel<>();
            java.util.LinkedHashSet<String> ids = new java.util.LinkedHashSet<>();
            if (c.getStudentIds() != null) ids.addAll(c.getStudentIds());

            for (String idOrUsername : ids) {
                // Try to resolve by userId first, then by username
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
                    "Enrolled students",
                    JOptionPane.PLAIN_MESSAGE
            );
        });
        btnInsights.addActionListener(e -> {
            Course selected = list.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Select a course to view insights.");
                return;
            }

            java.util.Map<String, Double> avgScores = new java.util.LinkedHashMap<>();
            java.util.Map<String, Double> completionRates = new java.util.LinkedHashMap<>();

            // 🔹 Get full Student objects from student IDs
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

    // Add this helper method inside InstructorDashboardFrame (e.g., below init())
    @SuppressWarnings("unchecked")
    private java.util.List<Student> loadAllStudents() {
        // Prefer a direct API if available: db.getAllStudents()
        try {
            java.lang.reflect.Method m = db.getClass().getMethod("getAllStudents");
            Object result = m.invoke(db);
            if (result instanceof java.util.List) {
                return (java.util.List<Student>) result;
            }
        } catch (Throwable ignore) { }

        // Fallback: try db.getAllUsers() and filter students, if such a method exists
        try {
            java.lang.reflect.Method m = db.getClass().getMethod("getAllUsers");
            Object result = m.invoke(db);
            java.util.List<Student> out = new java.util.ArrayList<>();
            if (result instanceof java.util.List<?>) {
                for (Object u : (java.util.List<?>) result) {
                    if (u instanceof Student s) out.add(s);
                }
            }
            return out;
        } catch (Throwable ignore) { }

        // Last resort: empty list, UI will still show stored identifiers
        return java.util.Collections.emptyList();
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

        // Customize the chart
        var plot = chart.getCategoryPlot();
        var renderer = (org.jfree.chart.renderer.category.BarRenderer) plot.getRenderer();

        // 1. Set bar color
        renderer.setSeriesPaint(0, new Color(137, 207, 240)); // Baby blue

        // 2. Adjust bar width
        int itemCount = dataset.getColumnCount();
        if (itemCount == 1) {
            renderer.setMaximumBarWidth(0.1); // Narrower bar when only one item
        } else {
            renderer.setMaximumBarWidth(0.2); // Default width for multiple bars
        }

        return new ChartPanel(chart);
    }
}