package com.realityGameShow.game.engine.core;

import com.realityGameShow.game.engine.model.*;

import java.util.HashSet;
import java.util.Set;

public class GameEngine {

    private final GameState gameState;

    // Round 1 tracking
    private final Set<String> attemptedTeams = new HashSet<>();

    public GameEngine(GameState gameState) {
        this.gameState = gameState;
    }

    //---------------------------------
    //Round 1 Methods
    //---------------------------------

    public void startRound1(Question question) {
        if(gameState.getGame().getPhase() != GamePhase.TEAM_FORMATION){
            throw new IllegalStateException("Cannot Start round 1 now", null);
        }
        gameState.getGame().setPhase(GamePhase.ROUND1);
        gameState.getGame().setCurrentRound(1);

        gameState.setCurrentQuestion(question);
        gameState.setBuzzerOpen(true);
        gameState.setActiveTeamId(null);
        gameState.setQuestionStartTime(System.currentTimeMillis());

        attemptedTeams.clear();
    }

    public synchronized void buzzerPress(String teamId) {
        if(gameState.getGame().getPhase() != GamePhase.ROUND1){
            return;
        }

        if (!gameState.isBuzzerOpen()) {
            return;
        }

        if (attemptedTeams.contains(teamId)) {
            return;
        }

        gameState.setBuzzerOpen(false);;
        gameState.setActiveTeamId(teamId);
        gameState.setQuestionStartTime(System.currentTimeMillis());
    }

    public synchronized void submitAnswer(String teamId, boolean isCorrect) {
        if(gameState.getGame().getPhase() != GamePhase.ROUND1){
            return;
        }

        if (!teamId.equals(gameState.getActiveTeamId())) {
            return;
        }

        long elapsed = System.currentTimeMillis() - gameState.getQuestionStartTime();
        boolean timeOut = elapsed > 10000; // 10 seconds timeout
        
        Team team = gameState.getTeams().get(teamId);
        int points = gameState.getCurrentQuestion().getPoints();

        attemptedTeams.add(teamId);

        if(isCorrect && !timeOut){
            team.setScore(team.getScore() + points);
            endRound1Question();
            return;
        }
        //wrong answer or timeout
        team.setScore(team.getScore() - points);

        // Reset for next team
        gameState.setActiveTeamId(null);

        if (attemptedTeams.size() == gameState.getTeams().size()) {
            endRound1Question();
        } else {
            gameState.setBuzzerOpen(true);
        }
        
    }

    private void endRound1Question() {
        gameState.setBuzzerOpen(false);
        gameState.setActiveTeamId(null);
        gameState.setCurrentQuestion(null);
        attemptedTeams.clear();
    }
}