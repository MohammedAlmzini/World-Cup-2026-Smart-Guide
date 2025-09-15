package com.ahmmedalmzini783.wcguide.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.ahmmedalmzini783.wcguide.data.model.Banner;
import com.ahmmedalmzini783.wcguide.data.remote.FirebaseDataSource;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Local Banner Manager - مدير الإعلانات المحلي
 * يحفظ الإعلانات محلياً عندما يفشل Firebase
 */
public class LocalBannerManager {
    
    private static final String TAG = "LocalBannerManager";
    private static final String PREFS_NAME = "local_banners";
    private static final String KEY_BANNERS = "banners_list";
    private static final String KEY_PENDING = "pending_banners";
    
    private SharedPreferences prefs;
    private Gson gson;
    
    public LocalBannerManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        Log.d(TAG, "تم إنشاء مدير الإعلانات المحلي");
    }
    
    /**
     * حفظ الإعلان محلياً كحل أخير
     */
    public void saveBannerLocally(Banner banner, FirebaseDataSource.OnCompleteListener<Void> listener) {
        try {
            // إنشاء معرف فريد إذا لم يوجد
            if (banner.getId() == null || banner.getId().isEmpty()) {
                banner.setId("local_" + System.currentTimeMillis());
            }
            
            // قراءة الإعلانات المحفوظة
            List<Banner> localBanners = getLocalBanners();
            
            // إضافة الإعلان الجديد
            localBanners.add(banner);
            
            // حفظ القائمة المحدثة
            String bannersJson = gson.toJson(localBanners);
            prefs.edit().putString(KEY_BANNERS, bannersJson).apply();
            
            // إضافة إلى قائمة الانتظار للرفع لاحقاً
            addToPendingList(banner);
            
            Log.d(TAG, "✅ تم حفظ الإعلان محلياً: " + banner.getTitle());
            listener.onComplete(true, "تم حفظ الإعلان محلياً - سيتم رفعه لاحقاً عند حل مشكلة Firebase");
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في الحفظ المحلي", e);
            listener.onComplete(false, "فشل في الحفظ المحلي: " + e.getMessage());
        }
    }
    
    /**
     * قراءة الإعلانات المحفوظة محلياً
     */
    public List<Banner> getLocalBanners() {
        try {
            String bannersJson = prefs.getString(KEY_BANNERS, "[]");
            Type listType = new TypeToken<List<Banner>>(){}.getType();
            List<Banner> banners = gson.fromJson(bannersJson, listType);
            return banners != null ? banners : new ArrayList<>();
        } catch (Exception e) {
            Log.e(TAG, "خطأ في قراءة الإعلانات المحلية", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * إضافة إعلان لقائمة الانتظار للرفع لاحقاً
     */
    private void addToPendingList(Banner banner) {
        try {
            String pendingJson = prefs.getString(KEY_PENDING, "[]");
            Type listType = new TypeToken<List<Banner>>(){}.getType();
            List<Banner> pendingBanners = gson.fromJson(pendingJson, listType);
            if (pendingBanners == null) pendingBanners = new ArrayList<>();
            
            pendingBanners.add(banner);
            
            String updatedJson = gson.toJson(pendingBanners);
            prefs.edit().putString(KEY_PENDING, updatedJson).apply();
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في إضافة للقائمة المعلقة", e);
        }
    }
    
    /**
     * محاولة رفع الإعلانات المعلقة لـ Firebase
     */
    public void uploadPendingBanners(FirebaseDataSource firebaseDataSource) {
        try {
            String pendingJson = prefs.getString(KEY_PENDING, "[]");
            Type listType = new TypeToken<List<Banner>>(){}.getType();
            List<Banner> pendingBanners = gson.fromJson(pendingJson, listType);
            
            if (pendingBanners == null || pendingBanners.isEmpty()) {
                Log.d(TAG, "لا توجد إعلانات معلقة للرفع");
                return;
            }
            
            Log.d(TAG, "محاولة رفع " + pendingBanners.size() + " إعلان معلق");
            
            for (Banner banner : pendingBanners) {
                firebaseDataSource.addBanner(banner, new FirebaseDataSource.OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(boolean success, String message) {
                        if (success) {
                            Log.d(TAG, "✅ تم رفع إعلان معلق: " + banner.getTitle());
                            removePendingBanner(banner);
                        } else {
                            Log.w(TAG, "⚠️ فشل رفع إعلان معلق: " + banner.getTitle());
                        }
                    }
                });
            }
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في رفع الإعلانات المعلقة", e);
        }
    }
    
    /**
     * حذف إعلان من قائمة الانتظار بعد الرفع بنجاح
     */
    private void removePendingBanner(Banner banner) {
        try {
            String pendingJson = prefs.getString(KEY_PENDING, "[]");
            Type listType = new TypeToken<List<Banner>>(){}.getType();
            List<Banner> pendingBanners = gson.fromJson(pendingJson, listType);
            
            if (pendingBanners != null) {
                pendingBanners.removeIf(b -> b.getId().equals(banner.getId()));
                String updatedJson = gson.toJson(pendingBanners);
                prefs.edit().putString(KEY_PENDING, updatedJson).apply();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في حذف الإعلان المعلق", e);
        }
    }
    
    /**
     * عدد الإعلانات المعلقة للرفع
     */
    public int getPendingCount() {
        try {
            String pendingJson = prefs.getString(KEY_PENDING, "[]");
            Type listType = new TypeToken<List<Banner>>(){}.getType();
            List<Banner> pendingBanners = gson.fromJson(pendingJson, listType);
            return pendingBanners != null ? pendingBanners.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * مسح جميع البيانات المحلية
     */
    public void clearAllData() {
        prefs.edit().clear().apply();
        Log.d(TAG, "تم مسح جميع البيانات المحلية");
    }
}
