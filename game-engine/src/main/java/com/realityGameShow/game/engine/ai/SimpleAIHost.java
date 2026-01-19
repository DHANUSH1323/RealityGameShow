package com.realityGameShow.game.engine.ai;

import com.realityGameShow.game.engine.model.Question;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SimpleAIHost implements AIHost {

    @Override
    public Question generateQuestion(int round) {
        return generateQuestion(round, null);
    }

    @Override
    public Question generateQuestion(int round, String category) {

        return switch (round) {
            case 1 -> new Question(
                    "What is 2 + 2?",
                    "4",
                    10
            );

            case 2 -> new Question(
                    "What is the capital of France?",
                    "Paris",
                    5
            );

            case 3 -> new Question(
                    "What does JVM stand for?",
                    "Java Virtual Machine",
                    10
            );

            default -> throw new IllegalArgumentException(
                    "Unsupported round"
            );
        };
    }

    @Override
    public List<String> getAvailableCategories(int round) {
        // All rounds now use the same 4 fixed categories
        return Arrays.asList(
                "General Knowledge",
                "Science and Tech",
                "Entertainment and Pop Culture",
                "Sport and Games"
        );
    }

    @Override
    public boolean validateAnswer(
            Question question,
            String answer
    ) {
        return question
                .getCorrectAnswer()
                .equalsIgnoreCase(answer.trim());
    }
}