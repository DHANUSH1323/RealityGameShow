// src/types/events.ts
import type { GameState, CategoriesResponse, ServerError } from "./game";

/**
 * Client -> Server events
 */
export type GameEventType =
    | "JOIN_AS_HOST"
    | "JOIN_AS_TEAM"
    | "JOIN_AS_VIEWER"
    | "START_ROUND_1"
    | "START_ROUND_2"
    | "START_ROUND_3"
    | "SELECT_CATEGORY_R1"
    | "SELECT_CATEGORY_R2"
    | "SELECT_CATEGORY_R3"
    | "BUZZ"
    | "SUBMIT_ANSWER_R1"
    | "SUBMIT_ANSWER_R2"
    | "SUBMIT_ANSWER_R3";

export type JoinTeamPayload = {
    gameId: string;
    teamId: string;
};

export type CategorySelectionPayload = {
    category: string;
};

export type AnswerPayload = {
    answer: string;
};

export type ClientEvent =
    | {
        gameId: string;
        eventType: "JOIN_AS_HOST" | "JOIN_AS_VIEWER";
    }
    | {
        gameId: string;
        eventType: "JOIN_AS_TEAM";
        payload: JoinTeamPayload;
    }
    | {
        gameId: string;
        eventType:
        | "START_ROUND_1"
        | "START_ROUND_2"
        | "START_ROUND_3";
    }
    | {
        gameId: string;
        eventType:
        | "SELECT_CATEGORY_R1"
        | "SELECT_CATEGORY_R2"
        | "SELECT_CATEGORY_R3";
        payload: CategorySelectionPayload;
    }
    | {
        gameId: string;
        eventType: "BUZZ";
    }
    | {
        gameId: string;
        eventType:
        | "SUBMIT_ANSWER_R1"
        | "SUBMIT_ANSWER_R2"
        | "SUBMIT_ANSWER_R3";
        payload: AnswerPayload;
    };

/**
 * Server -> Client events
 * Discriminated by SHAPE
 */
export type GameStateUpdateEvent = {
    gameId: string;
    phase: string;
    currentRound: number;
    activeTeamId: string | null;
    scores: Record<string, number>;
};

export type CategoriesResponseEvent = {
    gameId: string;
    round: number;
    categories: string[];
};

export type ErrorEvent = {
    code: string;
    message: string;
};

export type ServerEvent =
    | GameStateUpdateEvent
    | CategoriesResponseEvent
    | ErrorEvent;