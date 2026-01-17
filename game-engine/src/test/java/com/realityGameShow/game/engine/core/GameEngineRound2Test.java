package com.realityGameShow.game.engine.core;

import com.realityGameShow.game.engine.ai.AIHost;
import com.realityGameShow.game.engine.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameEngineRound2Test {

    private GameEngine gameEngine;
    private GameState gameState;
    private AIHost aiHost;

    @BeforeEach
    void setup(){
        Game game = new Game("game-1");
        gameState = new GameState(game);

        //Adding 3 teams
        gameState.addTeam(new Team("T1", "Alpha", 3));
        gameState.addTeam(new Team("T2", "Beta", 2));
        gameState.addTeam(new Team("T3", "Gamma", 4));

        // Create a simple test AIHost
        aiHost = new AIHost() {
            @Override
            public Question generateQuestion(int round) {
                return new Question("Round 2 Test Question", "Test Answer", 5);
            }

            @Override
            public boolean validateAnswer(Question question, String answer) {
                return question.getCorrectAnswer().equalsIgnoreCase(answer.trim());
            }
        };

        gameEngine = new GameEngine(gameState, aiHost);

        game.setPhase(GamePhase.ROUND1);
    }

    @Test
    void startRound2_setFirstTeamActive(){
        gameEngine.startRound2();

        assertEquals(GamePhase.ROUND2, gameState.getGame().getPhase());
        assertEquals(2, gameState.getGame().getCurrentRound());
        assertEquals("T1", gameState.getActiveTeamId());
        assertNotNull(gameState.getCurrentQuestion(), "Question should be generated when Round 2 starts");
    }

    @Test
    void onlyActiveTeamCanScore(){
        gameEngine.startRound2();

        //Wrong team tries to score
        gameEngine.submitRound2Answer("T2", true, 10);
        assertEquals(0, gameState.getTeams().get("T2").getScore());
        
        //Active team scores
        Question firstQuestion = gameState.getCurrentQuestion();
        gameEngine.submitRound2Answer("T1", true, 10);
        assertEquals(10, gameState.getTeams().get("T1").getScore());
        // After correct answer, a new question should be generated
        assertNotNull(gameState.getCurrentQuestion(), "New question should be generated after correct answer");
        assertNotSame(firstQuestion, gameState.getCurrentQuestion(), "Question should be different after correct answer");
    }

    @Test
    void wrongAnswerHasNoPenalty(){
        gameEngine.startRound2();
        
        //Active team answers wrong
        gameEngine.submitRound2Answer("T1", false, 10);
        assertEquals(0, gameState.getTeams().get("T1").getScore());
    }

    @Test
    void endCurrentTurn_movesToNextTeam() {
        gameEngine.startRound2();

        // T1's turn ends
        gameEngine.endCurrentRound2Turn();
        assertEquals("T2", gameState.getActiveTeamId());
    }

    @Test
    void timeoutEndsTurnAutomatically() throws InterruptedException {
        gameEngine.startRound2();

        // Force timeout
        gameState.setRound2TurnStartTime(System.currentTimeMillis() - 61_000);

        gameEngine.submitRound2Answer("T1", true, 10);

        assertEquals("T2", gameState.getActiveTeamId());
        assertEquals(0, gameState.getTeams().get("T1").getScore());
    }

    @Test
    void round2EndsAfterAllTeamsPlay() {
        gameEngine.startRound2();

        gameEngine.endCurrentRound2Turn();
        gameEngine.endCurrentRound2Turn();
        gameEngine.endCurrentRound2Turn();

        assertEquals(GamePhase.ROUND2, gameState.getGame().getPhase());
        assertNull(gameState.getActiveTeamId());
    }
    
}
