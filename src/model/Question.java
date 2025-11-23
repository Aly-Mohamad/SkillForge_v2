package model;

public class Question {
    private String questionId;
    private String []answers = new String[4];
    private String correctAnswer;
    private String questionText;
    private String courseId;
    private String studentAnswer;
    private boolean isCorrect;

    public Question(String questionText, String []answers, String correctAnswer) {
        this.questionId = genereateQuestionId();
        this.answers = answers;
        this.correctAnswer = correctAnswer;
        this.questionText = questionText;
        this.studentAnswer = null;
        this.isCorrect = false;
    }

    public String []getAnswers() { return answers; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getQuestionText() { return questionText; }
    public String getStudentAnswer() { return studentAnswer; }
    public boolean isCorrect() { return isCorrect; }

    public void setQuestionId(String questionId) { this.questionId = questionId; }
    public void setAnswers(String []answers) { this.answers = answers; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public void setStudentAnswer(String studentAnswer) { this.studentAnswer = studentAnswer; }
    public void setCorrect(boolean isCorrect) { this.isCorrect = isCorrect; }

    public void checkStudentAnswer(){
        if(studentAnswer == null) return;
        isCorrect = studentAnswer.equalsIgnoreCase(correctAnswer);
    }

    private String genereateQuestionId(){
        int number = (int)(Math.random() * 10000);
        return String.format("Q%04d", number);
    }
}
