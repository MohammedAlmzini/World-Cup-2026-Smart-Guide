package com.ahmmedalmzini783.wcguide.data.model;

import java.util.List;

public class AiResponse {

    public static class ChatMessage {
        private String role; // "user" or "assistant"
        private String content;
        private long timestamp;

        public ChatMessage() {}

        public ChatMessage(String role, String content, long timestamp) {
            this.role = role;
            this.content = content;
            this.timestamp = timestamp;
        }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    public static class DailyPlanItem {
        private String name;
        private String why; // Brief reason for recommendation
        private int durationMin; // Estimated duration in minutes
        private String mapsQuery; // Query for Google Maps

        public DailyPlanItem() {}

        public DailyPlanItem(String name, String why, int durationMin, String mapsQuery) {
            this.name = name;
            this.why = why;
            this.durationMin = durationMin;
            this.mapsQuery = mapsQuery;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getWhy() { return why; }
        public void setWhy(String why) { this.why = why; }

        public int getDurationMin() { return durationMin; }
        public void setDurationMin(int durationMin) { this.durationMin = durationMin; }

        public String getMapsQuery() { return mapsQuery; }
        public void setMapsQuery(String mapsQuery) { this.mapsQuery = mapsQuery; }
    }

    public static class TranslationResponse {
        private String originalText;
        private String translatedText;
        private String sourceLang;
        private String targetLang;

        public TranslationResponse() {}

        public TranslationResponse(String originalText, String translatedText,
                                   String sourceLang, String targetLang) {
            this.originalText = originalText;
            this.translatedText = translatedText;
            this.sourceLang = sourceLang;
            this.targetLang = targetLang;
        }

        public String getOriginalText() { return originalText; }
        public void setOriginalText(String originalText) { this.originalText = originalText; }

        public String getTranslatedText() { return translatedText; }
        public void setTranslatedText(String translatedText) { this.translatedText = translatedText; }

        public String getSourceLang() { return sourceLang; }
        public void setSourceLang(String sourceLang) { this.sourceLang = sourceLang; }

        public String getTargetLang() { return targetLang; }
        public void setTargetLang(String targetLang) { this.targetLang = targetLang; }
    }
}