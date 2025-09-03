# دليل إعداد Firebase - World Cup 2026 Guide

## الخطوة 1: إنشاء مشروع Firebase

### 1.1 الوصول إلى Firebase Console
1. اذهب إلى [Firebase Console](https://console.firebase.google.com/)
2. اضغط على "إنشاء مشروع" أو "Add project"

### 1.2 إعداد المشروع
1. **اسم المشروع**: `world-cup-2026-guide`
2. **معرف المشروع**: `world-cup-2026-guide` (سيتم إنشاؤه تلقائياً)
3. **تفعيل Google Analytics**: اختياري (موصى به)
4. اضغط "إنشاء المشروع"

## الخطوة 2: إضافة تطبيق Android

### 2.1 إعداد التطبيق
1. في لوحة التحكم، اضغط على أيقونة Android
2. **Package name**: `com.ahmmedalmzini783.wcguide`
3. **App nickname**: `World Cup 2026 Guide` (اختياري)
4. **Debug signing certificate SHA-1**: (سنضيفه لاحقاً)
5. اضغط "تسجيل التطبيق"

### 2.2 تحميل ملف google-services.json
1. سيتم تحميل ملف `google-services.json` تلقائياً
2. انسخ الملف إلى مجلد `app/` في مشروعك
3. تأكد من أن الملف موجود في المسار: `app/google-services.json`

## الخطوة 3: تفعيل الخدمات المطلوبة

### 3.1 Authentication
1. في القائمة الجانبية، اختر "Authentication"
2. اضغط "Get started"
3. فعّل "Email/Password"
4. فعّل "Google" (اختياري)
5. في إعدادات Google، أضف SHA-1 fingerprint

### 3.2 Realtime Database
1. اختر "Realtime Database"
2. اضغط "Create database"
3. اختر "Start in test mode" (سنغير القواعد لاحقاً)
4. اختر موقع قاعدة البيانات (الأقرب لموقعك)

### 3.3 Storage
1. اختر "Storage"
2. اضغط "Get started"
3. اختر "Start in test mode"
4. اختر موقع التخزين

### 3.4 Cloud Messaging (FCM)
1. اختر "Cloud Messaging"
2. اضغط "Get started"
3. احفظ Server Key (ستحتاجه لاحقاً)

## الخطوة 4: إعداد قواعد الأمان

### 4.1 Realtime Database Rules
1. في Realtime Database، اختر "Rules"
2. استبدل القواعد الحالية بما يلي:

```json
{
  "rules": {
    ".read": true,
    ".write": "auth != null",
    
    "users": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid",
        "profile": {
          ".read": "$uid === auth.uid",
          ".write": "$uid === auth.uid"
        },
        "favorites": {
          ".read": "$uid === auth.uid",
          ".write": "$uid === auth.uid"
        },
        "reviews": {
          ".read": "$uid === auth.uid",
          ".write": "$uid === auth.uid"
        }
      }
    },
    
    "events": {
      ".read": true,
      ".write": "auth != null && root.child('users').child(auth.uid).child('roles').child('admin').val() === true"
    },
    
    "places": {
      ".read": true,
      ".write": "auth != null && root.child('users').child(auth.uid).child('roles').child('admin').val() === true"
    },
    
    "banners": {
      ".read": true,
      ".write": "auth != null && root.child('users').child(auth.uid).child('roles').child('admin').val() === true"
    },
    
    "quickInfo": {
      ".read": true,
      ".write": "auth != null && root.child('users').child(auth.uid).child('roles').child('admin').val() === true"
    },
    
    "reviews": {
      "$targetId": {
        ".read": true,
        "$reviewId": {
          ".read": true,
          ".write": "auth != null && newData.child('userId').val() === auth.uid",
          ".validate": "newData.hasChildren(['userId', 'targetId', 'rating', 'comment', 'timestamp'])"
        }
      }
    },
    
    "notifications": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid"
      }
    }
  }
}
```

### 4.2 Storage Rules
1. في Storage، اختر "Rules"
2. استبدل القواعد بما يلي:

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read: if true;
      allow write: if request.auth != null;
    }
  }
}
```

## الخطوة 5: رفع البيانات الوهمية

### 5.1 رفع البيانات إلى Realtime Database
1. في Realtime Database، اضغط على "Data"
2. اضغط على أيقونة "+" لإضافة عقدة جديدة
3. ارفع محتوى ملف `sample-data.json` عقدة تلو الأخرى:

#### البطاقات الإعلانية (Banners)
```json
{
  "banners": {
    "banner_001": {
      "id": "banner_001",
      "title": "احجز تذاكر كأس العالم 2026",
      "imageUrl": "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=800&h=400&fit=crop",
      "deeplink": "app://tickets/world_cup_2026"
    },
    "banner_002": {
      "id": "banner_002",
      "title": "مناطق المشجعين - أحداث مثيرة",
      "imageUrl": "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=800&h=400&fit=crop",
      "deeplink": "app://events/fan_zones"
    },
    "banner_003": {
      "id": "banner_003",
      "title": "باقات السفر الحصرية",
      "imageUrl": "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=800&h=400&fit=crop",
      "deeplink": "app://travel/packages"
    }
  }
}
```

#### الأحداث (Events)
```json
{
  "events": {
    "event_001": {
      "id": "event_001",
      "title": "افتتاح كأس العالم 2026",
      "description": "حفل الافتتاح الرسمي لكأس العالم 2026 في ملعب ميتلايف بنيويورك",
      "startTime": 1761325200000,
      "endTime": 1761332400000,
      "venue": "ملعب ميتلايف",
      "city": "نيويورك",
      "country": "US",
      "type": "ceremony",
      "capacity": 82500,
      "imageUrl": "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=800&h=400&fit=crop",
      "lat": 40.8136,
      "lng": -74.0744
    }
  }
}
```

#### الأماكن (Places)
```json
{
  "places": {
    "place_001": {
      "id": "place_001",
      "name": "تمثال الحرية",
      "kind": "attraction",
      "country": "US",
      "city": "نيويورك",
      "address": "Liberty Island, New York, NY 10004",
      "lat": 40.6892,
      "lng": -74.0445,
      "avgRating": 4.8,
      "ratingCount": 12500,
      "priceLevel": 2,
      "description": "أشهر معلم سياحي في نيويورك ورمز للحرية والديمقراطية",
      "images": [
        "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=800&h=400&fit=crop"
      ],
      "amenities": ["مطعم", "متجر هدايا", "مرشد سياحي", "متحف"]
    }
  }
}
```

#### المعلومات السريعة (Quick Info)
```json
{
  "quickInfo": {
    "US": {
      "countryCode": "US",
      "countryName": "الولايات المتحدة الأمريكية",
      "currency": "الدولار الأمريكي (USD)",
      "languages": "الإنجليزية",
      "transport": "مترو، حافلات، سيارات أجرة",
      "weather": "متنوع حسب المنطقة - صيف حار في الجنوب، معتدل في الشمال"
    },
    "CA": {
      "countryCode": "CA",
      "countryName": "كندا",
      "currency": "الدولار الكندي (CAD)",
      "languages": "الإنجليزية، الفرنسية",
      "transport": "مترو، حافلات، قطارات",
      "weather": "بارد في الشتاء، معتدل في الصيف"
    },
    "MX": {
      "countryCode": "MX",
      "countryName": "المكسيك",
      "currency": "البيزو المكسيكي (MXN)",
      "languages": "الإسبانية",
      "transport": "مترو، حافلات، سيارات أجرة",
      "weather": "استوائي، حار ورطب"
    }
  }
}
```

### 5.2 رفع الصور إلى Storage
1. في Storage، أنشئ مجلد `images/`
2. ارفع الصور التالية:
   - `banner_001.jpg` - صورة البطاقة الإعلانية الأولى
   - `banner_002.jpg` - صورة البطاقة الإعلانية الثانية
   - `banner_003.jpg` - صورة البطاقة الإعلانية الثالثة
   - `liberty_statue.jpg` - صورة تمثال الحرية
   - `waldorf_hotel.jpg` - صورة فندق والدورف

## الخطوة 6: إعداد SHA-1 Fingerprint

### 6.1 الحصول على SHA-1
```bash
# في مجلد المشروع
cd android
./gradlew signingReport
```

### 6.2 إضافة SHA-1 إلى Firebase
1. في Firebase Console، اختر مشروعك
2. اختر "Project settings"
3. في قسم "Your apps"، اختر تطبيق Android
4. اضغط "Add fingerprint"
5. أضف SHA-1 fingerprint

## الخطوة 7: اختبار الاتصال

### 7.1 اختبار التطبيق
1. شغل التطبيق على جهاز أو محاكي
2. تأكد من ظهور البطاقات الإعلانية
3. تأكد من عمل العد التنازلي
4. تأكد من تحميل البيانات من Firebase

### 7.2 مراقبة الاستخدام
1. في Firebase Console، اختر "Analytics"
2. راقب الأحداث والاستخدام
3. تحقق من الأخطاء في "Crashlytics"

## الخطوة 8: الإعدادات المتقدمة

### 8.1 إعداد Cloud Functions (اختياري)
```javascript
// functions/index.js
const functions = require('firebase-functions');

exports.sendNotification = functions.database
  .ref('/events/{eventId}')
  .onCreate((snapshot, context) => {
    // إرسال إشعار عند إنشاء حدث جديد
  });
```

### 8.2 إعداد Hosting (اختياري)
```bash
npm install -g firebase-tools
firebase login
firebase init hosting
```

## استكشاف الأخطاء

### مشاكل شائعة وحلولها

#### 1. خطأ في الاتصال بـ Firebase
- تأكد من وجود ملف `google-services.json` في المكان الصحيح
- تحقق من صحة معرف المشروع
- تأكد من تفعيل خدمات Firebase

#### 2. خطأ في قراءة البيانات
- تحقق من قواعد الأمان في Realtime Database
- تأكد من وجود البيانات في قاعدة البيانات
- تحقق من صحة هيكل البيانات

#### 3. خطأ في تحميل الصور
- تحقق من قواعد Storage
- تأكد من صحة روابط الصور
- تحقق من إعدادات CORS

#### 4. خطأ في Authentication
- تأكد من تفعيل Authentication
- تحقق من إضافة SHA-1 fingerprint
- تأكد من صحة إعدادات Google Sign-In

## روابط مفيدة

- [Firebase Documentation](https://firebase.google.com/docs)
- [Android Setup Guide](https://firebase.google.com/docs/android/setup)
- [Realtime Database Rules](https://firebase.google.com/docs/database/security)
- [Storage Rules](https://firebase.google.com/docs/storage/security)
- [Authentication Setup](https://firebase.google.com/docs/auth/android/start)

---

**ملاحظة**: تأكد من تحديث قواعد الأمان قبل النشر للإنتاج وإضافة المزيد من القيود الأمنية.