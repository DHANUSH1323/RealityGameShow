package com.realityGameShow.game.engine.persistence;

import com.realityGameShow.game.engine.ws.dto.GameStateSnapshot;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryGameStatePersistenceService
        implements GameStatePersistenceService {

    private final Map<String, GameStateSnapshot> store =
            new ConcurrentHashMap<>();

    @Override
    public void save(GameStateSnapshot snapshot) {
        store.put(snapshot.getGameId(), snapshot);
    }

    @Override
    public GameStateSnapshot load(String gameId) {
        return store.get(gameId);
    }

    @Override
    public void delete(String gameId) {
        store.remove(gameId);
    }
}