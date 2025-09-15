package com.ahmmedalmzini783.wcguide.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class LandmarkRating implements Parcelable {
    
    private String id;
    private String landmarkId;
    private String landmarkName;
    private float overallRating; // التقييم العام (1-5)
    private String description; // وصف التقييم العام
    private String highlights; // النقاط المميزة
    private String facilities; // المرافق المتوفرة
    private String accessibility; // سهولة الوصول
    private String bestTimeToVisit; // أفضل وقت للزيارة
    private String duration; // المدة المقترحة للزيارة
    private List<String> pros; // الإيجابيات
    private List<String> cons; // السلبيات
    private List<ReviewImage> images; // صور المعلم
    private long timestamp;
    private String adminId; // معرف الأدمن الذي أضاف التقييم
    
    // Constructors
    public LandmarkRating() {
        this.pros = new ArrayList<>();
        this.cons = new ArrayList<>();
        this.images = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
    }
    
    public LandmarkRating(String landmarkId, String landmarkName, float overallRating, String description) {
        this();
        this.landmarkId = landmarkId;
        this.landmarkName = landmarkName;
        this.overallRating = overallRating;
        this.description = description;
        this.id = generateId();
    }
    
    private String generateId() {
        return "landmark_rating_" + landmarkId + "_" + System.currentTimeMillis();
    }
    
    // Parcelable implementation
    protected LandmarkRating(Parcel in) {
        id = in.readString();
        landmarkId = in.readString();
        landmarkName = in.readString();
        overallRating = in.readFloat();
        description = in.readString();
        highlights = in.readString();
        facilities = in.readString();
        accessibility = in.readString();
        bestTimeToVisit = in.readString();
        duration = in.readString();
        pros = in.createStringArrayList();
        cons = in.createStringArrayList();
        images = in.createTypedArrayList(ReviewImage.CREATOR);
        timestamp = in.readLong();
        adminId = in.readString();
    }
    
    public static final Creator<LandmarkRating> CREATOR = new Creator<LandmarkRating>() {
        @Override
        public LandmarkRating createFromParcel(Parcel in) {
            return new LandmarkRating(in);
        }
        
        @Override
        public LandmarkRating[] newArray(int size) {
            return new LandmarkRating[size];
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
        dest.writeString(landmarkName);
        dest.writeFloat(overallRating);
        dest.writeString(description);
        dest.writeString(highlights);
        dest.writeString(facilities);
        dest.writeString(accessibility);
        dest.writeString(bestTimeToVisit);
        dest.writeString(duration);
        dest.writeStringList(pros);
        dest.writeStringList(cons);
        dest.writeTypedList(images);
        dest.writeLong(timestamp);
        dest.writeString(adminId);
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getLandmarkId() { return landmarkId; }
    public void setLandmarkId(String landmarkId) { this.landmarkId = landmarkId; }
    
    public String getLandmarkName() { return landmarkName; }
    public void setLandmarkName(String landmarkName) { this.landmarkName = landmarkName; }
    
    public float getOverallRating() { return overallRating; }
    public void setOverallRating(float overallRating) { this.overallRating = overallRating; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getHighlights() { return highlights; }
    public void setHighlights(String highlights) { this.highlights = highlights; }
    
    public String getFacilities() { return facilities; }
    public void setFacilities(String facilities) { this.facilities = facilities; }
    
    public String getAccessibility() { return accessibility; }
    public void setAccessibility(String accessibility) { this.accessibility = accessibility; }
    
    public String getBestTimeToVisit() { return bestTimeToVisit; }
    public void setBestTimeToVisit(String bestTimeToVisit) { this.bestTimeToVisit = bestTimeToVisit; }
    
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    
    public List<String> getPros() { return pros; }
    public void setPros(List<String> pros) { this.pros = pros; }
    
    public List<String> getCons() { return cons; }
    public void setCons(List<String> cons) { this.cons = cons; }
    
    public List<ReviewImage> getImages() { return images; }
    public void setImages(List<ReviewImage> images) { this.images = images; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }
}
