package com.ahmmedalmzini783.wcguide.data.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Place implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String kind; // attraction, hotel, restaurant
    private String name;
    private String country;
    private String city;
    private String address;
    private double lat;
    private double lng;
    private List<String> images;
    private float avgRating;
    private int ratingCount;
    private int priceLevel; // 1-4 ($ to $$$$)
    private String description;
    private List<String> amenities;

    public Place() {
        // Default constructor required for Firebase
    }

    public Place(String id, String kind, String name, String country, String city,
                 String address, double lat, double lng, List<String> images,
                 float avgRating, int ratingCount, int priceLevel, String description,
                 List<String> amenities) {
        this.id = id;
        this.kind = kind;
        this.name = name;
        this.country = country;
        this.city = city;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.images = images;
        this.avgRating = avgRating;
        this.ratingCount = ratingCount;
        this.priceLevel = priceLevel;
        this.description = description;
        this.amenities = amenities;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public float getAvgRating() { return avgRating; }
    public void setAvgRating(float avgRating) { this.avgRating = avgRating; }

    public int getRatingCount() { return ratingCount; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }

    public int getPriceLevel() { return priceLevel; }
    public void setPriceLevel(int priceLevel) { this.priceLevel = priceLevel; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getAmenities() { return amenities; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return Objects.equals(id, place.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Place{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", kind='" + kind + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}