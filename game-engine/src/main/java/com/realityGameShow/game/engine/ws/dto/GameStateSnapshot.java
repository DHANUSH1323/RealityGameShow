// package com.realityGameShow.game.engine.ws.dto;

// import java.util.Map;

// public class GameStateSnapshot {

//     private String gameId;
//     private String phase;
//     private int currentRound;
//     private String activeTeamId;
//     private Map<String, Integer> teamScores;

//     private String questionText;
//     private String correctAnswer;
//     private int questionPoints;
//     private String category;

//     private String pendingCategoryRound;
//     private String selectedCategory;

//     public GameStateSnapshot() {}

//     // ---------- getters & setters ----------

//     public String getGameId() {
//         return gameId;
//     }

//     public void setGameId(String gameId) {
//         this.gameId = gameId;
//     }

//     public String getPhase() {
//         return phase;
//     }

//     public void setPhase(String phase) {
//         this.phase = phase;
//     }

//     public int getCurrentRound() {
//         return currentRound;
//     }

//     public void setCurrentRound(int currentRound) {
//         this.currentRound = currentRound;
//     }

//     public String getActiveTeamId() {
//         return activeTeamId;
//     }

//     public void setActiveTeamId(String activeTeamId) {
//         this.activeTeamId = activeTeamId;
//     }

//     public Map<String, Integer> getTeamScores() {
//         return teamScores;
//     }

//     public void setTeamScores(Map<String, Integer> teamScores) {
//         this.teamScores = teamScores;
//     }

//     public String getQuestionText() {
//         return questionText;
//     }

//     public void setQuestionText(String questionText) {
//         this.questionText = questionText;
//     }

//     public String getCorrectAnswer() {
//         return correctAnswer;
//     }

//     public void setCorrectAnswer(String correctAnswer) {
//         this.correctAnswer = correctAnswer;
//     }

//     public int getQuestionPoints() {
//         return questionPoints;
//     }

//     public void setQuestionPoints(int questionPoints) {
//         this.questionPoints = questionPoints;
//     }

//     public String getCategory() {
//         return category;
//     }

//     public void setCategory(String category) {
//         this.category = category;
//     }

//     public String getPendingCategoryRound() {
//         return pendingCategoryRound;
//     }

//     public void setPendingCategoryRound(String pendingCategoryRound) {
//         this.pendingCategoryRound = pendingCategoryRound;
//     }

//     public String getSelectedCategory() {
//         return selectedCategory;
//     }

//     public void setSelectedCategory(String selectedCategory) {
//         this.selectedCategory = selectedCategory;
//     }
// }








package com.realityGameShow.game.engine.ws.dto;

import java.util.Map;

public class GameStateSnapshot {

    private String gameId;
    private String phase;
    private int currentRound;
    private String activeTeamId;
    private String pendingCategoryRound;
    private String selectedCategory;

    private Map<String, Integer> teamScores;

    private String questionText;
    private String correctAnswer;
    private int questionPoints;

    // ✅ NEW: timestamps for frontend timer display
    private Long round2TurnStartTime;
    private Long round3QuestionStartTime;

    public GameStateSnapshot(
            String gameId,
            String phase,
            int currentRound,
            String activeTeamId,
            String pendingCategoryRound,
            String selectedCategory,
            Map<String, Integer> teamScores,
            String questionText,
            String correctAnswer,
            int questionPoints,
            Long round2TurnStartTime,
            Long round3QuestionStartTime
    ) {
        this.gameId = gameId;
        this.phase = phase;
        this.currentRound = currentRound;
        this.activeTeamId = activeTeamId;
        this.pendingCategoryRound = pendingCategoryRound;
        this.selectedCategory = selectedCategory;
        this.teamScores = teamScores;
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.questionPoints = questionPoints;
        this.round2TurnStartTime = round2TurnStartTime;
        this.round3QuestionStartTime = round3QuestionStartTime;
    }

    public String getGameId() { return gameId; }
    public String getPhase() { return phase; }
    public int getCurrentRound() { return currentRound; }
    public String getActiveTeamId() { return activeTeamId; }
    public String getPendingCategoryRound() { return pendingCategoryRound; }
    public String getSelectedCategory() { return selectedCategory; }
    public Map<String, Integer> getTeamScores() { return teamScores; }
    public String getQuestionText() { return questionText; }
    public String getCorrectAnswer() { return correctAnswer; }
    public int getQuestionPoints() { return questionPoints; }

    public Long getRound2TurnStartTime() { return round2TurnStartTime; }
    public Long getRound3QuestionStartTime() { return round3QuestionStartTime; }
}