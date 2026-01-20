package com.realityGameShow.game.engine.persistence;

import com.realityGameShow.game.engine.ws.dto.GameStateSnapshot;

public interface GameStatePersistenceService {

    void save(GameStateSnapshot snapshot);

    GameStateSnapshot load(String gameId);

    void delete(String gameId);
}