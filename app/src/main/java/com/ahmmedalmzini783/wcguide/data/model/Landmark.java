package com.ahmmedalmzini783.wcguide.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Landmark implements Parcelable {
    private String id;
    private String name;
    private String description;
    private String address;
    private double latitude;
    private double longitude;
    private String imageUrl;
    private String category;
    private float rating;
    private long timestamp;
    private boolean isActive;

    public Landmark() {
        // Required empty constructor for Firebase
    }

    public Landmark(String name, String description, String address, double latitude, double longitude) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = 0.0f;
        this.timestamp = System.currentTimeMillis();
        this.isActive = true;
    }

    // Parcelable implementation
    protected Landmark(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        imageUrl = in.readString();
        category = in.readString();
        rating = in.readFloat();
        timestamp = in.readLong();
        isActive = in.readByte() != 0;
    }

    public static final Creator<Landmark> CREATOR = new Creator<Landmark>() {
        @Override
        public Landmark createFromParcel(Parcel in) {
            return new Landmark(in);
        }

        @Override
        public Landmark[] newArray(int size) {
            return new Landmark[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(imageUrl);
        dest.writeString(category);
        dest.writeFloat(rating);
        dest.writeLong(timestamp);
        dest.writeByte((byte) (isActive ? 1 : 0));
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Landmark landmark = (Landmark) o;
        return Objects.equals(id, landmark.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Landmark{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
