package com.realityGameShow.game.engine.persistence;

import com.realityGameShow.game.engine.core.GameRegistry;
import com.realityGameShow.game.engine.model.GameState;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class GameStateRecoveryService {

    private final GameStatePersistenceService persistence;
    private final GameRegistry gameRegistry;

    public GameStateRecoveryService(
            GameStatePersistenceService persistence,
            GameRegistry gameRegistry
    ) {
        this.persistence = persistence;
        this.gameRegistry = gameRegistry;
    }

    @PostConstruct
    public void recoverGame() {
        persistence.loadLatest().ifPresent(snapshot -> {
            GameState restored =
                    GameStateMapper.fromSnapshot(snapshot);
            gameRegistry.register(restored);
        });
    }
}