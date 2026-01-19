package com.realityGameShow.game.engine.ai;

import com.realityGameShow.game.engine.ai.gemini.GeminiService;
import com.realityGameShow.game.engine.model.Question;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LLM-based AI Host implementation for generating questions and validating answers.
 * 
 * This implementation uses Gemini API to:
 * - Generate dynamic questions for each round
 * - Validate answers with semantic understanding
 */
@Component
@Primary
public class LLMAIHost implements AIHost {

    private final GeminiService geminiService;

    public LLMAIHost(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @Override
    public Question generateQuestion(int round) {
        return generateQuestion(round, null);
    }

    @Override
    public Question generateQuestion(int round, String category) {
        String prompt = buildQuestionPrompt(round, category);
        String response = geminiService.generateContentSync(prompt);
        return parseQuestionFromResponse(response, round);
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
    public boolean validateAnswer(Question question, String answer) {
        String prompt = buildValidationPrompt(question, answer);
        String response = geminiService.generateContentSync(prompt);
        return parseValidationFromResponse(response);
    }

    /**
     * Builds a prompt for generating questions.
     */
    private String buildQuestionPrompt(int round, String category) {
        String difficulty = switch (round) {
            case 1 -> "easy to medium";
            case 2 -> "medium";
            case 3 -> "medium to hard";
            default -> "medium";
        };
        
        int points = switch (round) {
            case 1 -> 10;
            case 2 -> 5;
            case 3 -> 10;
            default -> 10;
        };
        
        String categoryText = (category != null && !category.isEmpty()) 
                ? String.format(" in the category of '%s'", category)
                : "";
        
        return String.format(
            "Generate a trivia question for round %d of a game show%s. " +
            "The question should be %s difficulty. " +
            "The question should be interesting and engaging. " +
            "Return your response in the following JSON format (no markdown, just JSON): " +
            "{\"question\": \"the question text\", \"answer\": \"the correct answer\", \"points\": %d}",
            round, categoryText, difficulty, points
        );
    }

    /**
     * Parses categories from Gemini response.
     * NOTE: This method is now unused since we use fixed categories,
     * but kept for potential future use.
     */
    private List<String> parseCategoriesFromResponse(String response) {
        try {
            // Extract JSON array from response
            String json = extractJsonFromResponse(response);
            
            // Remove brackets and split by comma
            json = json.trim();
            if (json.startsWith("[")) {
                json = json.substring(1);
            }
            if (json.endsWith("]")) {
                json = json.substring(0, json.length() - 1);
            }
            
            // Split by comma and clean up
            String[] categories = json.split(",");
            List<String> result = new ArrayList<>();
            
            for (String cat : categories) {
                String cleaned = cat.trim()
                        .replace("\"", "")
                        .replace("'", "")
                        .trim();
                if (!cleaned.isEmpty()) {
                    result.add(cleaned);
                }
            }
            
            // Fallback if parsing fails
            if (result.isEmpty()) {
                return getAvailableCategories(1); // Use fixed categories as fallback
            }
            
            return result;
        } catch (Exception e) {
            // Fallback to fixed categories
            return getAvailableCategories(1);
        }
    }

    /**
     * Parses the Gemini response to extract question details.
     */
    private Question parseQuestionFromResponse(String response, int round) {
        try {
            // Extract JSON from response (might have extra text)
            String json = extractJsonFromResponse(response);
            
            // Parse JSON manually or use a simple regex approach
            String questionText = extractField(json, "question");
            String answer = extractField(json, "answer");
            String pointsStr = extractField(json, "points");
            
            int points = 10; // default
            try {
                points = Integer.parseInt(pointsStr);
            } catch (NumberFormatException e) {
                // Use default points based on round
                points = switch (round) {
                    case 1 -> 10;
                    case 2 -> 5;
                    case 3 -> 10;
                    default -> 10;
                };
            }
            
            // Clean up the extracted values
            questionText = cleanJsonValue(questionText);
            answer = cleanJsonValue(answer);
            
            if (questionText == null || questionText.isEmpty() || 
                answer == null || answer.isEmpty()) {
                // Fallback to placeholder if parsing fails
                return generateQuestionPlaceholder(round);
            }
            
            return new Question(questionText, answer, points);
        } catch (Exception e) {
            // Fallback to placeholder if parsing fails
            return generateQuestionPlaceholder(round);
        }
    }

    /**
     * Extracts JSON from response (removes markdown code blocks if present).
     */
    private String extractJsonFromResponse(String response) {
        if (response == null) {
            return "{}";
        }
        
        // Remove markdown code blocks if present
        response = response.replaceAll("```json\\s*", "");
        response = response.replaceAll("```\\s*", "");
        response = response.trim();
        
        // Find JSON object
        int start = response.indexOf("{");
        int end = response.lastIndexOf("}");
        
        if (start >= 0 && end > start) {
            return response.substring(start, end + 1);
        }
        
        return response;
    }

    /**
     * Extracts a field value from JSON string using simple regex.
     */
    private String extractField(String json, String fieldName) {
        if (json == null) {
            return null;
        }
        
        // Simple regex to extract field value
        String pattern = "\"" + fieldName + "\"\\s*:\\s*\"([^\"]+)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        
        if (m.find()) {
            return m.group(1);
        }
        
        return null;
    }

    /**
     * Cleans JSON value (removes escape characters).
     */
    private String cleanJsonValue(String value) {
        if (value == null) {
            return null;
        }
        return value.replace("\\\"", "\"")
                    .replace("\\n", " ")
                    .replace("\\t", " ")
                    .trim();
    }

    /**
     * Fallback method for question generation if API fails.
     */
    private Question generateQuestionPlaceholder(int round) {
        return switch (round) {
            case 1 -> new Question(
                    "What is the capital of France?",
                    "Paris",
                    10
            );
            case 2 -> new Question(
                    "What is 15 * 7?",
                    "105",
                    5
            );
            case 3 -> new Question(
                    "What does API stand for?",
                    "Application Programming Interface",
                    10
            );
            default -> throw new IllegalArgumentException(
                    "Unsupported round: " + round
            );
        };
    }

    /**
     * Builds a prompt for validating answers.
     */
    private String buildValidationPrompt(Question question, String answer) {
        return String.format(
            "Question: %s\n" +
            "Correct Answer: %s\n" +
            "Given Answer: %s\n\n" +
            "Is the given answer correct? Consider synonyms, semantic similarity, and variations in wording. " +
            "Respond with ONLY the word 'true' or 'false' (lowercase, no punctuation, no explanation).",
            question.getText(),
            question.getCorrectAnswer(),
            answer
        );
    }

    /**
     * Parses the validation response from Gemini.
     */
    private boolean parseValidationFromResponse(String response) {
        if (response == null) {
            return false;
        }
        
        String lowerResponse = response.toLowerCase().trim();
        
        // Check for true/false in response
        if (lowerResponse.contains("true")) {
            return true;
        } else if (lowerResponse.contains("false")) {
            return false;
        }
        
        // Default to false if unclear
        return false;
    }
}
