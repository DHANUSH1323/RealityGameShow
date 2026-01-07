package com.realityGameShow.game.engine.model;

public class Question {

    private String questionId;
    private String text;
    private String correctAnswer;
    private int points;

    public Question(String questionId, String text, String correctAnswer, int points) {
        this.questionId = questionId;
        this.text = text;
        this.correctAnswer = correctAnswer;
        this.points = points;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getText() {
        return text;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public int getPoints() {
        return points;
    }
}