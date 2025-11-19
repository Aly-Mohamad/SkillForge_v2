package ui.frames;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionEditorDialog extends JDialog {
    private final List<Question> questions = new ArrayList<>();
    private boolean confirmed = false;

    public QuestionEditorDialog(JFrame owner) {
        super(owner, "Add Questions", true);
        setSize(500, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(questionPanel);

        JButton addQuestion = new JButton("Add Question");
        addQuestion.addActionListener(e -> {
            QuestionInputPanel qPanel = new QuestionInputPanel();
            questionPanel.add(qPanel);
            questionPanel.revalidate();
        });

        JButton done = new JButton("Done");
        done.addActionListener(e -> {
            for (Component comp : questionPanel.getComponents()) {
                if (comp instanceof QuestionInputPanel qPanel) {
                    questions.add(qPanel.toQuestion());
                }
            }
            if (questions.isEmpty()) {
                JOptionPane.showMessageDialog(this, "At least one question is required.");
            } else {
                confirmed = true;
                dispose();
            }
        });

        JPanel bottom = new JPanel();
        bottom.add(addQuestion);
        bottom.add(done);

        add(scrollPane, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}