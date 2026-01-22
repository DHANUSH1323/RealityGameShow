// src/screens/Round1.tsx
import { useState } from "react";
import { useGameStore } from "../store/gameStore";
import Scoreboard from "../components/Scoreboard";

export default function Round1() {
    const role = useGameStore((s) => s.role);
    const teamId = useGameStore((s) => s.teamId);
    const gameId = useGameStore((s) => s.gameId);
    const activeTeamId = useGameStore((s) => s.activeTeamId);
    const sendEvent = useGameStore((s) => s.sendEvent);

    const [answer, setAnswer] = useState("");

    const isMyTurn = role === "TEAM" && teamId === activeTeamId;

    return (
        <div>
            <h2>Round 1</h2>

            <Scoreboard />

            <hr />

            <h3>Question</h3>
            <p>📣 Question is live! Buzz to answer.</p>

            {/* TEAM VIEW */}
            {role === "TEAM" && (
                <>
                    {/* BUZZ BUTTON */}
                    {!activeTeamId && (
                        <button
                            onClick={() =>
                                sendEvent({
                                    gameId: gameId!,
                                    eventType: "BUZZ",
                                })
                            }
                        >
                            🔴 BUZZ
                        </button>
                    )}

                    {/* ANSWER INPUT */}
                    {isMyTurn && (
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
                                        eventType: "SUBMIT_ANSWER_R1",
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
                    {activeTeamId && !isMyTurn && (
                        <p>⏳ Waiting for {activeTeamId} to answer…</p>
                    )}
                </>
            )}

            {/* HOST / VIEWER */}
            {role !== "TEAM" && (
                <p>👀 Watching teams buzz and answer…</p>
            )}
        </div>
    );
}