package com.realityGameShow.game.engine.ws.dto;

import java.util.List;

public class CategoriesResponseEvent {
    
    private String gameId;
    private int round;
    private List<String> categories;
    private String eventType = "CATEGORIES_AVAILABLE";

    public CategoriesResponseEvent() {}

    public CategoriesResponseEvent(String gameId, int round, List<String> categories) {
        this.gameId = gameId;
        this.round = round;
        this.categories = categories;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
