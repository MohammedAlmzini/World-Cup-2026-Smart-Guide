package com.ahmmedalmzini783.wcguide.ui.chatbot;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ahmmedalmzini783.wcguide.data.model.AiResponse;
import com.ahmmedalmzini783.wcguide.data.repo.AiRepository;
import com.ahmmedalmzini783.wcguide.util.Resource;

import java.util.ArrayList;
import java.util.List;

public class ChatbotViewModel extends AndroidViewModel {

    private final AiRepository aiRepository;

    private final MutableLiveData<List<ChatMessage>> chatMessages = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Resource<String>> lastResponse = new MutableLiveData<>();

    public ChatbotViewModel(@NonNull Application application) {
        super(application);
        aiRepository = new AiRepository();

        // Add welcome message
        addWelcomeMessage();
    }

    private void addWelcomeMessage() {
        List<ChatMessage> messages = chatMessages.getValue();
        if (messages != null) {
            messages.add(new ChatMessage(
                    "assistant",
                    "مرحباً بك في مرشد كأس العالم 2026! كيف يمكنني مساعدتك اليوم؟\n\nWelcome to World Cup 2026 Smart Guide! How can I help you today?",
                    System.currentTimeMillis()
            ));
            chatMessages.setValue(messages);
        }
    }

    public void sendMessage(String userMessage) {
        if (userMessage.trim().isEmpty()) return;

        // Add user message
        addMessage("user", userMessage);

        // Show loading
        isLoading.setValue(true);

        // Send to AI
        aiRepository.askQuestion(userMessage).observeForever(resource -> {
            isLoading.setValue(false);

            if (resource != null) {
                switch (resource.getStatus()) {
                    case SUCCESS:
                        if (resource.getData() != null) {
                            addMessage("assistant", resource.getData());
                            lastResponse.setValue(Resource.success(resource.getData()));
                        }
                        break;
                    case ERROR:
                        String errorMessage = resource.getMessage() != null ?
                                resource.getMessage() : "عذراً، حدث خطأ. يرجى المحاولة مرة أخرى.\n\nSorry, an error occurred. Please try again.";
                        addMessage("assistant", errorMessage);
                        lastResponse.setValue(Resource.error(errorMessage, null));
                        break;
                }
            }
        });
    }

    public void generateDailyPlan(String city, int hours, String interests) {
        isLoading.setValue(true);

        aiRepository.generateDailyPlan(city, hours, interests).observeForever(resource -> {
            isLoading.setValue(false);

            if (resource != null) {
                switch (resource.getStatus()) {
                    case SUCCESS:
                        if (resource.getData() != null && !resource.getData().isEmpty()) {
                            StringBuilder planMessage = new StringBuilder();
                            planMessage.append("إليك خطة يومية مقترحة لـ ").append(city).append(":\n\n");
                            planMessage.append("Here's a suggested daily plan for ").append(city).append(":\n\n");

                            for (int i = 0; i < resource.getData().size(); i++) {
                                AiResponse.DailyPlanItem item = resource.getData().get(i);
                                planMessage.append(i + 1).append(". ")
                                        .append(item.getName()).append("\n")
                                        .append("   ⏱️ ").append(item.getDurationMin()).append(" minutes\n")
                                        .append("   💡 ").append(item.getWhy()).append("\n\n");
                            }

                            addMessage("assistant", planMessage.toString());
                            lastResponse.setValue(Resource.success(planMessage.toString()));
                        }
                        break;
                    case ERROR:
                        String errorMessage = "عذراً، لم أتمكن من إنشاء خطة يومية. يرجى المحاولة مرة أخرى.\n\nSorry, I couldn't generate a daily plan. Please try again.";
                        addMessage("assistant", errorMessage);
                        lastResponse.setValue(Resource.error(errorMessage, null));
                        break;
                }
            }
        });
    }

    public void translateText(String text, String targetLanguage) {
        isLoading.setValue(true);

        aiRepository.translateText(text, targetLanguage).observeForever(resource -> {
            isLoading.setValue(false);

            if (resource != null) {
                switch (resource.getStatus()) {
                    case SUCCESS:
                        if (resource.getData() != null) {
                            String translationMessage = "الترجمة / Translation:\n\n" + resource.getData().getTranslatedText();
                            addMessage("assistant", translationMessage);
                            lastResponse.setValue(Resource.success(translationMessage));
                        }
                        break;
                    case ERROR:
                        String errorMessage = "عذراً، لم أتمكن من ترجمة النص. يرجى المحاولة مرة أخرى.\n\nSorry, I couldn't translate the text. Please try again.";
                        addMessage("assistant", errorMessage);
                        lastResponse.setValue(Resource.error(errorMessage, null));
                        break;
                }
            }
        });
    }

    public void startVoiceInput() {
        // This method is called when voice input starts
        // The actual speech recognition is handled in the Fragment
    }

    private void addMessage(String role, String content) {
        List<ChatMessage> messages = chatMessages.getValue();
        if (messages != null) {
            messages.add(new ChatMessage(role, content, System.currentTimeMillis()));
            chatMessages.setValue(messages);
        }
    }

    public void clearChat() {
        chatMessages.setValue(new ArrayList<>());
        addWelcomeMessage();
    }

    // Getters
    public LiveData<List<ChatMessage>> getChatMessages() {
        return chatMessages;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Resource<String>> getLastResponse() {
        return lastResponse;
    }

    // Chat message class
    public static class ChatMessage {
        private final String role; // "user" or "assistant"
        private final String content;
        private final long timestamp;

        public ChatMessage(String role, String content, long timestamp) {
            this.role = role;
            this.content = content;
            this.timestamp = timestamp;
        }

        public String getRole() { return role; }
        public String getContent() { return content; }
        public long getTimestamp() { return timestamp; }

        public boolean isFromUser() {
            return "user".equals(role);
        }
    }
}