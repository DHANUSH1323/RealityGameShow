package com.realityGameShow.game.engine.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class GameState {

    private Game game;
    private Map<String, Team> teams = new LinkedHashMap<>();

    private String activeTeamId; // which team is currently playing

    // Round 1 fields (Fastest Finger First)
    private Question currentQuestion;
    private boolean buzzerOpen;
    private long questionStartTime;
    // private Question round1CurrentQuestion;
    // private boolean round1BuzzerOpen;
    // private long round1QuestionStartTime;

    // Round 2 fields (Lightning Round)
    private long round2TurnStartTime;
    private int round2TeamIndex; // index of the team whose next turn in round 2

    // Round 3 fields (Risk & Reward)
    private int round3QuestionIndex;
    private long round3QuestionStartTime;

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

    public long getRound2TurnStartTime(){
        return round2TurnStartTime;
    }

    public void setRound2TurnStartTime(long round2TurnStartTime){
        this.round2TurnStartTime = round2TurnStartTime;
    }

    public int getRound2TeamIndex() {
        return round2TeamIndex;
    }
    
    public void setRound2TeamIndex(int round2TeamIndex) {
        this.round2TeamIndex = round2TeamIndex;
    }

    public int getRound3QuestionIndex() {
        return round3QuestionIndex;
    }

    public void setRound3QuestionIndex(int round3QuestionIndex) {
        this.round3QuestionIndex = round3QuestionIndex;
    }

    public long getRound3QuestionStartTime() {
        return round3QuestionStartTime;
    }

    public void setRound3QuestionStartTime(long round3QuestionStartTime) {
        this.round3QuestionStartTime = round3QuestionStartTime;
    }
}