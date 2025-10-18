package com.ahmmedalmzini783.wcguide.util;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {
    private static final String PREF_NAME = "user_preferences";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_FAVORITES_COUNT = "favorites_count";
    private static final String KEY_COMMENTS_COUNT = "comments_count";
    private static final String KEY_EVENTS_ATTENDED_COUNT = "events_attended_count";
    private static final String KEY_LAST_LOGIN = "last_login";
    
    private static UserPreferences instance;
    private SharedPreferences preferences;
    
    private UserPreferences(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public static synchronized UserPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new UserPreferences(context.getApplicationContext());
        }
        return instance;
    }
    
    // User data methods
    public void saveUserData(String userId, String name, String email) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putLong(KEY_LAST_LOGIN, System.currentTimeMillis());
        editor.apply();
    }
    
    public String getUserId() {
        return preferences.getString(KEY_USER_ID, null);
    }
    
    public String getUserName() {
        return preferences.getString(KEY_USER_NAME, null);
    }
    
    public String getUserEmail() {
        return preferences.getString(KEY_USER_EMAIL, null);
    }
    
    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    public long getLastLogin() {
        return preferences.getLong(KEY_LAST_LOGIN, 0);
    }
    
    // Stats methods
    public void setFavoritesCount(int count) {
        preferences.edit().putInt(KEY_FAVORITES_COUNT, count).apply();
    }
    
    public int getFavoritesCount() {
        return preferences.getInt(KEY_FAVORITES_COUNT, 0);
    }
    
    public void incrementFavoritesCount() {
        int current = getFavoritesCount();
        setFavoritesCount(current + 1);
    }
    
    public void decrementFavoritesCount() {
        int current = getFavoritesCount();
        if (current > 0) {
            setFavoritesCount(current - 1);
        }
    }
    
    public void setCommentsCount(int count) {
        preferences.edit().putInt(KEY_COMMENTS_COUNT, count).apply();
    }
    
    public int getCommentsCount() {
        return preferences.getInt(KEY_COMMENTS_COUNT, 0);
    }
    
    public void incrementCommentsCount() {
        int current = getCommentsCount();
        setCommentsCount(current + 1);
    }
    
    public void setEventsAttendedCount(int count) {
        preferences.edit().putInt(KEY_EVENTS_ATTENDED_COUNT, count).apply();
    }
    
    public int getEventsAttendedCount() {
        return preferences.getInt(KEY_EVENTS_ATTENDED_COUNT, 0);
    }
    
    public void incrementEventsAttendedCount() {
        int current = getEventsAttendedCount();
        setEventsAttendedCount(current + 1);
    }
    
    // Clear all data
    public void clearUserData() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
    
    // Update user name
    public void updateUserName(String name) {
        preferences.edit().putString(KEY_USER_NAME, name).apply();
    }
    
    // Update user email
    public void updateUserEmail(String email) {
        preferences.edit().putString(KEY_USER_EMAIL, email).apply();
    }
}
