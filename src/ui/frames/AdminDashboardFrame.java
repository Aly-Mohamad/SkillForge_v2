package ui.frames;

import model.Admin;
import model.Course;
import model.JsonDatabaseManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminDashboardFrame extends JFrame {
    private JsonDatabaseManager db;
    private Admin admin;
    private DefaultListModel<Course> model;
    private JList<Course> list;

    public AdminDashboardFrame(JsonDatabaseManager db, Admin admin) {
        this.db = db;
        this.admin = admin;
        setTitle("Admin Dashboard - " + admin.getUsername());
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        init();
    }

    private void init() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        model = new DefaultListModel<>();
        list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFixedCellHeight(60);

        // Custom cell renderer for display only
        list.setCellRenderer(new ListCellRenderer<Course>() {
            public Component getListCellRendererComponent(JList<? extends Course> l, Course value, int index, boolean isSelected, boolean cellHasFocus) {
                JPanel panel = new JPanel(new BorderLayout());
                panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                panel.setOpaque(true);
                panel.setBackground(isSelected ? new Color(220, 240, 255) : Color.WHITE);

                // Course info panel
                JPanel infoPanel = new JPanel(new GridLayout(2, 1));
                int studentCount = value.getStudentIds().size();
                JLabel titleLabel = new JLabel("📘 " + value.getTitle());
                titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

                JLabel detailsLabel = new JLabel("👥 " + studentCount + " enrolled | Instructor: " + value.getInstructorId());
                detailsLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
                detailsLabel.setForeground(Color.GRAY);

                infoPanel.add(titleLabel);
                infoPanel.add(detailsLabel);

                panel.add(infoPanel, BorderLayout.CENTER);


                return panel;
            }
        });

        // Load pending courses
        loadPendingCourses();

        // Button panel for actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton acceptButton = new JButton("✅ Accept");
        JButton rejectButton = new JButton("❌ Reject");
        JButton detailsButton = new JButton("🔍 Details");
        JButton logoutButton = new JButton("🚪 Logout");

        acceptButton.setBackground(new Color(144, 238, 144));
        acceptButton.setOpaque(true);
        acceptButton.setBorderPainted(false);

        rejectButton.setBackground(new Color(255, 182, 193));
        rejectButton.setOpaque(true);
        rejectButton.setBorderPainted(false);

        detailsButton.setBackground(new Color(173, 216, 230));
        detailsButton.setOpaque(true);
        detailsButton.setBorderPainted(false);

        actionPanel.add(acceptButton);
        actionPanel.add(rejectButton);
        actionPanel.add(detailsButton);
        actionPanel.add(logoutButton);


        acceptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Course selected = list.getSelectedValue();
                if (selected == null) {
                    JOptionPane.showMessageDialog(AdminDashboardFrame.this,
                            "Please select a course to accept.");
                    return;
                }

                selected.setApprovalStatus("APPROVED");
                db.updateCourse(selected);
                model.removeElement(selected);
                JOptionPane.showMessageDialog(AdminDashboardFrame.this,
                        "Course '" + selected.getTitle() + "' has been approved!");
            }
        });

        rejectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Course selected = list.getSelectedValue();
                if (selected == null) {
                    JOptionPane.showMessageDialog(AdminDashboardFrame.this,
                            "Please select a course to reject.");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(
                        AdminDashboardFrame.this,
                        "Are you sure you want to reject and delete the course: '" + selected.getTitle() + "'?",
                        "Confirm Rejection",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    db.deleteCourse(selected.getCourseId());
                    model.removeElement(selected);
                    JOptionPane.showMessageDialog(AdminDashboardFrame.this,
                            "Course '" + selected.getTitle() + "' has been rejected and deleted!");
                }
            }
        });

        detailsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Course selected = list.getSelectedValue();
                if (selected == null) {
                    JOptionPane.showMessageDialog(AdminDashboardFrame.this,
                            "Please select a course to view details.");
                    return;
                }
                showCourseDetails(selected);
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                        AdminDashboardFrame.this,
                        "Are you sure you want to logout?",
                        "Confirm Logout",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (choice == JOptionPane.YES_OPTION) {
                    AdminDashboardFrame.this.setVisible(false);
                    new LoginFrame(db).setVisible(true);
                }
            }
        });

        mainPanel.add(new JLabel("📋 Pending Courses for Approval", SwingConstants.CENTER), BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(list), BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }
    private void showCourseDetails(Course course) {

        JDialog detailsDialog = new JDialog(this, "Course Details - " + course.getTitle(), true);
        detailsDialog.setSize(500, 400);
        detailsDialog.setLocationRelativeTo(this);
        detailsDialog.setLayout(new BorderLayout(10, 10));


        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));


        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));

        infoPanel.add(new JLabel("📘 Course Title: " + course.getTitle()));
        infoPanel.add(new JLabel("🆔 Course ID: " + course.getCourseId()));
        infoPanel.add(new JLabel("👨‍🏫 Instructor ID: " + course.getInstructorId()));


        // Description area
        JTextArea descArea = new JTextArea(course.getDescription());
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(new Color(245, 245, 245));
        descArea.setBorder(BorderFactory.createTitledBorder("Course Description"));

        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(400, 80));

        // Lessons list




        // Combine all components
        JPanel mainDetailsPanel = new JPanel(new BorderLayout(10, 10));
        mainDetailsPanel.add(infoPanel, BorderLayout.NORTH);
        mainDetailsPanel.add(descScroll, BorderLayout.CENTER);


        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                detailsDialog.dispose();
            }
        });

        contentPanel.add(mainDetailsPanel, BorderLayout.CENTER);
        contentPanel.add(closeButton, BorderLayout.SOUTH);

        detailsDialog.add(contentPanel);
        detailsDialog.setVisible(true);
    }

    private void loadPendingCourses() {
        model.clear();
        for (Course c : db.getAllCourses()) {
            if ("PENDING".equals(c.getApprovalStatus())) {
                model.addElement(c);
            }
        }


        // If no pending courses
        if (model.isEmpty()) {
            list.setCellRenderer(new ListCellRenderer<Course>() {
                public Component getListCellRendererComponent(JList<? extends Course> l, Course value, int index, boolean isSelected, boolean cellHasFocus) {
                    JLabel label = new JLabel(" No pending courses for approval");
                    label.setFont(new Font("SansSerif", Font.ITALIC, 14));
                    label.setForeground(Color.GRAY);
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
                    return label;
                }
            });
        } else {
            // Reset to original renderer
            list.setCellRenderer(new ListCellRenderer<Course>() {
                public Component getListCellRendererComponent(JList<? extends Course> l, Course value, int index, boolean isSelected, boolean cellHasFocus) {
                    JPanel panel = new JPanel(new BorderLayout());
                    panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                    panel.setOpaque(true);
                    panel.setBackground(isSelected ? new Color(220, 240, 255) : Color.WHITE);

                    JPanel infoPanel = new JPanel(new GridLayout(2, 1));
                    int studentCount = value.getStudentIds().size();
                    JLabel titleLabel = new JLabel("📘 " + value.getTitle());
                    titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

                    JLabel detailsLabel = new JLabel("👥 " + studentCount + " enrolled | Instructor: " + value.getInstructorId());
                    detailsLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    detailsLabel.setForeground(Color.GRAY);

                    infoPanel.add(titleLabel);
                    infoPanel.add(detailsLabel);

                    panel.add(infoPanel, BorderLayout.CENTER);

                    return panel;
                }
            });
        }
    }
}