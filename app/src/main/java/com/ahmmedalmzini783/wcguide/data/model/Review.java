package com.ahmmedalmzini783.wcguide.data.model;

import java.util.Objects;

public class Review {
    private String id;
    private String userId;
    private String targetId; // Event or Place ID
    private String targetKind; // "event" or "place"
    private int rating; // 1-5
    private String text;
    private long createdAt;

    public Review() {
        // Default constructor required for Firebase
    }

    public Review(String id, String userId, String targetId, String targetKind,
                  int rating, String text, long createdAt) {
        this.id = id;
        this.userId = userId;
        this.targetId = targetId;
        this.targetKind = targetKind;
        this.rating = rating;
        this.text = text;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return Objects.equals(id, review.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Review{" +
                "id='" + id + '\'' +
                ", targetId='" + targetId + '\'' +
                ", rating=" + rating +
                '}';
    }
}