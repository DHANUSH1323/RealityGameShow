package com.realityGameShow.game.engine.ws.orchestrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realityGameShow.game.engine.core.GameEngine;
import com.realityGameShow.game.engine.model.GamePhase;
import com.realityGameShow.game.engine.model.GameState;
import com.realityGameShow.game.engine.ws.dto.*;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class GameOrchestrator {

    private final GameEngine gameEngine;
    private final GameState gameState;
    private final ObjectMapper objectMapper;

    private final Set<WebSocketSession> sessions =
            ConcurrentHashMap.newKeySet();

    public GameOrchestrator(
            GameEngine gameEngine,
            GameState gameState,
            ObjectMapper objectMapper
    ) {
        this.gameEngine = gameEngine;
        this.gameState = gameState;
        this.objectMapper = objectMapper;
    }

    public void handleEvent(GameEvent event, WebSocketSession session) {

        if (!isPhaseAllowed(event)) {
            return;
        }

        if (!isSenderAllowed(event)) {
            return;
        }

        boolean stateChanged = false;

        switch (event.getEventType()) {

            case START_ROUND_1 -> {
                gameEngine.startRound1(null);
                stateChanged = true;
            }

            case START_ROUND_2 -> {
                gameEngine.startRound2();
                stateChanged = true;
            }

            case START_ROUND_3 -> {
                gameEngine.startRound3();
                stateChanged = true;
            }

            case BUZZ -> {
                gameEngine.buzzerPress(event.getSenderId());
                stateChanged = true;
            }

            case SUBMIT_ANSWER_R1 -> {
                Round1AnswerPayload p =
                        (Round1AnswerPayload) event.getPayload();
                gameEngine.submitAnswer(
                        event.getSenderId(),
                        p.isCorrect()
                );
                stateChanged = true;
            }

            case SUBMIT_ANSWER_R2 -> {
                Round2AnswerPayload p =
                        (Round2AnswerPayload) event.getPayload();
                gameEngine.submitRound2Answer(
                        event.getSenderId(),
                        p.isCorrect(),
                        p.getPoints()
                );
                stateChanged = true;
            }

            case SUBMIT_ANSWER_R3 -> {
                Round3AnswerPayload p =
                        (Round3AnswerPayload) event.getPayload();
                gameEngine.submitRound3Answer(
                        event.getSenderId(),
                        p.isCorrect()
                );
                stateChanged = true;
            }

            default -> {
                // ignore unknown events
            }
        }

        if (stateChanged) {
            broadcastGameState();
        }
    }

    // ----------------------------
    // Validation helpers
    // ----------------------------

    private boolean isPhaseAllowed(GameEvent event) {
        GamePhase phase = gameState.getGame().getPhase();

        return switch (event.getEventType()) {
            case START_ROUND_1 -> phase == GamePhase.TEAM_FORMATION;
            case BUZZ, SUBMIT_ANSWER_R1 -> phase == GamePhase.ROUND1;
            case START_ROUND_2 -> phase == GamePhase.ROUND1;
            case SUBMIT_ANSWER_R2 -> phase == GamePhase.ROUND2;
            case START_ROUND_3 -> phase == GamePhase.ROUND2;
            case SUBMIT_ANSWER_R3 -> phase == GamePhase.ROUND3;
            default -> false;
        };
    }

    private boolean isSenderAllowed(GameEvent event) {
        boolean isTeam =
                gameState.getTeams().containsKey(event.getSenderId());

        return switch (event.getEventType()) {
            case START_ROUND_1,
                 START_ROUND_2,
                 START_ROUND_3 -> !isTeam;

            case BUZZ,
                 SUBMIT_ANSWER_R1,
                 SUBMIT_ANSWER_R2,
                 SUBMIT_ANSWER_R3 -> isTeam;

            default -> false;
        };
    }

    // ----------------------------
    // Session management
    // ----------------------------

    public void registerSession(WebSocketSession session) {
        sessions.add(session);
    }

    public void unregisterSession(WebSocketSession session) {
        sessions.remove(session);
    }

    // ----------------------------
    // Broadcasting
    // ----------------------------

    private void broadcastGameState() {
        try {
            Map<String, Integer> scores =
                    gameState.getTeams().values().stream()
                            .collect(Collectors.toMap(
                                    t -> t.getTeamId(),
                                    t -> t.getScore()
                            ));

            GameStateUpdateEvent update =
                    new GameStateUpdateEvent(
                            gameState.getGame().getGameId(),
                            gameState.getGame().getPhase().name(),
                            gameState.getGame().getCurrentRound(),
                            gameState.getActiveTeamId(),
                            scores
                    );

            String payload =
                    objectMapper.writeValueAsString(update);

            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(payload));
                }
            }
        } catch (Exception e) {
            // logging later
        }
    }
}