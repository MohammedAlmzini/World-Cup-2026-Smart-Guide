package com.ahmmedalmzini783.wcguide.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;
import androidx.annotation.NonNull;

@Entity(tableName = "reviews")
public class ReviewEntity {
    @PrimaryKey
    @NonNull
    private String id;
    private String userId;
    private String targetId;
    private String targetKind;
    private int rating;
    private String text;
    private long createdAt;
    private long lastUpdated;

    public ReviewEntity() {}

    @Ignore
    public ReviewEntity(@NonNull String id, String userId, String targetId,
                        String targetKind, int rating, String text, long createdAt,
                        long lastUpdated) {
        this.id = id;
        this.userId = userId;
        this.targetId = targetId;
        this.targetKind = targetKind;
        this.rating = rating;
        this.text = text;
        this.createdAt = createdAt;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }

    public String getTargetKind() { return targetKind; }
    public void setTargetKind(String targetKind) { this.targetKind = targetKind; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }
}