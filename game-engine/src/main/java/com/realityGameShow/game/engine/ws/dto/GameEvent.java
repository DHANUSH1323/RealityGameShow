package com.realityGameShow.game.engine.ws.dto;

public class GameEvent {

    private String gameId;
    private SenderType senderType;
    private String senderId;
    private GameEventType eventType;
    private Object payload;

    public GameEvent() {
    }

    public String getGameId() {
        return gameId;
    }

    public SenderType getSenderType() {
        return senderType;
    }

    public String getSenderId() {
        return senderId;
    }

    public GameEventType getEventType() {
        return eventType;
    }

    public Object getPayload() {
        return payload;
    }
}