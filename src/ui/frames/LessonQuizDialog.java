package ui.frames;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

// Change constructor to accept Course so we can update course progress
public class LessonQuizDialog extends JDialog {
    public LessonQuizDialog(JFrame owner, JsonDatabaseManager db, Student student, Course course, Lesson lesson) {
        super(owner, "Quiz - " + lesson.getTitle(), true);
        setSize(600, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        List<ButtonGroup> answerGroups = new ArrayList<>();

        Quiz quiz = lesson.getQuiz();
        if (quiz == null || quiz.getQuestions() == null || quiz.getQuestions().isEmpty()) {
            JOptionPane.showMessageDialog(this, "This lesson has no quiz questions.");
            dispose();
            return;
        }

        List<Question> questions = quiz.getQuestions();

        for (Question q : questions) {
            JPanel qBox = new JPanel(new GridLayout(q.getAnswers().length, 1));
            qBox.setBorder(BorderFactory.createTitledBorder(q.getQuestionText()));
            ButtonGroup group = new ButtonGroup();
            answerGroups.add(group);

            for (String ans : q.getAnswers()) {
                JRadioButton option = new JRadioButton(ans);
                group.add(option);
                qBox.add(option);
            }
            questionPanel.add(qBox);
        }

        JScrollPane scrollPane = new JScrollPane(questionPanel);
        JButton submitBtn = new JButton("Submit");

        submitBtn.addActionListener(e -> {
            int correct = 0;
            int total = questions.size();

            for (int i = 0; i < total; i++) {
                Question q = questions.get(i);
                ButtonGroup group = answerGroups.get(i);

                boolean answered = false;
                for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements(); ) {
                    AbstractButton btn = buttons.nextElement();
                    if (btn.isSelected()) {
                        answered = true;
                        // Compare safely
                        String sel = btn.getText() == null ? "" : btn.getText().trim();
                        String corr = q.getCorrectAnswer() == null ? "" : q.getCorrectAnswer().trim();
                        if (sel.equalsIgnoreCase(corr)) {
                            correct++;
                        }
                        //Added to check student answers
                        q.setStudentAnswer(sel);
                        break;
                    }
                }

                if (!answered) {
                    JOptionPane.showMessageDialog(this, "Please answer all questions before submitting.");
                    return;
                }
            }

            int score = (int) ((correct * 100.0) / total);
            boolean passed = score >= 50;

            // Update per-student progress
            student.markLessonCompleted(lesson.getLessonId(), score, passed);

            course.recordLessonProgress(student.getUserId(), lesson.getLessonId(), score, passed);

            // Update lesson state inside the course so it persists
            lesson.incrementTries();
            lesson.setScore(score);
            lesson.setPassed(passed);
            if (passed) {
                lesson.setCompleted(true);
            }

            // Optional: keep quiz stats in sync, if you display them
            quiz.setTries(quiz.getTries() + 1);
            quiz.setScore(score);
            quiz.setPassed(passed);

            // If your course tracks per-student completion, mark it as completed when passed
            if (passed) {
                course.markLessonCompleted(student, lesson.getLessonId());
            }

            // Persist to disk (update the course that contains this lesson)
            db.updateCourse(course);
            db.save();

            JOptionPane.showMessageDialog(this,
                    "You scored: " + score + "%\n" +
                            (passed ? "✅ You passed the quiz!" : "❌ You did not pass. Try again later."));

            if (course.getCompletionPercentage(student) == 100){
                student.generateCertificate(course);
                db.save();
                JOptionPane.showMessageDialog(this,
                        "🎉 Congratulations! You completed the entire course!\n" +
                                "Your certificate has been generated.");
            }



            // Add this helper inside LessonQuizDialog and call it after grading:
            showReviewPanel(quiz.getQuestions(), correct, total);

        });

        add(scrollPane, BorderLayout.CENTER);
        add(submitBtn, BorderLayout.SOUTH);
    }

    private void showReviewPanel(java.util.List<model.Question> questions, int score, int total) {
        JPanel review = new JPanel();
        review.setLayout(new BoxLayout(review, BoxLayout.Y_AXIS));
        review.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Score header
        JLabel scoreLabel = new JLabel("Your score: " + score + " / " + total);
        scoreLabel.setFont(scoreLabel.getFont().deriveFont(Font.BOLD, 14f));
        scoreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        review.add(scoreLabel);
        review.add(Box.createVerticalStrut(8));

        for (int qi = 0; qi < questions.size(); qi++) {
            model.Question q = questions.get(qi);

            JPanel qPanel = new JPanel();
            qPanel.setLayout(new BoxLayout(qPanel, BoxLayout.Y_AXIS));
            qPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220)),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));
            qPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel qTitle = new JLabel((qi + 1) + ". " + q.getQuestionText());
            qTitle.setFont(qTitle.getFont().deriveFont(Font.BOLD));
            qPanel.add(qTitle);
            qPanel.add(Box.createVerticalStrut(4));

            String correct = q.getCorrectAnswer() == null ? "" : q.getCorrectAnswer().trim();
            String student = q.getStudentAnswer() == null ? "" : q.getStudentAnswer().trim();

            String[] answers = q.getAnswers();
            if (answers != null) {
                for (String ans : answers) {
                    String a = ans == null ? "" : ans.trim();
                    boolean isCorrect = a.equalsIgnoreCase(correct);
                    boolean isStudent = !student.isEmpty() && a.equalsIgnoreCase(student);

                    JLabel line = new JLabel(a.isEmpty() ? "(empty)" : a);

                    if (isCorrect) {
                        line.setForeground(new Color(0, 128, 0)); // green
                        line.setText("✔ Correct: " + line.getText());
                    } else if (isStudent) {
                        line.setForeground(new Color(180, 0, 0)); // red
                        line.setText("✖ Your answer: " + line.getText());
                    } else {
                        line.setForeground(new Color(80, 80, 80));
                    }

                    qPanel.add(line);
                }
            }

            review.add(qPanel);
            review.add(Box.createVerticalStrut(8));
        }

        JScrollPane scroll = new JScrollPane(review);
        scroll.setBorder(null);

        // Replace the main content area of the dialog with the review panel.
        // Adjust to match your layout: remove old center panel and add this one.
        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        bottom.add(closeBtn);
        getContentPane().add(bottom, BorderLayout.SOUTH);

        // Prevent editing after submission
        getRootPane().setDefaultButton(closeBtn);

        revalidate();
        repaint();
    }

    // java
    // Call this after grading and persisting results:
    // showSubmissionSummary(quiz.getQuestions());

    private void showSubmissionSummary(java.util.List<model.Question> questions) {
        int correct = 0;
        int total = questions != null ? questions.size() : 0;

        JPanel review = new JPanel();
        review.setLayout(new BoxLayout(review, BoxLayout.Y_AXIS));
        review.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel header = new JLabel();
        header.setFont(header.getFont().deriveFont(Font.BOLD, 14f));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        review.add(header);
        review.add(Box.createVerticalStrut(8));

        if (questions != null) {
            for (int i = 0; i < questions.size(); i++) {
                model.Question q = questions.get(i);

                // Ensure correctness is computed
                q.checkStudentAnswer();
                if (q.isCorrect()) correct++;

                JPanel qPanel = new JPanel();
                qPanel.setLayout(new BoxLayout(qPanel, BoxLayout.Y_AXIS));
                qPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220)),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)
                ));
                qPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

                JLabel qTitle = new JLabel((i + 1) + ". " + safe(q.getQuestionText()));
                qTitle.setFont(qTitle.getFont().deriveFont(Font.BOLD));
                qPanel.add(qTitle);
                qPanel.add(Box.createVerticalStrut(4));

                String studentAns = safe(q.getStudentAnswer());
                boolean hasAnswer = !studentAns.isBlank();

                // Only show the student's answer (no exposure of the correct answer)
                JLabel yourAns = new JLabel(hasAnswer ? ("Your answer: " + studentAns) : "No answer");
                if (q.isCorrect() && hasAnswer) {
                    yourAns.setForeground(new Color(0, 128, 0)); // green
                    yourAns.setText("✔ " + yourAns.getText());
                } else if (hasAnswer) {
                    yourAns.setForeground(new Color(180, 0, 0)); // red
                    yourAns.setText("✖ " + yourAns.getText());
                } else {
                    yourAns.setForeground(new Color(120, 120, 120));
                }
                qPanel.add(yourAns);

                review.add(qPanel);
                review.add(Box.createVerticalStrut(8));
            }
        }

        header.setText("You answered " + correct + " out of " + total + " correctly.");

        JScrollPane scroll = new JScrollPane(review);
        scroll.setBorder(null);

        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        bottom.add(closeBtn);
        getContentPane().add(bottom, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(closeBtn);
        revalidate();
        repaint();
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}