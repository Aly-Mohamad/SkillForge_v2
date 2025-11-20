package model;

import java.util.ArrayList;

public class Quiz {
    private String quizId;
    private boolean isPassed;
    private int tries;
    private int score;
    private String lessonId;
    private ArrayList<Question> questions = new ArrayList<>();

    public Quiz(String lessonId) {
        this.lessonId = lessonId;
        this.isPassed = false;
        this.tries = 0;
    }

    public boolean isPassed() { return isPassed; }
    public void setPassed(boolean passed) { isPassed = passed; }

    public int getTries() { return tries; }
    public void setTries(int tries) { this.tries = tries; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getLessonId() { return lessonId; }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = (questions != null) ? questions : new ArrayList<>();
    }

    public ArrayList<Question> getQuestions() {
        return this.questions;
    }

    public void addQuestion(Question question){
        if(questions == null) questions = new ArrayList<>();
        questions.add(question);
    }
}