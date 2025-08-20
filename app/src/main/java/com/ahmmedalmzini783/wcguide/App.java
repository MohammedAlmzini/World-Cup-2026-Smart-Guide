package com.ahmmedalmzini783.wcguide;

import android.app.Application;
import androidx.multidex.MultiDexApplication;
import androidx.work.Configuration;
import androidx.work.WorkManager;

import com.google.firebase.FirebaseApp;

public class App extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Initialize WorkManager with custom configuration
        Configuration workManagerConfig = new Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.INFO)
                .build();

        WorkManager.initialize(this, workManagerConfig);
    }
}