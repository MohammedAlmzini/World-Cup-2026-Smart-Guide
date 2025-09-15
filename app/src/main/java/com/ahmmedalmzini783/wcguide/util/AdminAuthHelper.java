package com.ahmmedalmzini783.wcguide.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AdminAuthHelper {
    
    private static final String ADMIN_EMAIL = "admin@worldcupguide.com";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String PREF_NAME = "admin_auth";
    private static final String KEY_ADMIN_LOGGED_IN = "admin_logged_in";
    
    public interface AdminLoginListener {
        void onAdminLoginSuccess();
        void onAdminLoginFailure(String error);
    }
    
    public static void loginAsAdmin(Context context, AdminLoginListener listener) {
        Log.d("AdminAuthHelper", "🔑 بدء تسجيل دخول الأدمن بكلمة مرور admin123...");
        
        // تسجيل كأدمن محلياً
        markAdminLoggedIn(context);
        
        // تسجيل دخول Firebase بكلمة مرور admin123
        FirebaseAuth auth = FirebaseAuth.getInstance();
        
        auth.signInWithEmailAndPassword(ADMIN_EMAIL, ADMIN_PASSWORD)
            .addOnCompleteListener(signInTask -> {
                if (signInTask.isSuccessful()) {
                    Log.d("AdminAuthHelper", "✅ نجح تسجيل دخول الأدمن بكلمة مرور admin123");
                    listener.onAdminLoginSuccess();
                } else {
                    // إذا فشل تسجيل الدخول، أنشئ حساب جديد بكلمة المرور admin123
                    Log.d("AdminAuthHelper", "⚠️ فشل تسجيل الدخول، سأنشئ حساب جديد...");
                    createAdminAccount(context, listener);
                }
            });
    }
    
    private static void createAdminAccount(Context context, AdminLoginListener listener) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        
        Log.d("AdminAuthHelper", "🆕 إنشاء حساب أدمن جديد بكلمة مرور admin123...");
        auth.createUserWithEmailAndPassword(ADMIN_EMAIL, ADMIN_PASSWORD)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("AdminAuthHelper", "✅ تم إنشاء حساب الأدمن بنجاح");
                    listener.onAdminLoginSuccess();
                } else {
                    Log.e("AdminAuthHelper", "❌ فشل إنشاء حساب الأدمن: " + task.getException());
                    // تسجيل دخول مجهول كحل أخير
                    tryAnonymousLogin(listener);
                }
            });
    }
    
    private static void tryAnonymousLogin(AdminLoginListener listener) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        
        Log.d("AdminAuthHelper", "🔄 محاولة تسجيل دخول مجهول...");
        auth.signInAnonymously()
            .addOnCompleteListener(anonTask -> {
                if (anonTask.isSuccessful()) {
                    Log.d("AdminAuthHelper", "✅ نجح تسجيل الدخول المجهول");
                    listener.onAdminLoginSuccess();
                } else {
                    Log.e("AdminAuthHelper", "❌ فشل تسجيل الدخول المجهول: " + anonTask.getException());
                    listener.onAdminLoginFailure("فشل في جميع طرق تسجيل الدخول");
                }
            });
    }
    
    private static void markAdminLoggedIn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_ADMIN_LOGGED_IN, true).apply();
    }
    
    public static boolean isAdminLoggedIn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_ADMIN_LOGGED_IN, false);
    }
    
    public static void logoutAdmin(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_ADMIN_LOGGED_IN, false).apply();
        
        FirebaseAuth.getInstance().signOut();
        Log.d("AdminAuthHelper", "🚪 تم تسجيل خروج الأدمن");
    }
}
