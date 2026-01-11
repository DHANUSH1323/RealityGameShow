package com.realityGameShow.game.engine.ws.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realityGameShow.game.engine.ws.dto.GameEvent;
import com.realityGameShow.game.engine.ws.orchestrator.GameOrchestrator;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameWebSocketController extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final GameOrchestrator orchestrator;

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        orchestrator.registerSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        orchestrator.unregisterSession(session);
    }

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