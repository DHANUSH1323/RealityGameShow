package com.realityGameShow.game.engine.ws.config;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;

public class WebSocketConfig implements WebSocketConfigurer{

    private final WebSocketHandler gameWebSocketHandler;

    public WebSocketConfig(WebSocketHandler gameWebSocketHandler){
        this.gameWebSocketHandler = gameWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry){
        registry.addHandler(gameWebSocketHandler, "/ws/game").setAllowedOrigins("*");
    }
    
}
