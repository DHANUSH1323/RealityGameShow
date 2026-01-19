package com.realityGameShow.game.engine.core;

import com.realityGameShow.game.engine.ai.AIHost;
import com.realityGameShow.game.engine.model.*;

import java.util.HashSet;
import java.util.Set;

public class GameEngine {

    private final GameState gameState;
    private final AIHost aiHost;

    // Round 1 tracking
    private final Set<String> attemptedTeams = new HashSet<>();

    public GameEngine(GameState gameState, AIHost aiHost) {
        this.gameState = gameState;
        this.aiHost = aiHost;
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

    //---------------------------------
    //Round 2 Methods
    //---------------------------------


    public void startRound2(){
        if(gameState.getGame().getPhase() != GamePhase.ROUND1){
            throw new IllegalStateException("Cannot start Round 2 now" );
        }

        gameState.getGame().setPhase(GamePhase.ROUND2);
        gameState.getGame().setCurrentRound(2);

        gameState.setRound2TeamIndex(0);
        startNextRound2Team();
    }


    public void startNextRound2Team(){
        if(gameState.getRound2TeamIndex() >= gameState.getTeams().size()){
            endRound2();
            return;
        }

        String teamId = gameState.getTeams().keySet().stream().toList().get(gameState.getRound2TeamIndex());
        
        gameState.setActiveTeamId(teamId);
        gameState.setRound2TurnStartTime(System.currentTimeMillis());
        
        // Generate a new question for this team's turn
        Question question = aiHost.generateQuestion(2);
        gameState.setCurrentQuestion(question);
    }

    public synchronized void submitRound2Answer(String teamId, boolean isCorrect, int points) {
        if (gameState.getGame().getPhase() != GamePhase.ROUND2) {
            return;
        }
    
        if (!teamId.equals(gameState.getActiveTeamId())) {
            return;
        }
    
        long elapsed =
                System.currentTimeMillis() - gameState.getRound2TurnStartTime();
    
        //Time Over
        if (elapsed > 60_000) {
            endCurrentRound2Turn();
            return;
        }
    
        if (isCorrect) {
            Team team = gameState.getTeams().get(teamId);
            team.setScore(team.getScore() + points);
            
            // Generate a new question for the next answer (teams can answer multiple questions)
            Question nextQuestion = aiHost.generateQuestion(2);
            gameState.setCurrentQuestion(nextQuestion);
        }
        // If wrong answer, keep the same question so they can try again (if time permits)
    }

    public void endCurrentRound2Turn() {
        gameState.setActiveTeamId(null);
        gameState.setCurrentQuestion(null);
        gameState.setRound2TeamIndex(gameState.getRound2TeamIndex() + 1);
        startNextRound2Team();
    }

    private void endRound2() {
        gameState.setActiveTeamId(null);
    }

    
    //---------------------------------
    //Round 3 Methods
    //---------------------------------


    public void startRound3(){
        if(gameState.getGame().getPhase() != GamePhase.ROUND2){
            throw new IllegalStateException("Cannot start round 3 now" );
        }

        gameState.getGame().setPhase(GamePhase.ROUND3);
        gameState.getGame().setCurrentRound(3);

        gameState.setRound2TeamIndex(0);
        startNextRound3Team();
    }

    private void startNextRound3Team() {
        if (gameState.getRound2TeamIndex() >= gameState.getTeams().size()) {
            endGame();
            return;
        }
    
        String teamId = gameState.getTeams().keySet().stream().toList().get(gameState.getRound2TeamIndex());
    
        gameState.setActiveTeamId(teamId);
        gameState.setRound3QuestionIndex(0);
        gameState.setRound3QuestionStartTime(System.currentTimeMillis());
        
        // Generate the first question for this team using selected category
        String category = gameState.getSelectedCategory();
        Question question = aiHost.generateQuestion(3, category);
        gameState.setCurrentQuestion(question);
    }

    public synchronized void submitRound3Answer(String teamId, boolean isCorrect) {
        if (gameState.getGame().getPhase() != GamePhase.ROUND3) {
            return;
        }
    
        if (!teamId.equals(gameState.getActiveTeamId())) {
            return;
        }
    
        long elapsed =
                System.currentTimeMillis() - gameState.getRound3QuestionStartTime();
    
        // Timeout current team loses chance
        if (elapsed > 30_000) {
            endCurrentRound3Team();
            return;
        }
    
        int questionIndex = gameState.getRound3QuestionIndex();
        int points = (questionIndex + 1) * 10;
    
        Team team = gameState.getTeams().get(teamId);
    
        if (!isCorrect) {
            endCurrentRound3Team();
            return;
        }
    
        // Correct answer
        team.setScore(team.getScore() + points);
    
        // Move to next question
        gameState.setRound3QuestionIndex(questionIndex + 1);
    
        // All 5 answered correctly
        if (gameState.getRound3QuestionIndex() == 5) {
            team.setScore(team.getScore() + 50); // bonus
            endCurrentRound3Team();
            return;
        }
    
        // Generate the next question for this team using selected category
        String category = gameState.getSelectedCategory();
        Question nextQuestion = aiHost.generateQuestion(3, category);
        gameState.setCurrentQuestion(nextQuestion);
        gameState.setRound3QuestionStartTime(System.currentTimeMillis());
    }

    private void endCurrentRound3Team() {
        gameState.setActiveTeamId(null);
        gameState.setCurrentQuestion(null);
        gameState.setRound2TeamIndex(
                gameState.getRound2TeamIndex() + 1
        );
        startNextRound3Team();
    }

    private void endGame() {
        gameState.setActiveTeamId(null);
        gameState.getGame().setPhase(GamePhase.GAME_OVER);
    }
}