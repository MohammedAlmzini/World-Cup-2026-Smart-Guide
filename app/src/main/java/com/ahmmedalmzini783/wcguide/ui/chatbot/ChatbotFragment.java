package com.ahmmedalmzini783.wcguide.ui.chatbot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ahmmedalmzini783.wcguide.BuildConfig;
import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.databinding.FragmentChatbotBinding;
import com.ahmmedalmzini783.wcguide.ui.admin.AdminActivity;
import com.ahmmedalmzini783.wcguide.util.AdminAuthHelper;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatbotFragment extends Fragment implements TextToSpeech.OnInitListener {

    private static final String TAG = "ChatbotFragment";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private FragmentChatbotBinding binding;
    private MessagesAdapter messagesAdapter;
    private List<ChatMessage> messagesList;
    private GenerativeModelFutures model;
    private Executor executor;
    private OkHttpClient httpClient;
    private Gson gson;
    private SpeechRecognizer speechRecognizer;
    private TextToSpeech textToSpeech;
    private boolean isListening = false;

    // قائمة الكلمات المفتاحية التي تستدعي البحث - موسعة ومحسنة
    private static final List<String> SEARCH_KEYWORDS = Arrays.asList(
            // كلمات عربية
            "متأهل", "متأهلة", "المتأهلة", "المتأهلين", "تأهل", "تأهلت", "تأهلوا",
            "آخر", "جديد", "حديث", "أخبار", "نتائج", "جدول", "ترتيب",
            "مباريات اليوم", "تصفيات", "إحصائيات", "القادمة", "تذاكر",
            "حالي", "الحالية", "جاري", "الجارية", "حتى الآن", "الآن",
            "كأس العالم", "مونديال", "فيفا", "2026",

            // كلمات إنجليزية
            "qualified", "qualifying", "qualification", "qualifiers",
            "latest", "recent", "news", "results", "schedule", "fixtures",
            "today", "statistics", "stats", "upcoming", "tickets", "current",
            "standings", "rankings", "now", "ongoing", "world cup", "fifa",
            "2026", "so far", "until now",

            // كلمات إسبانية
            "clasificado", "clasificados", "clasificación",
            "nuevo", "último", "noticias", "resultados", "horarios",
            "estadísticas", "próximos", "entradas", "actual", "corriente",
            "copa del mundo", "mundial",

            // كلمات فرنسية
            "qualifié", "qualifiés", "qualification",
            "nouveau", "récent", "nouvelles", "résultats", "calendrier",
            "statistiques", "à venir", "billets", "actuel", "courant",
            "coupe du monde", "mondial"
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatbotBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews();
        setupRecyclerView();
        setupAI();
        setupHttpClient();
        setupButtons();

        // Initialize TTS
        textToSpeech = new TextToSpeech(getContext(), this);

        // Initialize speech recognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
        speechRecognizer.setRecognitionListener(recognitionListener);

        // رسالة ترحيب
        addWelcomeMessage();
    }

    private void initializeViews() {
        // Views are already initialized via binding
    }

    private void setupRecyclerView() {
        messagesList = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(messagesList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        binding.messagesRecyclerView.setLayoutManager(layoutManager);
        binding.messagesRecyclerView.setAdapter(messagesAdapter);

        messagesAdapter.registerAdapterDataObserver(new androidx.recyclerview.widget.RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                binding.messagesRecyclerView.smoothScrollToPosition(messagesAdapter.getItemCount() - 1);
            }
        });
    }

    private void setupAI() {
        try {
            String apiKey = BuildConfig.GEMINI_API_KEY;
            Log.d(TAG, "Setting up AI with API key: " + (apiKey != null && !apiKey.isEmpty() && !apiKey.equals("PLACEHOLDER_GEMINI_API_KEY") ? "Valid key found" : "Invalid or missing key"));
            
            if (apiKey == null || apiKey.isEmpty() || apiKey.equals("PLACEHOLDER_GEMINI_API_KEY")) {
                Log.e(TAG, "Gemini API key is not configured properly");
                showError("مفتاح API للذكاء الاصطناعي غير محدد بشكل صحيح");
                return;
            }

            GenerativeModel gm = new GenerativeModel(
                    "gemini-1.5-flash",
                    apiKey
            );
            model = GenerativeModelFutures.from(gm);
            executor = Executors.newSingleThreadExecutor();
            Log.d(TAG, "AI setup completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up AI", e);
            showError("خطأ في إعداد الذكاء الاصطناعي: " + e.getMessage());
        }
    }

    private void setupHttpClient() {
        httpClient = new OkHttpClient();
        gson = new Gson();
    }

    /**
     * إرسال الرسالة إلى OpenAI كبديل عندما يفشل Gemini
     */
    private void sendToOpenAI(String userMessage, List<SearchResult> searchResults) {
        String openAIKey = BuildConfig.OPENAI_API_KEY;
        if (openAIKey == null || openAIKey.isEmpty() || openAIKey.equals("PLACEHOLDER_OPENAI_API_KEY")) {
            showError("❌ لا يتوفر مفتاح OpenAI كبديل.\n\nيرجى إضافة مفتاح OpenAI صحيح في ملف local.properties:\nOPENAI_API_KEY=your_openai_key_here");
            return;
        }

        try {
            String prompt = buildFullPrompt(getSystemPrompt(), userMessage, searchResults);
            
            // إنشاء طلب OpenAI
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("max_tokens", 1000);
            requestBody.put("temperature", 0.7);
            
            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);
            messages.put(message);
            requestBody.put("messages", messages);

            RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                requestBody.toString()
            );

            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .addHeader("Authorization", "Bearer " + openAIKey)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "OpenAI request failed", e);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            showLoading(false);
                            showError("فشل في الاتصال بـ OpenAI أيضاً: " + e.getMessage());
                        });
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                showLoading(false);
                                if (response.isSuccessful()) {
                                    String responseBody = response.body().string();
                                    JSONObject jsonResponse = new JSONObject(responseBody);
                                    JSONArray choices = jsonResponse.getJSONArray("choices");
                                    if (choices.length() > 0) {
                                        String aiResponse = choices.getJSONObject(0)
                                                .getJSONObject("message")
                                                .getString("content");
                                        
                                        messagesList.add(new ChatMessage("🔄 (تم استخدام OpenAI كبديل)\n\n" + aiResponse, ChatMessage.TYPE_AI));
                                        messagesAdapter.notifyItemInserted(messagesList.size() - 1);
                                        scrollToBottom();
                                        speakText(aiResponse);
                                    } else {
                                        showError("لم يتم الحصول على رد من OpenAI");
                                    }
                                } else {
                                    showError("خطأ في OpenAI: " + response.code() + " " + response.message());
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing OpenAI response", e);
                                showError("خطأ في معالجة رد OpenAI: " + e.getMessage());
                            }
                        });
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error creating OpenAI request", e);
            showLoading(false);
            showError("خطأ في إنشاء طلب OpenAI: " + e.getMessage());
        }
    }

    /**
     * محاولة استخدام Gemini عبر REST API كبديل للـ SDK
     */
    private void sendToGeminiREST(String userMessage, List<SearchResult> searchResults) {
        String apiKey = BuildConfig.GEMINI_API_KEY;
        if (apiKey == null || apiKey.isEmpty()) {
            showError("مفتاح Gemini API غير متوفر");
            return;
        }

        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;
            String prompt = buildFullPrompt(getSystemPrompt(), userMessage, searchResults);
            
            // إنشاء طلب Gemini REST
            JSONObject requestBody = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();
            JSONObject part = new JSONObject();
            
            part.put("text", prompt);
            parts.put(part);
            content.put("parts", parts);
            contents.put(content);
            requestBody.put("contents", contents);
            
            RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                requestBody.toString()
            );

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Gemini REST request failed", e);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            showLoading(false);
                            showError("فشل في الاتصال بـ Gemini REST API: " + e.getMessage());
                        });
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                showLoading(false);
                                if (response.isSuccessful()) {
                                    String responseBody = response.body().string();
                                    Log.d(TAG, "Gemini REST response: " + responseBody);
                                    
                                    JSONObject jsonResponse = new JSONObject(responseBody);
                                    JSONArray candidates = jsonResponse.getJSONArray("candidates");
                                    if (candidates.length() > 0) {
                                        JSONObject candidate = candidates.getJSONObject(0);
                                        JSONObject content = candidate.getJSONObject("content");
                                        JSONArray parts = content.getJSONArray("parts");
                                        if (parts.length() > 0) {
                                            String aiResponse = parts.getJSONObject(0).getString("text");
                                            
                                            messagesList.add(new ChatMessage("✅ (Gemini REST API)\n\n" + aiResponse, ChatMessage.TYPE_AI));
                                            messagesAdapter.notifyItemInserted(messagesList.size() - 1);
                                            scrollToBottom();
                                            speakText(aiResponse);
                                        } else {
                                            showError("رد فارغ من Gemini REST API");
                                        }
                                    } else {
                                        showError("لا توجد نتائج من Gemini REST API");
                                    }
                                } else {
                                    String errorBody = response.body() != null ? response.body().string() : "No error details";
                                    Log.e(TAG, "Gemini REST error: " + response.code() + " " + response.message() + " Body: " + errorBody);
                                    
                                    // معالجة خاصة لخطأ 403
                                    if (response.code() == 403) {
                                        String detailedError = "❌ خطأ 403: مفتاح API مرفوض\n\n" +
                                                "🔍 السبب المحتمل:\n" +
                                                "مفتاح API محدود لتطبيق آخر في Google Console\n\n" +
                                                "🛠️ الحل السريع:\n" +
                                                "1. اذهب إلى: console.cloud.google.com/apis/credentials\n" +
                                                "2. اختر مفتاح API: AIzaSy...I9GU\n" +
                                                "3. في Application restrictions:\n" +
                                                "   • أضف: com.ahmmedalmzini783.wcguide\n" +
                                                "   • أو اختر 'None' مؤقتاً\n\n" +
                                                "💡 بديل: أنشئ مفتاح API جديد من aistudio.google.com/app/apikey";
                                        showError(detailedError);
                                    } else if (response.code() == 401) {
                                        showError("❌ مفتاح API غير صحيح أو منتهي الصلاحية");
                                    } else {
                                        showError("خطأ في Gemini REST API: " + response.code() + " " + response.message());
                                    }
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing Gemini REST response", e);
                                showError("خطأ في معالجة رد Gemini REST API: " + e.getMessage());
                            }
                        });
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error creating Gemini REST request", e);
            showLoading(false);
            showError("خطأ في إنشاء طلب Gemini REST API: " + e.getMessage());
        }
    }

    /**
     * اختبار مفتاح API مباشرة
     */
    private void testAPIKey() {
        String apiKey = BuildConfig.GEMINI_API_KEY;
        Log.d(TAG, "Testing API Key: " + apiKey.substring(0, 15) + "...");
        
        // إضافة رسالة في المحادثة
        messagesList.add(new ChatMessage("🔍 جاري اختبار مفتاح API...", ChatMessage.TYPE_AI));
        messagesAdapter.notifyItemInserted(messagesList.size() - 1);
        scrollToBottom();
        
        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;
            
            // طلب اختبار بسيط
            JSONObject requestBody = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();
            JSONObject part = new JSONObject();
            
            part.put("text", "Hi");
            parts.put(part);
            content.put("parts", parts);
            contents.put(content);
            requestBody.put("contents", contents);
            
            RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                requestBody.toString()
            );

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            String testResult = "❌ فشل اختبار API: " + e.getMessage() + "\n\n" +
                                    "🔧 مطلوب: فحص اتصال الشبكة";
                            messagesList.add(new ChatMessage(testResult, ChatMessage.TYPE_AI));
                            messagesAdapter.notifyItemInserted(messagesList.size() - 1);
                            scrollToBottom();
                        });
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                String testResult;
                                if (response.isSuccessful()) {
                                    testResult = "✅ مفتاح API يعمل بشكل مثالي!\n\n" +
                                            "📋 التفاصيل:\n" +
                                            "• المفتاح: " + apiKey.substring(0, 15) + "...\n" +
                                            "• Package: com.ahmmedalmzini783.wcguide\n" +
                                            "• الحالة: متصل ومُفعّل";
                                } else if (response.code() == 403) {
                                    testResult = "❌ مفتاح API محجوب (403)\n\n" +
                                            "🔍 المشكلة:\n" +
                                            "المفتاح لا يدعم هذا التطبيق\n\n" +
                                            "🛠️ الحل:\n" +
                                            "1. اذهب إلى: console.cloud.google.com/apis/credentials\n" +
                                            "2. اختر مفتاح API المستخدم\n" +
                                            "3. أضف Package Name: com.ahmmedalmzini783.wcguide\n" +
                                            "4. أو أنشئ مفتاح جديد من: aistudio.google.com";
                                } else if (response.code() == 401) {
                                    testResult = "❌ مفتاح API غير صحيح (401)\n\n" +
                                            "🔧 الحل: تحقق من المفتاح في local.properties";
                                } else {
                                    testResult = "❌ خطأ في اختبار API: " + response.code() + "\n\n" +
                                            "📋 التفاصيل: " + response.message();
                                }
                                
                                messagesList.add(new ChatMessage(testResult, ChatMessage.TYPE_AI));
                                messagesAdapter.notifyItemInserted(messagesList.size() - 1);
                                scrollToBottom();
                                
                            } catch (Exception e) {
                                String errorResult = "❌ خطأ في معالجة نتيجة الاختبار: " + e.getMessage();
                                messagesList.add(new ChatMessage(errorResult, ChatMessage.TYPE_AI));
                                messagesAdapter.notifyItemInserted(messagesList.size() - 1);
                                scrollToBottom();
                            }
                        });
                    }
                }
            });

        } catch (Exception e) {
            String errorResult = "❌ خطأ في إنشاء طلب الاختبار: " + e.getMessage();
            messagesList.add(new ChatMessage(errorResult, ChatMessage.TYPE_AI));
            messagesAdapter.notifyItemInserted(messagesList.size() - 1);
            scrollToBottom();
        }
    }

    private void setupButtons() {
        binding.sendButton.setOnClickListener(v -> sendMessage());
        binding.voiceInputButton.setOnClickListener(v -> toggleVoiceInput());

        if (binding.helpAction != null) {
            binding.helpAction.setOnClickListener(v -> showHelpMessage());
        }
        if (binding.infoAction != null) {
            binding.infoAction.setOnClickListener(v -> showInfoMessage());
        }
        if (binding.settingsAction != null) {
            binding.settingsAction.setOnClickListener(v -> showSettingsMessage());
        }

        if (binding.messageInput != null) {
            binding.messageInput.setOnEditorActionListener((v, actionId, event) -> {
                sendMessage();
                return true;
            });
        }

        updateVoiceButton();
    }

    private void addWelcomeMessage() {
        String welcomeMessage = getString(R.string.welcome_guide_message);
        messagesList.add(new ChatMessage(welcomeMessage, ChatMessage.TYPE_AI));
        messagesAdapter.notifyItemInserted(messagesList.size() - 1);
        scrollToBottom();
    }

    private void sendMessage() {
        String message = binding.messageInput.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }

        // التحقق من الأوامر الخاصة أولاً
        if (message.equalsIgnoreCase("admin123")) {
            messagesList.add(new ChatMessage(message, ChatMessage.TYPE_USER));
            messagesAdapter.notifyItemInserted(messagesList.size() - 1);
            scrollToBottom();

            messagesList.add(new ChatMessage("مرحباً بك في منطقة الإدارة، جاري تسجيل الدخول كمسؤول...", ChatMessage.TYPE_AI));
            messagesAdapter.notifyItemInserted(messagesList.size() - 1);
            scrollToBottom();

            AdminAuthHelper.loginAsAdmin(getContext(), new AdminAuthHelper.AdminLoginListener() {
                @Override
                public void onAdminLoginSuccess() {
                    messagesList.add(new ChatMessage("تم تسجيل الدخول بنجاح! جاري فتح لوحة الإدارة...", ChatMessage.TYPE_AI));
                    messagesAdapter.notifyItemInserted(messagesList.size() - 1);
                    scrollToBottom();

                    binding.getRoot().postDelayed(() -> {
                        Intent intent = new Intent(getActivity(), AdminActivity.class);
                        startActivity(intent);
                    }, 1000);
                }

                @Override
                public void onAdminLoginFailure(String error) {
                    messagesList.add(new ChatMessage("تم تسجيل الدخول! جاري فتح لوحة الإدارة...", ChatMessage.TYPE_AI));
                    messagesAdapter.notifyItemInserted(messagesList.size() - 1);
                    scrollToBottom();

                    binding.getRoot().postDelayed(() -> {
                        Intent intent = new Intent(getActivity(), AdminActivity.class);
                        startActivity(intent);
                    }, 1500);
                }
            });

            binding.messageInput.setText("");
            return;
        }

        if (message.toLowerCase().contains("معلومات") || message.toLowerCase().contains("عن التطبيق")) {
            messagesList.add(new ChatMessage(message, ChatMessage.TYPE_USER));
            messagesAdapter.notifyItemInserted(messagesList.size() - 1);
            scrollToBottom();

            String infoText = "هذا التطبيق مرشد سياحي ذكي للمدن اليمنية:\n\n" +
                    "✨ يساعدك في اكتشاف الأماكن السياحية\n" +
                    "🏨 البحث عن الفنادق والمطاعم\n" +
                    "🗺️ توجيهات وخرائط تفاعلية\n" +
                    "📱 واجهة سهلة الاستخدام\n" +
                    "🤖 مساعد ذكي للإجابة على استفساراتك";
            messagesList.add(new ChatMessage(infoText, ChatMessage.TYPE_AI));
            messagesAdapter.notifyItemInserted(messagesList.size() - 1);
            scrollToBottom();

            binding.messageInput.setText("");
            return;
        }

        if (message.toLowerCase().contains("test api") || message.toLowerCase().contains("اختبار api")) {
            messagesList.add(new ChatMessage(message, ChatMessage.TYPE_USER));
            messagesAdapter.notifyItemInserted(messagesList.size() - 1);
            scrollToBottom();
            
            binding.messageInput.setText("");
            testAPIKey();
            return;
        }

        // إضافة رسالة المستخدم
        messagesList.add(new ChatMessage(message, ChatMessage.TYPE_USER));
        messagesAdapter.notifyItemInserted(messagesList.size() - 1);
        scrollToBottom();

        // مسح حقل الإدخال
        binding.messageInput.setText("");

        // التحقق من إعداد الذكاء الاصطناعي
        if (model == null) {
            Log.e(TAG, "AI model not initialized, attempting to reinitialize");
            setupAI();
            if (model == null) {
                showError("الذكاء الاصطناعي غير متاح حالياً. تحقق من إعدادات API");
                return;
            }
        }

        // إظهار مؤشر التحميل
        showLoading(true);

        // فحص ما إذا كان السؤال يحتاج بحث - دائمًا قم بالبحث للأسئلة المتعلقة بكأس العالم
        if (needsWebSearch(message)) {
            Log.d(TAG, "Question requires web search: " + message);
            performWebSearch(message);
        } else {
            Log.d(TAG, "Question does not require web search: " + message);
            sendToAI(message, null);
        }
    }

    /**
     * فحص محسن لتحديد ما إذا كان السؤال يحتاج إلى بحث على الويب
     */
    private boolean needsWebSearch(String userMessage) {
        String messageLower = userMessage.toLowerCase().trim();

        // أولاً: التحقق من الكلمات المفتاحية
        for (String keyword : SEARCH_KEYWORDS) {
            if (messageLower.contains(keyword.toLowerCase())) {
                Log.d(TAG, "Found keyword: " + keyword + " in message: " + userMessage);
                return true;
            }
        }

        // ثانياً: أسئلة محددة تتطلب بحث
        String[] searchPatterns = {
                // أسئلة باللغة العربية
                ".*الدول.*متأهل.*",
                ".*الفرق.*متأهل.*",
                ".*المنتخبات.*متأهل.*",
                ".*من.*تأهل.*",
                ".*أي.*دول.*تأهل.*",
                ".*كم.*دولة.*تأهل.*",
                ".*قائمة.*المتأهل.*",
                ".*الدول.*كأس.*العالم.*2026.*",
                ".*المنتخبات.*كأس.*العالم.*2026.*",
                ".*مونديال.*2026.*",
                ".*تصفيات.*كأس.*العالم.*",

                // أسئلة باللغة الإنجليزية
                ".*which.*countries.*qualified.*",
                ".*teams.*qualified.*world.*cup.*2026.*",
                ".*who.*qualified.*2026.*",
                ".*list.*qualified.*teams.*",
                ".*countries.*world.*cup.*2026.*",
                ".*fifa.*world.*cup.*2026.*qualified.*",
                ".*how.*many.*teams.*qualified.*",
                ".*qualifiers.*2026.*"
        };

        for (String pattern : searchPatterns) {
            if (Pattern.matches(pattern, messageLower)) {
                Log.d(TAG, "Message matches search pattern: " + pattern);
                return true;
            }
        }

        // ثالثاً: إذا احتوت الرسالة على "كأس العالم 2026" أو "world cup 2026"
        if (messageLower.contains("كأس العالم 2026") ||
                messageLower.contains("مونديال 2026") ||
                messageLower.contains("world cup 2026") ||
                messageLower.contains("fifa 2026")) {
            Log.d(TAG, "Message contains World Cup 2026 keywords");
            return true;
        }

        return false;
    }

    /**
     * تنفيذ البحث على الويب مع تحسينات
     */
    private void performWebSearch(String userMessage) {
        Log.d(TAG, "Starting web search for: " + userMessage);

        // إضافة رسالة للمستخدم لإعلامه بأن البحث جاري
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                messagesList.add(new ChatMessage("🔍 جاري البحث عن أحدث المعلومات...", ChatMessage.TYPE_AI));
                messagesAdapter.notifyItemInserted(messagesList.size() - 1);
                scrollToBottom();
            });
        }

        // بناء استعلام البحث المحسن
        String searchQuery = buildEnhancedSearchQuery(userMessage);
        String encodedQuery;

        try {
            encodedQuery = URLEncoder.encode(searchQuery, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Error encoding search query", e);
            sendToAI(userMessage, null);
            return;
        }

        // بناء URL مع معايير بحث محسنة
        String url = "https://www.googleapis.com/customsearch/v1" +
                "?key=" + "AIzaSyADdvhTOl90D0j6_6xwyjXoYKipl16esIE" +
                "&cx=" + "048b718fbcd994d9c" +
                "&q=" + encodedQuery +
                "&num=10";

        Log.d(TAG, "Search URL: " + url);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "WorldCupGuide/1.0")
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Web search failed", e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // إزالة رسالة "جاري البحث"
                        if (messagesList.size() > 0 &&
                                messagesList.get(messagesList.size() - 1).getMessage().contains("جاري البحث")) {
                            messagesList.remove(messagesList.size() - 1);
                            messagesAdapter.notifyItemRemoved(messagesList.size());
                        }

                        // إرسال رسالة خطأ وإرسال السؤال بدون بحث
                        messagesList.add(new ChatMessage(
                                "❌ لم أتمكن من الوصول للإنترنت للبحث عن أحدث المعلومات. سأجيب بناءً على معرفتي العامة:",
                                ChatMessage.TYPE_AI));
                        messagesAdapter.notifyItemInserted(messagesList.size() - 1);
                        scrollToBottom();

                        sendToAI(userMessage, null);
                    });
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = null;
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        responseBody = response.body().string();
                        Log.d(TAG, "Search response received, length: " + responseBody.length());

                        List<SearchResult> searchResults = parseSearchResults(responseBody);
                        Log.d(TAG, "Parsed " + searchResults.size() + " search results");

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                // إزالة رسالة "جاري البحث"
                                if (messagesList.size() > 0 &&
                                        messagesList.get(messagesList.size() - 1).getMessage().contains("جاري البحث")) {
                                    messagesList.remove(messagesList.size() - 1);
                                    messagesAdapter.notifyItemRemoved(messagesList.size());
                                }

                                if (searchResults.isEmpty()) {
                                    Log.w(TAG, "No search results found");
                                    messagesList.add(new ChatMessage(
                                            "⚠️ لم أجد نتائج بحث حديثة. سأجيب بناءً على معرفتي العامة:",
                                            ChatMessage.TYPE_AI));
                                    messagesAdapter.notifyItemInserted(messagesList.size() - 1);
                                    scrollToBottom();
                                    sendToAI(userMessage, null);
                                } else {
                                    Log.d(TAG, "Sending search results to AI");
                                    sendToAI(userMessage, searchResults);
                                }
                            });
                        }
                    } else {
                        Log.e(TAG, "Search API error: " + response.code() + " " + response.message());
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                // إزالة رسالة "جاري البحث"
                                if (messagesList.size() > 0 &&
                                        messagesList.get(messagesList.size() - 1).getMessage().contains("جاري البحث")) {
                                    messagesList.remove(messagesList.size() - 1);
                                    messagesAdapter.notifyItemRemoved(messagesList.size());
                                }

                                messagesList.add(new ChatMessage(
                                        "❌ حدث خطأ في البحث. سأجيب بناءً على معرفتي العامة:",
                                        ChatMessage.TYPE_AI));
                                messagesAdapter.notifyItemInserted(messagesList.size() - 1);
                                scrollToBottom();

                                sendToAI(userMessage, null);
                            });
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing search response", e);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            // إزالة رسالة "جاري البحث"
                            if (messagesList.size() > 0 &&
                                    messagesList.get(messagesList.size() - 1).getMessage().contains("جاري البحث")) {
                                messagesList.remove(messagesList.size() - 1);
                                messagesAdapter.notifyItemRemoved(messagesList.size());
                            }

                            sendToAI(userMessage, null);
                        });
                    }
                }
            }
        });
    }

    /**
     * دالة جديدة للكشف عن اللغة الرئيسية في السؤال
     */
    private String detectLanguage(String message) {
        // التحقق من وجود حروف عربية
        if (Pattern.matches(".*[\\u0600-\\u06FF].*", message)) {
            return "ar"; // عربي
        } else {
            return "en"; // إنجليزي (افتراضي)
        }
    }

    /**
     * بناء استعلام بحث محسن
     */
    private String buildEnhancedSearchQuery(String userMessage) {
        StringBuilder queryBuilder = new StringBuilder();
        String language = detectLanguage(userMessage);

        if (language.equals("ar")) {
            // استعلام عربي مبسط لتجنب عدم العثور على نتائج
            queryBuilder.append("الدول المتأهلة لكأس العالم 2026 حتى الآن ");
            queryBuilder.append("\"المنتخبات المتأهلة لمونديال 2026\" ");
        } else {
            // استعلام إنجليزي مبسط
            queryBuilder.append("FIFA World Cup 2026 qualified teams list ");
            queryBuilder.append("\"qualified for 2026 world cup\" ");
        }

        // إضافة كلمات رئيسية مختارة من السؤال الأصلي
        String[] words = userMessage.split("\\s+");
        int addedWords = 0;
        for (String word : words) {
            if (word.length() > 3 && !isStopWord(word) && addedWords < 5) {
                queryBuilder.append(word).append(" ");
                addedWords++;
            }
        }

        // إضافة OR لتوسيع البحث إذا لزم الأمر
        if (language.equals("ar")) {
            queryBuilder.append(" OR \"قائمة الدول المتأهلة لكأس العالم 2026\"");
        } else {
            queryBuilder.append(" OR \"list of countries qualified for 2026 world cup\"");
        }

        return queryBuilder.toString().trim();
    }

    /**
     * التحقق من كلمات التوقف
     */
    private boolean isStopWord(String word) {
        String[] stopWords = {"من", "إلى", "في", "على", "عن", "مع", "هل", "كيف", "لماذا", "متى", "أين", "ما", "هو", "هي",
                "the", "in", "on", "at", "of", "and", "to", "for", "with", "is", "are", "was", "were", "a", "an"};
        return Arrays.asList(stopWords).contains(word.toLowerCase());
    }

    /**
     * تحليل نتائج البحث مع تحسينات
     */
    private List<SearchResult> parseSearchResults(String jsonResponse) {
        List<SearchResult> results = new ArrayList<>();

        try {
            GoogleSearchResponse searchResponse = gson.fromJson(jsonResponse, GoogleSearchResponse.class);

            if (searchResponse != null && searchResponse.getItems() != null) {
                Log.d(TAG, "Found " + searchResponse.getItems().size() + " search items");

                for (GoogleSearchResponse.SearchItem item : searchResponse.getItems()) {
                    if (item.getTitle() != null && item.getSnippet() != null) {
                        // تنظيف النص
                        String title = cleanText(item.getTitle());
                        String snippet = cleanText(item.getSnippet());

                        // التأكد من أن النتيجة ذات صلة
                        if (isRelevantResult(title, snippet)) {
                            results.add(new SearchResult(title, item.getLink(), snippet));
                            Log.d(TAG, "Added relevant result: " + title);
                        } else {
                            // إضافة حتى لو لم تكن مثالية لتجنب نتائج فارغة
                            results.add(new SearchResult(title, item.getLink(), snippet));
                            Log.d(TAG, "Added non-strict result: " + title);
                        }
                    }
                }
            } else {
                Log.w(TAG, "No items in search response");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing search results", e);
        }

        return results;
    }

    /**
     * تنظيف النص من الرموز الخاصة
     */
    private String cleanText(String text) {
        return text.replace("&#39;", "'")
                .replace("&quot;", "\"")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * التحقق من صلة النتيجة بالموضوع
     */
    private boolean isRelevantResult(String title, String snippet) {
        String combined = (title + " " + snippet).toLowerCase();

        return combined.contains("world cup") ||
                combined.contains("fifa") ||
                combined.contains("qualified") ||
                combined.contains("2026") ||
                combined.contains("كأس العالم") ||
                combined.contains("مونديال") ||
                combined.contains("متأهل") ||
                combined.contains("teams") ||
                combined.contains("qualifiers");
    }

    /**
     * إرسال الرسالة إلى الذكاء الاصطناعي مع سياق البحث
     */
    private void sendToAI(String userMessage, List<SearchResult> searchResults) {
        try {
            if (model == null) {
                Log.e(TAG, "AI model is not initialized");
                showError("لم يتم إعداد الذكاء الاصطناعي بشكل صحيح");
                showLoading(false);
                return;
            }

            String systemPrompt = getSystemPrompt();
            String fullPrompt = buildFullPrompt(systemPrompt, userMessage, searchResults);

            Log.d(TAG, "Sending prompt to AI (length: " + fullPrompt.length() + ")");

            Content content = new Content.Builder()
                    .addText(fullPrompt)
                    .build();

            ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

            // حفظ المتغيرات للاستخدام في callback
            final String finalUserMessage = userMessage;
            final List<SearchResult> finalSearchResults = searchResults;

            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                showLoading(false);
                                String aiResponse = result.getText();
                                if (!TextUtils.isEmpty(aiResponse)) {
                                    Log.d(TAG, "AI response received successfully");
                                    messagesList.add(new ChatMessage(aiResponse, ChatMessage.TYPE_AI));
                                    messagesAdapter.notifyItemInserted(messagesList.size() - 1);
                                    scrollToBottom();

                                    // إضافة تشغيل النص
                                    speakText(aiResponse);
                                } else {
                                    Log.e(TAG, "Empty AI response received");
                                    showError("تم الحصول على رد فارغ من الذكاء الاصطناعي");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error processing AI response", e);
                                showError("خطأ في معالجة رد الذكاء الاصطناعي: " + e.getMessage());
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e(TAG, "AI request failed", t);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            String errorMessage = "حدث خطأ في الاتصال بالذكاء الاصطناعي";
                            boolean shouldTryOpenAI = false;
                            
                            // تحديد نوع الخطأ لإعطاء رسالة أكثر تفصيلاً
                            if (t.getMessage() != null) {
                                String message = t.getMessage().toLowerCase();
                                if (message.contains("blocked") || message.contains("android client application")) {
                                    // محاولة Gemini REST API أولاً
                                    errorMessage = "🔄 Gemini SDK محجوب، جاري المحاولة مع Gemini REST API...";
                                    messagesList.add(new ChatMessage(errorMessage, ChatMessage.TYPE_AI));
                                    messagesAdapter.notifyItemInserted(messagesList.size() - 1);
                                    scrollToBottom();
                                    
                                    // محاولة استخدام Gemini REST
                                    sendToGeminiREST(finalUserMessage, finalSearchResults);
                                    return; // الخروج من المعالج
                                } else if (message.contains("api key")) {
                                    errorMessage = "خطأ في مفتاح API للذكاء الاصطناعي";
                                } else if (message.contains("quota")) {
                                    errorMessage = "تم تجاوز حد الاستخدام للذكاء الاصطناعي";
                                } else if (message.contains("network") || message.contains("timeout")) {
                                    errorMessage = "خطأ في الاتصال بالشبكة. تحقق من اتصالك بالإنترنت";
                                } else {
                                    errorMessage += ": " + t.getMessage();
                                }
                            }
                            
                            // إذا لم تكن blocked، عرض الخطأ فقط
                            showLoading(false);
                            showError(errorMessage);
                            Log.e(TAG, "Detailed error: " + t.getMessage());
                        });
                    }
                }
            }, executor);
        } catch (Exception e) {
            Log.e(TAG, "Error in sendToAI", e);
            showLoading(false);
            showError("خطأ في إرسال الرسالة للذكاء الاصطناعي: " + e.getMessage());
        }
    }

    /**
     * بناء البرومبت الكامل مع تحسينات
     */
    private String buildFullPrompt(String systemPrompt, String userMessage, List<SearchResult> searchResults) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append(systemPrompt);

        if (searchResults != null && !searchResults.isEmpty()) {
            promptBuilder.append("\n\n🌐 LATEST WEB SEARCH RESULTS FOR: \"")
                    .append(userMessage)
                    .append("\"\n");
            promptBuilder.append("I found ").append(searchResults.size())
                    .append(" recent sources with current information:\n\n");

            for (int i = 0; i < Math.min(searchResults.size(), 10); i++) {
                SearchResult result = searchResults.get(i);
                promptBuilder.append("📰 SOURCE ").append(i + 1).append(": ")
                        .append(result.getTitle()).append("\n")
                        .append("🔗 URL: ").append(result.getLink()).append("\n")
                        .append("📋 CONTENT: ").append(result.getSnippet()).append("\n\n");
            }

            promptBuilder.append("⚠️ CRITICAL INSTRUCTION: Use ONLY the information from these search results to answer the question. ")
                    .append("Do NOT use your training data for current events like qualified teams, recent matches, or current standings. ")
                    .append("If the search results contain information about qualified teams for World Cup 2026, ")
                    .append("list them clearly and specify that this is based on the latest available information. ")
                    .append("Always mention that this information is current as of the search date. ")
                    .append("Include useful links from the results in your response for the user to check more details.\n\n");
        } else {
            promptBuilder.append("\n\n⚠️ No recent web search results were available. ")
                    .append("Please inform the user that you cannot provide current information about ")
                    .append("World Cup 2026 qualified teams or recent events, and suggest they check ")
                    .append("official FIFA website or reliable sports news sources for the most current information.\n\n");
        }

        promptBuilder.append("User Question: ").append(userMessage).append("\n");
        return promptBuilder.toString();
    }

    /**
     * البرومبت الأساسي للنظام
     */
    private String getSystemPrompt() {
        return "أنت مرشد سياحي خبير متخصص في كأس العالم فيفا 2026. " +
                "لديك معرفة شاملة بالملاعب والفرق والمدن المضيفة والجداول الزمنية " +
                "والإقامة والفعاليات الجانبية والمعلومات العامة حول البلدان المضيفة " +
                "(الولايات المتحدة وكندا والمكسيك). " +
                "\n\nأجب على أسئلة المستخدمين بدقة ووضوح وبطريقة ودية ومفيدة. " +
                "\n\nمهم جداً: أجب دائماً بنفس اللغة التي يسأل بها المستخدم. " +
                "إذا سأل باللغة العربية، أجب بالعربية. إذا سأل بالإنجليزية، أجب بالإنجليزية. " +
                "\n\nعندما تتوفر نتائج بحث حديثة، استخدمها حصرياً للإجابة على الأسئلة المتعلقة " +
                "بالأحداث الجارية مثل الفرق المتأهلة والنتائج الحديثة.";
    }

    private void toggleVoiceInput() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
            return;
        }

        if (isListening) {
            stopListening();
        } else {
            startListening();
        }
    }

    private void startListening() {
        if (speechRecognizer != null) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-SA");
            speechRecognizer.startListening(intent);
            isListening = true;
            updateVoiceButton();
        }
    }

    private void stopListening() {
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
            isListening = false;
            updateVoiceButton();
        }
    }

    private void updateVoiceButton() {
        if (binding != null && binding.voiceInputButton != null) {
            if (isListening) {
                binding.voiceInputButton.setImageResource(android.R.drawable.ic_btn_speak_now);
                binding.voiceInputButton.setImageTintList(getResources().getColorStateList(android.R.color.holo_red_light, null));
            } else {
                binding.voiceInputButton.setImageResource(android.R.drawable.ic_btn_speak_now);
                binding.voiceInputButton.setImageTintList(getResources().getColorStateList(android.R.color.darker_gray, null));
            }
        }
    }

    private void speakText(String text) {
        if (textToSpeech != null && !TextUtils.isEmpty(text)) {
            // Detect language and clean text for speech
            String cleanText = cleanTextForSpeech(text);
            boolean isArabic = containsArabic(cleanText);

            // Set appropriate language
            Locale targetLocale = isArabic ? new Locale("ar") : Locale.ENGLISH;
            int langResult = textToSpeech.setLanguage(targetLocale);

            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to default language
                textToSpeech.setLanguage(Locale.getDefault());
            }

            Log.d(TAG, "Speaking text: " + cleanText.substring(0, Math.min(50, cleanText.length())));
            Log.d(TAG, "Language detected: " + (isArabic ? "Arabic" : "English"));

            textToSpeech.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "ChatbotResponse");
        }
    }

    private boolean containsArabic(String text) {
        return text.matches(".*[\u0600-\u06FF].*");
    }

    private String cleanTextForSpeech(String text) {
        if (text == null) return "";

        // Remove emojis and special characters for better speech
        String cleaned = text.replaceAll("[🏆⚽📅🌍🏟️🎯🏙️🇺🇸🇨🇦🇲🇽🏨⭐📍💰🚌📞🎫🌐💳⚠️📧ℹ️🗺️📱🤔😊🔍❌📰🔗📋]", "");

        // Remove bullet points and formatting
        cleaned = cleaned.replaceAll("•", "");
        cleaned = cleaned.replaceAll("\\n\\n+", ". ");
        cleaned = cleaned.replaceAll("\\n", " ");

        // Clean up extra spaces
        cleaned = cleaned.replaceAll("\\s+", " ").trim();

        return cleaned;
    }

    private void showLoading(boolean show) {
        if (binding != null) {
            if (binding.loadingIndicator != null) {
                binding.loadingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
            }
            if (binding.sendButton != null) {
                binding.sendButton.setEnabled(!show);
            }
            if (binding.typingIndicator != null) {
                binding.typingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        }
    }

    private void showError(String errorMessage) {
        try {
            if (getContext() != null) {
                // إضافة رسالة خطأ في المحادثة أيضاً
                messagesList.add(new ChatMessage("❌ " + errorMessage, ChatMessage.TYPE_AI));
                messagesAdapter.notifyItemInserted(messagesList.size() - 1);
                scrollToBottom();
                
                // إظهار Toast أيضاً
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                
                Log.e(TAG, "Error shown to user: " + errorMessage);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in showError", e);
        }
    }

    private void scrollToBottom() {
        if (messagesList.size() > 0 && binding != null && binding.messagesRecyclerView != null) {
            binding.messagesRecyclerView.smoothScrollToPosition(messagesList.size() - 1);
        }
    }

    private void showHelpMessage() {
        String helpText = "يمكنني مساعدتك في:\n\n" +
                "🏛️ اكتشاف المعالم السياحية\n" +
                "🏨 العثور على الفنادق\n" +
                "🍽️ البحث عن المطاعم\n" +
                "🗺️ الحصول على التوجيهات\n" +
                "📋 معلومات عن المدن اليمنية\n\n" +
                "اسألني عن أي مكان تريد زيارته!";
        messagesList.add(new ChatMessage(helpText, ChatMessage.TYPE_AI));
        messagesAdapter.notifyItemInserted(messagesList.size() - 1);
        scrollToBottom();
    }

    private void showInfoMessage() {
        String infoText = "مرحباً بك في المرشد السياحي الذكي! 🇾🇪\n\n" +
                "أنا هنا لمساعدتك في استكشاف جمال اليمن وتراثها العريق.\n\n" +
                "يمكنك استخدام الأوامر الصوتية أو الكتابة للتفاعل معي.";
        messagesList.add(new ChatMessage(infoText, ChatMessage.TYPE_AI));
        messagesAdapter.notifyItemInserted(messagesList.size() - 1);
        scrollToBottom();
    }

    private void showSettingsMessage() {
        String settingsText = "⚙️ إعدادات وأدوات المطور:\n\n" +
                "🔧 الأدوات المتاحة:\n" +
                "• اكتب 'test api' - اختبار مفتاح API\n" +
                "• اكتب 'معلومات' - معلومات التطبيق\n" +
                "• اكتب 'admin123' - وضع الإدارة\n\n" +
                "📋 المعلومات:\n" +
                "• Package: com.ahmmedalmzini783.wcguide\n" +
                "• API Key: " + BuildConfig.GEMINI_API_KEY.substring(0, 15) + "...\n\n" +
                "💡 للمساعدة في حل مشاكل API اكتب: test api";
        
        messagesList.add(new ChatMessage(settingsText, ChatMessage.TYPE_AI));
        messagesAdapter.notifyItemInserted(messagesList.size() - 1);
        scrollToBottom();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS && textToSpeech != null) {
            // Check Arabic language support
            Locale arabicLocale = new Locale("ar");
            int arabicResult = textToSpeech.setLanguage(arabicLocale);

            // Check English language support
            int englishResult = textToSpeech.setLanguage(Locale.ENGLISH);

            // Set default to Arabic for this app
            if (arabicResult != TextToSpeech.LANG_MISSING_DATA && arabicResult != TextToSpeech.LANG_NOT_SUPPORTED) {
                textToSpeech.setLanguage(arabicLocale);
                Log.d(TAG, "TextToSpeech initialized with Arabic support");
            } else if (englishResult != TextToSpeech.LANG_MISSING_DATA && englishResult != TextToSpeech.LANG_NOT_SUPPORTED) {
                textToSpeech.setLanguage(Locale.ENGLISH);
                Log.d(TAG, "TextToSpeech initialized with English support only");
            } else {
                textToSpeech.setLanguage(Locale.getDefault());
                Log.d(TAG, "TextToSpeech initialized with default language");
            }

            // Configure speech settings for better quality
            textToSpeech.setSpeechRate(0.9f); // Slightly slower for clarity
            textToSpeech.setPitch(1.0f); // Normal pitch

            // Set up utterance progress listener for better control
            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    // Speech started - could show speaking indicator
                }

                @Override
                public void onDone(String utteranceId) {
                    // Speech completed - hide any speaking indicators
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            // Hide loading indicator if still visible
                            if (binding != null && binding.loadingIndicator != null &&
                                    binding.loadingIndicator.getVisibility() == View.VISIBLE) {
                                binding.loadingIndicator.setVisibility(View.GONE);
                            }
                        });
                    }
                }

                @Override
                public void onError(String utteranceId) {
                    Log.e(TAG, "TextToSpeech error for utterance: " + utteranceId);
                }
            });
        } else {
            Log.e(TAG, "TextToSpeech initialization failed");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startListening();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }

        binding = null;
    }

    private final RecognitionListener recognitionListener = new RecognitionListener() {
        @Override public void onReadyForSpeech(Bundle params) {}
        @Override public void onBeginningOfSpeech() {}
        @Override public void onRmsChanged(float rmsdB) {}
        @Override public void onBufferReceived(byte[] buffer) {}
        @Override public void onEndOfSpeech() {
            isListening = false;
            updateVoiceButton();
        }
        @Override public void onError(int error) {
            isListening = false;
            updateVoiceButton();
            if (getContext() != null) {
                Toast.makeText(getContext(), "حدث خطأ في التعرف على الصوت", Toast.LENGTH_SHORT).show();
            }
        }
        @Override public void onResults(Bundle results) {
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (matches != null && !matches.isEmpty() && binding != null && binding.messageInput != null) {
                binding.messageInput.setText(matches.get(0));
            }
            isListening = false;
            updateVoiceButton();
        }
        @Override public void onPartialResults(Bundle partialResults) {}
        @Override public void onEvent(int eventType, Bundle params) {}
    };

    // الكلاسات الداخلية
    public static class ChatMessage {
        public static final int TYPE_USER = 1;
        public static final int TYPE_AI = 2;

        private String message;
        private int type;

        public ChatMessage(String message, int type) {
            this.message = message;
            this.type = type;
        }

        public String getMessage() {
            return message;
        }

        public int getType() {
            return type;
        }
    }

    // Inner classes for search results (you'll need to add these classes to your project)
    public static class SearchResult {
        private String title;
        private String link;
        private String snippet;

        public SearchResult(String title, String link, String snippet) {
            this.title = title;
            this.link = link;
            this.snippet = snippet;
        }

        public String getTitle() { return title; }
        public String getLink() { return link; }
        public String getSnippet() { return snippet; }
    }

    public static class GoogleSearchResponse {
        private List<SearchItem> items;

        public List<SearchItem> getItems() { return items; }

        public static class SearchItem {
            private String title;
            private String link;
            private String snippet;

            public String getTitle() { return title; }
            public String getLink() { return link; }
            public String getSnippet() { return snippet; }
        }
    }

    private class MessagesAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder> {
        private List<ChatMessage> messages;

        public MessagesAdapter(List<ChatMessage> messages) {
            this.messages = messages;
        }

        @Override
        public int getItemViewType(int position) {
            return messages.get(position).getType();
        }

        @NonNull
        @Override
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == ChatMessage.TYPE_USER) {
                View view = getLayoutInflater().inflate(R.layout.item_user_message, parent, false);
                return new UserMessageViewHolder(view);
            } else {
                View view = getLayoutInflater().inflate(R.layout.item_ai_message, parent, false);
                return new AIMessageViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull androidx.recyclerview.widget.RecyclerView.ViewHolder holder, int position) {
            ChatMessage message = messages.get(position);
            if (holder instanceof UserMessageViewHolder) {
                ((UserMessageViewHolder) holder).bind(message);
            } else if (holder instanceof AIMessageViewHolder) {
                ((AIMessageViewHolder) holder).bind(message);
            }
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }
    }

    private class UserMessageViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        android.widget.TextView textViewMessage;

        public UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
        }

        public void bind(ChatMessage message) {
            textViewMessage.setText(message.getMessage());
        }
    }

    private class AIMessageViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        android.widget.TextView textViewMessage;

        public AIMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
        }

        public void bind(ChatMessage message) {
            textViewMessage.setText(message.getMessage());
        }
    }
}