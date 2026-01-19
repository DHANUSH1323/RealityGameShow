package com.realityGameShow.game.engine.ai;

import com.realityGameShow.game.engine.model.Question;

import java.util.List;

public interface AIHost {

    Question generateQuestion(int round);
    
    Question generateQuestion(int round, String category);

    List<String> getAvailableCategories(int round);

    boolean validateAnswer(
            Question question,
            String answer
    );
}