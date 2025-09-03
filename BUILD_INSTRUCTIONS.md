# تعليمات البناء والتشغيل - World Cup 2026 Guide

## المتطلبات الأساسية

### 1. أدوات التطوير
- **Android Studio**: Arctic Fox (2020.3.1) أو أحدث
- **Java Development Kit (JDK)**: الإصدار 17
- **Android SDK**: الإصدار 34 أو أحدث
- **Gradle**: الإصدار 8.2 أو أحدث

### 2. متطلبات النظام
- **نظام التشغيل**: Windows 10/11, macOS 10.15+, أو Linux
- **الذاكرة**: 8GB RAM على الأقل (16GB موصى به)
- **مساحة التخزين**: 10GB مساحة خالية على الأقل

### 3. خدمات Google
- **Google Play Services**: محدثة
- **Google Maps API Key**: صالح ومفعل
- **Firebase Project**: مُعد ومُفعّل

## الخطوة 1: إعداد البيئة

### 1.1 تثبيت Android Studio
1. حمل Android Studio من [الموقع الرسمي](https://developer.android.com/studio)
2. اتبع خطوات التثبيت
3. عند أول تشغيل، اتبع Setup Wizard
4. تأكد من تثبيت:
   - Android SDK Platform 34
   - Android SDK Build-Tools
   - Android Emulator
   - Android SDK Platform-Tools

### 1.2 إعداد متغيرات البيئة
#### Windows
```batch
set ANDROID_HOME=C:\Users\YourUsername\AppData\Local\Android\Sdk
set PATH=%PATH%;%ANDROID_HOME%\platform-tools
set PATH=%PATH%;%ANDROID_HOME%\tools
```

#### macOS/Linux
```bash
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/tools
```

## الخطوة 2: استنساخ المشروع

### 2.1 استنساخ الكود
```bash
git clone https://github.com/yourusername/world-cup-2026-guide.git
cd world-cup-2026-guide
```

### 2.2 فتح المشروع في Android Studio
1. افتح Android Studio
2. اختر "Open an existing Android Studio project"
3. اختر مجلد المشروع
4. انتظر حتى يكتمل تحميل Gradle

## الخطوة 3: إعداد المفاتيح والبيانات

### 3.1 إعداد ملف local.properties
أنشئ أو عدّل ملف `local.properties` في مجلد المشروع:

```properties
# SDK Location
sdk.dir=/path/to/your/android/sdk

# API Keys
MAPS_API_KEY=your_google_maps_api_key_here
OPENAI_API_KEY=your_openai_api_key_here
GEMINI_API_KEY=your_gemini_api_key_here

# Firebase Configuration
FIREBASE_PROJECT_ID=world-cup-2026-guide
FIREBASE_STORAGE_BUCKET=world-cup-2026-guide.appspot.com
FIREBASE_DATABASE_URL=https://world-cup-2026-guide-default-rtdb.firebaseio.com
```

### 3.2 إعداد ملف google-services.json
1. اذهب إلى [Firebase Console](https://console.firebase.google.com/)
2. اختر مشروعك أو أنشئ مشروع جديد
3. أضف تطبيق Android جديد
4. حمل ملف `google-services.json`
5. ضع الملف في مجلد `app/`

### 3.3 الحصول على المفاتيح المطلوبة

#### Google Maps API Key
1. اذهب إلى [Google Cloud Console](https://console.cloud.google.com/)
2. أنشئ مشروع جديد أو اختر مشروع موجود
3. فعّل Maps SDK for Android
4. أنشئ credentials → API Key
5. قيّد المفتاح لتطبيقات Android واسم الحزمة

#### OpenAI API Key (اختياري)
1. اذهب إلى [OpenAI Platform](https://platform.openai.com/api-keys)
2. سجل حساب جديد أو سجل دخول
3. أنشئ API Key جديد
4. أضف معلومات الفواتير

#### Gemini API Key (بديل لـ OpenAI)
1. اذهب إلى [Google AI Studio](https://makersuite.google.com/app/apikey)
2. أنشئ API Key جديد
3. فعّل Gemini Pro API

## الخطوة 4: إعداد Firebase

### 4.1 إنشاء مشروع Firebase
راجع ملف `FIREBASE_SETUP.md` للحصول على تعليمات مفصلة.

### 4.2 رفع البيانات الوهمية
1. اذهب إلى Firebase Console → Realtime Database
2. ارفع البيانات من ملف `sample-data.json`
3. تأكد من صحة قواعد الأمان

## الخطوة 5: بناء المشروع

### 5.1 تنظيف المشروع
```bash
# في مجلد المشروع
./gradlew clean
```

### 5.2 بناء المشروع
```bash
# بناء APK للتطوير
./gradlew assembleDebug

# بناء APK للإنتاج
./gradlew assembleRelease
```

### 5.3 بناء من Android Studio
1. اختر Build → Clean Project
2. اختر Build → Rebuild Project
3. انتظر حتى يكتمل البناء

## الخطوة 6: تشغيل التطبيق

### 6.1 إعداد المحاكي
1. في Android Studio، اختر Tools → AVD Manager
2. اضغط "Create Virtual Device"
3. اختر جهاز (مثل Pixel 7)
4. اختر نظام تشغيل (API 34 موصى به)
5. اضغط "Finish"

### 6.2 تشغيل التطبيق
1. اختر المحاكي من القائمة المنسدلة
2. اضغط زر "Run" (الزر الأخضر)
3. انتظر حتى يفتح المحاكي ويشغل التطبيق

### 6.3 تشغيل على جهاز حقيقي
1. فعّل "Developer Options" على هاتفك
2. فعّل "USB Debugging"
3. اربط الهاتف بالكمبيوتر
4. اختر هاتفك من قائمة الأجهزة
5. اضغط "Run"

## الخطوة 7: اختبار التطبيق

### 7.1 اختبار المميزات الأساسية
- [ ] العد التنازلي يعمل ويحدث كل ثانية
- [ ] البطاقات الإعلانية تظهر بشكل صحيح
- [ ] البيانات تُحمل من Firebase
- [ ] التنقل بين الشاشات يعمل
- [ ] الخرائط تعرض المواقع

### 7.2 اختبار الاتصال بـ Firebase
1. افتح Firebase Console
2. اذهب إلى Realtime Database
3. راقب البيانات في الوقت الفعلي
4. تحقق من الأخطاء في Logcat

### 7.3 اختبار الأداء
```bash
# تحليل الأداء
./gradlew assembleRelease
# ثم استخدم Android Studio Profiler
```

## الخطوة 8: استكشاف الأخطاء

### 8.1 أخطاء شائعة وحلولها

#### خطأ في Gradle Sync
```
Error: Could not resolve all dependencies
```
**الحل:**
1. تحقق من اتصال الإنترنت
2. امسح cache: File → Invalidate Caches
3. Sync Project with Gradle Files

#### خطأ في Google Services
```
File google-services.json is missing
```
**الحل:**
1. تأكد من وجود الملف في `app/google-services.json`
2. تحقق من صحة محتوى الملف
3. Sync Project with Gradle Files

#### خطأ في Maps API
```
Maps API key is invalid
```
**الحل:**
1. تحقق من صحة المفتاح في `local.properties`
2. تأكد من تفعيل Maps SDK for Android
3. تحقق من قيود المفتاح

#### خطأ في Firebase
```
Firebase connection failed
```
**الحل:**
1. تحقق من صحة `google-services.json`
2. تأكد من تفعيل خدمات Firebase
3. تحقق من قواعد الأمان

### 8.2 أدوات التشخيص

#### Logcat
```bash
# عرض الأخطاء
adb logcat | grep -E "(ERROR|FATAL)"
```

#### Gradle Debug
```bash
# بناء مع معلومات تفصيلية
./gradlew assembleDebug --info
```

#### Firebase Debug
```bash
# تفعيل وضع التطوير
adb shell setprop debug.firebase.analytics.app com.ahmmedalmzini783.wcguide
```

## الخطوة 9: النشر

### 9.1 إنشاء APK للإنتاج
```bash
# إنشاء APK مُوقّع
./gradlew assembleRelease
```

### 9.2 إنشاء Bundle للإنتاج
```bash
# إنشاء AAB
./gradlew bundleRelease
```

### 9.3 توقيع التطبيق
1. أنشئ keystore جديد
2. عدّل `build.gradle` لإضافة إعدادات التوقيع
3. أنشئ APK مُوقّع

## الخطوة 10: المراقبة والصيانة

### 10.1 مراقبة الأداء
- استخدم Firebase Performance Monitoring
- راقب Crashlytics للأخطاء
- تحقق من Analytics للاستخدام

### 10.2 تحديث البيانات
- ارفع بيانات جديدة إلى Firebase
- حدث الصور في Storage
- راقب استخدام API

### 10.3 النسخ الاحتياطية
- احتفظ بنسخة من `google-services.json`
- احتفظ بنسخة من المفاتيح
- احتفظ بنسخة من البيانات الوهمية

## روابط مفيدة

- [Android Developer Documentation](https://developer.android.com/docs)
- [Firebase Documentation](https://firebase.google.com/docs)
- [Google Maps API Documentation](https://developers.google.com/maps/documentation/android-sdk)
- [Material Design Guidelines](https://material.io/design)

---

**ملاحظة**: تأكد من اختبار التطبيق على أجهزة مختلفة وأحجام شاشات مختلفة قبل النشر.