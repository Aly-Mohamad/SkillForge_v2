package model;

public class LessonProgress {
    private boolean completed;
    private boolean passed;
    private int score;
    private int tries;

    public boolean isCompleted() { return completed; }
    public boolean isPassed() { return passed; }
    public int getScore() { return score; }
    public int getTries() { return tries; }

    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setPassed(boolean passed) { this.passed = passed; }
    public void setScore(int score) { this.score = score; }
    public void incrementTries() { this.tries++; }
}