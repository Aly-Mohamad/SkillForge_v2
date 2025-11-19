package model;

import java.util.ArrayList;

public class Quiz {
    private String quizId;
    private boolean isPassed;
    private int tries;
    private int score;
    private String lessonId;
    private ArrayList<Question> questions;

    public Quiz(String lessonId) {
        this.lessonId = lessonId;
        this.isPassed = false;
        this.tries = 0;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public ArrayList<Question> getQuestions() {
        return this.questions;
    }

    public void addQuestion(Question question){
        if(questions == null) questions = new ArrayList<>();
        questions.add(question);
    }
}
