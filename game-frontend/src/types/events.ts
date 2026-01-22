// src/types/events.ts
import type { GameState, CategoriesResponse, ServerError } from "./game";

/**
 * Matches backend GameEventType enum.
 * Add values only if backend has them.
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

/**
 * Payloads for JOIN events
 */
export type JoinTeamPayload = {
    gameId: string;
    teamId: string;
};

/**
 * Category selection payload
 */
export type CategorySelectionPayload = {
    category: string;
};

/**
 * Answer payloads: frontend sends raw answer text.
 * Backend/AI decides correctness.
 */
export type AnswerPayload = {
    answer: string;
};

/**
 * Client -> Server event format.
 * NOTE: senderId is OPTIONAL because backend now derives identity from SessionContext.
 * Sending it doesn't hurt, but it shouldn't be trusted by backend.
 */
export type ClientEvent =
    | {
        gameId: string;
        eventType: "JOIN_AS_HOST" | "JOIN_AS_VIEWER";
        payload?: undefined;
        senderId?: string;
    }
    | {
        gameId: string;
        eventType: "JOIN_AS_TEAM";
        payload: JoinTeamPayload;
        senderId?: string;
    }
    | {
        gameId: string;
        eventType: "START_ROUND_1" | "START_ROUND_2" | "START_ROUND_3";
        payload?: undefined;
        senderId?: string;
    }
    | {
        gameId: string;
        eventType: "SELECT_CATEGORY_R1" | "SELECT_CATEGORY_R2" | "SELECT_CATEGORY_R3";
        payload: CategorySelectionPayload;
        senderId?: string;
    }
    | {
        gameId: string;
        eventType: "BUZZ";
        payload?: undefined;
        senderId?: string;
    }
    | {
        gameId: string;
        eventType: "SUBMIT_ANSWER_R1" | "SUBMIT_ANSWER_R2" | "SUBMIT_ANSWER_R3";
        payload: AnswerPayload;
        senderId?: string;
    };

/**
 * Server -> Client events.
 * Your backend currently sends:
 * - GameStateUpdateEvent (snapshot)
 * - CategoriesResponseEvent
 * - ErrorEvent
 *
 * We'll decode them by shape.
 */

// Backend broadcasts game state
export type GameStateUpdateEvent = GameState & {
    // backend might not include eventType; keep optional
    eventType?: "GAME_STATE_UPDATE";
};

// Backend sends categories list
export type CategoriesResponseEvent = CategoriesResponse & {
    eventType?: "CATEGORIES";
};

// Backend sends error
export type ErrorEvent = ServerError & {
    type?: "ERROR";
};

export type ServerEvent =
    | GameStateUpdateEvent
    | CategoriesResponseEvent
    | ErrorEvent;