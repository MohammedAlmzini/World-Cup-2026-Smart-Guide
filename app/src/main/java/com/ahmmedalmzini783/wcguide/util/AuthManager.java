package com.ahmmedalmzini783.wcguide.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.ahmmedalmzini783.wcguide.ui.auth.LoginActivity;
import com.ahmmedalmzini783.wcguide.ui.profile.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthManager {
    private static final String PREF_NAME = "auth_preferences";
    private static final String KEY_AUTH_STATE = "auth_state";
    private static final String KEY_USER_ID = "user_id";
    
    private static AuthManager instance;
    private SharedPreferences preferences;
    private FirebaseAuth mAuth;
    private Context context;
    
    private AuthManager(Context context) {
        this.context = context.getApplicationContext();
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
    }
    
    public static synchronized AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context);
        }
        return instance;
    }
    
    // Check if user is logged in
    public boolean isLoggedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        boolean isLoggedIn = currentUser != null && preferences.getBoolean(KEY_AUTH_STATE, false);
        
        // Verify that the stored user ID matches current user
        if (isLoggedIn && currentUser != null) {
            String storedUserId = preferences.getString(KEY_USER_ID, null);
            if (storedUserId == null || !storedUserId.equals(currentUser.getUid())) {
                // User ID mismatch, clear auth state
                clearAuthState();
                return false;
            }
        }
        
        return isLoggedIn;
    }
    
    // Get current user
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }
    
    // Save auth state after successful login
    public void saveAuthState(FirebaseUser user) {
        if (user != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(KEY_AUTH_STATE, true);
            editor.putString(KEY_USER_ID, user.getUid());
            editor.apply();
            
            // Save user data to UserPreferences
            UserPreferences userPrefs = UserPreferences.getInstance(context);
            userPrefs.saveUserData(
                user.getUid(),
                user.getDisplayName() != null ? user.getDisplayName() : "مستخدم",
                user.getEmail() != null ? user.getEmail() : ""
            );
        }
    }
    
    // Clear auth state
    public void clearAuthState() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_AUTH_STATE, false);
        editor.remove(KEY_USER_ID);
        editor.apply();
        
        // Clear user data
        UserPreferences userPrefs = UserPreferences.getInstance(context);
        userPrefs.clearUserData();
    }
    
    // Require authentication - redirect to login if not logged in
    public boolean requireAuth(Context context) {
        if (!isLoggedIn()) {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            return false;
        }
        return true;
    }
    
    // Navigate to profile
    public void navigateToProfile(Context context) {
        if (isLoggedIn()) {
            Intent intent = new Intent(context, ProfileActivity.class);
            context.startActivity(intent);
        } else {
            requireAuth(context);
        }
    }
    
    // Get user ID
    public String getUserId() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }
    
    // Get user display name
    public String getUserDisplayName() {
        FirebaseUser user = getCurrentUser();
        if (user != null && user.getDisplayName() != null) {
            return user.getDisplayName();
        }
        
        // Fallback to UserPreferences
        UserPreferences userPrefs = UserPreferences.getInstance(context);
        return userPrefs.getUserName();
    }
    
    // Get user email
    public String getUserEmail() {
        FirebaseUser user = getCurrentUser();
        if (user != null && user.getEmail() != null) {
            return user.getEmail();
        }
        
        // Fallback to UserPreferences
        UserPreferences userPrefs = UserPreferences.getInstance(context);
        return userPrefs.getUserEmail();
    }
}
