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
        if (BuildConfig.OPENAI_API_KEY != null && !BuildConfig.OPENAI_API_KEY.equals("PLACEHOLDER_OPENAI_API_KEY")) {
            askOpenAI(question, callback);
        } else if (BuildConfig.GEMINI_API_KEY != null && !BuildConfig.GEMINI_API_KEY.equals("PLACEHOLDER_GEMINI_API_KEY")) {
            askGemini(question, callback);
        } else {
            callback.onError("No AI API key configured");
        }
    }

    // Generate daily plan
    public void generateDailyPlan(String city, int availableHours, String interests,
                                  ApiCallback<List<AiResponse.DailyPlanItem>> callback) {
        String prompt = buildDailyPlanPrompt(city, availableHours, interests);

        if (BuildConfig.OPENAI_API_KEY != null && !BuildConfig.OPENAI_API_KEY.equals("PLACEHOLDER_OPENAI_API_KEY")) {
            askOpenAIForDailyPlan(prompt, callback);
        } else if (BuildConfig.GEMINI_API_KEY != null && !BuildConfig.GEMINI_API_KEY.equals("PLACEHOLDER_GEMINI_API_KEY")) {
            askGeminiForDailyPlan(prompt, callback);
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
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseBody = response.body().string();
                        JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();

                        if (responseJson.has("choices") && responseJson.getAsJsonArray("choices").size() > 0) {
                            JsonObject choice = responseJson.getAsJsonArray("choices").get(0).getAsJsonObject();
                            String content = choice.getAsJsonObject("message").get("content").getAsString();
                            callback.onSuccess(content);
                        } else {
                            callback.onError("Invalid response format");
                        }
                    } else {
                        callback.onError("API error: " + response.code());
                    }
                } catch (Exception e) {
                    callback.onError("Parse error: " + e.getMessage());
                }
            }
        });
    }

    private void askGemini(String question, ApiCallback<String> callback) {
        JsonObject requestJson = new JsonObject();

        JsonArray contents = new JsonArray();
        JsonObject content = new JsonObject();
        JsonArray parts = new JsonArray();
        JsonObject part = new JsonObject();
        part.addProperty("text", question);
        parts.add(part);
        content.add("parts", parts);
        contents.add(content);
        requestJson.add("contents", contents);

        RequestBody body = RequestBody.create(
                requestJson.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(GEMINI_BASE_URL + "models/gemini-pro:generateContent?key=" + BuildConfig.GEMINI_API_KEY)
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseBody = response.body().string();
                        JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();

                        if (responseJson.has("candidates") && responseJson.getAsJsonArray("candidates").size() > 0) {
                            JsonObject candidate = responseJson.getAsJsonArray("candidates").get(0).getAsJsonObject();
                            JsonObject content = candidate.getAsJsonObject("content");
                            JsonArray parts = content.getAsJsonArray("parts");
                            if (parts.size() > 0) {
                                String text = parts.get(0).getAsJsonObject().get("text").getAsString();
                                callback.onSuccess(text);
                            } else {
                                callback.onError("Empty response");
                            }
                        } else {
                            callback.onError("Invalid response format");
                        }
                    } else {
                        callback.onError("API error: " + response.code());
                    }
                } catch (Exception e) {
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
}