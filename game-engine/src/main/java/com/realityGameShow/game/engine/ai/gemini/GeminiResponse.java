package com.realityGameShow.game.engine.ai.gemini;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class GeminiResponse {
    
    @JsonProperty("candidates")
    private List<Candidate> candidates;

    public GeminiResponse() {}

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }

    public String getText() {
        if (candidates != null && !candidates.isEmpty()) {
            Candidate candidate = candidates.get(0);
            if (candidate != null && candidate.getContent() != null) {
                List<Part> parts = candidate.getContent().getParts();
                if (parts != null && !parts.isEmpty()) {
                    return parts.get(0).getText();
                }
            }
        }
        return null;
    }

    public static class Candidate {
        @JsonProperty("content")
        private Content content;

        public Candidate() {}

        public Content getContent() {
            return content;
        }

        public void setContent(Content content) {
            this.content = content;
        }
    }

    public static class Content {
        @JsonProperty("parts")
        private List<Part> parts;

        public Content() {}

        public List<Part> getParts() {
            return parts;
        }

        public void setParts(List<Part> parts) {
            this.parts = parts;
        }
    }

    public static class Part {
        @JsonProperty("text")
        private String text;

        public Part() {}

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
