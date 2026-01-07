package com.realityGameShow.game.engine.model;

public class Game {

    private String gameId;
    private GamePhase phase;
    private int currentRound;

    public Game(String gameId) {
        this.gameId = gameId;
        this.phase = GamePhase.TEAM_FORMATION;
        this.currentRound = 0;
    }

    public String getGameId() {
        return gameId;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }
}