package com.ahmmedalmzini783.wcguide.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;
import androidx.annotation.NonNull;

@Entity(tableName = "places")
public class PlaceEntity {
    @PrimaryKey
    @NonNull
    private String id;
    private String kind;
    private String name;
    private String country;
    private String city;
    private String address;
    private double lat;
    private double lng;
    private String imagesJson; // JSON string of List<String>
    private float avgRating;
    private int ratingCount;
    private int priceLevel;
    private String description;
    private String amenitiesJson; // JSON string of List<String>
    private long lastUpdated;

    public PlaceEntity() {}

    @Ignore
    public PlaceEntity(@NonNull String id, String kind, String name, String country,
                       String city, String address, double lat, double lng,
                       String imagesJson, float avgRating, int ratingCount, int priceLevel,
                       String description, String amenitiesJson, long lastUpdated) {
        this.id = id;
        this.kind = kind;
        this.name = name;
        this.country = country;
        this.city = city;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.imagesJson = imagesJson;
        this.avgRating = avgRating;
        this.ratingCount = ratingCount;
        this.priceLevel = priceLevel;
        this.description = description;
        this.amenitiesJson = amenitiesJson;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }

    public String getImagesJson() { return imagesJson; }
    public void setImagesJson(String imagesJson) { this.imagesJson = imagesJson; }

    public float getAvgRating() { return avgRating; }
    public void setAvgRating(float avgRating) { this.avgRating = avgRating; }

    public int getRatingCount() { return ratingCount; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }

    public int getPriceLevel() { return priceLevel; }
    public void setPriceLevel(int priceLevel) { this.priceLevel = priceLevel; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAmenitiesJson() { return amenitiesJson; }
    public void setAmenitiesJson(String amenitiesJson) { this.amenitiesJson = amenitiesJson; }

    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }
}