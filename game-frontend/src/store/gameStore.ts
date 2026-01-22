// src/store/gameStore.ts
import { create } from "zustand";
import { gameSocket } from "../ws/gameSocket";
import type { ClientEvent, ServerEvent } from "../types/events";

export type Role = "HOST" | "TEAM" | "VIEWER";

export type GameStoreState = {
    // session
    gameId: string | null;
    role: Role | null;
    teamId: string | null;

    // game state
    phase: string;
    currentRound: number;
    activeTeamId: string | null;
    scores: Record<string, number>;
    categories: string[];
    categoryRound: number | null;

    // ui
    error: string | null;

    // actions
    connect: (gameId: string, role: Role, teamId?: string) => void;
    sendEvent: (event: ClientEvent) => void;
    clearError: () => void;
};

export const useGameStore = create<GameStoreState>((set) => ({
    // -------------------------
    // initial state
    // -------------------------
    gameId: null,
    role: null,
    teamId: null,

    phase: "TEAM_FORMATION",
    currentRound: 0,
    activeTeamId: null,
    scores: {},
    categories: [],
    categoryRound: null,

    error: null,

    // -------------------------
    // connect
    // -------------------------
    connect: (gameId, role, teamId) => {
        gameSocket.connect(gameId);

        set({ gameId, role, teamId });

        // JOIN once socket opens
        gameSocket.subscribe(() => {
            if (role === "HOST") {
                gameSocket.send({ gameId, eventType: "JOIN_AS_HOST" });
            }

            if (role === "VIEWER") {
                gameSocket.send({ gameId, eventType: "JOIN_AS_VIEWER" });
            }

            if (role === "TEAM") {
                gameSocket.send({
                    gameId,
                    eventType: "JOIN_AS_TEAM",
                    payload: { gameId, teamId: teamId! },
                });
            }
        });

        // listen to server events
        gameSocket.subscribe((event: ServerEvent) => {
            const type = (event as any).eventType ?? (event as any).type;

            switch (type) {
                case "GAME_STATE_UPDATE":
                    set({
                        phase: (event as any).phase,
                        currentRound: (event as any).currentRound,
                        activeTeamId: (event as any).activeTeamId,
                        scores: (event as any).scores,
                        categories: [],
                        categoryRound: null,
                    });
                    break;

                case "CATEGORIES_RESPONSE":
                    set({
                        categories: (event as any).categories,
                        categoryRound: (event as any).round,
                    });
                    break;

                case "ERROR":
                    set({ error: (event as any).message });
                    break;
            }
        });
    },

    // -------------------------
    // send event
    // -------------------------
    sendEvent: (event: ClientEvent) => {
        gameSocket.send(event);
    },

    // -------------------------
    // clear error
    // -------------------------
    clearError: () => set({ error: null }),
}));