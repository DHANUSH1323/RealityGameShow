package com.realityGameShow.game.engine.persistence;

import com.realityGameShow.game.engine.model.GameState;
import com.realityGameShow.game.engine.model.Question;
import com.realityGameShow.game.engine.ws.dto.GameStateSnapshot;

import java.util.stream.Collectors;

public class GameStateMapper {

    public static GameStateSnapshot toSnapshot(GameState state) {
        GameStateSnapshot s = new GameStateSnapshot();

        s.setGameId(state.getGame().getGameId());
        s.setPhase(state.getGame().getPhase().name());
        s.setCurrentRound(state.getGame().getCurrentRound());
        s.setActiveTeamId(state.getActiveTeamId());

        s.setTeamScores(
                state.getTeams().values().stream()
                        .collect(Collectors.toMap(
                                t -> t.getTeamId(),
                                t -> t.getScore()
                        ))
        );

        if (state.getCurrentQuestion() != null) {
            s.setQuestionText(state.getCurrentQuestion().getText());
            s.setCorrectAnswer(state.getCurrentQuestion().getCorrectAnswer());
            s.setQuestionPoints(state.getCurrentQuestion().getPoints());
            s.setCategory(state.getSelectedCategory());
        }

        s.setPendingCategoryRound(state.getPendingCategoryRound());
        s.setSelectedCategory(state.getSelectedCategory());

        return s;
    }

    // reverse mapping comes later (recovery)
}