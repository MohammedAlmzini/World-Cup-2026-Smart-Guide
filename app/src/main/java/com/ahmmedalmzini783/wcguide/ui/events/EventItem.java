package com.ahmmedalmzini783.wcguide.ui.events;

import java.io.Serializable;
import java.util.Date;

public class EventItem implements Serializable {
    private String id;
    private String title;
    private String description;
    private String location;
    private String imageUrl;
    private Date eventDate;
    private String category;
    private boolean isActive;
    private Date createdAt;
    private Date updatedAt;

    public EventItem() {
        // Default constructor required for Firestore
    }

    public EventItem(String title, String description, String location, String imageUrl, Date eventDate, String category, boolean isActive) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.imageUrl = imageUrl;
        this.eventDate = eventDate;
        this.category = category;
        this.isActive = isActive;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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
