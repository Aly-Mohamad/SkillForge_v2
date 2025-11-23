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

    private JPanel listPanel;
    private JScrollPane listScrollPane;

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

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        listScrollPane = new JScrollPane(listPanel);
        listScrollPane.getVerticalScrollBar().setUnitIncrement(12);

        mainPanel.add(new JLabel("📋 Pending Courses for Approval", SwingConstants.CENTER), BorderLayout.NORTH);
        mainPanel.add(listScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("🚪 Logout");
        bottomPanel.add(logoutButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

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

        add(mainPanel);
        loadPendingCourses();
    }

    private void loadPendingCourses() {
        listPanel.removeAll();

        boolean any = false;
        for (Course c : db.getAllCourses()) {
            if ("PENDING".equals(c.getApprovalStatus())) {
                listPanel.add(createCourseRow(c));
                listPanel.add(Box.createRigidArea(new Dimension(0, 6))); // spacing between rows
                any = true;
            }
        }

        if (!any) {
            JLabel noneLabel = new JLabel(" No pending courses for approval");
            noneLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
            noneLabel.setForeground(Color.GRAY);
            noneLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
            noneLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(noneLabel);
        }

        listPanel.revalidate();
        listPanel.repaint();
    }


    private JPanel createCourseRow(Course course) {
        JPanel row = new JPanel(new BorderLayout(10, 10));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        row.setBackground(Color.WHITE);

        // Info area (left)
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);
        int studentCount = (course.getStudentIds() == null) ? 0 : course.getStudentIds().size();
        JLabel titleLabel = new JLabel("📘 " + course.getTitle());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel detailsLabel = new JLabel("👥 " + studentCount + " enrolled | Instructor: " + course.getInstructorId());
        detailsLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        detailsLabel.setForeground(Color.GRAY);
        infoPanel.add(titleLabel);
        infoPanel.add(detailsLabel);

        row.add(infoPanel, BorderLayout.CENTER);

        // Buttons area (right)
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);

        JButton acceptBtn = new JButton("✅ Accept");
        JButton rejectBtn = new JButton("❌ Reject");
        JButton detailsBtn = new JButton("🔍 Details");

        acceptBtn.setBackground(new Color(144, 238, 144));
        acceptBtn.setOpaque(true);
        acceptBtn.setBorderPainted(false);

        rejectBtn.setBackground(new Color(255, 182, 193));
        rejectBtn.setOpaque(true);
        rejectBtn.setBorderPainted(false);

        detailsBtn.setBackground(new Color(173, 216, 230));
        detailsBtn.setOpaque(true);
        detailsBtn.setBorderPainted(false);

        // Action listeners
        acceptBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        AdminDashboardFrame.this,
                        "Approve course '" + course.getTitle() + "'?",
                        "Confirm Approval",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    course.setApprovalStatus("APPROVED");
                    db.updateCourse(course);
                    // remove row from UI
                    loadPendingCourses();
                    JOptionPane.showMessageDialog(AdminDashboardFrame.this,
                            "Course '" + course.getTitle() + "' has been approved!");
                }
            }
        });

        rejectBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        AdminDashboardFrame.this,
                        "Are you sure you want to reject and delete the course: '" + course.getTitle() + "'?",
                        "Confirm Rejection",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    db.deleteCourse(course.getCourseId());
                    loadPendingCourses();
                    JOptionPane.showMessageDialog(AdminDashboardFrame.this,
                            "Course '" + course.getTitle() + "' has been rejected and deleted!");
                }
            }
        });

        detailsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showCourseDetails(course);
            }
        });

        buttons.add(detailsBtn);
        buttons.add(acceptBtn);
        buttons.add(rejectBtn);

        row.add(buttons, BorderLayout.EAST);

        return row;
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

        JTextArea descArea = new JTextArea(course.getDescription());
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(new Color(245, 245, 245));
        descArea.setBorder(BorderFactory.createTitledBorder("Course Description"));

        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(400, 80));

        JPanel mainDetailsPanel = new JPanel(new BorderLayout(10, 10));
        mainDetailsPanel.add(infoPanel, BorderLayout.NORTH);
        mainDetailsPanel.add(descScroll, BorderLayout.CENTER);

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
}
