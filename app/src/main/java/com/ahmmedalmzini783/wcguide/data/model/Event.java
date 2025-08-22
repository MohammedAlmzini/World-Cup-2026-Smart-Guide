package com.ahmmedalmzini783.wcguide.data.model;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.Objects;

public class Event {
    private String id;
    private String title;
    private String description;
    private String location;
    private String type;
    private Date date;
    private String imageUrl;
    private boolean isFavorite;
    private String country;
    private String city;

    public Event() {
        // Required empty constructor for Firebase
    }

    public Event(String id, String title, String description, String location, String type, Date date, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.date = date;
        this.imageUrl = imageUrl;
        this.isFavorite = false;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFormattedDate() {
        if (date == null) return "";
        // Simple date formatting - you can enhance this
        return date.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NonNull
    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", type='" + type + '\'' +
                ", date=" + date +
                '}';
    }
}