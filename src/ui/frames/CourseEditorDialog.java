package ui.frames;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CourseEditorDialog extends JDialog {

    public CourseEditorDialog(JFrame owner, final JsonDatabaseManager db, final Course course) {
        super(owner, "Edit Course - " + course.getTitle(), true);
        setSize(700, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new GridLayout(3, 2));
        final JTextField title = new JTextField(course.getTitle());
        final JTextArea desc = new JTextArea(course.getDescription());
        top.add(new JLabel("Title:"));
        top.add(title);
        top.add(new JLabel("Description:"));
        top.add(new JScrollPane(desc));

        final JPanel lessonPanel = new JPanel();
        lessonPanel.setLayout(new BoxLayout(lessonPanel, BoxLayout.Y_AXIS));

        for (Lesson lesson : course.getLessons()) {
            lessonPanel.add(createLessonRow(lesson, course, db, lessonPanel));
        }

        JScrollPane scrollPane = new JScrollPane(lessonPanel);

        JPanel buttons = new JPanel();
        JButton addLesson = new JButton("Add Lesson");
        JButton save = new JButton("Save Course");
        buttons.add(addLesson);
        buttons.add(save);

        add(top, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        addLesson.addActionListener(e -> {
            Lesson newLesson = new Lesson("New Lesson", "");
            course.addLesson(newLesson);
            lessonPanel.add(createLessonRow(newLesson, course, db, lessonPanel));
            lessonPanel.revalidate();
            lessonPanel.repaint();
            db.save();
        });

        save.addActionListener(e -> {
            course.setTitle(title.getText());
            course.setDescription(desc.getText());
            db.updateCourse(course);
            JOptionPane.showMessageDialog(CourseEditorDialog.this, "Saved");
            dispose();
        });
    }

    private JPanel createLessonRow(Lesson lesson, Course course, JsonDatabaseManager db, JPanel container) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 6, 4, 6),
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Left panel for title and content
        JPanel fieldsPanel = new JPanel(new GridLayout(2, 2, 5, 2));
        JLabel titleLabel = new JLabel("📘 Title:");
        JTextField titleField = new JTextField(lesson.getTitle());
        titleField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        titleField.setPreferredSize(new Dimension(200, 25));

        JLabel contentLabel = new JLabel("📝 Content:");
        JTextField contentField = new JTextField(lesson.getContent());
        contentField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        contentField.setPreferredSize(new Dimension(200, 25));

        fieldsPanel.add(titleLabel);
        fieldsPanel.add(titleField);
        fieldsPanel.add(contentLabel);
        fieldsPanel.add(contentField);

        // Right panel for buttons stacked vertically
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");

        Dimension btnSize = new Dimension(80, 25);
        updateBtn.setMaximumSize(btnSize);
        deleteBtn.setMaximumSize(btnSize);

        updateBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        deleteBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));

        updateBtn.addActionListener(e -> {
            lesson.setTitle(titleField.getText());
            lesson.setContent(contentField.getText());
            db.save();
            container.revalidate();
            container.repaint();
        });

        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(container, "Delete this lesson?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                course.removeLesson(lesson.getLessonId());
                container.remove(row);
                db.save();
                container.revalidate();
                container.repaint();
            }
        });

        buttonPanel.add(updateBtn);
        buttonPanel.add(Box.createVerticalStrut(5));
        buttonPanel.add(deleteBtn);

        row.add(fieldsPanel, BorderLayout.CENTER);
        row.add(buttonPanel, BorderLayout.EAST);
        return row;
    }
}