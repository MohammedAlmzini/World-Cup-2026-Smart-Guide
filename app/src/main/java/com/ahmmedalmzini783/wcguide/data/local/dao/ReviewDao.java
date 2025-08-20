package com.ahmmedalmzini783.wcguide.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.lifecycle.LiveData;

import com.ahmmedalmzini783.wcguide.data.local.entity.ReviewEntity;

import java.util.List;

@Dao
public interface ReviewDao {

    @Query("SELECT * FROM reviews WHERE targetId = :targetId AND targetKind = :targetKind ORDER BY createdAt DESC")
    LiveData<List<ReviewEntity>> getReviewsForTarget(String targetId, String targetKind);

    @Query("SELECT * FROM reviews WHERE targetId = :targetId AND targetKind = :targetKind ORDER BY createdAt DESC LIMIT :limit")
    LiveData<List<ReviewEntity>> getReviewsForTargetLimit(String targetId, String targetKind, int limit);

    @Query("SELECT * FROM reviews WHERE userId = :userId ORDER BY createdAt DESC")
    LiveData<List<ReviewEntity>> getUserReviews(String userId);

    @Query("SELECT * FROM reviews WHERE id = :reviewId")
    LiveData<ReviewEntity> getReviewById(String reviewId);

    @Query("SELECT AVG(rating) FROM reviews WHERE targetId = :targetId AND targetKind = :targetKind")
    LiveData<Float> getAverageRating(String targetId, String targetKind);

    @Query("SELECT COUNT(*) FROM reviews WHERE targetId = :targetId AND targetKind = :targetKind")
    LiveData<Integer> getReviewCount(String targetId, String targetKind);

    @Query("SELECT COUNT(*) FROM reviews WHERE targetId = :targetId AND targetKind = :targetKind AND rating = :rating")
    LiveData<Integer> getReviewCountByRating(String targetId, String targetKind, int rating);

    @Query("SELECT EXISTS(SELECT 1 FROM reviews WHERE userId = :userId AND targetId = :targetId AND targetKind = :targetKind)")
    LiveData<Boolean> hasUserReviewed(String userId, String targetId, String targetKind);

    @Query("SELECT * FROM reviews WHERE userId = :userId AND targetId = :targetId AND targetKind = :targetKind")
    LiveData<ReviewEntity> getUserReviewForTarget(String userId, String targetId, String targetKind);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReview(ReviewEntity review);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReviews(List<ReviewEntity> reviews);

    @Update
    void updateReview(ReviewEntity review);

    @Delete
    void deleteReview(ReviewEntity review);

    @Query("DELETE FROM reviews WHERE id = :reviewId")
    void deleteReviewById(String reviewId);

    @Query("DELETE FROM reviews WHERE userId = :userId")
    void deleteUserReviews(String userId);

    @Query("DELETE FROM reviews WHERE targetId = :targetId AND targetKind = :targetKind")
    void deleteReviewsForTarget(String targetId, String targetKind);

    @Query("DELETE FROM reviews")
    void deleteAllReviews();

    @Query("SELECT * FROM reviews WHERE lastUpdated < :threshold")
    List<ReviewEntity> getStaleReviews(long threshold);
}