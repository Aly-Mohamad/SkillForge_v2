package model;

import java.util.ArrayList;

public class Quiz {
    private String quizId;
    private boolean isPassed;
    private int tries;
    private int score;
    private String lessonId;
    private ArrayList<Question> questions;

    public Quiz(String quizId, boolean isPassed, int tries, int score, String lessonId) {
        this.quizId = quizId;
        this.isPassed = isPassed;
        this.tries = tries;
        this.score = score;
        this.lessonId = lessonId;
        this.questions = new ArrayList<>();
    }
}
