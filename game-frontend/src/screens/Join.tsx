// src/screens/Join.tsx
import { useState } from "react";
import { useGameStore } from "../store/gameStore";
import type { Role } from "../store/gameStore";

export default function Join() {
    const connect = useGameStore((s) => s.connect);

    const [gameId, setGameId] = useState("");
    const [role, setRole] = useState<Role>("VIEWER");
    const [teamId, setTeamId] = useState("");

    const onJoin = () => {
        if (!gameId) return alert("Game ID required");

        if (role === "TEAM" && !teamId) {
            return alert("Team ID required for TEAM role");
        }

        connect(gameId, role, role === "TEAM" ? teamId : undefined);
    };

    return (
        <div style={{ maxWidth: 400 }}>
            <h2>Join Game</h2>

            {/* Game ID */}
            <input
                placeholder="Game ID"
                value={gameId}
                onChange={(e) => setGameId(e.target.value)}
                style={{ width: "100%", marginBottom: 8 }}
            />

            {/* Role selector */}
            <div style={{ marginBottom: 8 }}>
                <label>
                    <input
                        type="radio"
                        checked={role === "HOST"}
                        onChange={() => setRole("HOST")}
                    />
                    Host
                </label>

                <label style={{ marginLeft: 12 }}>
                    <input
                        type="radio"
                        checked={role === "TEAM"}
                        onChange={() => setRole("TEAM")}
                    />
                    Team
                </label>

                <label style={{ marginLeft: 12 }}>
                    <input
                        type="radio"
                        checked={role === "VIEWER"}
                        onChange={() => setRole("VIEWER")}
                    />
                    Viewer
                </label>
            </div>

            {/* Team ID (only for TEAM) */}
            {role === "TEAM" && (
                <input
                    placeholder="Team ID"
                    value={teamId}
                    onChange={(e) => setTeamId(e.target.value)}
                    style={{ width: "100%", marginBottom: 8 }}
                />
            )}

            <button onClick={onJoin}>Join</button>
        </div>
    );
}