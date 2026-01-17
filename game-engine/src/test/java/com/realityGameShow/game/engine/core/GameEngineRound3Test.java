package com.realityGameShow.game.engine.core;

import com.realityGameShow.game.engine.ai.AIHost;
import com.realityGameShow.game.engine.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameEngineRound3Test {

    private GameEngine gameEngine;
    private GameState gameState;
    private AIHost aiHost;

    @BeforeEach
    void setup() {
        Game game = new Game("game-1");
        gameState = new GameState(game);

        gameState.addTeam(new Team("T1", "Alpha", 3));
        gameState.addTeam(new Team("T2", "Beta", 2));

        // Create a simple test AIHost
        aiHost = new AIHost() {
            @Override
            public Question generateQuestion(int round) {
                return new Question("Round 3 Test Question", "Test Answer", 10);
            }

            @Override
            public boolean validateAnswer(Question question, String answer) {
                return question.getCorrectAnswer().equalsIgnoreCase(answer.trim());
            }
        };

        gameEngine = new GameEngine(gameState, aiHost);

        // Simulate Round 2 completion
        game.setPhase(GamePhase.ROUND2);
    }

    @Test
    void startRound3_setsFirstTeamActive() {
        gameEngine.startRound3();

        assertEquals(GamePhase.ROUND3, gameState.getGame().getPhase());
        assertEquals(3, gameState.getGame().getCurrentRound());
        assertEquals("T1", gameState.getActiveTeamId());
        assertEquals(0, gameState.getRound3QuestionIndex());
        assertNotNull(gameState.getCurrentQuestion(), "Question should be generated when Round 3 starts");
    }

    @Test
    void correctAnswerIncreasesScoreAndMovesToNextQuestion() {
        gameEngine.startRound3();

        Question firstQuestion = gameState.getCurrentQuestion();
        gameEngine.submitRound3Answer("T1", true);

        assertEquals(10, gameState.getTeams().get("T1").getScore());
        assertEquals(1, gameState.getRound3QuestionIndex());
        // After correct answer, a new question should be generated
        assertNotNull(gameState.getCurrentQuestion(), "New question should be generated after correct answer");
        assertNotSame(firstQuestion, gameState.getCurrentQuestion(), "Question should be different after correct answer");
    }

    @Test
    void wrongAnswerEndsTeamRoundImmediately() {
        gameEngine.startRound3();

        gameEngine.submitRound3Answer("T1", false);

        assertEquals("T2", gameState.getActiveTeamId());
        assertEquals(0, gameState.getTeams().get("T1").getScore());
    }

    @Test
    void timeoutEndsTeamRound() {
        gameEngine.startRound3();

        // Force timeout
        gameState.setRound3QuestionStartTime(
                System.currentTimeMillis() - 31_000
        );

        gameEngine.submitRound3Answer("T1", true);

        assertEquals("T2", gameState.getActiveTeamId());
        assertEquals(0, gameState.getTeams().get("T1").getScore());
    }

    @Test
    void answeringAllFiveCorrectlyAwardsBonus() {
        gameEngine.startRound3();

        //Question 1 - 5 correct answers
        gameEngine.submitRound3Answer("T1", true);
        gameEngine.submitRound3Answer("T1", true);
        gameEngine.submitRound3Answer("T1", true);
        gameEngine.submitRound3Answer("T1", true);
        gameEngine.submitRound3Answer("T1", true);

        int expectedScore = 10 + 20 + 30 + 40 + 50 + 50;
        assertEquals(expectedScore, gameState.getTeams().get("T1").getScore());
        assertEquals("T2", gameState.getActiveTeamId());
    }

    @Test
    void gameEndsAfterAllTeamsFinishRound3() {
        gameEngine.startRound3();

        // Team 1 fails immediately
        gameEngine.submitRound3Answer("T1", false);

        // Team 2 fails immediately
        gameEngine.submitRound3Answer("T2", false);

        assertEquals(GamePhase.GAME_OVER, gameState.getGame().getPhase());
        assertNull(gameState.getActiveTeamId());
    }
}