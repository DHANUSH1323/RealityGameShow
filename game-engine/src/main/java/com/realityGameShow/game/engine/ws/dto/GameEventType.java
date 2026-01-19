package com.realityGameShow.game.engine.ws.dto;

public enum GameEventType {

    // Game control
    START_ROUND_1,
    START_ROUND_2,
    START_ROUND_3,
    
    // Category selection
    REQUEST_CATEGORY_R1,
    REQUEST_CATEGORY_R2,
    REQUEST_CATEGORY_R3,
    SELECT_CATEGORY_R1,
    SELECT_CATEGORY_R2,
    SELECT_CATEGORY_R3,

    // Round 1
    BUZZ,
    SUBMIT_ANSWER_R1,

    // Round 2
    SUBMIT_ANSWER_R2,

    // Round 3
    SUBMIT_ANSWER_R3,

    // Engine output
    GAME_STATE_UPDATE,
    SCORE_UPDATE,
    ACTIVE_TEAM_CHANGED,
    QUESTION_PUBLISHED,
    ROUND_ENDED,
    GAME_OVER
}