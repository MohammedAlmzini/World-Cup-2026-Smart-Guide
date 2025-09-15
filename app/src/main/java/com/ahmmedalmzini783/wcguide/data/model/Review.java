package com.ahmmedalmzini783.wcguide.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Review implements Parcelable {
    private String id;
    private String landmarkId;
    private String reviewerName;
    private String reviewerEmail;
    private float rating; // من 1 إلى 5
    private String title;
    private String description;
    private List<ReviewImage> images;
    private long timestamp;
    private boolean isApproved;
    private String adminNotes;

    public Review() {
        this.images = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
        this.isApproved = true;
    }

    public Review(String landmarkId, String reviewerName, String reviewerEmail,
                  float rating, String title, String description) {
        this();
        this.landmarkId = landmarkId;
        this.reviewerName = reviewerName;
        this.reviewerEmail = reviewerEmail;
        this.rating = rating;
        this.title = title;
        this.description = description;
    }

    // Parcelable implementation
    protected Review(Parcel in) {
        id = in.readString();
        landmarkId = in.readString();
        reviewerName = in.readString();
        reviewerEmail = in.readString();
        rating = in.readFloat();
        title = in.readString();
        description = in.readString();
        images = in.createTypedArrayList(ReviewImage.CREATOR);
        timestamp = in.readLong();
        isApproved = in.readByte() != 0;
        adminNotes = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(landmarkId);
        dest.writeString(reviewerName);
        dest.writeString(reviewerEmail);
        dest.writeFloat(rating);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeTypedList(images);
        dest.writeLong(timestamp);
        dest.writeByte((byte) (isApproved ? 1 : 0));
        dest.writeString(adminNotes);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLandmarkId() {
        return landmarkId;
    }

    public void setLandmarkId(String landmarkId) {
        this.landmarkId = landmarkId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getReviewerEmail() {
        return reviewerEmail;
    }

    public void setReviewerEmail(String reviewerEmail) {
        this.reviewerEmail = reviewerEmail;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ReviewImage> getImages() {
        return images;
    }

    public void setImages(List<ReviewImage> images) {
        this.images = images;
    }

    public void addImage(ReviewImage image) {
        if (this.images == null) {
            this.images = new ArrayList<>();
        }
        this.images.add(image);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return Objects.equals(id, review.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Review{" +
                "id='" + id + '\'' +
                ", landmarkId='" + landmarkId + '\'' +
                ", rating=" + rating +
                ", title='" + title + '\'' +
                '}';
    }
}