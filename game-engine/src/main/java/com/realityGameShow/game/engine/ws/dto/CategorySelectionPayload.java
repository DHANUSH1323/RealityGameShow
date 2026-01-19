package com.realityGameShow.game.engine.ws.dto;

public class CategorySelectionPayload {
    
    private String category;

    public CategorySelectionPayload() {}

    public CategorySelectionPayload(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
