package com.realityGameShow.game.engine.ws.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realityGameShow.game.engine.ws.dto.GameEvent;
import com.realityGameShow.game.engine.ws.orchestrator.GameOrchestrator;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class GameWebSocketController extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final GameOrchestrator orchestrator;

    public GameWebSocketController(GameOrchestrator orchestrator, ObjectMapper objectMapper) {
        this.orchestrator = orchestrator;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        GameEvent event =objectMapper.readValue(message.getPayload(), GameEvent.class);
        orchestrator.handleEvent(event, session);
    }
}