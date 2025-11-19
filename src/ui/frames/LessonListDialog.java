package ui.frames;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LessonListDialog extends JDialog {

    public LessonListDialog(JFrame owner,JsonDatabaseManager databaseManager ,Student student, Course course) {
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

        lessonList.setCellRenderer(new ListCellRenderer<Lesson>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends Lesson> list, Lesson lesson, int index, boolean isSelected, boolean cellHasFocus) {
                JPanel panel = new JPanel(new BorderLayout());
                panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                panel.setOpaque(true);
                panel.setBackground(isSelected ? new Color(220, 240, 255) : Color.WHITE);

                JLabel titleLabel = new JLabel(lesson.getTitle());
                titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
                titleLabel.setForeground(isSelected ? Color.BLACK : new Color(50, 50, 50));

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
        JButton markCompletedButton = new JButton("Mark as Completed");
        JButton backButton = new JButton("Back");

        buttonPanel.add(markCompletedButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        markCompletedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Lesson selected = lessonList.getSelectedValue();
                if (selected == null) {
                    JOptionPane.showMessageDialog(LessonListDialog.this, "Select a lesson to mark as completed.");
                } else {
                    JOptionPane.showMessageDialog(LessonListDialog.this, "Marked '" + selected.getTitle() + "' as completed ✅");
                    lessonList.repaint();
                }
            }
        });

        backButton.addActionListener(e -> dispose());
    }
}