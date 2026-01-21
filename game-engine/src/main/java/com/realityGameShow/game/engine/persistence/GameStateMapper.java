package com.realityGameShow.game.engine.persistence;

import com.realityGameShow.game.engine.model.*;
import com.realityGameShow.game.engine.ws.dto.GameStateSnapshot;

import java.util.HashMap;
import java.util.Map;

public class GameStateMapper {

    public static GameStateSnapshot toSnapshot(GameState gameState) {

        Map<String, Integer> scores = new HashMap<>();
        gameState.getTeams().forEach(
                (id, team) -> scores.put(id, team.getScore())
        );

        Question q = gameState.getCurrentQuestion();

        return new GameStateSnapshot(
                gameState.getGame().getGameId(),
                gameState.getGame().getPhase().name(),
                gameState.getGame().getCurrentRound(),
                gameState.getActiveTeamId(),
                gameState.getPendingCategoryRound(),
                gameState.getSelectedCategory(),
                scores,
                q != null ? q.getText() : null,
                q != null ? q.getCorrectAnswer() : null,
                q != null ? q.getPoints() : 0
        );
    }

    public static GameState fromSnapshot(GameStateSnapshot snapshot) {

        Game game = new Game(snapshot.getGameId());
        game.setPhase(GamePhase.valueOf(snapshot.getPhase()));
        game.setCurrentRound(snapshot.getCurrentRound());

        GameState gameState = new GameState(game);

        snapshot.getTeamScores().forEach((id, score) -> {
            Team team = new Team(id,"Team " + id, 0);
            team.setScore(score);
            gameState.addTeam(team);
        });

        gameState.setActiveTeamId(snapshot.getActiveTeamId());
        gameState.setPendingCategoryRound(snapshot.getPendingCategoryRound());
        gameState.setSelectedCategory(snapshot.getSelectedCategory());

        if (snapshot.getQuestionText() != null) {
            Question q = new Question(
                    snapshot.getQuestionText(),
                    snapshot.getCorrectAnswer(),
                    snapshot.getQuestionPoints()
            );
            gameState.setCurrentQuestion(q);
        }

        return gameState;
    }
}