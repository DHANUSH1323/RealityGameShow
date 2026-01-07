package com.realityGameShow.game.engine.core;

import com.realityGameShow.game.engine.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameEngineRound1Test {

    private GameEngine gameEngine;
    private GameState gameState;

    @BeforeEach
    void setup() {
        Game game = new Game("game-1");
        gameState = new GameState(game);

        // Add 3 teams
        gameState.addTeam(new Team("T1", "Alpha", 3));
        gameState.addTeam(new Team("T2", "Beta", 2));
        gameState.addTeam(new Team("T3", "Gamma", 4));

        gameEngine = new GameEngine(gameState);
    }

    @Test
    void firstBuzzerWins() {
        Question q = new Question("Q1", "2+2?", "4", 10);
        gameEngine.startRound1(q);

        gameEngine.buzzerPress("T1");
        gameEngine.buzzerPress("T2");

        assertEquals("T1", gameState.getActiveTeamId());
        assertFalse(gameState.isBuzzerOpen());
    }

    @Test
    void wrongAnswerAllowsNextTeam() {
        Question q = new Question("Q1", "2+2?", "4", 10);
        gameEngine.startRound1(q);

        gameEngine.buzzerPress("T1");
        gameEngine.submitAnswer("T1", false);

        assertEquals(-10, gameState.getTeams().get("T1").getScore());
        assertTrue(gameState.isBuzzerOpen());
        assertNull(gameState.getActiveTeamId());
    }

    @Test
    void correctAnswerEndsQuestion() {
        Question q = new Question("Q1", "2+2?", "4", 10);
        gameEngine.startRound1(q);

        gameEngine.buzzerPress("T2");
        gameEngine.submitAnswer("T2", true);

        assertEquals(10, gameState.getTeams().get("T2").getScore());
        assertNull(gameState.getCurrentQuestion());
        assertFalse(gameState.isBuzzerOpen());
    }

    @Test
    void teamCannotBuzzTwiceForSameQuestion() {
        Question q = new Question("Q1", "2+2?", "4", 10);
        gameEngine.startRound1(q);

        gameEngine.buzzerPress("T1");
        gameEngine.submitAnswer("T1", false);

        gameEngine.buzzerPress("T1");
        gameEngine.buzzerPress("T2");

        assertEquals("T2", gameState.getActiveTeamId());
    }

    @Test
    void allTeamsFailEndsQuestion() {
        Question q = new Question("Q1", "2+2?", "4", 10);
        gameEngine.startRound1(q);

        gameEngine.buzzerPress("T1");
        gameEngine.submitAnswer("T1", false);

        gameEngine.buzzerPress("T2");
        gameEngine.submitAnswer("T2", false);

        gameEngine.buzzerPress("T3");
        gameEngine.submitAnswer("T3", false);

        assertNull(gameState.getCurrentQuestion());
        assertFalse(gameState.isBuzzerOpen());
    }
}