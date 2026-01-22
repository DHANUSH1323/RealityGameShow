// src/ws/gameSocket.ts
import type { ClientEvent, ServerEvent } from "../types/events";

type MessageHandler = (event: ServerEvent) => void;

class GameSocket {
    private socket: WebSocket | null = null;
    private handlers: Set<MessageHandler> = new Set();
    private onOpenCallback: (() => void) | null = null;

    /**
     * Connect to backend WebSocket
     */
    connect(gameId: string, onOpen?: () => void) {
        if (this.socket) return;

        const url = `ws://localhost:8080/ws/game?gameId=${gameId}`;
        this.socket = new WebSocket(url);
        this.onOpenCallback = onOpen ?? null;

        this.socket.onopen = () => {
            console.log("WebSocket connected");
            this.onOpenCallback?.();
        };

        this.socket.onmessage = (message) => {
            try {
                const data = JSON.parse(message.data);
                this.dispatch(data);
            } catch (e) {
                console.error("Invalid WS message", e);
            }
        };

        this.socket.onclose = () => {
            console.log("WebSocket disconnected");
            this.socket = null;
            this.handlers.clear();
            this.onOpenCallback = null;
        };

        this.socket.onerror = (err) => {
            console.error("WebSocket error", err);
        };
    }

    /**
     * Send event to backend
     */
    send(event: ClientEvent) {
        if (!this.socket || this.socket.readyState !== WebSocket.OPEN) {
            console.warn("WebSocket not connected");
            return;
        }

        this.socket.send(JSON.stringify(event));
    }

    /**
     * Subscribe to server events
     */
    subscribe(handler: MessageHandler) {
        this.handlers.add(handler);
    }

    /**
     * Unsubscribe from server events
     */
    unsubscribe(handler: MessageHandler) {
        this.handlers.delete(handler);
    }

    /**
     * Dispatch incoming message to all subscribers
     */
    private dispatch(event: ServerEvent) {
        for (const handler of this.handlers) {
            handler(event);
        }
    }

    /**
     * Disconnect socket
     */
    disconnect() {
        this.socket?.close();
        this.socket = null;
        this.handlers.clear();
        this.onOpenCallback = null;
    }
}

export const gameSocket = new GameSocket();