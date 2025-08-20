package com.ahmmedalmzini783.wcguide.data.model;

import java.util.Objects;

public class Banner {
    private String id;
    private String title;
    private String imageUrl;
    private String deeplink; // app://event/evt_123 or app://place/pl_001

    public Banner() {
        // Default constructor required for Firebase
    }

    public Banner(String id, String title, String imageUrl, String deeplink) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.deeplink = deeplink;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDeeplink() { return deeplink; }
    public void setDeeplink(String deeplink) { this.deeplink = deeplink; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Banner banner = (Banner) o;
        return Objects.equals(id, banner.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Banner{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}