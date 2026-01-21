package com.realityGameShow.game.engine.core;

import com.realityGameShow.game.engine.model.GameState;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameRegistry {

    private final Map<String, GameState> games = new ConcurrentHashMap<>();

    public GameState createGame(String gameId, GameState gameState) {
        games.put(gameId, gameState);
        return gameState;
    }

    public GameState getGame(String gameId) {
        return games.get(gameId);
    }

    public boolean exists(String gameId) {
        return games.containsKey(gameId);
    }

    public void removeGame(String gameId) {
        games.remove(gameId);
    }

    public void register(GameState gameState) {
        games.put(gameState.getGame().getGameId(), gameState);
    }
}