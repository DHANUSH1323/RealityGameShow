// src/screens/Round2.tsx
import { useState } from "react";
import { useGameStore } from "../store/gameStore";
import Scoreboard from "../components/Scoreboard";
import Timer from "../components/Timer";

export default function Round2() {
    const role = useGameStore((s) => s.role);
    const teamId = useGameStore((s) => s.teamId);
    const activeTeamId = useGameStore((s) => s.activeTeamId);
    const gameId = useGameStore((s) => s.gameId);
    const sendEvent = useGameStore((s) => s.sendEvent);

    const [answer, setAnswer] = useState("");

    const isMyTurn = role === "TEAM" && teamId === activeTeamId;

    return (
        <div>
            <h2>Round 2</h2>

            <Scoreboard />

            <hr />

            <h3>Current Turn</h3>
            <p>
                {activeTeamId
                    ? `🎯 ${activeTeamId}'s turn`
                    : "⏳ Waiting for next team"}
            </p>

            <Timer />

            {/* TEAM CONTROLS */}
            {role === "TEAM" && isMyTurn && (
                <div style={{ marginTop: 16 }}>
                    <input
                        type="text"
                        placeholder="Your answer"
                        value={answer}
                        onChange={(e) => setAnswer(e.target.value)}
                    />

                    <button
                        onClick={() => {
                            sendEvent({
                                gameId: gameId!,
                                eventType: "SUBMIT_ANSWER_R2",
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

            {/* WAITING */}
            {role === "TEAM" && !isMyTurn && (
                <p>⏳ Waiting for your turn…</p>
            )}

            {/* HOST / VIEWER */}
            {role !== "TEAM" && (
                <p>👀 Watching teams answer…</p>
            )}
        </div>
    );
}