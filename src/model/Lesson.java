package model;

import java.util.ArrayList;
import java.util.List;

public class Lesson {

    private String lessonId;
    private String title;
    private String content;
    private List<String> resources = new ArrayList<>();
    private boolean isCompleted = false;
    private boolean isPassed = false;
    private int score = 0;
    private int tries = 0;
    private Quiz quiz;

    public Lesson(String title, String content) {
        this.title = title;
        this.content = content;
        this.lessonId = generateLessonId();
    }

    public String getLessonId() { return lessonId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }

    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }

    public void setCompleted(boolean completed) { isCompleted = completed; }
    public void setPassed(boolean passed) { isPassed = passed; }
    public void setScore(int score) { this.score = score; }
    public void incrementTries() { this.tries++; }

    private String generateLessonId() {
        int number = (int)(Math.random() * 10000);
        return String.format("L%04d", number);
    }

    public void setQuiz(Quiz quiz) { this.quiz = quiz; }
    public Quiz getQuiz(){ return this.quiz;}
}