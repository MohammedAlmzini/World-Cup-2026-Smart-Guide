# بدء سريع - World Cup 2026 Guide

## 🚀 الخطوات السريعة

### 1. تثبيت Android SDK (إذا لم يكن مثبتاً)

```bash
# إنشاء مجلد SDK
mkdir -p ~/Android/Sdk
cd ~/Android/Sdk

# تحميل Command Line Tools
wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
unzip commandlinetools-linux-11076708_latest.zip

# إعداد هيكل المجلدات
mkdir -p cmdline-tools/latest
mv cmdline-tools/* cmdline-tools/latest/
rmdir cmdline-tools/latest/cmdline-tools

# إعداد متغيرات البيئة
echo 'export ANDROID_HOME=$HOME/Android/Sdk' >> ~/.bashrc
echo 'export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin' >> ~/.bashrc
echo 'export PATH=$PATH:$ANDROID_HOME/platform-tools' >> ~/.bashrc
source ~/.bashrc

# تثبيت SDK المطلوب
yes | sdkmanager --licenses
sdkmanager "platforms;android-34" "build-tools;34.0.0" "platform-tools" "tools"
```

### 2. إعداد Firebase

1. اذهب إلى [Firebase Console](https://console.firebase.google.com/)
2. أنشئ مشروع جديد باسم `world-cup-2026-guide`
3. أضف تطبيق Android جديد
4. حمل `google-services.json` وضعه في مجلد `app/`
5. فعّل خدمات Authentication و Realtime Database و Storage

### 3. إعداد المفاتيح

```bash
# تحديث ملف local.properties
cat > local.properties << EOF
# SDK Location
sdk.dir=/home/ubuntu/Android/Sdk

# API Keys (أضف مفاتيحك الحقيقية)
MAPS_API_KEY=your_google_maps_api_key_here
OPENAI_API_KEY=your_openai_api_key_here
GEMINI_API_KEY=your_gemini_api_key_here

# Firebase Configuration
FIREBASE_PROJECT_ID=world-cup-2026-guide
FIREBASE_STORAGE_BUCKET=world-cup-2026-guide.appspot.com
FIREBASE_DATABASE_URL=https://world-cup-2026-guide-default-rtdb.firebaseio.com
EOF
```

### 4. رفع البيانات إلى Firebase

1. اذهب إلى Firebase Console → Realtime Database
2. ارفع البيانات من ملف `sample-data.json`
3. تطبيق قواعد الأمان من ملف `firebase-rules.json`

### 5. بناء وتشغيل التطبيق

```bash
# تنظيف المشروع
./gradlew clean

# بناء APK
./gradlew assembleDebug

# تشغيل على محاكي أو جهاز
./gradlew installDebug
```

## 📋 قائمة التحقق السريعة

- [ ] Android SDK مثبت
- [ ] Firebase مشروع مُعد
- [ ] google-services.json في مجلد app/
- [ ] local.properties محدث
- [ ] البيانات مرفوعة إلى Firebase
- [ ] التطبيق يُبنى بنجاح

## 🔧 استكشاف الأخطاء السريع

### خطأ: SDK location not found
```bash
# تأكد من وجود SDK
ls -la ~/Android/Sdk

# تأكد من متغير البيئة
echo $ANDROID_HOME
```

### خطأ: Firebase connection failed
```bash
# تأكد من وجود google-services.json
ls -la app/google-services.json

# تأكد من تفعيل خدمات Firebase
```

### خطأ: Build failed
```bash
# تنظيف وإعادة بناء
./gradlew clean
./gradlew assembleDebug --info
```

## 🎯 النتيجة المتوقعة

بعد اتباع هذه الخطوات:
- ✅ العد التنازلي يعمل بالثانية والدقيقة والساعة واليوم
- ✅ 3 بطاقات إعلانية تظهر بشكل جميل
- ✅ البيانات تُحمل من Firebase
- ✅ التطبيق يعمل بدون مشاكل

## 📞 الدعم السريع

للمساعدة السريعة:
- راجع `ANDROID_SDK_SETUP.md` لتثبيت SDK
- راجع `FIREBASE_SETUP.md` لإعداد Firebase
- راجع `QUICK_FIX.md` لحل مشاكل Firebase

---

**ملاحظة**: هذه الخطوات مخصصة لبيئة Linux. لبيئات أخرى، راجع الملفات التفصيلية.