package com.ahmmedalmzini783.wcguide;

import android.app.Application;
import androidx.multidex.MultiDexApplication;
import androidx.work.Configuration;
import androidx.work.WorkManager;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class App extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Enable offline persistence (must be called before any database usage)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // Initialize WorkManager with custom configuration
        Configuration workManagerConfig = new Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.INFO)
                .build();

        WorkManager.initialize(this, workManagerConfig);
    }
}