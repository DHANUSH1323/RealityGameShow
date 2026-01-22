// src/types/game.ts
import type { GamePhase } from "./phase";

export type TeamScores = Record<string, number>;

export type GameState = {
    gameId: string;
    phase: GamePhase;
    currentRound: number;
    activeTeamId: string | null;
    teamScores: TeamScores;
};

// For category selection UI
export type CategoriesResponse = {
    gameId: string;
    round: number;
    categories: string[];
};

// For showing server errors in UI
export type ServerError = {
    code: string;
    message: string;
};