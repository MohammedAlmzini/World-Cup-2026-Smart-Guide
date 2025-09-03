# World Cup 2026 Smart Guide

تطبيق ذكي لمرشد كأس العالم 2026 يوفر معلومات شاملة عن الأحداث والفنادق والمطاعم والمعالم السياحية.

## المميزات الرئيسية

### 🏠 الصفحة الرئيسية (Home)
- **العد التنازلي**: يعرض العد التنازلي لكأس العالم 2026 باليوم والساعة والدقيقة والثانية
- **البطاقات الإعلانية**: 3 بطاقات إعلانية وهمية مؤقتة مع صور وعناوين جذابة
- **أقسام متعددة**: المعالم السياحية، الفنادق، المطاعم، المعلومات السريعة

### 🎫 الأحداث (Events)
- عرض جميع أحداث كأس العالم 2026
- تصفية حسب البلد والمدينة والنوع والتاريخ
- تفاصيل شاملة لكل حدث

### 🤖 المساعد الذكي (AI Assistant)
- مساعد ذكي للإجابة على الأسئلة
- توليد خطط يومية للسفر
- ترجمة فورية
- إدخال صوتي

### 👤 الملف الشخصي (Profile)
- إدارة الملف الشخصي
- المفضلة والمراجعات
- المدن المفضلة

## إعداد Firebase

### 1. إنشاء مشروع Firebase
1. اذهب إلى [Firebase Console](https://console.firebase.google.com/)
2. أنشئ مشروع جديد باسم `world-cup-2026-guide`
3. فعّل خدمات Authentication و Realtime Database و Storage

### 2. إعداد Android App
1. أضف تطبيق Android جديد
2. استخدم Package Name: `com.ahmmedalmzini783.wcguide`
3. حمل ملف `google-services.json` وضعه في مجلد `app/`

### 3. إعداد Realtime Database
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
  },
  "events": {
    "event_001": {
      "id": "event_001",
      "title": "افتتاح كأس العالم 2026",
      "description": "حفل الافتتاح الرسمي لكأس العالم 2026",
      "startTime": 1761325200000,
      "endTime": 1761332400000,
      "venue": "ملعب ميتلايف",
      "city": "نيويورك",
      "country": "US",
      "type": "ceremony",
      "capacity": 82500,
      "imageUrl": "https://example.com/opening.jpg"
    }
  },
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
      "description": "أشهر معلم سياحي في نيويورك",
      "images": ["https://example.com/liberty1.jpg", "https://example.com/liberty2.jpg"],
      "amenities": ["مطعم", "متجر هدايا", "مرشد سياحي"]
    }
  },
  "quickInfo": {
    "US": {
      "countryCode": "US",
      "countryName": "الولايات المتحدة الأمريكية",
      "currency": "الدولار الأمريكي (USD)",
      "languages": "الإنجليزية",
      "transport": "مترو، حافلات، سيارات أجرة",
      "weather": "متنوع حسب المنطقة"
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

### 4. إعداد Authentication
1. فعّل Email/Password Authentication
2. فعّل Google Sign-In
3. أضف SHA-1 fingerprint للتطبيق

### 5. إعداد Storage
1. أنشئ مجلد `images/` في Storage
2. ارفع صور البطاقات الإعلانية والمعالم السياحية

## المفاتيح المطلوبة

### في ملف `local.properties`:
```properties
MAPS_API_KEY=your_google_maps_api_key
OPENAI_API_KEY=your_openai_api_key
GEMINI_API_KEY=your_gemini_api_key
```

### في ملف `google-services.json`:
- Project ID: `world-cup-2026-guide`
- Storage Bucket: `world-cup-2026-guide.appspot.com`
- Database URL: `https://world-cup-2026-guide-default-rtdb.firebaseio.com`

## البناء والتشغيل

### المتطلبات
- Android Studio Arctic Fox أو أحدث
- Android SDK 24+
- Java 17

### الخطوات
1. استنسخ المشروع
2. افتح المشروع في Android Studio
3. أضف المفاتيح المطلوبة في `local.properties`
4. اربط مشروع Firebase
5. ارفع البيانات المطلوبة إلى Realtime Database
6. شغل التطبيق

## هيكل المشروع

```
app/
├── src/main/
│   ├── java/com/ahmmedalmzini783/wcguide/
│   │   ├── data/
│   │   │   ├── model/          # نماذج البيانات
│   │   │   ├── repo/           # مستودعات البيانات
│   │   │   ├── local/          # قاعدة البيانات المحلية
│   │   │   └── remote/         # Firebase
│   │   ├── ui/
│   │   │   ├── home/           # الصفحة الرئيسية
│   │   │   ├── events/         # الأحداث
│   │   │   ├── chatbot/        # المساعد الذكي
│   │   │   └── main/           # النشاط الرئيسي
│   │   └── util/               # الأدوات المساعدة
│   └── res/
│       ├── layout/             # تخطيطات الواجهة
│       ├── values/            # النصوص والألوان
│       └── drawable/          # الصور والأيقونات
└── google-services.json       # إعدادات Firebase
```

## المميزات التقنية

- **Architecture**: MVVM مع Repository Pattern
- **Database**: Room (محلي) + Firebase Realtime Database
- **Authentication**: Firebase Auth
- **Image Loading**: Glide
- **Networking**: Retrofit + OkHttp
- **UI**: Material Design 3
- **Navigation**: Navigation Component
- **Background Tasks**: WorkManager

## المساهمة

1. Fork المشروع
2. أنشئ branch جديد للميزة
3. اكتب الكود مع التعليقات
4. اكتب اختبارات للكود
5. أرسل Pull Request

## الترخيص

هذا المشروع مرخص تحت رخصة MIT. راجع ملف `LICENSE` للتفاصيل.

## الدعم

للدعم والمساعدة، يرجى التواصل عبر:
- Email: support@worldcupguide.com
- GitHub Issues: [رابط المشروع]

---

**ملاحظة**: هذا التطبيق مخصص لكأس العالم 2026 ويحتوي على بيانات وهمية مؤقتة للعرض. يرجى استبدال البيانات الوهمية ببيانات حقيقية عند النشر.