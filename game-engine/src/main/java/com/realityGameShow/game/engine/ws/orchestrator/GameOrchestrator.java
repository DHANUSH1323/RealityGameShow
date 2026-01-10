package com.realityGameShow.game.engine.ws.orchestrator;

import com.realityGameShow.game.engine.core.GameEngine;
import com.realityGameShow.game.engine.model.GameState;
import com.realityGameShow.game.engine.ws.dto.*;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class GameOrchestrator {

    private final GameEngine gameEngine;
    private final GameState gameState;

    public GameOrchestrator(GameEngine gameEngine, GameState gameState) {
        this.gameEngine = gameEngine;
        this.gameState = gameState;
    }

    public void handleEvent(GameEvent event, WebSocketSession session) {

        switch (event.getEventType()) {

            case START_ROUND_1 -> {
                gameEngine.startRound1(/* question injected later */ null);
            }

            case START_ROUND_2 -> {
                gameEngine.startRound2();
            }

            case START_ROUND_3 -> {
                gameEngine.startRound3();
            }

            case BUZZ -> {
                gameEngine.buzzerPress(event.getSenderId());
            }

            case SUBMIT_ANSWER_R1 -> {
                Round1AnswerPayload p =
                        (Round1AnswerPayload) event.getPayload();
                gameEngine.submitAnswer(
                        event.getSenderId(),
                        p.isCorrect()
                );
            }

            case SUBMIT_ANSWER_R2 -> {
                Round2AnswerPayload p =
                        (Round2AnswerPayload) event.getPayload();
                gameEngine.submitRound2Answer(
                        event.getSenderId(),
                        p.isCorrect(),
                        p.getPoints()
                );
            }

            case SUBMIT_ANSWER_R3 -> {
                Round3AnswerPayload p =
                        (Round3AnswerPayload) event.getPayload();
                gameEngine.submitRound3Answer(
                        event.getSenderId(),
                        p.isCorrect()
                );
            }

            default -> {
            }
        }

    }
}