package com.realityGameShow.game.engine.model;

public class Team {

    private String teamId;
    private String name;
    private int memberCount;
    private int score;
    private TeamStatus status;

    public Team(String teamId, String name, int memberCount) {
        this.teamId = teamId;
        this.name = name;
        this.memberCount = memberCount;
        this.score = 0;
        this.status = TeamStatus.FORMING;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getName() {
        return name;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public TeamStatus getStatus() {
        return status;
    }

    public void setStatus(TeamStatus status) {
        this.status = status;
    }
}