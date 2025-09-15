package com.ahmmedalmzini783.wcguide.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ReviewImage implements Parcelable {
    private String id;
    private String imageUrl;
    private String imageName;
    private String description;
    private long timestamp;

    public ReviewImage() {
        this.timestamp = System.currentTimeMillis();
    }

    public ReviewImage(String imageUrl, String imageName) {
        this();
        this.imageUrl = imageUrl;
        this.imageName = imageName;
    }

    public ReviewImage(String imageUrl, String imageName, String description) {
        this(imageUrl, imageName);
        this.description = description;
    }

    // Parcelable implementation
    protected ReviewImage(Parcel in) {
        id = in.readString();
        imageUrl = in.readString();
        imageName = in.readString();
        description = in.readString();
        timestamp = in.readLong();
    }

    public static final Creator<ReviewImage> CREATOR = new Creator<ReviewImage>() {
        @Override
        public ReviewImage createFromParcel(Parcel in) {
            return new ReviewImage(in);
        }

        @Override
        public ReviewImage[] newArray(int size) {
            return new ReviewImage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(imageUrl);
        dest.writeString(imageName);
        dest.writeString(description);
        dest.writeLong(timestamp);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
