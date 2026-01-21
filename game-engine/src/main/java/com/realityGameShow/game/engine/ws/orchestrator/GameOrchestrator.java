package com.realityGameShow.game.engine.ws.orchestrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realityGameShow.game.engine.ai.AIHost;
import com.realityGameShow.game.engine.core.GameEngine;
import com.realityGameShow.game.engine.core.GameRegistry;
import com.realityGameShow.game.engine.model.GamePhase;
import com.realityGameShow.game.engine.model.GameState;
import com.realityGameShow.game.engine.model.Question;
import com.realityGameShow.game.engine.model.Team;
import com.realityGameShow.game.engine.persistence.GameStateMapper;
import com.realityGameShow.game.engine.persistence.GameStatePersistenceService;
import com.realityGameShow.game.engine.ws.dto.*;
import com.realityGameShow.game.engine.ws.session.SessionContext;
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
    private final GameRegistry gameRegistry;
    private final ObjectMapper objectMapper;
    private final AIHost aiHost;
    private final GameStatePersistenceService persistence;

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final Map<WebSocketSession, SessionContext> sessionContexts = new ConcurrentHashMap<>();

    // Enforce single host per gameId
    private final Map<String, WebSocketSession> hostSessionsByGameId = new ConcurrentHashMap<>();

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

        // ----------------------------
        // 1) JOIN handling (server binds identity to session)
        // ----------------------------

        if (event.getEventType() == GameEventType.JOIN_AS_HOST) {
            String gameId = event.getGameId();

            // Ensure game exists
            GameState gameState = gameRegistry.getGame(gameId);
            if (gameState == null) {
                sendError(session, "GAME_NOT_FOUND", "Invalid gameId");
                return;
            }

            // Enforce single host per game
            WebSocketSession existingHost = hostSessionsByGameId.get(gameId);
            if (existingHost != null && existingHost.isOpen() && !existingHost.equals(session)) {
                sendError(session, "HOST_EXISTS", "A host already joined this game");
                return;
            }

            hostSessionsByGameId.put(gameId, session);
            sessionContexts.put(session, new SessionContext(gameId, SessionContext.Role.HOST, null));
            return;
        }

        if (event.getEventType() == GameEventType.JOIN_AS_VIEWER) {
            String gameId = event.getGameId();

            // Ensure game exists
            GameState gameState = gameRegistry.getGame(gameId);
            if (gameState == null) {
                sendError(session, "GAME_NOT_FOUND", "Invalid gameId");
                return;
            }

            sessionContexts.put(session, new SessionContext(gameId, SessionContext.Role.VIEWER, null));
            return;
        }

        if (event.getEventType() == GameEventType.JOIN_AS_TEAM) {
            JoinTeamPayload p = (JoinTeamPayload) event.getPayload();
            String gameId = p.getGameId();

            // Ensure game exists
            GameState gameState = gameRegistry.getGame(gameId);
            if (gameState == null) {
                sendError(session, "GAME_NOT_FOUND", "Invalid gameId");
                return;
            }

            // Ensure team exists
            if (!gameState.getTeams().containsKey(p.getTeamId())) {
                sendError(session, "TEAM_NOT_FOUND", "Invalid teamId for this game");
                return;
            }

            sessionContexts.put(session, new SessionContext(gameId, SessionContext.Role.TEAM, p.getTeamId()));
            return;
        }

        // ----------------------------
        // 2) Require JOIN before any action
        // ----------------------------
        SessionContext context = sessionContexts.get(session);
        if (context == null) {
            sendError(session, "NOT_JOINED", "You must join before sending actions");
            return;
        }

        // Ensure the action targets the same game this session joined
        if (!context.getGameId().equals(event.getGameId())) {
            sendError(session, "GAME_MISMATCH", "Session is not joined to this gameId");
            return;
        }

        // ----------------------------
        // 3) Load game state
        // ----------------------------
        GameState gameState = gameRegistry.getGame(event.getGameId());
        if (gameState == null) {
            sendError(session, "GAME_NOT_FOUND", "Invalid gameId");
            return;
        }

        // ----------------------------
        // 4) Validate phase + role for this event
        // ----------------------------
        if (!isPhaseAllowed(event, gameState)) {
            sendError(session, "INVALID_PHASE", "Action not allowed in current game phase");
            return;
        }

        if (!isRoleAllowed(event, context)) {
            sendError(session, "UNAUTHORIZED_ACTION", "You are not allowed to perform this action");
            return;
        }

        // Extra protection: for SUBMIT_ANSWER events, must be active team
        if (event.getEventType().name().startsWith("SUBMIT_ANSWER")) {
            if (!context.isTeam()) {
                sendError(session, "UNAUTHORIZED_ACTION", "Only a team can submit answers");
                return;
            }
            if (gameState.getActiveTeamId() == null ||
                    !context.getTeamId().equals(gameState.getActiveTeamId())) {
                sendError(session, "NOT_ACTIVE_TEAM", "It is not your turn");
                return;
            }
        }

        boolean stateChanged = false;

        // ----------------------------
        // 5) Execute event (NEVER trust senderId from payload)
        // ----------------------------
        switch (event.getEventType()) {

            // HOST actions
            case START_ROUND_1 -> {
                gameState.setPendingCategoryRound("1");
                sendCategories(session, 1, gameState);
                stateChanged = false;
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
                gameState.setPendingCategoryRound("2");
                sendCategories(session, 2, gameState);
                stateChanged = false;
            }

            case SELECT_CATEGORY_R2 -> {
                CategorySelectionPayload p = (CategorySelectionPayload) event.getPayload();
                gameState.setSelectedCategory(p.getCategory());
                gameState.setPendingCategoryRound(null);

                gameEngine.startRound2();
                stateChanged = true;
            }

            case START_ROUND_3 -> {
                gameState.setPendingCategoryRound("3");
                sendCategories(session, 3, gameState);
                stateChanged = false;
            }

            case SELECT_CATEGORY_R3 -> {
                CategorySelectionPayload p = (CategorySelectionPayload) event.getPayload();
                gameState.setSelectedCategory(p.getCategory());
                gameState.setPendingCategoryRound(null);

                gameEngine.startRound3();
                stateChanged = true;
            }

            // TEAM actions
            case BUZZ -> {
                gameEngine.buzzerPress(context.getTeamId());
                stateChanged = true;
            }

            case SUBMIT_ANSWER_R1 -> {
                Round1AnswerPayload p = (Round1AnswerPayload) event.getPayload();
                boolean isCorrect = aiHost.validateAnswer(gameState.getCurrentQuestion(), p.getAnswer());

                gameEngine.submitAnswer(context.getTeamId(), isCorrect);
                stateChanged = true;
            }

            case SUBMIT_ANSWER_R2 -> {
                Round2AnswerPayload p = (Round2AnswerPayload) event.getPayload();
                boolean isCorrect = aiHost.validateAnswer(gameState.getCurrentQuestion(), p.getAnswer());

                gameEngine.submitRound2Answer(context.getTeamId(), isCorrect);
                stateChanged = true;
            }

            case SUBMIT_ANSWER_R3 -> {
                Round3AnswerPayload p = (Round3AnswerPayload) event.getPayload();
                boolean isCorrect = aiHost.validateAnswer(gameState.getCurrentQuestion(), p.getAnswer());

                gameEngine.submitRound3Answer(context.getTeamId(), isCorrect);
                stateChanged = true;
            }

            default -> {
                // ignore unknown events
            }
        }

        // ----------------------------
        // 6) Persist + broadcast after state changes
        // ----------------------------
        if (stateChanged) {
            GameStateSnapshot snapshot = GameStateMapper.toSnapshot(gameState);
            persistence.save(snapshot);
            broadcastGameState(gameState);
        }
    }

    private boolean isPhaseAllowed(GameEvent event, GameState gameState) {
        GamePhase phase = gameState.getGame().getPhase();

        return switch (event.getEventType()) {
            case START_ROUND_1, SELECT_CATEGORY_R1 -> phase == GamePhase.TEAM_FORMATION;

            case BUZZ,
                 SUBMIT_ANSWER_R1,
                 START_ROUND_2,
                 SELECT_CATEGORY_R2 -> phase == GamePhase.ROUND1;

            case SUBMIT_ANSWER_R2,
                 START_ROUND_3,
                 SELECT_CATEGORY_R3 -> phase == GamePhase.ROUND2;

            case SUBMIT_ANSWER_R3 -> phase == GamePhase.ROUND3;

            // join events handled earlier
            case JOIN_AS_HOST, JOIN_AS_TEAM, JOIN_AS_VIEWER -> false;

            default -> false;
        };
    }

    private boolean isRoleAllowed(GameEvent event, SessionContext context) {
        return switch (event.getEventType()) {

            // Only HOST controls flow
            case START_ROUND_1,
                 START_ROUND_2,
                 START_ROUND_3,
                 SELECT_CATEGORY_R1,
                 SELECT_CATEGORY_R2,
                 SELECT_CATEGORY_R3 -> context.isHost();

            // Only TEAMS can play
            case BUZZ,
                 SUBMIT_ANSWER_R1,
                 SUBMIT_ANSWER_R2,
                 SUBMIT_ANSWER_R3 -> context.isTeam();

            default -> false;
        };
    }

    // Session management called by WebSocketController
    public void registerSession(WebSocketSession session) {
        sessions.add(session);
    }

    public void unregisterSession(WebSocketSession session) {
        sessions.remove(session);

        SessionContext ctx = sessionContexts.remove(session);
        if (ctx != null && ctx.isHost()) {
            WebSocketSession host = hostSessionsByGameId.get(ctx.getGameId());
            if (host != null && host.equals(session)) {
                hostSessionsByGameId.remove(ctx.getGameId());
            }
        }
    }

    private void broadcastGameState(GameState gameState) {
        try {
            Map<String, Integer> scores =
                    gameState.getTeams().values().stream()
                            .collect(Collectors.toMap(
                                    Team::getTeamId,
                                    Team::getScore
                            ));

            GameStateUpdateEvent update =
                    new GameStateUpdateEvent(
                            gameState.getGame().getGameId(),
                            gameState.getGame().getPhase().name(),
                            gameState.getGame().getCurrentRound(),
                            gameState.getActiveTeamId(),
                            scores
                    );

            String payload = objectMapper.writeValueAsString(update);

            for (WebSocketSession s : sessions) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(payload));
                }
            }
        } catch (Exception e) {
            // log later
        }
    }

    private void sendError(WebSocketSession session, String code, String message) {
        try {
            ErrorEvent error = new ErrorEvent(code, message);
            String payload = objectMapper.writeValueAsString(error);

            if (session.isOpen()) {
                session.sendMessage(new TextMessage(payload));
            }
        } catch (Exception e) {
            // log later
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

            if (session.isOpen()) {
                session.sendMessage(new TextMessage(payload));
            }

            for (WebSocketSession s : sessions) {
                if (s.isOpen() && !s.equals(session)) {
                    s.sendMessage(new TextMessage(payload));
                }
            }
        } catch (Exception e) {
            sendError(session, "ERROR", "Failed to retrieve categories");
        }
    }
}