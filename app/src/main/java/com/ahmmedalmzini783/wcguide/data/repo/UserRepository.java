package com.ahmmedalmzini783.wcguide.data.repo;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.ahmmedalmzini783.wcguide.data.local.AppDatabase;
import com.ahmmedalmzini783.wcguide.data.local.dao.FavoriteDao;
import com.ahmmedalmzini783.wcguide.data.local.entity.FavoriteEntity;
import com.ahmmedalmzini783.wcguide.data.model.UserProfile;
import com.ahmmedalmzini783.wcguide.data.remote.FirebaseDataSource;
import com.ahmmedalmzini783.wcguide.util.Resource;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private final FavoriteDao favoriteDao;
    private final FirebaseDataSource firebaseDataSource;
    private final ExecutorService executor;

    public UserRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        favoriteDao = database.favoriteDao();
        firebaseDataSource = new FirebaseDataSource();
        executor = Executors.newFixedThreadPool(2);
    }

    // Authentication methods
    public LiveData<Resource<FirebaseUser>> signInWithEmail(String email, String password) {
        return firebaseDataSource.signInWithEmail(email, password);
    }

    public LiveData<Resource<FirebaseUser>> createUserWithEmail(String email, String password, String displayName) {
        return firebaseDataSource.createUserWithEmail(email, password, displayName);
    }

    public void signOut() {
        firebaseDataSource.signOut();
        // Clear local favorites cache
        executor.execute(() -> favoriteDao.deleteAllFavorites());
    }

    public FirebaseUser getCurrentUser() {
        return firebaseDataSource.getCurrentUser();
    }

    // Profile methods
    public LiveData<Resource<UserProfile>> getUserProfile(String uid) {
        return firebaseDataSource.getUserProfile(uid);
    }

    public LiveData<Resource<Void>> updateUserProfile(UserProfile profile) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        firebaseDataSource.updateUserProfile(profile)
                .addOnSuccessListener(aVoid -> result.setValue(Resource.success(null)))
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getMessage(), null)));

        return result;
    }

    // Favorites methods
    public LiveData<Resource<Void>> addToFavorites(String userId, String targetId, String targetKind) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        // Add to Firebase
        firebaseDataSource.addToFavorites(userId, targetId, targetKind)
                .addOnSuccessListener(aVoid -> {
                    // Add to local cache
                    executor.execute(() -> {
                        FavoriteEntity favorite = new FavoriteEntity();
                        favorite.setId(userId + "_" + targetId);
                        favorite.setUserId(userId);
                        favorite.setTargetId(targetId);
                        favorite.setTargetKind(targetKind);
                        favorite.setCreatedAt(System.currentTimeMillis());
                        favoriteDao.insertFavorite(favorite);
                    });
                    result.setValue(Resource.success(null));
                })
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getMessage(), null)));

        return result;
    }

    public LiveData<Resource<Void>> removeFromFavorites(String userId, String targetId, String targetKind) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        // Remove from Firebase
        firebaseDataSource.removeFromFavorites(userId, targetId, targetKind)
                .addOnSuccessListener(aVoid -> {
                    // Remove from local cache
                    executor.execute(() -> {
                        favoriteDao.deleteFavoriteByKey(userId, targetId, targetKind);
                    });
                    result.setValue(Resource.success(null));
                })
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getMessage(), null)));

        return result;
    }

    public LiveData<List<FavoriteEntity>> getUserFavorites(String userId) {
        return favoriteDao.getUserFavorites(userId);
    }

    public LiveData<List<String>> getUserFavoriteEventIds(String userId) {
        return favoriteDao.getUserFavoriteEventIds(userId);
    }

    public LiveData<List<String>> getUserFavoritePlaceIds(String userId) {
        return favoriteDao.getUserFavoritePlaceIds(userId);
    }

    public LiveData<Boolean> isFavorite(String userId, String targetId, String targetKind) {
        return favoriteDao.isFavorite(userId, targetId, targetKind);
    }

    public LiveData<Integer> getUserFavoriteCount(String userId) {
        return favoriteDao.getUserFavoriteCount(userId);
    }
}