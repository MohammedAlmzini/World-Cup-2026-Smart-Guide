package com.ahmmedalmzini783.wcguide.data.model;

import java.util.List;
import java.util.Objects;

public class Hotel {
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
    private List<HotelRoom> rooms;
    private String country;
    private String city;
    private double lat;
    private double lng;
    private String address;
    private String phone;
    private String email;
    private String website;
    private long createdAt;
    private long updatedAt;

    public Hotel() {
        // Default constructor required for Firebase
    }

    public Hotel(String id, String name, String description, String mainImageUrl, 
                 String location, String workingHours, List<String> services, 
                 float rating, boolean isOpen, List<String> facilities,
                 List<String> worldCupServices, List<AdditionalImage> additionalImages,
                 List<HotelRoom> rooms, String country, String city, double lat, double lng,
                 String address, String phone, String email, String website) {
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
        this.rooms = rooms;
        this.country = country;
        this.city = city;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.website = website;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMainImageUrl() { return mainImageUrl; }
    public void setMainImageUrl(String mainImageUrl) { this.mainImageUrl = mainImageUrl; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getWorkingHours() { return workingHours; }
    public void setWorkingHours(String workingHours) { this.workingHours = workingHours; }

    public List<String> getServices() { return services; }
    public void setServices(List<String> services) { this.services = services; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public boolean isOpen() { return isOpen; }
    public void setOpen(boolean open) { isOpen = open; }

    public List<String> getFacilities() { return facilities; }
    public void setFacilities(List<String> facilities) { this.facilities = facilities; }

    public List<String> getWorldCupServices() { return worldCupServices; }
    public void setWorldCupServices(List<String> worldCupServices) { this.worldCupServices = worldCupServices; }

    public List<AdditionalImage> getAdditionalImages() { return additionalImages; }
    public void setAdditionalImages(List<AdditionalImage> additionalImages) { this.additionalImages = additionalImages; }

    public List<HotelRoom> getRooms() { return rooms; }
    public void setRooms(List<HotelRoom> rooms) { this.rooms = rooms; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hotel hotel = (Hotel) o;
        return Objects.equals(id, hotel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", rating=" + rating +
                '}';
    }

    // Nested classes for additional data structures
    public static class AdditionalImage {
        private String imageUrl;
        private String imageName;

        public AdditionalImage() {}

        public AdditionalImage(String imageUrl, String imageName) {
            this.imageUrl = imageUrl;
            this.imageName = imageName;
        }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

        public String getImageName() { return imageName; }
        public void setImageName(String imageName) { this.imageName = imageName; }
    }

    public static class HotelRoom {
        private String roomName;
        private int capacity;
        private double pricePerNight;
        private List<String> roomFacilities;
        private String bookingUrl;

        public HotelRoom() {}

        public HotelRoom(String roomName, int capacity, double pricePerNight, 
                        List<String> roomFacilities, String bookingUrl) {
            this.roomName = roomName;
            this.capacity = capacity;
            this.pricePerNight = pricePerNight;
            this.roomFacilities = roomFacilities;
            this.bookingUrl = bookingUrl;
        }

        public String getRoomName() { return roomName; }
        public void setRoomName(String roomName) { this.roomName = roomName; }

        public int getCapacity() { return capacity; }
        public void setCapacity(int capacity) { this.capacity = capacity; }

        public double getPricePerNight() { return pricePerNight; }
        public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }

        public List<String> getRoomFacilities() { return roomFacilities; }
        public void setRoomFacilities(List<String> roomFacilities) { this.roomFacilities = roomFacilities; }

        public String getBookingUrl() { return bookingUrl; }
        public void setBookingUrl(String bookingUrl) { this.bookingUrl = bookingUrl; }
    }
}
