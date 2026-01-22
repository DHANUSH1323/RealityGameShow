// src/screens/Lobby.tsx
import { useGameStore } from "../store/gameStore";
import CategorySelector from "../components/CategorySelector";
import Scoreboard from "../components/Scoreboard";

export default function Lobby() {
    const gameId = useGameStore((s) => s.gameId);
    const role = useGameStore((s) => s.role);
    const sendEvent = useGameStore((s) => s.sendEvent);
    const error = useGameStore((s) => s.error);
    const clearError = useGameStore((s) => s.clearError);

    if (!gameId) {
        return <div>Please join a game first.</div>;
    }

    return (
        <div>
            <h2>Lobby</h2>

            {/* Error banner */}
            {error && (
                <div
                    style={{
                        border: "1px solid red",
                        padding: 8,
                        marginBottom: 12,
                        background: "#220000",
                    }}
                >
                    <b>Error:</b> {error}
                    <button
                        onClick={clearError}
                        style={{ marginLeft: 12 }}
                    >
                        Close
                    </button>
                </div>
            )}

            {/* Scoreboard always visible */}
            <Scoreboard />

            {/* Host-only controls */}
            {role === "HOST" && (
                <div style={{ marginTop: 16 }}>
                    <h3>Host Controls</h3>

                    <button
                        onClick={() =>
                            sendEvent({
                                gameId,
                                eventType: "START_ROUND_1",
                            })
                        }
                    >
                        Start Round 1
                    </button>

                    <button
                        onClick={() =>
                            sendEvent({
                                gameId,
                                eventType: "START_ROUND_2",
                            })
                        }
                        style={{ marginLeft: 8 }}
                    >
                        Start Round 2
                    </button>

                    <button
                        onClick={() =>
                            sendEvent({
                                gameId,
                                eventType: "START_ROUND_3",
                            })
                        }
                        style={{ marginLeft: 8 }}
                    >
                        Start Round 3
                    </button>
                </div>
            )}

            {/* Category selector appears ONLY when backend sends categories */}
            <div style={{ marginTop: 20 }}>
                <CategorySelector />
            </div>
        </div>
    );
}