package com.ahmmedalmzini783.wcguide;

import android.app.Application;
import android.util.Log;
import androidx.work.Configuration;
import androidx.work.WorkManager;

import com.ahmmedalmzini783.wcguide.data.local.AppDatabase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.libraries.places.api.Places;
import com.ahmmedalmzini783.wcguide.BuildConfig;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Initialize Google Places SDK with New Places API
        try {
            if (!Places.isInitialized()) {
                Places.initialize(getApplicationContext(), getString(R.string.places_api_key));
                Log.d("App", "Google Places SDK initialized successfully");
                Log.d("App", "Places API Key: " + (getString(R.string.places_api_key) != null ? "Present" : "Missing"));
            }
        } catch (Exception e) {
            Log.e("App", "Failed to initialize Places SDK", e);
        }

        // Enable offline persistence (must be called before any database usage)
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
            // Persistence might already be enabled
        }

        // Initialize WorkManager with custom configuration
        try {
            Configuration workManagerConfig = new Configuration.Builder()
                    .setMinimumLoggingLevel(android.util.Log.INFO)
                    .build();

            WorkManager.initialize(this, workManagerConfig);
        } catch (Exception e) {
            // WorkManager might already be initialized
        }

        // Handle database schema issues
        try {
            AppDatabase.getInstance(this);
        } catch (Exception e) {
            Log.e("App", "Database schema issue detected, clearing database", e);
            AppDatabase.clearDatabase(this);
        }
    }
}