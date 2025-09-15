package com.ahmmedalmzini783.wcguide.util;

import android.content.Context;
import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.FirebaseApp;
import com.ahmmedalmzini783.wcguide.data.remote.FirebaseDataSource;
import com.ahmmedalmzini783.wcguide.data.model.Banner;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Firebase Connection Diagnostics - تشخيص اتصال Firebase
 */
public class FirebaseDiagnostics {
    
    private static final String TAG = "FirebaseDiagnostics";
    
    public interface DiagnosticsListener {
        void onDiagnosticsComplete(String report);
        void onProgress(String step);
    }
    
    public static void runComprehensiveDiagnostics(Context context, DiagnosticsListener listener) {
        Log.d(TAG, "🔍 بدء التشخيص الشامل لـ Firebase...");
        
        StringBuilder report = new StringBuilder();
        report.append("🔍 تقرير تشخيص Firebase الشامل\n");
        report.append("=====================================\n\n");
        
        AtomicInteger completedTests = new AtomicInteger(0);
        AtomicInteger successfulTests = new AtomicInteger(0);
        
        listener.onProgress("فحص حالة Firebase Auth");
        runAuthDiagnostics(report, () -> {
            listener.onProgress("فحص اتصال Firebase Database");
            runDatabaseDiagnostics(report, () -> {
                listener.onProgress("اختبار المسارات المختلفة");
                runPathTestDiagnostics(report, completedTests, successfulTests, () -> {
                    listener.onProgress("اختبار إنشاء البيانات");
                    runDataCreationDiagnostics(report, completedTests, successfulTests, () -> {
                        // إنهاء التقرير
                        report.append("\n📊 خلاصة النتائج:\n");
                        report.append("اختبارات نجحت: ").append(successfulTests.get()).append("\n");
                        report.append("إجمالي الاختبارات: ").append(completedTests.get()).append("\n");
                        
                        if (successfulTests.get() >= completedTests.get() / 2) {
                            report.append("✅ حالة Firebase: جيدة\n");
                        } else {
                            report.append("⚠️ حالة Firebase: تحتاج إلى إصلاح\n");
                        }
                        
                        report.append("\n💡 توصيات الإصلاح:\n");
                        if (successfulTests.get() == 0) {
                            report.append("- تحقق من إعدادات Firebase في المشروع\n");
                            report.append("- تأكد من ملف google-services.json\n");
                            report.append("- استخدم قواعد التطوير المفتوحة\n");
                        } else if (successfulTests.get() < completedTests.get()) {
                            report.append("- بعض المسارات تعمل، جرب استخدام المسارات البديلة\n");
                            report.append("- راجع قواعد الأمان في Firebase Console\n");
                        }
                        
                        listener.onDiagnosticsComplete(report.toString());
                    });
                });
            });
        });
    }
    
    private static void runAuthDiagnostics(StringBuilder report, Runnable onComplete) {
        report.append("1️⃣ فحص Firebase Auth:\n");
        
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            report.append("✅ مستخدم مسجل: ").append(auth.getCurrentUser().getUid()).append("\n");
            report.append("📧 البريد: ").append(auth.getCurrentUser().getEmail()).append("\n");
        } else {
            report.append("⚠️ لا يوجد مستخدم مسجل\n");
            report.append("🔄 محاولة تسجيل دخول تلقائي...\n");
            
            // محاولة تسجيل دخول تلقائي
            auth.signInAnonymously().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    report.append("✅ تم تسجيل دخول مجهول بنجاح\n");
                } else {
                    report.append("❌ فشل في تسجيل الدخول: ")
                          .append(task.getException() != null ? task.getException().getMessage() : "سبب غير معروف")
                          .append("\n");
                }
                report.append("\n");
                onComplete.run();
            });
            return;
        }
        
        report.append("\n");
        onComplete.run();
    }
    
    private static void runDatabaseDiagnostics(StringBuilder report, Runnable onComplete) {
        report.append("2️⃣ فحص Firebase Database:\n");
        
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            report.append("✅ تم الحصول على مرجع Database\n");
            report.append("🔗 URL: ").append(database.getApp().getOptions().getDatabaseUrl()).append("\n");
        } catch (Exception e) {
            report.append("❌ خطأ في Database: ").append(e.getMessage()).append("\n");
        }
        
        report.append("\n");
        onComplete.run();
    }
    
    private static void runPathTestDiagnostics(StringBuilder report, AtomicInteger completedTests, AtomicInteger successfulTests, Runnable onComplete) {
        report.append("3️⃣ اختبار المسارات المختلفة:\n");
        
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String[] testPaths = {
            "banners", "public_banners", "temp_banners", 
            "emergency_banners", "test_path_" + System.currentTimeMillis()
        };
        
        AtomicInteger pathTests = new AtomicInteger(0);
        
        for (String path : testPaths) {
            DatabaseReference testRef = database.getReference(path).child("diagnostic_test_" + System.currentTimeMillis());
            
            testRef.setValue("test_value_" + System.currentTimeMillis())
                .addOnCompleteListener(task -> {
                    completedTests.incrementAndGet();
                    pathTests.incrementAndGet();
                    
                    if (task.isSuccessful()) {
                        successfulTests.incrementAndGet();
                        report.append("✅ المسار '").append(path).append("' يعمل بشكل صحيح\n");
                        // حذف البيانات التجريبية
                        testRef.removeValue();
                    } else {
                        report.append("❌ المسار '").append(path).append("' فشل: ");
                        if (task.getException() != null) {
                            report.append(task.getException().getMessage());
                        } else {
                            report.append("سبب غير معروف");
                        }
                        report.append("\n");
                    }
                    
                    // انتهاء جميع اختبارات المسارات
                    if (pathTests.get() == testPaths.length) {
                        report.append("\n");
                        onComplete.run();
                    }
                });
        }
    }
    
    private static void runDataCreationDiagnostics(StringBuilder report, AtomicInteger completedTests, AtomicInteger successfulTests, Runnable onComplete) {
        report.append("4️⃣ اختبار إنشاء البيانات الفعلية:\n");
        
        // إنشاء إعلان تجريبي
        Banner testBanner = new Banner();
        testBanner.setId("diagnostic_banner_" + System.currentTimeMillis());
        testBanner.setTitle("إعلان تجريبي للتشخيص");
        testBanner.setDescription("هذا إعلان تجريبي لاختبار النظام");
        testBanner.setImageUrl("https://via.placeholder.com/300x200");
        
        EmergencyBannerManager emergencyManager = new EmergencyBannerManager();
        emergencyManager.addBannerDirectly(testBanner, new FirebaseDataSource.OnCompleteListener<Void>() {
            @Override
            public void onComplete(boolean success, String message) {
                completedTests.incrementAndGet();
                
                if (success) {
                    successfulTests.incrementAndGet();
                    report.append("✅ إنشاء إعلان تجريبي: نجح\n");
                    report.append("📝 الرسالة: ").append(message).append("\n");
                    
                    // حذف الإعلان التجريبي
                    emergencyManager.deleteBannerDirectly(testBanner.getId(), new FirebaseDataSource.OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(boolean deleteSuccess, String deleteMessage) {
                            if (deleteSuccess) {
                                report.append("🗑️ تم حذف الإعلان التجريبي بنجاح\n");
                            } else {
                                report.append("⚠️ لم يتم حذف الإعلان التجريبي: ").append(deleteMessage).append("\n");
                            }
                            report.append("\n");
                            onComplete.run();
                        }
                    });
                } else {
                    report.append("❌ إنشاء إعلان تجريبي: فشل\n");
                    report.append("📝 الرسالة: ").append(message).append("\n");
                    report.append("\n");
                    onComplete.run();
                }
            }
        });
    }
}
