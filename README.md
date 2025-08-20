# World Cup 2026 Smart Guide - مرشد كأس العالم 2026

A comprehensive Android application for World Cup 2026 fans, providing smart guidance for events, places, and AI-powered assistance across the USA, Canada, and Mexico.

## Features

### 🏆 Core Features
- **Multilingual Support**: Arabic and English
- **Event Management**: Browse and filter World Cup events
- **Smart Places**: Discover attractions, hotels, and restaurants
- **AI Assistant**: Powered by OpenAI/Gemini for travel planning and Q&A
- **Favorites & Reviews**: Save favorite places and events, write reviews
- **Real-time Updates**: Firebase integration for live data
- **Offline Support**: Room database for offline access
- **Push Notifications**: FCM for event reminders and updates

### 🤖 AI Features
- **Daily Plan Generator**: AI-powered itinerary creation
- **Voice Integration**: Speech-to-Text and Text-to-Speech
- **Smart Translation**: Multi-language translation support
- **Contextual Q&A**: World Cup 2026 specific assistance

### 📱 Technical Features
- **MVVM Architecture**: Clean architecture with Repository pattern
- **Material Design 3**: Modern UI with dark/light theme support
- **Google Maps Integration**: Location services and directions
- **Firebase Suite**: Authentication, Realtime Database, Storage, FCM
- **Room Database**: Local caching and offline support
- **WorkManager**: Background tasks and reminders

## Architecture

```
app/
├── data/
│   ├── model/          # Data models (POJOs)
│   ├── local/          # Room database (entities, DAOs, converters)
│   ├── remote/         # Firebase & AI API clients
│   └── repo/           # Repository layer (MVVM)
├── ui/
│   ├── main/           # MainActivity with bottom navigation
│   ├── home/           # Home screen with countdown & highlights
│   ├── events/         # Events listing and filtering
│   ├── details/        # Event and place detail screens
│   ├── chatbot/        # AI assistant with voice support
│   ├── auth/           # Authentication screens
│   └── settings/       # Settings and preferences
├── util/               # Utility classes
└── notifications/      # FCM and reminder services
```

## Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 17
- Android SDK 34
- Google Play Services

### 1. Clone and Import
```bash
git clone <repository-url>
cd WorldCup2026SmartGuide
```
Open the project in Android Studio.

### 2. Firebase Setup
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project named "World Cup 2026 Guide"
3. Add an Android app with package name: `com.ahmmedalmzini783.wcguide`
4. Download `google-services.json` and place it in `app/` directory
5. Enable the following services:
    - Authentication (Email/Password)
    - Realtime Database
    - Cloud Storage
    - Cloud Messaging (FCM)

### 3. API Keys Configuration
Create/update `local.properties` file in project root:

```properties
# Google Maps API Key
MAPS_API_KEY=your_google_maps_api_key_here

# OpenAI API Key (optional - for AI features)
OPENAI_API_KEY=your_openai_api_key_here

# Gemini API Key (alternative to OpenAI)
GEMINI_API_KEY=your_gemini_api_key_here
```

#### How to get API keys:

**Google Maps API:**
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Enable Maps SDK for Android
3. Create credentials → API Key
4. Restrict the key to Android apps and your package name

**OpenAI API Key:**
1. Visit [OpenAI Platform](https://platform.openai.com/api-keys)
2. Create new API key
3. Add billing information for usage

**Gemini API Key:**
1. Go to [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Create new API key
3. Enable Gemini Pro API

### 4. Firebase Database Rules
Set up Realtime Database rules:

```json
{
  "rules": {
    ".read": true,
    "users": {
      "$uid": {
        ".write": "$uid === auth.uid"
      }
    },
    "reviews": {
      "$targetId": {
        ".read": true,
        "$reviewId": {
          ".write": "auth != null && newData.child('userId').val() === auth.uid"
        }
      }
    },
    "events": {
      ".read": true,
      ".write": "root.child('users').child(auth.uid).child('roles').child('admin').val() === true"
    },
    "places": {
      ".read": true
    },
    "banners": {
      ".read": true
    },
    "quickInfo": {
      ".read": true
    }
  }
}
```

### 5. Sample Data
Import sample data to Firebase Realtime Database:

```json
{
  "events": {
    "evt_001": {
      "title": "Opening Ceremony",
      "country": "US",
      "city": "Los Angeles",
      "venueName": "SoFi Stadium",
      "type": "ceremony",
      "startUtc": 1761325200000,
      "endUtc": 1761332400000,
      "imageUrl": "https://example.com/opening.jpg",
      "capacity": 70000,
      "description": "Grand opening ceremony of FIFA World Cup 2026",
      "lat": 33.9533,
      "lng": -118.3391
    }
  },
  "places": {
    "pl_001": {
      "kind": "attraction",
      "name": "CN Tower",
      "country": "CA",
      "city": "Toronto",
      "address": "290 Bremner Blvd, Toronto, ON",
      "lat": 43.6426,
      "lng": -79.3871,
      "images": ["https://example.com/cn-tower.jpg"],
      "avgRating": 4.6,
      "ratingCount": 21837,
      "description": "Iconic telecommunications tower and tourist attraction"
    }
  },
  "quickInfo": {
    "US": {
      "currency": "USD",
      "languages": ["English"],
      "transportTips": "Metro, Uber, buses available",
      "weatherTip": "Summer: warm, check local weather"
    },
    "CA": {
      "currency": "CAD", 
      "languages": ["English", "French"],
      "transportTips": "TTC/Metro systems available",
      "weatherTip": "Variable weather, check forecasts"
    },
    "MX": {
      "currency": "MXN",
      "languages": ["Spanish"],
      "transportTips": "Metro/CDMX, buses available",
      "weatherTip": "Sunny weather, use sunblock"
    }
  }
}
```

### 6. Build and Run
1. Sync project with Gradle files
2. Build → Clean Project
3. Build → Rebuild Project
4. Run on device or emulator

## Features Overview

### Home Screen
- Countdown to World Cup 2026
- Featured attractions by country
- Hotel and restaurant recommendations
- Quick info cards (currency, language, transport, weather)
- Latest news banners

### Events Screen
- Filter by country, city, type, date
- Real-time event status (upcoming/live/ended)
- Event details with maps integration
- Favorite events and reminder notifications
- Ticket booking links

### AI Assistant
- Natural language Q&A about World Cup 2026
- Daily itinerary generation based on location and interests
- Voice input/output support
- Multi-language translation
- Context-aware responses

### Places & Reviews
- Attractions, hotels, restaurants by location
- User ratings and reviews
- Photo galleries
- Price level indicators
- Google Maps integration for directions

### User Features
- Firebase authentication
- Personal favorites and reviews
- Notification preferences
- Multi-language support
- Offline data caching

## Development Notes

### Dependencies
- **Firebase BoM**: 32.7.1
- **Material Components**: 1.11.0
- **Room**: 2.6.1
- **Glide**: 4.16.0
- **OkHttp/Retrofit**: Latest stable
- **Google Play Services**: Maps 18.2.0, Location 21.0.1

### Build Configuration
- **minSdk**: 24 (Android 7.0)
- **targetSdk**: 34 (Android 14)
- **compileSdk**: 34
- **Java**: 17
- **AGP**: 8.2.2

### Proguard Rules
The app includes comprehensive ProGuard rules for:
- Firebase components
- Glide image loading
- OkHttp/Retrofit networking
- Gson serialization
- Room database
- Google Maps

## Troubleshooting

### Common Issues

**Build Errors:**
- Ensure all API keys are properly set in `local.properties`
- Check that `google-services.json` is in the correct location
- Verify Firebase project configuration matches package name

**Maps Not Loading:**
- Verify Maps API key is valid and unrestricted for development
- Check that Maps SDK for Android is enabled in Google Cloud Console
- Ensure location permissions are granted

**AI Features Not Working:**
- Verify API keys for OpenAI or Gemini are valid
- Check network connectivity
- Ensure billing is set up for paid APIs

**Firebase Connection Issues:**
- Verify `google-services.json` is from the correct Firebase project
- Check Firebase project configuration
- Ensure Firebase services are enabled

**Offline Mode:**
- Room database provides offline access to cached data
- Firebase has offline persistence enabled
- Check device storage for cache limits

### Performance Optimization
- Images are cached using Glide with OkHttp integration
- Room database provides efficient local storage
- Firebase offline persistence reduces network calls
- Pagination is implemented for large data sets

### Security Considerations
- API keys are stored in `local.properties` (not in version control)
- Network security config enforces HTTPS
- Firebase rules restrict write access
- User data is properly scoped and protected

## Contributing

1. Fork the repository
2. Create a feature branch
3. Follow the existing code style and architecture
4. Add tests for new features
5. Submit a pull request

## License

This project is developed for educational and demonstration purposes. Please ensure compliance with FIFA World Cup 2026 guidelines and obtain proper licenses for commercial use.

## Support

For setup issues or questions:
1. Check the troubleshooting section above
2. Verify all configuration steps are completed
3. Check Firebase console for errors
4. Ensure all API keys are valid and properly configured

---

**Note**: This is a demonstration project. For production use, implement additional security measures, comprehensive testing, and proper error handling.