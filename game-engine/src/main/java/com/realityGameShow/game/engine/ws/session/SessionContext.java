package com.realityGameShow.game.engine.ws.session;

public class SessionContext {

    public enum Role {
        HOST,
        TEAM,
        VIEWER
    }

    private String gameId;
    private Role role;
    private String teamId; // only for TEAM role

    public SessionContext(String gameId, Role role, String teamId) {
        this.gameId = gameId;
        this.role = role;
        this.teamId = teamId;
    }

    public String getGameId() {
        return gameId;
    }

    public Role getRole() {
        return role;
    }

    public String getTeamId() {
        return teamId;
    }

    public boolean isHost() {
        return role == Role.HOST;
    }

    public boolean isTeam() {
        return role == Role.TEAM;
    }

    public boolean isViewer() {
        return role == Role.VIEWER;
    }
}