package ui.frames;

import model.*;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LessonListDialog extends JDialog {

    public LessonListDialog(JFrame owner, JsonDatabaseManager databaseManager, Student student, Course course) {
        super(owner, "Lessons - " + course.getTitle(), true);
        setSize(600, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        DefaultListModel<Lesson> lessonModel = new DefaultListModel<>();
        for (Lesson lesson : course.getLessons()) {
            lessonModel.addElement(lesson);
        }

        JList<Lesson> lessonList = new JList<>(lessonModel);
        lessonList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Helper to decide if the lesson at index is unlocked:
        // First lesson is always unlocked; others require previous lesson to be passed by this student
        final java.util.function.IntPredicate isUnlockedAtIndex = (int index) -> {
            if (index <= 0) return true;
            Lesson prev = lessonModel.get(index - 1);
            // Gate by per-student completion, which implies "passed"
            return student.hasCompleted(prev.getLessonId());
        };

        lessonList.setCellRenderer(new ListCellRenderer<Lesson>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends Lesson> list, Lesson lesson, int index, boolean isSelected, boolean cellHasFocus) {
                JPanel panel = new JPanel(new BorderLayout());
                panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                panel.setOpaque(true);
                panel.setBackground(isSelected ? new Color(220, 240, 255) : Color.WHITE);

                boolean unlocked = isUnlockedAtIndex.test(index);
                boolean completed =
                        course.isLessonCompleted(student, lesson.getLessonId())
                        || student.hasCompleted(lesson.getLessonId());

                String prefix = completed ? "✅ " : (unlocked ? "" : "🔒 ");
                JLabel titleLabel = new JLabel(prefix + lesson.getTitle());
                titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
                titleLabel.setForeground(isSelected ? Color.BLACK : (unlocked ? new Color(50, 50, 50) : Color.GRAY));
                titleLabel.setToolTipText(!unlocked ? "Locked: pass the previous lesson to unlock" : (completed ? "Completed" : "Not completed"));

                JLabel descLabel = new JLabel("<html><i>" + lesson.getContent() + "</i></html>");
                descLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
                descLabel.setForeground(isSelected ? Color.BLACK : Color.GRAY);

                panel.add(titleLabel, BorderLayout.NORTH);
                panel.add(descLabel, BorderLayout.CENTER);

                return panel;
            }
        });

        add(new JScrollPane(lessonList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        JButton markCompletedButton = new JButton("Open Quiz");
        JButton backButton = new JButton("Back");

        buttonPanel.add(markCompletedButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Disable the button when a locked lesson is selected
        ListSelectionListener selListener = e -> {
            int idx = lessonList.getSelectedIndex();
            boolean enable = idx >= 0 && isUnlockedAtIndex.test(idx);
            markCompletedButton.setEnabled(enable);
            markCompletedButton.setToolTipText(enable ? null : "Locked: pass the previous lesson to unlock");
        };
        lessonList.addListSelectionListener(selListener);
        // Initialize button state
        selListener.valueChanged(null);

        markCompletedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int idx = lessonList.getSelectedIndex();
                Lesson selected = lessonList.getSelectedValue();
                if (selected == null) {
                    JOptionPane.showMessageDialog(LessonListDialog.this, "Select a lesson.");
                    return;
                }

                // Enforce gating
                if (!isUnlockedAtIndex.test(idx)) {
                    JOptionPane.showMessageDialog(LessonListDialog.this, "This lesson is locked. Please pass the previous lesson first.");
                    return;
                }

                // Pass course so the dialog can update and persist it
                LessonQuizDialog quizDialog = new LessonQuizDialog(owner, databaseManager, student, course, selected);
                quizDialog.setVisible(true);

                // Refresh list visuals (completion/lock icons)
                lessonList.repaint();
                selListener.valueChanged(null);
            }
        });

        backButton.addActionListener(e -> dispose());
    }
}