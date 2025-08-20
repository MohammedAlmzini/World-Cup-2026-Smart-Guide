package com.ahmmedalmzini783.wcguide.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "favorites")
public class FavoriteEntity {
    @PrimaryKey
    @NonNull
    private String id; // Composite: userId + "_" + targetId
    private String userId;
    private String targetId; // Event or Place ID
    private String targetKind; // "event" or "place"
    private long createdAt;

    public FavoriteEntity() {}

    public FavoriteEntity(@NonNull String id, String userId, String targetId,
                          String targetKind, long createdAt) {
        this.id = id;
        this.userId = userId;
        this.targetId = targetId;
        this.targetKind = targetKind;
        this.createdAt = createdAt;
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

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}