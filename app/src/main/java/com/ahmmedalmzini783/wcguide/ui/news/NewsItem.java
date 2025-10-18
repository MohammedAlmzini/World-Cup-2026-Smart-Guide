package com.ahmmedalmzini783.wcguide.ui.news;

import java.io.Serializable;
import java.util.Date;

public class NewsItem implements Serializable {
    private String id;
    private String title;
    private String description;
    private String content;
    private String imageUrl;
    private String category;
    private boolean priority;
    private Date createdAt;
    private Date updatedAt;

    public NewsItem() {
        // Default constructor required for Firestore
    }

    public NewsItem(String title, String description, String content, String imageUrl, String category, boolean priority) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.imageUrl = imageUrl;
        this.category = category;
        this.priority = priority;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isPriority() {
        return priority;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
