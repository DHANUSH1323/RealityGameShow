package com.realityGameShow.game.engine.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class GameState {

    private Game game;
    private Map<String, Team> teams = new LinkedHashMap<>();

    private String activeTeamId;
    private Question currentQuestion;

    private boolean buzzerOpen;
    private long questionStartTime;

    public GameState(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public Map<String, Team> getTeams() {
        return teams;
    }

    public void addTeam(Team team) {
        teams.put(team.getTeamId(), team);
    }

    public String getActiveTeamId() {
        return activeTeamId;
    }

    public void setActiveTeamId(String activeTeamId) {
        this.activeTeamId = activeTeamId;
    }

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(Question currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public boolean isBuzzerOpen() {
        return buzzerOpen;
    }

    public void setBuzzerOpen(boolean buzzerOpen) {
        this.buzzerOpen = buzzerOpen;
    }

    public long getQuestionStartTime() {
        return questionStartTime;
    }

    public void setQuestionStartTime(long questionStartTime) {
        this.questionStartTime = questionStartTime;
    }
}