package com.realityGameShow.game.engine.ai;

import com.realityGameShow.game.engine.model.Question;

public interface AIHost {

    Question generateQuestion(int round);

    boolean validateAnswer(
            Question question,
            String answer
    );
}