package com.ahmmedalmzini783.wcguide.util;

import android.content.Context;
import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ahmmedalmzini783.wcguide.data.model.Banner;
import com.ahmmedalmzini783.wcguide.data.remote.FirebaseDataSource;

/**
 * Emergency Banner Manager - مدير الطوارئ للإعلانات
 * يستخدم مسارات مفتوحة بدون مصادقة + حفظ محلي
 */
public class EmergencyBannerManager {
    
    private static final String TAG = "EmergencyBannerManager";
    private static final String EMERGENCY_PATH = "public_banners";
    private static final String TEMP_PATH = "temp_banners";
    private DatabaseReference emergencyRef;
    private DatabaseReference tempRef;
    private LocalBannerManager localManager;
    private Context context;
    
    // مُنشِئ بدون معامل للاستخدام في FirebaseDataSource
    public EmergencyBannerManager() {
        this(null);
    }
    
    public EmergencyBannerManager(Context context) {
        this.context = context;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        emergencyRef = database.getReference(EMERGENCY_PATH);
        tempRef = database.getReference(TEMP_PATH);
        if (context != null) {
            localManager = new LocalBannerManager(context);
        }
        Log.d(TAG, "تم إنشاء مدير الطوارئ بنجاح");
    }
    
    public void addBannerDirectly(Banner banner, FirebaseDataSource.OnCompleteListener<Void> listener) {
        Log.d(TAG, "بدء إضافة إعلان عبر مدير الطوارئ: " + banner.getTitle());
        
        String bannerId = banner.getId();
        if (bannerId == null || bannerId.isEmpty()) {
            bannerId = "emergency_" + System.currentTimeMillis() + "_" + Math.random();
            banner.setId(bannerId);
            Log.d(TAG, "تم إنشاء معرف جديد: " + bannerId);
        }
        
        // جرب المسار المؤقت أولاً
        Log.d(TAG, "محاولة الحفظ في المسار المؤقت: " + TEMP_PATH);
        tempRef.child(bannerId).setValue(banner)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "✅ نجح الحفظ في المسار المؤقت");
                    listener.onComplete(true, "تم حفظ الإعلان بنجاح في المسار المؤقت");
                    // حاول النسخ للمسار الرئيسي
                    copyToMainPath(banner, null);
                } else {
                    Log.e(TAG, "❌ فشل المسار المؤقت: " + task.getException());
                    // جرب المسار العام
                    tryPublicPath(banner, listener);
                }
            });
    }
    
    private void tryPublicPath(Banner banner, FirebaseDataSource.OnCompleteListener<Void> listener) {
        Log.d(TAG, "محاولة الحفظ في المسار العام: " + EMERGENCY_PATH);
        emergencyRef.child(banner.getId()).setValue(banner)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "✅ نجح الحفظ في المسار العام");
                    listener.onComplete(true, "تم حفظ الإعلان في المسار العام");
                    copyToMainPath(banner, null);
                } else {
                    Log.e(TAG, "❌ فشل المسار العام: " + task.getException());
                    // أخر محاولة: استخدم مسار بتاريخ
                    tryDatePath(banner, listener);
                }
            });
    }
    
    private void tryDatePath(Banner banner, FirebaseDataSource.OnCompleteListener<Void> listener) {
        String datePath = "banners_" + System.currentTimeMillis();
        Log.d(TAG, "محاولة الحفظ في مسار مؤرخ: " + datePath);
        DatabaseReference dateRef = FirebaseDatabase.getInstance().getReference(datePath);
        
        dateRef.child(banner.getId()).setValue(banner)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "✅ نجح الحفظ في المسار المؤرخ");
                    listener.onComplete(true, "تم حفظ الإعلان في مسار مؤرخ: " + datePath);
                } else {
                    Log.e(TAG, "❌ فشل حتى المسار المؤرخ: " + task.getException());
                    // آخر محاولة: Firebase مباشرة بدون مسار
                    tryDirectFirebase(banner, listener);
                }
            });
    }
    
    private void tryDirectFirebase(Banner banner, FirebaseDataSource.OnCompleteListener<Void> listener) {
        Log.d(TAG, "المحاولة الأخيرة: Firebase مباشرة");
        try {
            // استخدم Root مباشرة
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            String directPath = "emergency_" + System.currentTimeMillis();
            
            rootRef.child(directPath).child(banner.getId()).setValue(banner)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "✅ نجح الحفظ في Root مباشرة: " + directPath);
                        listener.onComplete(true, "تم حفظ الإعلان في Root Firebase: " + directPath);
                    } else {
                        Log.e(TAG, "❌ فشل حتى Root Firebase: " + task.getException());
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "خطأ غير معروف";
                        listener.onComplete(false, "فشل في جميع المسارات الطارئة - آخر خطأ: " + errorMsg);
                    }
                });
        } catch (Exception e) {
            Log.e(TAG, "استثناء في المحاولة المباشرة: " + e.getMessage());
            listener.onComplete(false, "استثناء في Firebase: " + e.getMessage());
        }
    }
    
    
    private void copyToMainPath(Banner banner, FirebaseDataSource.OnCompleteListener<Void> listener) {
        Log.d(TAG, "محاولة النسخ للمسار الرئيسي: banners");
        DatabaseReference mainRef = FirebaseDatabase.getInstance().getReference("banners");
        mainRef.child(banner.getId()).setValue(banner)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "✅ نجح النسخ للمسار الرئيسي");
                    if (listener != null) {
                        listener.onComplete(true, "تم نسخ الإعلان للمسار الرئيسي بنجاح");
                    }
                } else {
                    Log.e(TAG, "❌ فشل النسخ للمسار الرئيسي: " + task.getException());
                    // لا نحذف من المسار الطارئ في حالة الفشل للاحتفاظ بالبيانات
                }
            });
    }
    
    public void updateBannerDirectly(Banner banner, FirebaseDataSource.OnCompleteListener<Void> listener) {
        if (banner.getId() != null) {
            // جرب التحديث في جميع المسارات الممكنة
            DatabaseReference mainRef = FirebaseDatabase.getInstance().getReference("banners");
            mainRef.child(banner.getId()).setValue(banner)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onComplete(true, "تم تحديث الإعلان بنجاح");
                    } else {
                        // جرب في المسار المؤقت
                        tempRef.child(banner.getId()).setValue(banner)
                            .addOnCompleteListener(tempTask -> {
                                if (tempTask.isSuccessful()) {
                                    listener.onComplete(true, "تم تحديث الإعلان في المسار المؤقت");
                                } else {
                                    listener.onComplete(false, "فشل في تحديث الإعلان في جميع المسارات");
                                }
                            });
                    }
                });
        } else {
            listener.onComplete(false, "معرف الإعلان غير صحيح");
        }
    }
    
    public void deleteBannerDirectly(String bannerId, FirebaseDataSource.OnCompleteListener<Void> listener) {
        if (bannerId != null) {
            DatabaseReference mainRef = FirebaseDatabase.getInstance().getReference("banners");
            mainRef.child(bannerId).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onComplete(true, "تم حذف الإعلان بنجاح");
                    } else {
                        // حذف من جميع المسارات الطارئة
                        deleteFromAllPaths(bannerId, listener);
                    }
                });
        } else {
            listener.onComplete(false, "معرف الإعلان غير صحيح");
        }
    }
    
    private void deleteFromAllPaths(String bannerId, FirebaseDataSource.OnCompleteListener<Void> listener) {
        // حذف من المسار المؤقت
        tempRef.child(bannerId).removeValue()
            .addOnCompleteListener(tempTask -> {
                // حذف من المسار العام
                emergencyRef.child(bannerId).removeValue()
                    .addOnCompleteListener(publicTask -> {
                        if (tempTask.isSuccessful() || publicTask.isSuccessful()) {
                            listener.onComplete(true, "تم حذف الإعلان من المسارات الطارئة");
                        } else {
                            listener.onComplete(false, "فشل في حذف الإعلان من جميع المسارات");
                        }
                    });
            });
    }
}
