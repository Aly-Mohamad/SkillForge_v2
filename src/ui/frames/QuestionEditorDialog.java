package ui.frames;

import model.Question;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class QuestionEditorDialog extends JDialog {
    private boolean confirmed = false;

    private final JPanel listPanel = new JPanel();
    private final List<Row> rows = new ArrayList<>();

    private static class Row {
        JTextField questionText;
        JTextField[] answers;
        JRadioButton[] radios;
        ButtonGroup group;
        JPanel panel;
    }

    public QuestionEditorDialog(JFrame owner) {
        super(owner, "Question Editor", true);
        setSize(600, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(8, 8));

        // Prevent accidental close; prompt to continue editing
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                        QuestionEditorDialog.this,
                        "Questions are incomplete. Continue editing?",
                        "Continue editing?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (choice == JOptionPane.NO_OPTION) {
                    confirmed = false;
                    setVisible(false);
                }
            }
        });

        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(listPanel), BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton("Add Question");
        JButton cancelBtn = new JButton("Cancel");
        JButton doneBtn = new JButton("Done");

        controls.add(addBtn);
        controls.add(cancelBtn);
        controls.add(doneBtn);
        add(controls, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> addRow(null));

        cancelBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Questions are incomplete. Continue editing?",
                    "Continue editing?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (choice == JOptionPane.NO_OPTION) {
                confirmed = false;
                setVisible(false);
            }
        });

        // Done: validate and only close when everything is filled
        doneBtn.addActionListener(e -> {
            String error = validateAll();
            if (error != null) {
                JOptionPane.showMessageDialog(
                        this,
                        error,
                        "Please complete all fields",
                        JOptionPane.ERROR_MESSAGE
                );
                // Stay open so the instructor can finish
                return;
            }
            confirmed = true;
            setVisible(false);
        });

        // Start with one question row
        addRow(null);

        // Enter key triggers Done (with validation)
        getRootPane().setDefaultButton(doneBtn);
    }

    private void addRow(Question preset) {
        Row r = new Row();
        r.panel = new JPanel(new GridBagLayout());
        r.panel.setBorder(BorderFactory.createTitledBorder("Question"));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 4, 4, 4);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;

        // Question text
        gc.gridx = 0; gc.gridy = 0;
        r.panel.add(new JLabel("Question text:"), gc);
        gc.gridx = 1; gc.gridy = 0;
        r.questionText = new JTextField(preset != null ? nz(preset.getQuestionText()) : "");
        r.panel.add(r.questionText, gc);

        // Answers and radios
        r.answers = new JTextField[4];
        r.radios = new JRadioButton[4];
        r.group = new ButtonGroup();

        for (int i = 0; i < 4; i++) {
            gc.gridx = 0; gc.gridy = i + 1;
            r.radios[i] = new JRadioButton("Correct");
            r.panel.add(r.radios[i], gc);
            r.group.add(r.radios[i]);

            gc.gridx = 1; gc.gridy = i + 1;
            String presetAns = "";
            if (preset != null && preset.getAnswers() != null && i < preset.getAnswers().length) {
                presetAns = nz(preset.getAnswers()[i]);
            }
            r.answers[i] = new JTextField(presetAns);
            r.panel.add(r.answers[i], gc);
        }

        // Default to first as selected so there is always a choice
        r.radios[0].setSelected(true);

        rows.add(r);
        listPanel.add(r.panel);
        listPanel.revalidate();
        listPanel.repaint();
    }

    private String validateAll() {
        if (rows.isEmpty()) {
            return "Add at least one question.";
        }
        for (int qi = 0; qi < rows.size(); qi++) {
            Row r = rows.get(qi);

            String qText = trim(r.questionText.getText());
            if (qText.isEmpty()) {
                return "Question " + (qi + 1) + " text is required.";
            }

            boolean anySelected = false;
            String selectedAnswerText = null;

            for (int ai = 0; ai < r.answers.length; ai++) {
                String aText = trim(r.answers[ai].getText());
                if (aText.isEmpty()) {
                    return "Question " + (qi + 1) + ": answer " + (ai + 1) + " is required.";
                }
                if (r.radios[ai].isSelected()) {
                    anySelected = true;
                    selectedAnswerText = aText;
                }
            }

            if (!anySelected) {
                return "Question " + (qi + 1) + ": select which answer is correct.";
            }
            if (selectedAnswerText == null || selectedAnswerText.isEmpty()) {
                return "Question " + (qi + 1) + ": the selected correct answer must have text.";
            }
        }
        return null;
    }

    private static String nz(String s) { return s == null ? "" : s; }
    private static String trim(String s) { return s == null ? "" : s.trim(); }

    public boolean isConfirmed() {
        return confirmed;
    }

    // Replace the existing getQuestions() with this version
    public List<Question> getQuestions() {
        List<Question> result = new ArrayList<>();
        for (Row r : rows) {
            String qText = r.questionText.getText();

            String[] answers = new String[r.answers.length];
            String correct = null;
            for (int i = 0; i < r.answers.length; i++) {
                String val = r.answers[i].getText();
                answers[i] = val;
                if (r.radios[i].isSelected()) {
                    correct = val;
                }
            }

            // Use the existing constructor: Question(String questionText, String[] answers, String correctAnswer)
            Question q = new Question(qText, answers, correct);
            result.add(q);
        }
        return result;
    }
}