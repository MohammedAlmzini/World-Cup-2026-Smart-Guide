package com.ahmmedalmzini783.wcguide.data.model;

import java.util.List;
import java.util.Objects;

public class Restaurant {
    private String id;
    private String name;
    private String description;
    private String mainImageUrl;
    private String location;
    private String workingHours;
    private List<String> services;
    private float rating;
    private boolean isOpen;
    private List<String> facilities;
    private List<String> worldCupServices;
    private List<AdditionalImage> additionalImages;
    private List<RestaurantMenu> menu;
    private String country;
    private String city;
    private double lat;
    private double lng;
    private String address;
    private String phone;
    private String email;
    private String website;
    private String cuisineType; // نوع المطبخ (إيطالي، عربي، آسيوي، إلخ)
    private String priceRange; // نطاق الأسعار ($ - $$$)
    private boolean hasDelivery; // يوفر توصيل
    private boolean hasReservation; // يقبل حجوزات
    private int capacity; // السعة (عدد الأشخاص)
    private long createdAt;
    private long updatedAt;

    public Restaurant() {
        // Default constructor required for Firebase
    }

    public Restaurant(String id, String name, String description, String mainImageUrl, 
                     String location, String workingHours, List<String> services, 
                     float rating, boolean isOpen, List<String> facilities,
                     List<String> worldCupServices, List<AdditionalImage> additionalImages,
                     List<RestaurantMenu> menu, String country, String city, double lat, double lng,
                     String address, String phone, String email, String website,
                     String cuisineType, String priceRange, boolean hasDelivery, 
                     boolean hasReservation, int capacity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.mainImageUrl = mainImageUrl;
        this.location = location;
        this.workingHours = workingHours;
        this.services = services;
        this.rating = rating;
        this.isOpen = isOpen;
        this.facilities = facilities;
        this.worldCupServices = worldCupServices;
        this.additionalImages = additionalImages;
        this.menu = menu;
        this.country = country;
        this.city = city;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.website = website;
        this.cuisineType = cuisineType;
        this.priceRange = priceRange;
        this.hasDelivery = hasDelivery;
        this.hasReservation = hasReservation;
        this.capacity = capacity;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public void setMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public List<String> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<String> facilities) {
        this.facilities = facilities;
    }

    public List<String> getWorldCupServices() {
        return worldCupServices;
    }

    public void setWorldCupServices(List<String> worldCupServices) {
        this.worldCupServices = worldCupServices;
    }

    public List<AdditionalImage> getAdditionalImages() {
        return additionalImages;
    }

    public void setAdditionalImages(List<AdditionalImage> additionalImages) {
        this.additionalImages = additionalImages;
    }

    public List<RestaurantMenu> getMenu() {
        return menu;
    }

    public void setMenu(List<RestaurantMenu> menu) {
        this.menu = menu;
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

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public boolean isHasDelivery() {
        return hasDelivery;
    }

    public void setHasDelivery(boolean hasDelivery) {
        this.hasDelivery = hasDelivery;
    }

    public boolean isHasReservation() {
        return hasReservation;
    }

    public void setHasReservation(boolean hasReservation) {
        this.hasReservation = hasReservation;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant restaurant = (Restaurant) o;
        return Objects.equals(id, restaurant.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", cuisineType='" + cuisineType + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", rating=" + rating +
                ", isOpen=" + isOpen +
                '}';
    }
}
