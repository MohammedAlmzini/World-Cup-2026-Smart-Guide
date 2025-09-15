package com.ahmmedalmzini783.wcguide.data.remote;

import android.util.Log;
import androidx.annotation.NonNull;

import com.ahmmedalmzini783.wcguide.BuildConfig;
import com.ahmmedalmzini783.wcguide.data.model.AiResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AiApiClient {
    private static final String TAG = "AiApiClient";

    // API endpoints
    private static final String OPENAI_BASE_URL = "https://api.openai.com/v1/";
    private static final String GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/";

    private final OkHttpClient client;
    private final Gson gson;

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    public AiApiClient() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        gson = new Gson();
    }

    // General chat/question answering
    public void askQuestion(String question, ApiCallback<String> callback) {
        Log.d(TAG, "askQuestion called with: " + question);
        
        // Try primary AI service first
        if (BuildConfig.GEMINI_API_KEY != null && !BuildConfig.GEMINI_API_KEY.equals("PLACEHOLDER_GEMINI_API_KEY")) {
            Log.d(TAG, "Using Gemini API with key: " + BuildConfig.GEMINI_API_KEY.substring(0, 10) + "...");
            askGeminiDirectly(question, callback);
        } else if (BuildConfig.OPENAI_API_KEY != null && !BuildConfig.OPENAI_API_KEY.equals("PLACEHOLDER_OPENAI_API_KEY")) {
            Log.d(TAG, "Using OpenAI API");
            askOpenAIWithFallback(question, callback);
        } else {
            Log.w(TAG, "No API key available, using fallback responses");
            useFallbackResponse(question, callback);
        }
    }

    // Generate daily plan
    public void generateDailyPlan(String city, int availableHours, String interests,
                                  ApiCallback<List<AiResponse.DailyPlanItem>> callback) {
        String prompt = buildDailyPlanPrompt(city, availableHours, interests);

        if (BuildConfig.GEMINI_API_KEY != null && !BuildConfig.GEMINI_API_KEY.equals("PLACEHOLDER_GEMINI_API_KEY")) {
            askGeminiForDailyPlan(prompt, callback);
        } else if (BuildConfig.OPENAI_API_KEY != null && !BuildConfig.OPENAI_API_KEY.equals("PLACEHOLDER_OPENAI_API_KEY")) {
            askOpenAIForDailyPlan(prompt, callback);
        } else {
            callback.onError("No AI API key configured");
        }
    }

    // Translation
    public void translateText(String text, String targetLanguage,
                              ApiCallback<AiResponse.TranslationResponse> callback) {
        String prompt = String.format(
                "Translate the following text to %s. Return only the translation:\n%s",
                targetLanguage, text
        );

        askQuestion(prompt, new ApiCallback<String>() {
            @Override
            public void onSuccess(String result) {
                AiResponse.TranslationResponse response = new AiResponse.TranslationResponse();
                response.setOriginalText(text);
                response.setTranslatedText(result.trim());
                response.setTargetLang(targetLanguage);
                callback.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    private String buildDailyPlanPrompt(String city, int availableHours, String interests) {
        return String.format(
                "You are a smart travel guide for World Cup 2026 in US/CA/MX. " +
                        "User city: %s; available hours: %d; interests: %s. " +
                        "Return JSON array of 3-4 places in visiting order with brief reasons, each: " +
                        "{\"name\": \"place name\", \"why\": \"brief reason\", \"durationMin\": 60, \"mapsQuery\": \"search term for Google Maps\"}. " +
                        "Return only valid JSON array, no additional text.",
                city, availableHours, interests
        );
    }

    private void askOpenAI(String question, ApiCallback<String> callback) {
        JsonObject requestJson = new JsonObject();
        requestJson.addProperty("model", "gpt-3.5-turbo");
        requestJson.addProperty("max_tokens", 500);
        requestJson.addProperty("temperature", 0.7);

        JsonArray messages = new JsonArray();
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", question);
        messages.add(message);
        requestJson.add("messages", messages);

        RequestBody body = RequestBody.create(
                requestJson.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(OPENAI_BASE_URL + "chat/completions")
                .header("Authorization", "Bearer " + BuildConfig.OPENAI_API_KEY)
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "OpenAI API call failed", e);
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseBody = response.body().string();
                        Log.d(TAG, "OpenAI API response: " + responseBody);
                        JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();

                        if (responseJson.has("choices") && responseJson.getAsJsonArray("choices").size() > 0) {
                            JsonObject choice = responseJson.getAsJsonArray("choices").get(0).getAsJsonObject();
                            String content = choice.getAsJsonObject("message").get("content").getAsString();
                            callback.onSuccess(content);
                        } else {
                            Log.e(TAG, "Invalid OpenAI response format: " + responseBody);
                            callback.onError("Invalid response format");
                        }
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "No error body";
                        Log.e(TAG, "OpenAI API error: " + response.code() + " - " + errorBody);
                        
                        if (response.code() == 404) {
                            callback.onError("API error: 404 - Service endpoint not found");
                        } else if (response.code() == 401) {
                            callback.onError("API error: 401 - Invalid API key");
                        } else if (response.code() == 429) {
                            callback.onError("API error: 429 - Rate limit exceeded");
                        } else {
                            callback.onError("API error: " + response.code());
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing OpenAI response", e);
                    callback.onError("Parse error: " + e.getMessage());
                }
            }
        });
    }

    private void askGemini(String question, ApiCallback<String> callback) {
        Log.d(TAG, "askGemini: Starting Gemini API request");
        Log.d(TAG, "Question: " + question);
        
        JsonObject requestJson = new JsonObject();

        // Simplified configuration
        JsonObject generationConfig = new JsonObject();
        generationConfig.addProperty("temperature", 0.7);
        generationConfig.addProperty("maxOutputTokens", 1000);
        requestJson.add("generationConfig", generationConfig);

        JsonArray contents = new JsonArray();
        JsonObject content = new JsonObject();
        JsonArray parts = new JsonArray();
        JsonObject part = new JsonObject();
        
        // Simplified prompt
        String prompt = "You are a helpful assistant for World Cup 2026. Answer in both Arabic and English. Question: " + question;
        
        part.addProperty("text", prompt);
        parts.add(part);
        content.add("parts", parts);
        contents.add(content);
        requestJson.add("contents", contents);

        RequestBody body = RequestBody.create(
                requestJson.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        // Use the correct Gemini Pro model endpoint
        Request request = new Request.Builder()
                .url(GEMINI_BASE_URL + "models/gemini-pro:generateContent?key=" + BuildConfig.GEMINI_API_KEY)
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        Log.d(TAG, "Gemini API URL: " + GEMINI_BASE_URL + "models/gemini-pro:generateContent");
        Log.d(TAG, "API Key length: " + BuildConfig.GEMINI_API_KEY.length());
        Log.d(TAG, "Request body: " + requestJson.toString());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Gemini API call failed", e);
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseBody = response.body().string();
                        Log.d(TAG, "Gemini API response: " + responseBody);
                        JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();

                        if (responseJson.has("candidates") && responseJson.getAsJsonArray("candidates").size() > 0) {
                            JsonObject candidate = responseJson.getAsJsonArray("candidates").get(0).getAsJsonObject();
                            
                            // Check if content was blocked
                            if (candidate.has("finishReason") && 
                                candidate.get("finishReason").getAsString().equals("SAFETY")) {
                                Log.w(TAG, "Content was blocked by safety filters");
                                callback.onError("Content blocked by safety filters");
                                return;
                            }
                            
                            if (candidate.has("content")) {
                                JsonObject content = candidate.getAsJsonObject("content");
                                JsonArray parts = content.getAsJsonArray("parts");
                                if (parts.size() > 0) {
                                    String text = parts.get(0).getAsJsonObject().get("text").getAsString();
                                    callback.onSuccess(text);
                                } else {
                                    Log.e(TAG, "Empty parts in Gemini response: " + responseBody);
                                    callback.onError("Empty response parts");
                                }
                            } else {
                                Log.e(TAG, "No content in Gemini candidate: " + responseBody);
                                callback.onError("No content in response");
                            }
                        } else {
                            Log.e(TAG, "No candidates in Gemini response: " + responseBody);
                            callback.onError("No candidates in response");
                        }
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "No error body";
                        Log.e(TAG, "Gemini API error: " + response.code() + " - " + errorBody);
                        
                        if (response.code() == 404) {
                            callback.onError("API error: 404 - Service endpoint not found");
                        } else if (response.code() == 401 || response.code() == 403) {
                            callback.onError("API error: " + response.code() + " - Invalid API key or permissions");
                        } else if (response.code() == 429) {
                            callback.onError("API error: 429 - Rate limit exceeded. Please try again later");
                        } else if (response.code() == 400) {
                            callback.onError("API error: 400 - Bad request format");
                        } else {
                            callback.onError("API error: " + response.code() + " - " + errorBody);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing Gemini response", e);
                    callback.onError("Parse error: " + e.getMessage());
                }
            }
        });
    }

    private void askOpenAIForDailyPlan(String prompt, ApiCallback<List<AiResponse.DailyPlanItem>> callback) {
        askOpenAI(prompt, new ApiCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    // Try to parse as JSON array
                    Type listType = new TypeToken<List<AiResponse.DailyPlanItem>>(){}.getType();
                    List<AiResponse.DailyPlanItem> planItems = gson.fromJson(result, listType);
                    callback.onSuccess(planItems);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to parse daily plan JSON: " + result, e);
                    callback.onError("Failed to parse daily plan: " + e.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    private void askGeminiForDailyPlan(String prompt, ApiCallback<List<AiResponse.DailyPlanItem>> callback) {
        askGemini(prompt, new ApiCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    // Clean the response (remove markdown formatting if present)
                    String cleanedResult = result.replace("```json", "").replace("```", "").trim();

                    Type listType = new TypeToken<List<AiResponse.DailyPlanItem>>(){}.getType();
                    List<AiResponse.DailyPlanItem> planItems = gson.fromJson(cleanedResult, listType);
                    callback.onSuccess(planItems);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to parse daily plan JSON: " + result, e);
                    callback.onError("Failed to parse daily plan: " + e.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    private void askGeminiDirectly(String question, ApiCallback<String> callback) {
        Log.d(TAG, "askGeminiDirectly: Attempting Gemini API call");
        Log.d(TAG, "Question: " + question);
        
        askGemini(question, new ApiCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Gemini API success: " + result.substring(0, Math.min(100, result.length())));
                callback.onSuccess(result);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Gemini API failed: " + error);
                
                // Check if it's an API key issue
                if (error.contains("403") || error.contains("401")) {
                    Log.e(TAG, "API key issue detected - using intelligent fallback");
                    callback.onSuccess(getIntelligentResponse(question));
                } else {
                    Log.d(TAG, "Network issue - using fallback responses");
                    useFallbackResponse(question, callback);
                }
            }
        });
    }

    private void askGeminiWithFallback(String question, ApiCallback<String> callback) {
        askGemini(question, new ApiCallback<String>() {
            @Override
            public void onSuccess(String result) {
                callback.onSuccess(result);
            }

            @Override
            public void onError(String error) {
                Log.w(TAG, "Gemini API failed, using fallback: " + error);
                useFallbackResponse(question, callback);
            }
        });
    }

    private void askOpenAIWithFallback(String question, ApiCallback<String> callback) {
        askOpenAI(question, new ApiCallback<String>() {
            @Override
            public void onSuccess(String result) {
                callback.onSuccess(result);
            }

            @Override
            public void onError(String error) {
                Log.w(TAG, "OpenAI API failed, using fallback: " + error);
                useFallbackResponse(question, callback);
            }
        });
    }

    private void useFallbackResponse(String question, ApiCallback<String> callback) {
        String response = getFallbackResponse(question.toLowerCase());
        callback.onSuccess(response);
    }

    private String getIntelligentResponse(String question) {
        Log.d(TAG, "getIntelligentResponse: Analyzing question: " + question);
        
        String lowerQuestion = question.toLowerCase().trim();
        boolean isArabic = containsArabic(question);
        
        // Greeting responses
        if (isGreeting(lowerQuestion)) {
            return getGreetingResponse(isArabic);
        }
        
        // World Cup specific questions
        if (isWorldCupQuestion(lowerQuestion)) {
            return getWorldCupResponse(lowerQuestion, isArabic);
        }
        
        // Cities and travel questions
        if (isCityQuestion(lowerQuestion)) {
            return getCityResponse(lowerQuestion, isArabic);
        }
        
        // Hotel questions
        if (isHotelQuestion(lowerQuestion)) {
            return getHotelResponse(isArabic);
        }
        
        // Ticket questions
        if (isTicketQuestion(lowerQuestion)) {
            return getTicketResponse(isArabic);
        }
        
        // General help
        if (isHelpQuestion(lowerQuestion)) {
            return getHelpResponse(isArabic);
        }
        
        // Default intelligent response based on language
        return getDefaultResponse(isArabic);
    }
    
    private boolean containsArabic(String text) {
        return text.matches(".*[\u0600-\u06FF].*");
    }
    
    private boolean isGreeting(String question) {
        return question.contains("مرحب") || question.contains("سلام") || question.contains("أهلا") ||
               question.contains("hello") || question.contains("hi") || question.contains("hey") ||
               question.contains("good morning") || question.contains("good evening") ||
               question.contains("كيف حال") || question.contains("how are you");
    }
    
    private boolean isWorldCupQuestion(String question) {
        return question.contains("كأس العالم") || question.contains("world cup") || 
               question.contains("2026") || question.contains("فيفا") || question.contains("fifa") ||
               question.contains("بطولة") || question.contains("tournament") || 
               question.contains("منتخب") || question.contains("team");
    }
    
    private boolean isCityQuestion(String question) {
        return question.contains("مدن") || question.contains("مدينة") || question.contains("cities") || 
               question.contains("city") || question.contains("سياحة") || question.contains("tourism") ||
               question.contains("أماكن") || question.contains("places") || question.contains("زيارة") || 
               question.contains("visit");
    }
    
    private boolean isHotelQuestion(String question) {
        return question.contains("فندق") || question.contains("فنادق") || question.contains("hotel") || 
               question.contains("إقامة") || question.contains("accommodation") || 
               question.contains("حجز") || question.contains("booking");
    }
    
    private boolean isTicketQuestion(String question) {
        return question.contains("تذكرة") || question.contains("تذاكر") || question.contains("ticket") ||
               question.contains("تذكر") || question.contains("دخول") || question.contains("entry");
    }
    
    private boolean isHelpQuestion(String question) {
        return question.contains("مساعدة") || question.contains("help") || question.contains("معلومات") || 
               question.contains("info") || question.contains("ماذا") || question.contains("what") ||
               question.contains("كيف") || question.contains("how");
    }
    
    private String getGreetingResponse(boolean isArabic) {
        if (isArabic) {
            return "👋 أهلاً وسهلاً بك! أنا مساعدك الذكي لكأس العالم 2026.\n\n" +
                   "يمكنني مساعدتك في معرفة معلومات عن البطولة والمدن المضيفة والفنادق والتذاكر.\n\n" +
                   "كيف يمكنني مساعدتك اليوم؟ 😊";
        } else {
            return "👋 Hello and welcome! I'm your World Cup 2026 smart assistant.\n\n" +
                   "I can help you with tournament information, host cities, hotels, and tickets.\n\n" +
                   "How can I assist you today? 😊";
        }
    }
    
    private String getWorldCupResponse(String question, boolean isArabic) {
        if (isArabic) {
            return "⚽ كأس العالم 2026:\n\n" +
                   "📅 التاريخ: 11 يونيو - 19 يوليو 2026\n" +
                   "🌍 الدول المضيفة: الولايات المتحدة، كندا، المكسيك\n" +
                   "🏟️ عدد الفرق: 48 منتخباً\n" +
                   "🎯 أول بطولة بثلاث دول مضيفة\n\n" +
                   "هل تريد معرفة المزيد عن المدن المضيفة أو التذاكر؟";
        } else {
            return "⚽ FIFA World Cup 2026:\n\n" +
                   "📅 Dates: June 11 - July 19, 2026\n" +
                   "🌍 Host Countries: USA, Canada, Mexico\n" +
                   "🏟️ Teams: 48 national teams\n" +
                   "🎯 First tournament with three host countries\n\n" +
                   "Would you like to know more about host cities or tickets?";
        }
    }
    
    private String getCityResponse(String question, boolean isArabic) {
        if (isArabic) {
            return "🏙️ المدن المضيفة لكأس العالم 2026:\n\n" +
                   "🇺🇸 الولايات المتحدة:\n" +
                   "• نيويورك - مدينة الأحلام\n" +
                   "• لوس أنجلوس - مدينة الملائكة\n" +
                   "• مايامي - الشواطئ الجميلة\n" +
                   "• أتلانتا - قلب الجنوب\n\n" +
                   "🇨🇦 كندا:\n" +
                   "• تورونتو - المدينة الكوزموبوليتانية\n" +
                   "• فانكوفر - الطبيعة الخلابة\n\n" +
                   "🇲🇽 المكسيك:\n" +
                   "• مكسيكو سيتي - العاصمة التاريخية\n" +
                   "• غوادالاخارا - مهد الثقافة المكسيكية";
        } else {
            return "🏙️ World Cup 2026 Host Cities:\n\n" +
                   "🇺🇸 United States:\n" +
                   "• New York - The City That Never Sleeps\n" +
                   "• Los Angeles - City of Angels\n" +
                   "• Miami - Beautiful Beaches\n" +
                   "• Atlanta - Heart of the South\n\n" +
                   "🇨🇦 Canada:\n" +
                   "• Toronto - Cosmopolitan City\n" +
                   "• Vancouver - Stunning Nature\n\n" +
                   "🇲🇽 Mexico:\n" +
                   "• Mexico City - Historic Capital\n" +
                   "• Guadalajara - Cultural Heart of Mexico";
        }
    }
    
    private String getHotelResponse(boolean isArabic) {
        if (isArabic) {
            return "🏨 نصائح حجز الفنادق لكأس العالم 2026:\n\n" +
                   "⭐ احجز مبكراً: الأسعار أقل والخيارات أكثر\n" +
                   "📍 اختر موقع قريب من الملعب أو المواصلات\n" +
                   "💰 قارن الأسعار بين المواقع المختلفة\n" +
                   "🚌 تحقق من خيارات النقل العام\n" +
                   "📞 احجز من مواقع موثوقة فقط\n\n" +
                   "هل تريد معرفة مدن محددة للحجز؟";
        } else {
            return "🏨 Hotel Booking Tips for World Cup 2026:\n\n" +
                   "⭐ Book Early: Better prices and more options\n" +
                   "📍 Choose location near stadiums or transport\n" +
                   "💰 Compare prices across different sites\n" +
                   "🚌 Check public transportation options\n" +
                   "📞 Book only from trusted websites\n\n" +
                   "Would you like information about specific cities?";
        }
    }
    
    private String getTicketResponse(boolean isArabic) {
        if (isArabic) {
            return "🎫 معلومات تذاكر كأس العالم 2026:\n\n" +
                   "📅 ستفتح مراحل البيع تدريجياً قبل البطولة\n" +
                   "🌐 الحجز من الموقع الرسمي لفيفا فقط\n" +
                   "💳 فئات مختلفة: عادية، VIP، مجموعات عائلية\n" +
                   "⚠️ احذر من المواقع المزيفة\n" +
                   "📧 سجل في القائمة البريدية لتلقي التحديثات\n\n" +
                   "تابع الموقع الرسمي لآخر الأخبار!";
        } else {
            return "🎫 World Cup 2026 Tickets Information:\n\n" +
                   "📅 Sales phases will open gradually before tournament\n" +
                   "🌐 Book only through FIFA's official website\n" +
                   "💳 Different categories: Regular, VIP, Family packages\n" +
                   "⚠️ Beware of fake websites\n" +
                   "📧 Subscribe to official updates\n\n" +
                   "Follow the official website for latest news!";
        }
    }
    
    private String getHelpResponse(boolean isArabic) {
        if (isArabic) {
            return "ℹ️ يمكنني مساعدتك في:\n\n" +
                   "⚽ معلومات كأس العالم 2026\n" +
                   "🏙️ المدن والملاعب المضيفة\n" +
                   "🏨 حجز الفنادق والإقامة\n" +
                   "🎫 معلومات التذاكر\n" +
                   "🗺️ النقل والتوجيهات\n" +
                   "📱 نصائح السفر والسياحة\n\n" +
                   "اسأل عن أي شيء تريد معرفته! 😊";
        } else {
            return "ℹ️ I can help you with:\n\n" +
                   "⚽ World Cup 2026 information\n" +
                   "🏙️ Host cities and stadiums\n" +
                   "🏨 Hotel bookings and accommodation\n" +
                   "🎫 Ticket information\n" +
                   "🗺️ Transportation and directions\n" +
                   "📱 Travel and tourism tips\n\n" +
                   "Ask me anything you'd like to know! 😊";
        }
    }
    
    private String getDefaultResponse(boolean isArabic) {
        if (isArabic) {
            return "🤔 لم أفهم سؤالك تماماً، ولكن يمكنني مساعدتك في:\n\n" +
                   "• معلومات كأس العالم 2026\n" +
                   "• المدن والملاعب المضيفة\n" +
                   "• الفنادق والحجوزات\n" +
                   "• التذاكر والدخول\n\n" +
                   "هل يمكنك إعادة صياغة سؤالك؟ 😊";
        } else {
            return "🤔 I didn't quite understand your question, but I can help you with:\n\n" +
                   "• World Cup 2026 information\n" +
                   "• Host cities and stadiums\n" +
                   "• Hotels and bookings\n" +
                   "• Tickets and entry\n\n" +
                   "Could you rephrase your question? 😊";
        }
    }

    private String getFallbackResponse(String question) {
        // World Cup 2026 related questions
        if (question.contains("كأس العالم") || question.contains("world cup") || question.contains("2026")) {
            return "🏆 كأس العالم 2026 ستقام في الولايات المتحدة وكندا والمكسيك من 11 يونيو إلى 19 يوليو 2026.\n\n" +
                   "هذه أول مرة يستضيف ثلاث دول البطولة معاً، وستشمل 48 منتخباً.\n\n" +
                   "🏆 The 2026 FIFA World Cup will be held in the United States, Canada, and Mexico from June 11 to July 19, 2026.\n\n" +
                   "This is the first time three countries will co-host the tournament, featuring 48 teams.";
        }

        // Cities and tourism
        if (question.contains("مدن") || question.contains("سياحة") || question.contains("cities") || question.contains("tourism")) {
            return "🌍 المدن المضيفة لكأس العالم 2026 تشمل:\n\n" +
                   "🇺🇸 الولايات المتحدة: نيويورك، لوس أنجلوس، مايامي، أتلانتا، دالاس\n" +
                   "🇨🇦 كندا: تورونتو، فانكوفر\n" +
                   "🇲🇽 المكسيك: مكسيكو سيتي، غوادالاخارا، مونتيري\n\n" +
                   "🌍 Host cities for the 2026 World Cup include:\n\n" +
                   "🇺🇸 USA: New York, Los Angeles, Miami, Atlanta, Dallas\n" +
                   "🇨🇦 Canada: Toronto, Vancouver\n" +
                   "🇲🇽 Mexico: Mexico City, Guadalajara, Monterrey";
        }

        // Hotels and accommodation
        if (question.contains("فنادق") || question.contains("إقامة") || question.contains("hotels") || question.contains("accommodation")) {
            return "🏨 نصائح للحجز في فنادق كأس العالم 2026:\n\n" +
                   "• احجز مبكراً للحصول على أفضل الأسعار\n" +
                   "• استخدم مواقع المقارنة للعثور على أفضل العروض\n" +
                   "• فكر في الإقامة خارج وسط المدينة لتوفير المال\n" +
                   "• تحقق من خيارات النقل إلى الملاعب\n\n" +
                   "🏨 Tips for booking hotels for World Cup 2026:\n\n" +
                   "• Book early for best prices\n" +
                   "• Use comparison sites for best deals\n" +
                   "• Consider staying outside city centers to save money\n" +
                   "• Check transportation options to stadiums";
        }

        // Tickets and booking
        if (question.contains("تذاكر") || question.contains("حجز") || question.contains("tickets") || question.contains("booking")) {
            return "🎫 معلومات عن تذاكر كأس العالم 2026:\n\n" +
                   "• ستفتح مراحل بيع التذاكر تدريجياً قبل البطولة\n" +
                   "• يمكن الحصول على التذاكر من الموقع الرسمي لفيفا\n" +
                   "• هناك فئات مختلفة من التذاكر وأسعار متنوعة\n" +
                   "• انتبه للمواعيد الرسمية لتجنب الاحتيال\n\n" +
                   "🎫 Information about 2026 World Cup tickets:\n\n" +
                   "• Ticket sales phases will open gradually before the tournament\n" +
                   "• Tickets available through FIFA's official website\n" +
                   "• Different ticket categories and price ranges available\n" +
                   "• Watch for official dates to avoid fraud";
        }

        // General help
        if (question.contains("مساعدة") || question.contains("help") || question.contains("معلومات") || question.contains("info")) {
            return "ℹ️ يمكنني مساعدتك في:\n\n" +
                   "🏆 معلومات عن كأس العالم 2026\n" +
                   "🌍 المدن والملاعب المضيفة\n" +
                   "🏨 نصائح عن الفنادق والإقامة\n" +
                   "🎫 معلومات عن التذاكر\n" +
                   "🗺️ التوجيهات والنقل\n\n" +
                   "ℹ️ I can help you with:\n\n" +
                   "🏆 Information about World Cup 2026\n" +
                   "🌍 Host cities and stadiums\n" +
                   "🏨 Hotel and accommodation tips\n" +
                   "🎫 Ticket information\n" +
                   "🗺️ Directions and transportation";
        }

        // Default response
        return "👋 مرحباً! أنا مساعدك الذكي لكأس العالم 2026.\n\n" +
               "يمكنك أن تسألني عن:\n" +
               "• معلومات البطولة والمدن المضيفة\n" +
               "• الفنادق والإقامة\n" +
               "• التذاكر والحجوزات\n" +
               "• التوجيهات والنقل\n\n" +
               "👋 Hello! I'm your smart assistant for World Cup 2026.\n\n" +
               "You can ask me about:\n" +
               "• Tournament info and host cities\n" +
               "• Hotels and accommodation\n" +
               "• Tickets and bookings\n" +
               "• Directions and transportation";
    }
}