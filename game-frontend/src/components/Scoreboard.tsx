import { useGameStore } from "../store/gameStore";

export default function Scoreboard() {
    const scores = useGameStore((s) => s.scores);

    return (
        <div>
            <h3>Scores</h3>
            {Object.entries(scores).map(([team, score]) => (
                <div key={team}>
                    {team}: {score}
                </div>
            ))}
        </div>
    );
}