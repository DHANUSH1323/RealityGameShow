package com.realityGameShow.game.engine.ws.dto;

import java.util.Map;

public class GameStateUpdateEvent {

    private String gameId;
    private String phase;
    private int currentRound;
    private String activeTeamId;
    private Map<String, Integer> teamScores;

    public GameStateUpdateEvent(
            String gameId,
            String phase,
            int currentRound,
            String activeTeamId,
            Map<String, Integer> teamScores
    ) {
        this.gameId = gameId;
        this.phase = phase;
        this.currentRound = currentRound;
        this.activeTeamId = activeTeamId;
        this.teamScores = teamScores;
    }

    public String getGameId() {
        return gameId;
    }

    public String getPhase() {
        return phase;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public String getActiveTeamId() {
        return activeTeamId;
    }

    public Map<String, Integer> getTeamScores() {
        return teamScores;
    }
}