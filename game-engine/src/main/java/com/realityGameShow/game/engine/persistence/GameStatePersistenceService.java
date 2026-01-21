package com.realityGameShow.game.engine.persistence;

import com.realityGameShow.game.engine.ws.dto.GameStateSnapshot;

import java.util.Optional;

public interface GameStatePersistenceService {

    void save(GameStateSnapshot snapshot);

    GameStateSnapshot load(String gameId);

    Optional<GameStateSnapshot> loadLatest();

    void delete(String gameId);
}