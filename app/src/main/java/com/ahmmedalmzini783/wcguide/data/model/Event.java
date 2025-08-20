package com.ahmmedalmzini783.wcguide.data.model;

import java.util.Objects;

public class Event {
    private String id;
    private String title;
    private String country;
    private String city;
    private String venueName;
    private String type; // match, ceremony, concert, etc.
    private long startUtc;
    private long endUtc;
    private String imageUrl;
    private int capacity;
    private String ticketUrl;
    private String description;
    private double lat;
    private double lng;

    public Event() {
        // Default constructor required for Firebase
    }

    public Event(String id, String title, String country, String city, String venueName,
                 String type, long startUtc, long endUtc, String imageUrl, int capacity,
                 String ticketUrl, String description, double lat, double lng) {
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
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", city='" + city + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}