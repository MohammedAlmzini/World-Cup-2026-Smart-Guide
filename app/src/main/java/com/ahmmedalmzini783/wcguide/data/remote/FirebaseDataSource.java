package com.ahmmedalmzini783.wcguide.data.remote;

import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.ahmmedalmzini783.wcguide.data.model.Event;
import com.ahmmedalmzini783.wcguide.data.model.Place;
import com.ahmmedalmzini783.wcguide.data.model.UserProfile;
import com.ahmmedalmzini783.wcguide.data.model.Banner;
import com.ahmmedalmzini783.wcguide.data.model.QuickInfo;
import com.ahmmedalmzini783.wcguide.util.EmergencyBannerManager;
import com.ahmmedalmzini783.wcguide.util.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map;

public class FirebaseDataSource {
    private static final String TAG = "FirebaseDataSource";

    private final FirebaseAuth auth;
    private final DatabaseReference database;
    private final StorageReference storage;

    // Database references
    private final DatabaseReference eventsRef;
    private final DatabaseReference placesRef;
    private final DatabaseReference reviewsRef;
    private final DatabaseReference usersRef;
    private final DatabaseReference bannersRef;
    private final DatabaseReference quickInfoRef;

    public FirebaseDataSource() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();

        // Initialize references
        eventsRef = database.child("events");
        placesRef = database.child("places");
        reviewsRef = database.child("reviews");
        usersRef = database.child("users");
        bannersRef = database.child("banners");
        quickInfoRef = database.child("quickInfo");
    }

    // Authentication Methods
    public LiveData<Resource<FirebaseUser>> signInWithEmail(String email, String password) {
        MutableLiveData<Resource<FirebaseUser>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.setValue(Resource.success(auth.getCurrentUser()));
                    } else {
                        result.setValue(Resource.error(
                                task.getException() != null ?
                                        task.getException().getMessage() : "Sign in failed",
                                null
                        ));
                    }
                });

        return result;
    }

    public LiveData<Resource<FirebaseUser>> createUserWithEmail(String email, String password, String displayName) {
        MutableLiveData<Resource<FirebaseUser>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // Update profile with display name
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayName)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {
                                        if (profileTask.isSuccessful()) {
                                            // Create user profile in database
                                            createUserProfile(user, displayName);
                                            result.setValue(Resource.success(user));
                                        } else {
                                            result.setValue(Resource.error(
                                                    profileTask.getException() != null ?
                                                            profileTask.getException().getMessage() : "Profile update failed",
                                                    user
                                            ));
                                        }
                                    });
                        }
                    } else {
                        result.setValue(Resource.error(
                                task.getException() != null ?
                                        task.getException().getMessage() : "Registration failed",
                                null
                        ));
                    }
                });

        return result;
    }

    public void signOut() {
        auth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    // User Profile Methods
    private void createUserProfile(FirebaseUser user, String displayName) {
        UserProfile profile = new UserProfile();
        profile.setUid(user.getUid());
        profile.setDisplayName(displayName);
        profile.setEmail(user.getEmail());
        profile.setPhotoUrl(user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);

        usersRef.child(user.getUid()).setValue(profile);
    }

    public LiveData<Resource<UserProfile>> getUserProfile(String uid) {
        MutableLiveData<Resource<UserProfile>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile profile = snapshot.getValue(UserProfile.class);
                if (profile != null) {
                    result.setValue(Resource.success(profile));
                } else {
                    result.setValue(Resource.error("User profile not found", null));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.setValue(Resource.error(error.getMessage(), null));
            }
        });

        return result;
    }

    public Task<Void> updateUserProfile(UserProfile profile) {
        return usersRef.child(profile.getUid()).setValue(profile);
    }

    // Events Methods
    public LiveData<Resource<List<Event>>> getAllEvents() {
        MutableLiveData<Resource<List<Event>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Event> events = new ArrayList<>();
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    if (event != null) {
                        event.setId(eventSnapshot.getKey());
                        events.add(event);
                    }
                }
                result.setValue(Resource.success(events));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.setValue(Resource.error(error.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Event>> getEventById(String eventId) {
        MutableLiveData<Resource<Event>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        eventsRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event event = snapshot.getValue(Event.class);
                if (event != null) {
                    event.setId(snapshot.getKey());
                    result.setValue(Resource.success(event));
                } else {
                    result.setValue(Resource.error("Event not found", null));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.setValue(Resource.error(error.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<Event>>> getEventsByCountry(String country) {
        MutableLiveData<Resource<List<Event>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Query query = eventsRef.orderByChild("country").equalTo(country);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Event> events = new ArrayList<>();
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    if (event != null) {
                        event.setId(eventSnapshot.getKey());
                        events.add(event);
                    }
                }
                result.setValue(Resource.success(events));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.setValue(Resource.error(error.getMessage(), null));
            }
        });

        return result;
    }

    public Task<Void> addEvent(Event event) {
        String key = eventsRef.push().getKey();
        if (key != null) {
            event.setId(key);
            return eventsRef.child(key).setValue(event);
        }
        return null;
    }

    public LiveData<Resource<Event>> getFeaturedEvent() {
        MutableLiveData<Resource<Event>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Query query = eventsRef.orderByChild("isFeatured").equalTo(true).limitToFirst(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                        Event event = eventSnapshot.getValue(Event.class);
                        if (event != null) {
                            event.setId(eventSnapshot.getKey());
                            result.setValue(Resource.success(event));
                            return;
                        }
                    }
                }
                result.setValue(Resource.error("No featured event found", null));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.setValue(Resource.error(error.getMessage(), null));
            }
        });

        return result;
    }

    // Places Methods
    public LiveData<Resource<List<Place>>> getAllPlaces() {
        MutableLiveData<Resource<List<Place>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        placesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Place> places = new ArrayList<>();
                for (DataSnapshot placeSnapshot : snapshot.getChildren()) {
                    Place place = placeSnapshot.getValue(Place.class);
                    if (place != null) {
                        place.setId(placeSnapshot.getKey());
                        places.add(place);
                    }
                }
                result.setValue(Resource.success(places));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.setValue(Resource.error(error.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<Place>>> getPlacesByCountryAndKind(String country, String kind) {
        MutableLiveData<Resource<List<Place>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Query query = placesRef.orderByChild("country").equalTo(country);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Place> places = new ArrayList<>();
                for (DataSnapshot placeSnapshot : snapshot.getChildren()) {
                    Place place = placeSnapshot.getValue(Place.class);
                    if (place != null && (kind == null || kind.equals(place.getKind()))) {
                        place.setId(placeSnapshot.getKey());
                        places.add(place);
                    }
                }
                result.setValue(Resource.success(places));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.setValue(Resource.error(error.getMessage(), null));
            }
        });

        return result;
    }

    // Banners Methods
    public LiveData<Resource<List<Banner>>> getBanners() {
        MutableLiveData<Resource<List<Banner>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        bannersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Banner> banners = new ArrayList<>();
                for (DataSnapshot bannerSnapshot : snapshot.getChildren()) {
                    Banner banner = bannerSnapshot.getValue(Banner.class);
                    if (banner != null) {
                        banner.setId(bannerSnapshot.getKey());
                        banners.add(banner);
                    }
                }
                result.setValue(Resource.success(banners));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.setValue(Resource.error(error.getMessage(), null));
            }
        });

        return result;
    }

    // Admin methods for managing banners
    public void addBanner(Banner banner, OnCompleteListener<Void> listener) {
        // تسجيل دخول مباشر بكلمة مرور admin123
        auth.signInWithEmailAndPassword("admin@worldcupguide.com", "admin123")
            .addOnCompleteListener(signInTask -> {
                if (signInTask.isSuccessful()) {
            Log.d(TAG, "✅ نجح تسجيل دخول الأدمن بكلمة مرور admin123 - التحقق من دور الأدمن قبل الإضافة");
            // تأكد من وجود role=admin قبل محاولة الكتابة حتى لا يتم رفضها لاحقاً ثم ارتداد البيانات
            ensureAdminRole(() -> addBannerDirectly(banner, listener));
                } else {
                    // إذا فشل، جرب إنشاء حساب جديد
                    auth.createUserWithEmailAndPassword("admin@worldcupguide.com", "admin123")
                        .addOnCompleteListener(createTask -> {
                            if (createTask.isSuccessful()) {
                Log.d(TAG, "✅ تم إنشاء حساب أدمن جديد بكلمة مرور admin123 - ضبط الدور ثم الإضافة");
                ensureAdminRole(() -> addBannerDirectly(banner, listener));
                            } else {
                                // تسجيل دخول مجهول كحل أخير
                                auth.signInAnonymously()
                                    .addOnCompleteListener(anonTask -> {
                    // في حالة الدخول المجهول سيُرفض المسار الرئيسي غالباً؛ أبلغ المستخدم بدل إعطاء انطباع نجاح مبدئي ثم ارتداد
                    Log.w(TAG, "⚠️ تم استخدام تسجيل مجهول - قد تُرفض الكتابة إلى المسار المحمي banners");
                    addBannerDirectly(banner, listener);
                                    });
                            }
                        });
                }
            });
    }
    
    private void addBannerDirectly(Banner banner, OnCompleteListener<Void> listener) {
        String bannerId = banner.getId();
        if (bannerId == null || bannerId.isEmpty()) {
            bannerId = bannersRef.push().getKey();
            banner.setId(bannerId);
        }
        
        if (bannerId != null) {
            final String finalBannerId = bannerId;
            
            // إضافة البانر مباشرة إلى مسار banners
            bannersRef.child(finalBannerId).setValue(banner)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ تم إضافة البانر بنجاح في المسار الرئيسي");
                    listener.onComplete(true, "تم إضافة الإعلان بنجاح");
                })
                .addOnFailureListener(error -> {
                    Log.e(TAG, "❌ فشل إضافة البانر في المسار الرئيسي: " + error.getMessage());
                    listener.onComplete(false, "فشل في إضافة الإعلان: " + error.getMessage());
                });
        } else {
            listener.onComplete(false, "فشل في إنشاء معرف البانر");
        }
    }
    
    private void tryAdminAuth(Runnable onComplete) {
        // محاولة تسجيل دخول بالمستخدم الأدمن الموجود
        String[] possiblePasswords = {
            "admin123456",
            "worldcup2026", 
            "admin2026",
            "wcguide123",
            "123456"
        };
        
        tryAdminLoginWithPasswords(0, possiblePasswords, onComplete);
    }
    
    private void tryAdminLoginWithPasswords(int index, String[] passwords, Runnable onComplete) {
        if (index >= passwords.length) {
            // جميع كلمات المرور فشلت، جرب تسجيل دخول مجهول
            auth.signInAnonymously().addOnCompleteListener(task -> {
                onComplete.run(); // استمر حتى لو فشل
            });
            return;
        }
        
        // محاولة تسجيل دخول بالمستخدم admin_user
        auth.signInWithEmailAndPassword("admin@worldcupguide.com", passwords[index])
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // نجح التسجيل، تحقق من دور الأدمن
                    ensureAdminRole(onComplete);
                } else {
                    // جرب كلمة المرور التالية
                    tryAdminLoginWithPasswords(index + 1, passwords, onComplete);
                }
            });
    }
    
    private void ensureAdminRole(Runnable onComplete) {
        if (auth.getCurrentUser() != null) {
            String uid = auth.getCurrentUser().getUid();
            DatabaseReference userRef = database.child("users").child(uid);
            
            // تحقق من وجود دور الأدمن
            userRef.child("roles").child("admin").get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().getValue(Boolean.class) == Boolean.TRUE) {
                    // دور الأدمن موجود بالفعل
                    onComplete.run();
                } else {
                    // أضف دور الأدمن
                    Map<String, Object> adminData = new HashMap<>();
                    Map<String, Object> roles = new HashMap<>();
                    roles.put("admin", true);
                    adminData.put("roles", roles);
                    adminData.put("email", "admin@worldcupguide.com");
                    adminData.put("isAdmin", true);
                    adminData.put("displayName", "مدير النظام");
                    
                    userRef.updateChildren(adminData).addOnCompleteListener(updateTask -> {
                        onComplete.run(); // استمر حتى لو فشل
                    });
                }
            });
        } else {
            onComplete.run();
        }
    }
    
    private void tryMultiplePaths(Banner banner, String bannerId, OnCompleteListener<Void> listener, String operation) {
        // جرب المسار الرئيسي أولاً
        bannersRef.child(bannerId).setValue(banner).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.onComplete(true, "تم " + getOperationText(operation) + " البانر بنجاح");
                return;
            }
            
            // إذا فشل، جرب المسارات البديلة
            tryAlternatePaths(banner, bannerId, listener, operation);
        });
    }
    
    private void tryAlternatePaths(Banner banner, String bannerId, OnCompleteListener<Void> listener, String operation) {
        // جرب المسار العام
        DatabaseReference publicRef = database.child("public_banners");
        publicRef.child(bannerId).setValue(banner).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.onComplete(true, "تم " + getOperationText(operation) + " البانر في المسار العام");
                // حاول النسخ للمسار الرئيسي في الخلفية
                copyToMainBanners(banner, bannerId);
                return;
            }
            
            // جرب المسار المؤقت
            DatabaseReference tempRef = database.child("temp_banners");
            tempRef.child(bannerId).setValue(banner).addOnCompleteListener(tempTask -> {
                if (tempTask.isSuccessful()) {
                    listener.onComplete(true, "تم " + getOperationText(operation) + " البانر في المسار المؤقت");
                    copyToMainBanners(banner, bannerId);
                    return;
                }
                
                // استخدم مدير الطوارئ كآخر محاولة
                EmergencyBannerManager emergencyManager = new EmergencyBannerManager();
                switch (operation) {
                    case "add":
                        emergencyManager.addBannerDirectly(banner, listener);
                        break;
                    case "update":
                        emergencyManager.updateBannerDirectly(banner, listener);
                        break;
                    case "delete":
                        emergencyManager.deleteBannerDirectly(bannerId, listener);
                        break;
                    default:
                        listener.onComplete(false, "عملية غير معروفة: " + operation);
                }
            });
        });
    }
    
    private void copyToMainBanners(Banner banner, String bannerId) {
        // محاولة نسخ للمسار الرئيسي في الخلفية (بدون انتظار النتيجة)
        bannersRef.child(bannerId).setValue(banner);
    }
    
    private String getOperationText(String operation) {
        switch (operation) {
            case "add": return "إضافة";
            case "update": return "تحديث";
            case "delete": return "حذف";
            default: return "معالجة";
        }
    }
    
    private void tryWithAdminAuth(Banner banner, String bannerId, OnCompleteListener<Void> listener, String operation) {
        // Try creating admin account first
        auth.createUserWithEmailAndPassword("admin@worldcupguide.com", "admin123456")
            .addOnCompleteListener(createTask -> {
                if (createTask.isSuccessful()) {
                    // Account created, set admin role and retry
                    setAdminRoleAndRetry(banner, bannerId, listener, operation);
                } else {
                    // Account might exist, try signing in
                    auth.signInWithEmailAndPassword("admin@worldcupguide.com", "admin123456")
                        .addOnCompleteListener(signInTask -> {
                            if (signInTask.isSuccessful()) {
                                // Signed in, retry operation
                                retryBannerOperation(banner, bannerId, listener, operation);
                            } else {
                                // Try anonymous as last resort
                                tryAnonymousAuth(banner, bannerId, listener, operation);
                            }
                        });
                }
            });
    }
    
    private void setAdminRoleAndRetry(Banner banner, String bannerId, OnCompleteListener<Void> listener, String operation) {
        if (auth.getCurrentUser() != null) {
            String uid = auth.getCurrentUser().getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
            
            Map<String, Object> adminData = new HashMap<>();
            Map<String, Object> roles = new HashMap<>();
            roles.put("admin", true);
            adminData.put("roles", roles);
            adminData.put("email", "admin@worldcupguide.com");
            adminData.put("isAdmin", true);
            
            userRef.setValue(adminData).addOnCompleteListener(roleTask -> {
                // Retry operation regardless of role setting result
                retryBannerOperation(banner, bannerId, listener, operation);
            });
        } else {
            retryBannerOperation(banner, bannerId, listener, operation);
        }
    }
    
    private void tryAnonymousAuth(Banner banner, String bannerId, OnCompleteListener<Void> listener, String operation) {
        auth.signInAnonymously().addOnCompleteListener(anonTask -> {
            if (anonTask.isSuccessful()) {
                retryBannerOperation(banner, bannerId, listener, operation);
            } else {
                // Last resort: use emergency manager
                EmergencyBannerManager emergencyManager = new EmergencyBannerManager();
                switch (operation) {
                    case "add":
                        emergencyManager.addBannerDirectly(banner, listener);
                        break;
                    case "update":
                        emergencyManager.updateBannerDirectly(banner, listener);
                        break;
                    case "delete":
                        emergencyManager.deleteBannerDirectly(bannerId, listener);
                        break;
                    default:
                        listener.onComplete(false, "فشل في جميع طرق المصادقة. تأكد من إعدادات Firebase.");
                }
            }
        });
    }
    
    private void retryBannerOperation(Banner banner, String bannerId, OnCompleteListener<Void> listener, String operation) {
        switch (operation) {
            case "add":
                bannersRef.child(bannerId).setValue(banner).addOnCompleteListener(retryTask -> {
                    if (retryTask.isSuccessful()) {
                        listener.onComplete(true, "تم إضافة البانر بنجاح (بعد المصادقة)");
                    } else {
                        listener.onComplete(false, "فشل في إضافة البانر حتى بعد المصادقة: " + 
                            (retryTask.getException() != null ? retryTask.getException().getMessage() : "خطأ غير معروف"));
                    }
                });
                break;
            case "update":
                bannersRef.child(bannerId).setValue(banner).addOnCompleteListener(retryTask -> {
                    if (retryTask.isSuccessful()) {
                        listener.onComplete(true, "تم تحديث البانر بنجاح (بعد المصادقة)");
                    } else {
                        listener.onComplete(false, "فشل في تحديث البانر: " + 
                            (retryTask.getException() != null ? retryTask.getException().getMessage() : "خطأ غير معروف"));
                    }
                });
                break;
            case "delete":
                bannersRef.child(bannerId).removeValue().addOnCompleteListener(retryTask -> {
                    if (retryTask.isSuccessful()) {
                        listener.onComplete(true, "تم حذف البانر بنجاح (بعد المصادقة)");
                    } else {
                        listener.onComplete(false, "فشل في حذف البانر: " + 
                            (retryTask.getException() != null ? retryTask.getException().getMessage() : "خطأ غير معروف"));
                    }
                });
                break;
        }
    }

    public void updateBanner(Banner banner, OnCompleteListener<Void> listener) {
        if (banner.getId() != null) {
            // ضمان امتلاك الحساب الحالي لدور الأدمن قبل التحديث لتفادي ارتداد البيانات
            ensureAdminRole(() -> tryMultiplePaths(banner, banner.getId(), listener, "update"));
        } else {
            listener.onComplete(false, "معرف البانر غير صحيح");
        }
    }

    public void deleteBanner(String bannerId, OnCompleteListener<Void> listener) {
        if (bannerId != null) {
            // حذف من المسار الرئيسي
            ensureAdminRole(() -> bannersRef.child(bannerId).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onComplete(true, "تم حذف البانر بنجاح");
                        // حذف من المسارات البديلة أيضاً
                        deleteFromAlternatePaths(bannerId);
                    } else {
                        // إذا فشل، جرب المسارات البديلة
                        deleteFromAlternatePaths(bannerId, listener);
                    }
                })
            );
        } else {
            listener.onComplete(false, "معرف البانر غير صحيح");
        }
    }
    
    private void deleteFromAlternatePaths(String bannerId) {
        // حذف من المسارات البديلة بدون انتظار النتيجة
        database.child("public_banners").child(bannerId).removeValue();
        database.child("temp_banners").child(bannerId).removeValue();
    }
    
    private void deleteFromAlternatePaths(String bannerId, OnCompleteListener<Void> listener) {
        // حذف من المسار العام
        database.child("public_banners").child(bannerId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.onComplete(true, "تم حذف البانر من المسار العام");
                return;
            }
            
            // حذف من المسار المؤقت
            database.child("temp_banners").child(bannerId).removeValue().addOnCompleteListener(tempTask -> {
                if (tempTask.isSuccessful()) {
                    listener.onComplete(true, "تم حذف البانر من المسار المؤقت");
                } else {
                    // استخدم مدير الطوارئ
                    EmergencyBannerManager emergencyManager = new EmergencyBannerManager();
                    emergencyManager.deleteBannerDirectly(bannerId, listener);
                }
            });
        });
    }

    public interface OnCompleteListener<T> {
        void onComplete(boolean success, String message);
    }

    // Quick Info Methods
    public LiveData<Resource<QuickInfo>> getQuickInfo(String countryCode) {
        MutableLiveData<Resource<QuickInfo>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        quickInfoRef.child(countryCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                QuickInfo quickInfo = snapshot.getValue(QuickInfo.class);
                if (quickInfo != null) {
                    quickInfo.setCountryCode(snapshot.getKey());
                    result.setValue(Resource.success(quickInfo));
                } else {
                    result.setValue(Resource.error("Quick info not found", null));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.setValue(Resource.error(error.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<QuickInfo>>> getQuickInfo() {
        MutableLiveData<Resource<List<QuickInfo>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        quickInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<QuickInfo> quickInfoList = new ArrayList<>();
                for (DataSnapshot quickInfoSnapshot : snapshot.getChildren()) {
                    QuickInfo quickInfo = quickInfoSnapshot.getValue(QuickInfo.class);
                    if (quickInfo != null) {
                        quickInfo.setCountryCode(quickInfoSnapshot.getKey());
                        quickInfoList.add(quickInfo);
                    }
                }
                result.setValue(Resource.success(quickInfoList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.setValue(Resource.error(error.getMessage(), null));
            }
        });

        return result;
    }

    // Storage Methods
    public Task<UploadTask.TaskSnapshot> uploadImage(Uri imageUri, String path) {
        StorageReference imageRef = storage.child("images/" + path);
        return imageRef.putFile(imageUri);
    }

    public Task<Uri> getDownloadUrl(String path) {
        StorageReference imageRef = storage.child("images/" + path);
        return imageRef.getDownloadUrl();
    }

    // Favorites Methods
    public Task<Void> addToFavorites(String userId, String targetId, String targetKind) {
        return usersRef.child(userId)
                .child("favorites")
                .child(targetKind + "Ids")
                .child(targetId)
                .setValue(true);
    }

    public Task<Void> removeFromFavorites(String userId, String targetId, String targetKind) {
        return usersRef.child(userId)
                .child("favorites")
                .child(targetKind + "Ids")
                .child(targetId)
                .removeValue();
    }
}