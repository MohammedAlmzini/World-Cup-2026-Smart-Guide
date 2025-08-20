package com.ahmmedalmzini783.wcguide.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "events")
public class EventEntity {
    @PrimaryKey
    @NonNull
    private String id;
    private String title;
    private String country;
    private String city;
    private String venueName;
    private String type;
    private long startUtc;
    private long endUtc;
    private String imageUrl;
    private int capacity;
    private String ticketUrl;
    private String description;
    private double lat;
    private double lng;
    private long lastUpdated; // For cache management

    public EventEntity() {}

    public EventEntity(@NonNull String id, String title, String country, String city,
                       String venueName, String type, long startUtc, long endUtc,
                       String imageUrl, int capacity, String ticketUrl, String description,
                       double lat, double lng, long lastUpdated) {
        this.id = id;
        this.title = title;
        this.country = country;
        this.city = city;
        this.venueName = venueName;
        this.type = type;
        this.startUtc = startUtc;
        this.endUtc = endUtc;
        this.imageUrl = imageUrl;
        this.capacity = capacity;
        this.ticketUrl = ticketUrl;
        this.description = description;
        this.lat = lat;
        this.lng = lng;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public long getStartUtc() { return startUtc; }
    public void setStartUtc(long startUtc) { this.startUtc = startUtc; }

    public long getEndUtc() { return endUtc; }
    public void setEndUtc(long endUtc) { this.endUtc = endUtc; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getTicketUrl() { return ticketUrl; }
    public void setTicketUrl(String ticketUrl) { this.ticketUrl = ticketUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }

    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }
}