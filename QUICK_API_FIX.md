# حل سريع لمشكلة Gemini API - خطأ 403

## 🚨 المشكلة
مفتاح API يعمل في تطبيق آخر لكن يظهر خطأ 403 في هذا التطبيق

## ⚡ الحل السريع (5 دقائق)

### 1. اذهب إلى Google Cloud Console
- الرابط: https://console.cloud.google.com/
- سجل دخول بنفس حساب API Key

### 2. اذهب إلى API Settings
- APIs & Services → Credentials
- انقر على مفتاح API: `AIzaSyAwN1ct7lm0LsnOlsxpkUd5EOEI4UgI9GU`

### 3. أزل القيود (الحل الأسرع)
- في "Application restrictions"
- اختر **"None"** بدلاً من Android apps
- احفظ التغييرات

### 4. اختبر التطبيق
- اكتب "test api" في المحادثة
- يجب أن يعمل الآن! ✅

## 🔄 إذا لم يعمل

### الحل البديل - إضافة التطبيق:
1. في "Application restrictions" اختر "Android apps"
2. أضف:
   - Package: `com.ahmmedalmzini783.wcguide`
   - SHA-1: (اختياري، يمكن تركه فارغ للاختبار)

### تأكد من:
- ✅ Generative Language API مفعل
- ✅ مفتاح API صحيح
- ✅ انتظر 5 دقائق بعد التغيير

---
**💡 نصيحة:** اختر "None" في Application restrictions هو الحل الأسرع للاختبار!