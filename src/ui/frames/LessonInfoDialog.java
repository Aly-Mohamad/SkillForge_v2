package ui.frames;

import javax.swing.*;
import java.awt.*;

public class LessonInfoDialog extends JDialog {
    private JTextField titleField;
    private JTextArea contentArea;
    private boolean confirmed = false;

    public LessonInfoDialog(JFrame owner) {
        super(owner, "New Lesson Info", true);
        setSize(400, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        titleField = new JTextField();
        contentArea = new JTextArea();

        JPanel form = new JPanel(new GridLayout(2, 2));
        form.add(new JLabel("Title:"));
        form.add(titleField);
        form.add(new JLabel("Content:"));
        form.add(new JScrollPane(contentArea));

        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(e -> {
            if (!titleField.getText().trim().isEmpty()) {
                confirmed = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Title is required.");
            }
        });

        add(form, BorderLayout.CENTER);
        add(nextButton, BorderLayout.SOUTH);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getLessonTitle() {
        return titleField.getText();
    }

    public String getLessonContent() {
        return contentArea.getText();
    }
}