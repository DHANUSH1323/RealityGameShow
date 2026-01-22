import { useGameStore } from "../store/gameStore";

export default function CategorySelector() {
    const categories = useGameStore((s) => s.categories);
    const currentRound = useGameStore((s) => s.currentRound);
    const gameId = useGameStore((s) => s.gameId);
    const sendEvent = useGameStore((s) => s.sendEvent);

    if (!categories.length) return null;

    const eventMap: Record<number, string> = {
        1: "SELECT_CATEGORY_R1",
        2: "SELECT_CATEGORY_R2",
        3: "SELECT_CATEGORY_R3",
    };

    return (
        <div>
            <h3>Select Category</h3>
            {categories.map((c) => (
                <button
                    key={c}
                    onClick={() =>
                        sendEvent({
                            gameId: gameId!,
                            eventType: eventMap[currentRound + 1] as any,
                            payload: { category: c },
                        })
                    }
                >
                    {c}
                </button>
            ))}
        </div>
    );
}