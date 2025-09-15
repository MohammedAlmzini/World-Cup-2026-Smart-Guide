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
                    "🤖 مرحباً بك في مرشد كأس العالم 2026! \n\n" +
                    "أنا مساعدك الذكي المطور بتقنية Gemini AI. كيف يمكنني مساعدتك اليوم؟\n\n" +
                    "يمكنك أن تسألني عن:\n" +
                    "🏆 معلومات كأس العالم 2026\n" +
                    "🌍 المدن والملاعب المضيفة\n" +
                    "🏨 الفنادق والإقامة\n" +
                    "🎫 التذاكر والحجوزات\n\n" +
                    "🤖 Welcome to World Cup 2026 Smart Guide!\n\n" +
                    "I'm your AI assistant powered by Gemini AI. How can I help you today?",
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
                        String errorMessage = getFormattedErrorMessage(resource.getMessage());
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
                        String errorMessage = getFormattedErrorMessage(resource.getMessage());
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
                        String errorMessage = getFormattedErrorMessage(resource.getMessage());
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

    public void addBotMessage(String content) {
        addMessage("assistant", content);
    }

    public void addUserMessage(String content) {
        addMessage("user", content);
    }

    public void clearChat() {
        chatMessages.setValue(new ArrayList<>());
        addWelcomeMessage();
    }

    private String getFormattedErrorMessage(String originalError) {
        if (originalError == null) {
            return "عذراً، حدث خطأ. يرجى المحاولة مرة أخرى.\n\nSorry, an error occurred. Please try again.";
        }

        // Handle API 404 errors specifically
        if (originalError.contains("404") || originalError.contains("API error: 404")) {
            return "⚠️ عذراً، خدمة Gemini AI غير متوفرة حالياً.\n\n" +
                   "تم تفعيل النظام البديل - يمكنك السؤال عن:\n" +
                   "• المعالم السياحية في المدن\n" +
                   "• معلومات عن كأس العالم 2026\n" +
                   "• الفنادق والمطاعم\n" +
                   "• التذاكر والحجوزات\n\n" +
                   "🌍 Sorry, Gemini AI service is temporarily unavailable.\n\n" +
                   "Fallback system activated - you can ask about:\n" +
                   "• Tourist attractions in cities\n" +
                   "• World Cup 2026 information\n" +
                   "• Hotels and restaurants\n" +
                   "• Tickets and bookings";
        }

        // Handle network errors
        if (originalError.contains("Network error") || originalError.contains("timeout")) {
            return "🌐 مشكلة في الاتصال بالإنترنت.\n\n" +
                   "يرجى التحقق من اتصالك والمحاولة مرة أخرى.\n\n" +
                   "🌐 Internet connection problem.\n\n" +
                   "Please check your connection and try again.";
        }

        // Handle API key errors
        if (originalError.contains("No AI API key configured") || originalError.contains("401") || originalError.contains("403")) {
            return "🔑 خدمة Gemini AI غير مفعلة حالياً.\n\n" +
                   "تم تفعيل النظام البديل - يمكنك استخدام الميزات الأخرى في التطبيق.\n\n" +
                   "🔑 Gemini AI service is not activated.\n\n" +
                   "Fallback system activated - you can use other app features.";
        }

        // Default error message
        return "عذراً، حدث خطأ. يرجى المحاولة مرة أخرى.\n\n" +
               "إذا استمرت المشكلة، تأكد من اتصالك بالإنترنت.\n\n" +
               "Sorry, an error occurred. Please try again.\n\n" +
               "If the problem persists, check your internet connection.";
    }

    // Getters
    public LiveData<List<ChatMessage>> getChatMessages() {
        return chatMessages;
    }

    public LiveData<List<ChatMessage>> getMessages() {
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