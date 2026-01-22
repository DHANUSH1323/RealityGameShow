// src/screens/Results.tsx
import { useGameStore } from "../store/gameStore";
import Scoreboard from "../components/Scoreboard";

export default function Results() {
    const scores = useGameStore((s) => s.scores);

    const sortedTeams = Object.entries(scores).sort(
        (a, b) => b[1] - a[1]
    );

    const winner = sortedTeams.length > 0 ? sortedTeams[0] : null;

    return (
        <div>
            <h2>🏁 Game Over</h2>

            {winner && (
                <h3 style={{ color: "green" }}>
                    🏆 Winner: {winner[0]} ({winner[1]} pts)
                </h3>
            )}

            <Scoreboard />

            <hr />

            <button
                onClick={() => window.location.reload()}
                style={{ marginTop: 12 }}
            >
                Start New Game
            </button>
        </div>
    );
}