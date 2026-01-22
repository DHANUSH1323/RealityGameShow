package com.realityGameShow.game.engine.persistence;

import com.realityGameShow.game.engine.core.GameRegistry;
import com.realityGameShow.game.engine.model.*;
import com.realityGameShow.game.engine.ws.dto.GameStateSnapshot;
import org.junit.jupiter.api.Test;

// import java.util.Optional;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class GameStatePersistenceTest {

    // --------------------------------------------------
    // TEST 1: save + load snapshot
    // --------------------------------------------------
    @Test
    void shouldSaveAndLoadGameStateSnapshot() {

        GameStatePersistenceService persistence =
                new InMemoryGameStatePersistenceService();

        GameStateSnapshot snapshot =
                new GameStateSnapshot(
                        "game-1",
                        GamePhase.ROUND1.name(),
                        1,
                        "team-1",
                        null,
                        "TECH",
                        Map.of("team-1", 10, "team-2", 0),
                        "What is JVM?",
                        "Java Virtual Machine",
                        10,
                        null,
                        null 
                );

        persistence.save(snapshot);

        GameStateSnapshot loaded =
                persistence.load("game-1");

        assertNotNull(loaded);
        assertEquals("game-1", loaded.getGameId());
        assertEquals("ROUND1", loaded.getPhase());
        assertEquals(10, loaded.getTeamScores().get("team-1"));
    }

    // --------------------------------------------------
    // TEST 2: GameState -> Snapshot -> GameState
    // --------------------------------------------------
    @Test
    void shouldRestoreGameStateFromSnapshot() {

        Game game = new Game("game-2");
        game.setPhase(GamePhase.ROUND2);
        game.setCurrentRound(2);

        GameState original = new GameState(game);

        Team team1 = new Team("team-1", "Alpha", 3);
        team1.setScore(20);

        original.addTeam(team1);
        original.setActiveTeamId("team-1");

        Question q =
                new Question("2 + 2?", "4", 5);

        original.setCurrentQuestion(q);

        GameStateSnapshot snapshot =
                GameStateMapper.toSnapshot(original);

        GameState restored =
                GameStateMapper.fromSnapshot(snapshot);

        assertEquals("game-2", restored.getGame().getGameId());
        assertEquals(GamePhase.ROUND2, restored.getGame().getPhase());
        assertEquals(20, restored.getTeams().get("team-1").getScore());
        assertEquals("2 + 2?", restored.getCurrentQuestion().getText());
    }

    // --------------------------------------------------
    // TEST 3: recovery service registers game
    // --------------------------------------------------
    @Test
    void shouldRecoverGameIntoRegistryOnStartup() {

        InMemoryGameStatePersistenceService persistence =
                new InMemoryGameStatePersistenceService();

        GameRegistry registry =
                new GameRegistry();

        GameStateSnapshot snapshot =
                new GameStateSnapshot(
                        "game-3",
                        GamePhase.ROUND1.name(),
                        1,
                        null,
                        null,
                        null,
                        Map.of("team-1", 0),
                        null,
                        null,
                        0,
                        null,
                        null
                );

        persistence.save(snapshot);

        GameStateRecoveryService recovery =
                new GameStateRecoveryService(
                        persistence,
                        registry
                );

        // simulate app startup
        recovery.recoverGame();

        GameState restored =
                registry.getGame("game-3");

        assertNotNull(restored);
        assertEquals("game-3", restored.getGame().getGameId());
        assertEquals(GamePhase.ROUND1, restored.getGame().getPhase());
    }
}