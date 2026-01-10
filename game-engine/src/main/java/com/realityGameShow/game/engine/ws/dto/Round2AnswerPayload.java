package com.realityGameShow.game.engine.ws.dto;

public class Round2AnswerPayload {

    private boolean correct;
    private int points;

    public Round2AnswerPayload() {}

    public boolean isCorrect() {
        return correct;
    }

    public int getPoints() {
        return points;
    }
}