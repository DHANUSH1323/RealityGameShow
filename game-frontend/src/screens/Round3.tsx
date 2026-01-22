// src/screens/Round3.tsx
import { useState } from "react";
import { useGameStore } from "../store/gameStore";
import Scoreboard from "../components/Scoreboard";
import Timer from "../components/Timer";

export default function Round3() {
    const role = useGameStore((s) => s.role);
    const teamId = useGameStore((s) => s.teamId);
    const activeTeamId = useGameStore((s) => s.activeTeamId);
    const gameId = useGameStore((s) => s.gameId);
    const sendEvent = useGameStore((s) => s.sendEvent);

    const [answer, setAnswer] = useState("");

    const isMyTurn = role === "TEAM" && teamId === activeTeamId;

    return (
        <div>
            <h2>Round 3 – Rapid Fire</h2>

            <Scoreboard />

            <hr />

            <h3>Current Team</h3>
            <p>
                {activeTeamId
                    ? `🔥 ${activeTeamId} is playing`
                    : "⏳ Waiting for next team"}
            </p>

            <Timer />

            {/* ACTIVE TEAM */}
            {isMyTurn && (
                <div style={{ marginTop: 16 }}>
                    <input
                        type="text"
                        placeholder="Answer quickly…"
                        value={answer}
                        onChange={(e) => setAnswer(e.target.value)}
                    />

                    <button
                        onClick={() => {
                            sendEvent({
                                gameId: gameId!,
                                eventType: "SUBMIT_ANSWER_R3",
                                payload: { answer },
                            });
                            setAnswer("");
                        }}
                        style={{ marginLeft: 8 }}
                    >
                        Submit
                    </button>
                </div>
            )}

            {/* WAITING TEAMS */}
            {role === "TEAM" && !isMyTurn && (
                <p>⏳ Waiting for your turn…</p>
            )}

            {/* HOST / VIEWER */}
            {role !== "TEAM" && (
                <p>👀 Watching rapid-fire round…</p>
            )}
        </div>
    );
}