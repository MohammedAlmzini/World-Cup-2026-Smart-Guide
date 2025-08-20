package com.ahmmedalmzini783.wcguide.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.lifecycle.LiveData;

import com.ahmmedalmzini783.wcguide.data.local.entity.FavoriteEntity;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Query("SELECT * FROM favorites WHERE userId = :userId ORDER BY createdAt DESC")
    LiveData<List<FavoriteEntity>> getUserFavorites(String userId);

    @Query("SELECT * FROM favorites WHERE userId = :userId AND targetKind = :targetKind ORDER BY createdAt DESC")
    LiveData<List<FavoriteEntity>> getUserFavoritesByKind(String userId, String targetKind);

    @Query("SELECT targetId FROM favorites WHERE userId = :userId AND targetKind = 'event'")
    LiveData<List<String>> getUserFavoriteEventIds(String userId);

    @Query("SELECT targetId FROM favorites WHERE userId = :userId AND targetKind = 'place'")
    LiveData<List<String>> getUserFavoritePlaceIds(String userId);

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND targetId = :targetId AND targetKind = :targetKind)")
    LiveData<Boolean> isFavorite(String userId, String targetId, String targetKind);

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND targetId = :targetId AND targetKind = :targetKind)")
    boolean isFavoriteSync(String userId, String targetId, String targetKind);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavorite(FavoriteEntity favorite);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavorites(List<FavoriteEntity> favorites);

    @Delete
    void deleteFavorite(FavoriteEntity favorite);

    @Query("DELETE FROM favorites WHERE userId = :userId AND targetId = :targetId AND targetKind = :targetKind")
    void deleteFavoriteByKey(String userId, String targetId, String targetKind);

    @Query("DELETE FROM favorites WHERE userId = :userId")
    void deleteUserFavorites(String userId);

    @Query("DELETE FROM favorites")
    void deleteAllFavorites();

    @Query("SELECT COUNT(*) FROM favorites WHERE userId = :userId")
    LiveData<Integer> getUserFavoriteCount(String userId);

    @Query("SELECT COUNT(*) FROM favorites WHERE userId = :userId AND targetKind = :targetKind")
    LiveData<Integer> getUserFavoriteCountByKind(String userId, String targetKind);
}