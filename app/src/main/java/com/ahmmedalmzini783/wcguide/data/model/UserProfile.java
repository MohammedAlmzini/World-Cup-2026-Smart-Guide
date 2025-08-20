package com.ahmmedalmzini783.wcguide.data.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserProfile {
    private String uid;
    private String displayName;
    private String photoUrl;
    private String email;
    private Map<String, Boolean> roles; // admin: true/false
    private Favorites favorites;
    private List<String> preferredCities;

    public static class Favorites {
        private List<String> eventIds;
        private List<String> placeIds;

        public Favorites() {}

        public Favorites(List<String> eventIds, List<String> placeIds) {
            this.eventIds = eventIds;
            this.placeIds = placeIds;
        }

        public List<String> getEventIds() { return eventIds; }
        public void setEventIds(List<String> eventIds) { this.eventIds = eventIds; }

        public List<String> getPlaceIds() { return placeIds; }
        public void setPlaceIds(List<String> placeIds) { this.placeIds = placeIds; }
    }

    public UserProfile() {
        // Default constructor required for Firebase
    }

    public UserProfile(String uid, String displayName, String photoUrl, String email,
                       Map<String, Boolean> roles, Favorites favorites, List<String> preferredCities) {
        this.uid = uid;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
        this.email = email;
        this.roles = roles;
        this.favorites = favorites;
        this.preferredCities = preferredCities;
    }

    // Getters and Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Map<String, Boolean> getRoles() { return roles; }
    public void setRoles(Map<String, Boolean> roles) { this.roles = roles; }

    public Favorites getFavorites() { return favorites; }
    public void setFavorites(Favorites favorites) { this.favorites = favorites; }

    public List<String> getPreferredCities() { return preferredCities; }
    public void setPreferredCities(List<String> preferredCities) { this.preferredCities = preferredCities; }

    public boolean isAdmin() {
        return roles != null && Boolean.TRUE.equals(roles.get("admin"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return Objects.equals(uid, that.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "uid='" + uid + '\'' +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}