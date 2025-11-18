package ui.frames;

import model.JsonDatabaseManager;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LessonListDialog extends JDialog {

    public LessonListDialog(JFrame owner, JsonDatabaseManager db, final Student student, final Course course) {
        super(owner, "Lessons - " + course.getTitle(), true);
        setSize(600, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        final DefaultListModel<Lesson> lm = new DefaultListModel<>();
        for (Lesson l : course.getLessons()) lm.addElement(l);

        final JList<Lesson> list = new JList<>(lm);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Custom renderer: title + description + completion
        list.setCellRenderer(new javax.swing.ListCellRenderer<Lesson>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends Lesson> jList, Lesson lesson, int index, boolean isSelected, boolean cellHasFocus) {
                JPanel panel = new JPanel(new BorderLayout());
                panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                panel.setOpaque(true);
                panel.setBackground(isSelected ? new Color(220, 240, 255) : Color.WHITE);

                // Title with completion indicator
                boolean completed = course.isLessonCompleted(student.getUserId(), lesson.getLessonId());
                String titleText = lesson.getTitle() + (completed ? " ✅" : "");
                JLabel titleLabel = new JLabel(titleText);
                titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
                titleLabel.setForeground(isSelected ? Color.BLACK : new Color(50, 50, 50));

                // Description in smaller, lighter font
                JLabel descLabel = new JLabel("<html><i>" + lesson.getContent() + "</i></html>");
                descLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
                descLabel.setForeground(isSelected ? Color.BLACK : Color.GRAY);

                panel.add(titleLabel, BorderLayout.NORTH);
                panel.add(descLabel, BorderLayout.CENTER);

                return panel;
            }
        });

        add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        JButton backButton = new JButton("Back");
        JButton markCompletedButton = new JButton("Mark as Completed");
        southPanel.add(markCompletedButton);
        southPanel.add(backButton);
        add(southPanel, BorderLayout.SOUTH);

        backButton.addActionListener(e -> LessonListDialog.this.dispose());

        markCompletedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Lesson selected = list.getSelectedValue();
                if (selected == null) {
                    JOptionPane.showMessageDialog(LessonListDialog.this, "Select a lesson to mark as completed.");
                    return;
                }

                if (!course.isLessonCompleted(student.getUserId(), selected.getLessonId())) {
                    course.markLessonCompleted(student.getUserId(), selected.getLessonId());
                    db.updateCourse(course);
                    db.save();
                    list.repaint();
                    JOptionPane.showMessageDialog(LessonListDialog.this,
                            "Marked '" + selected.getTitle() + "' as completed ✅");
                } else {
                    JOptionPane.showMessageDialog(LessonListDialog.this,
                            "This lesson is already completed.");
                }
            }
        });
    }
}
