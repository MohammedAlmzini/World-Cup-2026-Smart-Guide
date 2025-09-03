# إعداد Android SDK - World Cup 2026 Guide

## 🚨 المشكلة
```
SDK location not found. Define a valid SDK location with an ANDROID_HOME environment variable or by setting the sdk.dir path in your project's local.properties file.
```

## 🔧 الحلول

### الحل 1: تثبيت Android SDK عبر Command Line Tools

#### 1.1 تحميل Command Line Tools
```bash
# إنشاء مجلد Android SDK
mkdir -p ~/Android/Sdk

# تحميل Command Line Tools
cd ~/Android/Sdk
wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip

# فك الضغط
unzip commandlinetools-linux-11076708_latest.zip

# إنشاء هيكل المجلدات المطلوب
mkdir -p cmdline-tools/latest
mv cmdline-tools/* cmdline-tools/latest/
rmdir cmdline-tools/latest/cmdline-tools
```

#### 1.2 إعداد متغيرات البيئة
```bash
# إضافة إلى ~/.bashrc أو ~/.zshrc
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/tools

# تطبيق التغييرات
source ~/.bashrc
```

#### 1.3 تثبيت SDK المطلوب
```bash
# قبول التراخيص
yes | sdkmanager --licenses

# تثبيت Android SDK Platform 34
sdkmanager "platforms;android-34"

# تثبيت Build Tools
sdkmanager "build-tools;34.0.0"

# تثبيت Platform Tools
sdkmanager "platform-tools"

# تثبيت Tools
sdkmanager "tools"
```

### الحل 2: تحديث ملف local.properties

#### 2.1 تحديد مسار SDK
```properties
# في ملف local.properties
sdk.dir=/home/ubuntu/Android/Sdk
```

#### 2.2 أو استخدام متغير البيئة
```properties
# في ملف local.properties
sdk.dir=${ANDROID_HOME}
```

### الحل 3: تثبيت Android Studio (الأسهل)

#### 3.1 تحميل Android Studio
```bash
# تحميل Android Studio
wget https://redirector.gvt1.com/edgedl/android/studio/ide-zips/2023.1.1.26/android-studio-2023.1.1.26-linux.tar.gz

# فك الضغط
tar -xzf android-studio-2023.1.1.26-linux.tar.gz

# نقل إلى /opt
sudo mv android-studio /opt/

# إنشاء رابط رمزي
sudo ln -s /opt/android-studio/bin/studio.sh /usr/local/bin/studio
```

#### 3.2 تشغيل Android Studio
```bash
# تشغيل Android Studio
studio

# اتبع Setup Wizard لتثبيت SDK
```

## 🔍 التحقق من التثبيت

### 1. التحقق من متغير البيئة
```bash
echo $ANDROID_HOME
# يجب أن يظهر: /home/ubuntu/Android/Sdk
```

### 2. التحقق من وجود SDK
```bash
ls -la $ANDROID_HOME
# يجب أن يحتوي على:
# - platforms/
# - build-tools/
# - platform-tools/
# - tools/
```

### 3. التحقق من sdkmanager
```bash
sdkmanager --list
# يجب أن يعرض قائمة بالحزم المتاحة
```

## 📋 قائمة التحقق

- [ ] تم تثبيت Android SDK
- [ ] تم إعداد متغير ANDROID_HOME
- [ ] تم تحديث ملف local.properties
- [ ] تم تثبيت Android Platform 34
- [ ] تم تثبيت Build Tools
- [ ] تم قبول التراخيص

## 🚀 بعد التثبيت

### 1. إعادة بناء المشروع
```bash
./gradlew clean
./gradlew assembleDebug
```

### 2. اختبار التطبيق
```bash
# إنشاء محاكي
avdmanager create avd -n test_device -k "system-images;android-34;google_apis;x86_64"

# تشغيل المحاكي
emulator -avd test_device

# تثبيت التطبيق
./gradlew installDebug
```

## ⚠️ ملاحظات مهمة

### 1. متطلبات النظام
- **الذاكرة**: 8GB RAM على الأقل
- **مساحة التخزين**: 10GB مساحة خالية
- **نظام التشغيل**: Linux (Ubuntu 18.04+)

### 2. متطلبات Java
```bash
# التحقق من إصدار Java
java -version
# يجب أن يكون Java 17 أو أحدث
```

### 3. متطلبات Gradle
```bash
# التحقق من إصدار Gradle
./gradlew --version
# يجب أن يكون Gradle 8.2 أو أحدث
```

## 🔗 روابط مفيدة

- [Android Developer Documentation](https://developer.android.com/studio/command-line)
- [Android SDK Command Line Tools](https://developer.android.com/studio#command-tools)
- [Android Studio Download](https://developer.android.com/studio)

---

**ملاحظة**: بعد تثبيت Android SDK، تأكد من إعادة تشغيل Terminal أو تطبيق متغيرات البيئة.