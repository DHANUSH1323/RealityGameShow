package com.realityGameShow.game.engine.ws.orchestrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realityGameShow.game.engine.ai.AIHost;
import com.realityGameShow.game.engine.core.GameEngine;
import com.realityGameShow.game.engine.core.GameRegistry;
import com.realityGameShow.game.engine.model.GamePhase;
import com.realityGameShow.game.engine.model.GameState;
import com.realityGameShow.game.engine.model.Question;
import com.realityGameShow.game.engine.persistence.GameStateMapper;
import com.realityGameShow.game.engine.persistence.GameStatePersistenceService;
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
    // private final GameState gameState;
    private final GameRegistry gameRegistry;
    private final ObjectMapper objectMapper;
    private final AIHost aiHost;
    private final GameStatePersistenceService persistence;

    private final Set<WebSocketSession> sessions =
            ConcurrentHashMap.newKeySet();

            public GameOrchestrator(
                GameEngine gameEngine,
                GameRegistry gameRegistry,
                ObjectMapper objectMapper,
                AIHost aiHost,
                GameStatePersistenceService persistence
        ) {
            this.gameEngine = gameEngine;
            this.gameRegistry = gameRegistry;
            this.objectMapper = objectMapper;
            this.aiHost = aiHost;
            this.persistence = persistence;
        }

    public void handleEvent(GameEvent event, WebSocketSession session) {
        GameState gameState = gameRegistry.getGame(event.getGameId());

        if (gameState == null) {
            sendError(session, "GAME_NOT_FOUND", "Invalid gameId");
            return;
        }

        if (!isPhaseAllowed(event, gameState)) {
            sendError(session, "INVALID_PHASE", "Action not allowed in current game phase");
            return;
        }
        
        if (!isSenderAllowed(event, gameState)) {
            sendError(session, "UNAUTHORIZED_ACTION", "Sender not allowed to perform this action");
            return;
        }

        if (event.getEventType().name().startsWith("SUBMIT_ANSWER")) {
            if (!event.getSenderId().equals(gameState.getActiveTeamId())) {
                sendError(session, "NOT_ACTIVE_TEAM", "It is not your turn");
                return;
            }
        }

        boolean stateChanged = false;

        switch (event.getEventType()) {

            case START_ROUND_1 -> {
                // Request category selection for Round 1
                gameState.setPendingCategoryRound("1");
                sendCategories(session, 1, gameState);
                stateChanged = false; // Don't change game state yet, waiting for category
            }

            case SELECT_CATEGORY_R1 -> {
                CategorySelectionPayload p = (CategorySelectionPayload) event.getPayload();
                gameState.setSelectedCategory(p.getCategory());
                gameState.setPendingCategoryRound(null);
                Question q = aiHost.generateQuestion(1, p.getCategory());
                gameEngine.startRound1(q);
                stateChanged = true;
            }

            case START_ROUND_2 -> {
                // Request category selection for Round 2
                gameState.setPendingCategoryRound("2");
                sendCategories(session, 2, gameState);
                stateChanged = false; // Don't change game state yet, waiting for category
            }

            case SELECT_CATEGORY_R2 -> {
                CategorySelectionPayload p = (CategorySelectionPayload) event.getPayload();
                gameState.setSelectedCategory(p.getCategory());
                gameState.setPendingCategoryRound(null);
                gameEngine.startRound2();
                stateChanged = true;
            }

            case START_ROUND_3 -> {
                // Request category selection for Round 3
                gameState.setPendingCategoryRound("3");
                sendCategories(session, 3, gameState);
                stateChanged = false; // Don't change game state yet, waiting for category
            }

            case SELECT_CATEGORY_R3 -> {
                CategorySelectionPayload p = (CategorySelectionPayload) event.getPayload();
                gameState.setSelectedCategory(p.getCategory());
                gameState.setPendingCategoryRound(null);
                gameEngine.startRound3();
                // Question generation happens in startRound3 -> startNextRound3Team
                stateChanged = true;
            }

            case BUZZ -> {
                gameEngine.buzzerPress(event.getSenderId());
                stateChanged = true;
            }

            case SUBMIT_ANSWER_R1 -> {
                Round1AnswerPayload p = (Round1AnswerPayload) event.getPayload();
            
                boolean isCorrect = aiHost.validateAnswer(gameState.getCurrentQuestion(), p.getAnswer());
                gameEngine.submitAnswer(event.getSenderId(), isCorrect);
                stateChanged = true;
            }

            case SUBMIT_ANSWER_R2 -> {
                Round2AnswerPayload p = (Round2AnswerPayload) event.getPayload();
            
                // AI validates the answer automatically
                boolean isCorrect = aiHost.validateAnswer(gameState.getCurrentQuestion(), p.getAnswer());
                // Points are calculated automatically by GameEngine based on question's point value
                gameEngine.submitRound2Answer(event.getSenderId(), isCorrect);
                stateChanged = true;
            }

            case SUBMIT_ANSWER_R3 -> {
                Round3AnswerPayload p = (Round3AnswerPayload) event.getPayload();
            
                boolean isCorrect = aiHost.validateAnswer(gameState.getCurrentQuestion(), p.getAnswer());
                gameEngine.submitRound3Answer(event.getSenderId(), isCorrect);
                stateChanged = true;
            }

            default -> {
                // ignore unknown events
            }
        }

        if (stateChanged) {
            GameStateSnapshot snapshot =
                    GameStateMapper.toSnapshot(gameState);

            persistence.save(snapshot);
            broadcastGameState(gameState);
        }
    }

    // ----------------------------
    // Validation helpers
    // ----------------------------

    private boolean isPhaseAllowed(GameEvent event, GameState gameState) {
        GamePhase phase = gameState.getGame().getPhase();

        return switch (event.getEventType()) {
            case START_ROUND_1, SELECT_CATEGORY_R1 -> phase == GamePhase.TEAM_FORMATION;
            case BUZZ, SUBMIT_ANSWER_R1 -> phase == GamePhase.ROUND1;
            case START_ROUND_2, SELECT_CATEGORY_R2 -> phase == GamePhase.ROUND1;
            case SUBMIT_ANSWER_R2 -> phase == GamePhase.ROUND2;
            case START_ROUND_3, SELECT_CATEGORY_R3 -> phase == GamePhase.ROUND2;
            case SUBMIT_ANSWER_R3 -> phase == GamePhase.ROUND3;
            default -> false;
        };
    }

    private boolean isSenderAllowed(GameEvent event, GameState gameState) {
        boolean isTeam =
                gameState.getTeams().containsKey(event.getSenderId());

        return switch (event.getEventType()) {
            case START_ROUND_1,
                 START_ROUND_2,
                 START_ROUND_3,
                 SELECT_CATEGORY_R1,
                 SELECT_CATEGORY_R2,
                 SELECT_CATEGORY_R3 -> !isTeam;

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

    private void broadcastGameState(GameState gameState) {
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

    // ----------------------------
    // Error handling
    // ----------------------------

    private void sendError(WebSocketSession session, String code, String message) {
        try {
            ErrorEvent error =
                    new ErrorEvent(code, message);

            String payload =
                    objectMapper.writeValueAsString(error);

            if (session.isOpen()) {
                session.sendMessage(new TextMessage(payload));
            }
        } catch (Exception e) {
            // logging later
        }
    }

    private void sendCategories(WebSocketSession session, int round, GameState gameState) {
        try {
            java.util.List<String> categories = aiHost.getAvailableCategories(round);
            
            CategoriesResponseEvent categoriesEvent =
                    new CategoriesResponseEvent(
                            gameState.getGame().getGameId(),
                            round,
                            categories
                    );

            String payload = objectMapper.writeValueAsString(categoriesEvent);

            // Send to the requesting session
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(payload));
            }
            
            // Also broadcast to all sessions so everyone sees available categories
            for (WebSocketSession s : sessions) {
                if (s.isOpen() && !s.equals(session)) {
                    s.sendMessage(new TextMessage(payload));
                }
            }
        } catch (Exception e) {
            // logging later
            sendError(session, "ERROR", "Failed to retrieve categories");
        }
    }
}