package com.realityGameShow.game.engine.ws.dto;

public class ErrorEvent {

    private String type = "ERROR";
    private String code;
    private String message;

    public ErrorEvent(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}