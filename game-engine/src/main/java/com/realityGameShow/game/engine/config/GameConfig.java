package com.realityGameShow.game.engine.config;

import com.realityGameShow.game.engine.ai.AIHost;
import com.realityGameShow.game.engine.core.GameEngine;
import com.realityGameShow.game.engine.model.Game;
import com.realityGameShow.game.engine.model.GameState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameConfig {

    @Bean
    public Game game() {
        // TODO: Make this configurable or load from a data source
        return new Game("default-game");
    }

    @Bean
    public GameState gameState(Game game) {
        return new GameState(game);
    }

    @Bean
    public GameEngine gameEngine(GameState gameState, AIHost aiHost) {
        return new GameEngine(gameState, aiHost);
    }
}
