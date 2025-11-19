package ui.frames;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import model.Question;

class QuestionInputPanel extends JPanel {
    private final JTextField questionField = new JTextField();
    private final JTextField[] answerFields = new JTextField[4];
    private final JRadioButton[] radioButtons = new JRadioButton[4];
    private final ButtonGroup group = new ButtonGroup();

    public QuestionInputPanel() {
        setLayout(new GridLayout(6, 2));
        add(new JLabel("Question:"));
        add(questionField);

        for (int i = 0; i < 4; i++) {
            answerFields[i] = new JTextField();
            radioButtons[i] = new JRadioButton("Correct");
            group.add(radioButtons[i]);

            add(new JLabel("Answer " + (i + 1) + ":"));
            JPanel answerRow = new JPanel(new BorderLayout());
            answerRow.add(answerFields[i], BorderLayout.CENTER);
            answerRow.add(radioButtons[i], BorderLayout.EAST);
            add(answerRow);
        }

        // Optional: select the first radio button by default
        radioButtons[0].setSelected(true);
    }

    public Question toQuestion() {
        String qText = questionField.getText();
        String[] answers = new String[4];
        int correctIndex = -1;

        for (int i = 0; i < 4; i++) {
            answers[i] = answerFields[i].getText();
            if (radioButtons[i].isSelected()) {
                correctIndex = i;
            }
        }

        if (correctIndex == -1) {
            throw new IllegalStateException("No correct answer selected.");
        }

        return new Question(qText, answers, answers[correctIndex]);
    }
}