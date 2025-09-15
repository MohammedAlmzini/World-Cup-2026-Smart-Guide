# إرشادات إعداد Gemini API لتطبيقات Android

## المشكلة الحالية
`"Requests from this Android client application are blocked"`

## الحل: إعداد Google Console بشكل صحيح

### الخطوة 1: الوصول إلى Google Cloud Console
1. اذهب إلى [Google Cloud Console](https://console.cloud.google.com/)
2. تأكد من أنك في المشروع الصحيح

### الخطوة 2: تفعيل APIs المطلوبة
1. اذهب إلى **APIs & Services** > **Library**
2. ابحث عن وفعّل:
   - ✅ **Generative Language API** (للـ Gemini)
   - ✅ **AI Platform API** (إضافي)

### الخطوة 3: إعداد API Key (الأهم)
1. اذهب إلى **APIs & Services** > **Credentials**
2. اختر API Key الخاص بك: `AIzaSyC5DxLaRJodS_VABJqKMaUB2AqQQ8oB1AU`
3. اضغط على **Edit** (✏️)

### الخطوة 4: إضافة Application Restrictions
في صفحة إعداد API Key:

#### 4.1 Application restrictions:
اختر **Android apps** ثم أضف:

```
Package name: com.ahmmedalmzini783.worldcpguide
SHA-1 certificate fingerprint: [احصل عليه من Android Studio]
```

#### 4.2 للحصول على SHA-1:
في Android Studio Terminal:
```bash
cd android
./gradlew signingReport
```
أو:
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

#### 4.3 API restrictions:
اختر **Restrict key** وأضف:
- ✅ **Generative Language API**
- ✅ **AI Platform API** (اختياري)

### الخطوة 5: حفظ الإعدادات
اضغط **Save** وانتظر 5-10 دقائق حتى تنطبق التغييرات.

## الحل البديل المؤقت: REST API

إذا لم تعمل الطريقة أعلاه، يمكن استخدام Gemini REST API مباشرة:

### إعداد REST API:
```java
private void sendToGeminiREST(String userMessage, List<SearchResult> searchResults) {
    String apiKey = BuildConfig.GEMINI_API_KEY;
    String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;
    
    try {
        JSONObject requestBody = new JSONObject();
        JSONArray contents = new JSONArray();
        JSONObject content = new JSONObject();
        JSONArray parts = new JSONArray();
        JSONObject part = new JSONObject();
        
        String prompt = buildFullPrompt(getSystemPrompt(), userMessage, searchResults);
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

        // تنفيذ الطلب مع OkHttp...
    } catch (Exception e) {
        Log.e(TAG, "Error creating Gemini REST request", e);
    }
}
```

## تحقق من الإعدادات

### 1. تأكد من صحة API Key:
```bash
curl -H 'Content-Type: application/json' \
     -d '{"contents":[{"parts":[{"text":"Hello"}]}]}' \
     -X POST "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=AIzaSyC5DxLaRJodS_VABJqKMaUB2AqQQ8oB1AU"
```

### 2. إذا حصلت على error 403:
- تحقق من Application restrictions
- تأكد من SHA-1 fingerprint
- انتظر 10 دقائق وحاول مرة أخرى

### 3. إذا حصلت على error 404:
- تأكد من تفعيل Generative Language API
- تحقق من API restrictions

## إعدادات بديلة في local.properties

إذا أردت استخدام OpenAI كبديل (اختياري):
```properties
# Gemini (primary)
GEMINI_API_KEY=AIzaSyC5DxLaRJodS_VABJqKMaUB2AqQQ8oB1AU

# OpenAI (backup - optional)
OPENAI_API_KEY=sk-your-openai-key-here
```

## ملاحظات مهمة

1. **وقت التطبيق**: قد تستغرق التغييرات في Google Console 5-10 دقائق
2. **SHA-1**: يجب أن يكون من نفس keystore المستخدم في التطبيق
3. **Package Name**: يجب أن يطابق `applicationId` في `build.gradle`
4. **API Quota**: تحقق من حدود الاستخدام اليومية

## الخلاصة
المشكلة غالباً في إعدادات Google Console وليس في الكود. بعد ضبط Application Restrictions بشكل صحيح، يجب أن يعمل Gemini API بدون مشاكل.