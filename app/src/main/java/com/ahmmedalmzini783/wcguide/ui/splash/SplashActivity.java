package com.ahmmedalmzini783.wcguide.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.ui.main.MainActivity;
import com.ahmmedalmzini783.wcguide.ui.profile.ProfileActivity;
import com.ahmmedalmzini783.wcguide.util.AuthManager;


public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 4000; // 4 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Make status bar and navigation bar transparent
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        
        setContentView(R.layout.activity_splash);

        // Navigate to MainActivity after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }


    @Override
    public void onBackPressed() {
        // Disable back button during splash
        // Do nothing
    }
}
