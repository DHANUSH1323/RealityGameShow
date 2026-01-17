package com.realityGameShow.game.engine.ai;

import com.realityGameShow.game.engine.model.Question;
import org.springframework.stereotype.Component;

@Component
public class SimpleAIHost implements AIHost {

    @Override
    public Question generateQuestion(int round) {

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
    public boolean validateAnswer(
            Question question,
            String answer
    ) {
        return question
                .getCorrectAnswer()
                .equalsIgnoreCase(answer.trim());
    }
}