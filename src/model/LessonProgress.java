package model;

public class LessonProgress {
    private String studentId;
    private int score;
    private boolean passed;
    private boolean completed;
    private int tries;

    public LessonProgress() {}

    public LessonProgress(String studentId, int score, boolean passed, boolean completed) {
        this.studentId = studentId;
        this.score = score;
        this.passed = passed;
        this.completed = completed;
    }
    public String getStudentId() { return studentId; }
    public boolean isCompleted() { return completed; }
    public boolean isPassed() { return passed; }
    public int getScore() { return score; }
    public int getTries() { return tries; }

    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setPassed(boolean passed) { this.passed = passed; }
    public void setScore(int score) { this.score = score; }
    public void incrementTries() { this.tries++; }
}