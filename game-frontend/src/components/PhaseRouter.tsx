// src/components/PhaseRouter.tsx
import { useGameStore } from "../store/gameStore";

import Join from "../screens/Join";
import Lobby from "../screens/Lobby";
import Round1 from "../screens/Round1";
import Round2 from "../screens/Round2";
import Round3 from "../screens/Round3";
import Results from "../screens/Results";

export default function PhaseRouter() {
    const gameId = useGameStore((s) => s.gameId);
    const phase = useGameStore((s) => s.phase);

    // Not connected yet
    if (!gameId) {
        return <Join />;
    }

    switch (phase) {
        case "TEAM_FORMATION":
            return <Lobby />;

        case "ROUND1":
            return <Round1 />;

        case "ROUND2":
            return <Round2 />;

        case "ROUND3":
            return <Round3 />;

        case "GAME_OVER":
            return <Results />;

        default:
            return (
                <div>
                    <h2>Unknown Phase</h2>
                    <pre>{phase}</pre>
                </div>
            );
    }
}