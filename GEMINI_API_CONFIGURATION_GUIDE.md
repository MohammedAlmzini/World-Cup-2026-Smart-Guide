# دليل حل مشكلة Gemini API - خطأ 403

## 🚨 المشكلة
- مفتاح API يعمل في تطبيق آخر لكن يظهر خطأ 403 في هذا التطبيق
- رسالة الخطأ: "حدث خطأ في الاتصال بالذكاء الاصطناعي"
- Package Name: `com.ahmmedalmzini783.wcguide`
- API Key: `AIzaSyAwN1ct7lm0LsnOlsxpkUd5EOEI4UgI9GU`

## 🔧 الحل الكامل

### الخطوة 1: الوصول إلى Google Cloud Console
1. اذهب إلى: https://console.cloud.google.com/
2. قم بتسجيل الدخول بنفس الحساب المستخدم لإنشاء API Key
3. تأكد من اختيار نفس المشروع المرتبط بمفتاح API

### الخطوة 2: الانتقال إلى APIs & Services
1. من القائمة الجانبية، اختر "APIs & Services"
2. اختر "Credentials"
3. ابحث عن مفتاح API الخاص بك: `AIzaSyAwN1ct7lm0LsnOlsxpkUd5EOEI4UgI9GU`

### الخطوة 3: تعديل Application Restrictions
1. انقر على مفتاح API لفتح إعداداته
2. في قسم "Application restrictions":
   - إما اختر "None" (غير محدود - الأسهل)
   - أو اختر "Android apps" وأضف التطبيق التالي:
     - Package name: `com.ahmmedalmzini783.wcguide`
     - SHA-1 certificate fingerprint: احصل عليه من Android Studio

### الخطوة 4: تفعيل API المطلوب
1. اذهب إلى "APIs & Services" > "Library"
2. ابحث عن "Generative Language API"
3. تأكد من أنه مفعل (Enabled)

### الخطوة 5: احصل على SHA-1 Fingerprint (إذا لزم الأمر)
في Android Studio:
```bash
./gradlew signingReport
```
أو من Terminal:
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

## ✅ اختبار التكوين

### في التطبيق:
1. اكتب "test api" في المحادثة
2. سيظهر تقرير شامل عن حالة API
3. إذا ظهرت رسالة "API Test Successful" فالتكوين صحيح

### أوامر الاختبار المتاحة:
- `test api` - اختبار شامل لـ API
- `settings` - إعدادات المطور
- `معلومات` - معلومات التطبيق

## 🔍 استكشاف الأخطاء

### خطأ 403 - Forbidden
**السبب:** Application Restrictions تمنع استخدام API من هذا التطبيق
**الحل:** اتبع الخطوة 3 أعلاه

### خطأ 400 - Bad Request
**السبب:** API غير مفعل أو مفتاح خاطئ
**الحل:** تحقق من الخطوة 4 أو استبدال المفتاح

### خطأ 429 - Too Many Requests
**السبب:** تجاوز حد الاستخدام المسموح
**الحل:** انتظر أو ارفع الحد في Console

## 📋 معلومات المشروع

- **Package Name:** `com.ahmmedalmzini783.wcguide`
- **API Used:** Generative Language API (Gemini)
- **Current API Key:** `AIzaSyAwN1ct7lm0LsnOlsxpkUd5EOEI4UgI9GU`
- **Implementation:** REST API calls to `generativelanguage.googleapis.com`

## 🎯 التحقق النهائي

بعد إجراء التغييرات:
1. انتظر 5-10 دقائق لتفعيل التغييرات
2. اختبر API من خلال كتابة "test api"
3. إذا استمر الخطأ، تحقق من:
   - صحة مفتاح API
   - تفعيل Generative Language API
   - صحة Package Name في Restrictions

## 📞 الدعم

إذا استمرت المشكلة:
1. تحقق من Google Cloud Console Logs
2. جرب إنشاء مفتاح API جديد
3. تأكد من صحة billing account في المشروع

---
**ملاحظة:** هذا الدليل خاص بحل مشكلة API key `AIzaSyAwN1ct7lm0LsnOlsxpkUd5EOEI4UgI9GU` مع تطبيق `com.ahmmedalmzini783.wcguide`