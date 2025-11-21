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

    //Will be removed
    public Lesson(String title, String content) {
        this.title = title;
        this.content = content;
        this.lessonId = generateLessonId();
    }

    public String getLessonId() { return lessonId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public List<String> getResources() { return resources; }

    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setResources(List<String> resources) { this.resources = new ArrayList<>(resources); }
    public void addResource(String r) { if (r != null && !r.isEmpty()) resources.add(r); }

    // Added getters/setters to persist completion state
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public boolean isPassed() { return isPassed; }
    public void setPassed(boolean passed) { isPassed = passed; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTries() { return tries; }
    public void incrementTries() { this.tries++; }

    private String generateLessonId() {
        int number = (int)(Math.random() * 10000);
        return String.format("L%04d", number);
    }

    public void setQuiz(Quiz quiz) { this.quiz = quiz; }
    public Quiz getQuiz(){ return this.quiz;}
}