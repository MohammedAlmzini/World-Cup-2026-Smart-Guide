package com.ahmmedalmzini783.wcguide.data.model;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * فئة الاحتفالات - تشمل حفل انطلاق كأس العالم، حفل افتتاح ملعب، أو الحفلات العامة
 */
public class Celebration {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private Date date;
    private String location;
    private List<String> activities; // الأنشطة التي ستقام
    private String venueName; // اسم المكان
    private double lat;
    private double lng;
    private int capacity; // السعة المتوقعة
    private String ticketUrl; // رابط حجز التذاكر
    private boolean isMainEvent; // هل هو حدث رئيسي
    private String celebrationType; // نوع الاحتفال (افتتاح، ختام، عام)
    private List<String> performers; // المؤدون في الحفل
    private String duration; // مدة الحفل
    private boolean hasCountdown; // هل يظهر العد التنازلي
    private long createdAt;
    private long updatedAt;

    public Celebration() {
        // Required empty constructor for Firebase
    }

    public Celebration(String id, String name, String description, String imageUrl, Date date, String location) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.date = date;
        this.location = location;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.hasCountdown = false;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getActivities() {
        return activities;
    }

    public void setActivities(List<String> activities) {
        this.activities = activities;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getTicketUrl() {
        return ticketUrl;
    }

    public void setTicketUrl(String ticketUrl) {
        this.ticketUrl = ticketUrl;
    }

    public boolean isMainEvent() {
        return isMainEvent;
    }

    public void setMainEvent(boolean mainEvent) {
        isMainEvent = mainEvent;
    }

    public String getCelebrationType() {
        return celebrationType;
    }

    public void setCelebrationType(String celebrationType) {
        this.celebrationType = celebrationType;
    }

    public List<String> getPerformers() {
        return performers;
    }

    public void setPerformers(List<String> performers) {
        this.performers = performers;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isHasCountdown() {
        return hasCountdown;
    }

    public void setHasCountdown(boolean hasCountdown) {
        this.hasCountdown = hasCountdown;
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

    /**
     * فحص ما إذا كان الحدث يحتاج لعد تنازلي (أقل من 10 أيام)
     */
    public boolean shouldShowCountdown() {
        if (date == null) return false;
        
        long currentTime = System.currentTimeMillis();
        long eventTime = date.getTime();
        long diffInMillis = eventTime - currentTime;
        long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);
        
        return diffInDays >= 0 && diffInDays <= 10;
    }

    /**
     * حساب الوقت المتبقي للحدث
     */
    public CountdownTime getCountdownTime() {
        if (date == null) return null;
        
        long currentTime = System.currentTimeMillis();
        long eventTime = date.getTime();
        long diffInMillis = eventTime - currentTime;
        
        if (diffInMillis <= 0) return null;
        
        long days = diffInMillis / (24 * 60 * 60 * 1000);
        long hours = (diffInMillis % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
        long minutes = (diffInMillis % (60 * 60 * 1000)) / (60 * 1000);
        long seconds = (diffInMillis % (60 * 1000)) / 1000;
        
        return new CountdownTime(days, hours, minutes, seconds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Celebration that = (Celebration) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NonNull
    @Override
    public String toString() {
        return "Celebration{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", date=" + date +
                ", celebrationType='" + celebrationType + '\'' +
                '}';
    }

    /**
     * فئة لحفظ بيانات العد التنازلي
     */
    public static class CountdownTime {
        private long days;
        private long hours;
        private long minutes;
        private long seconds;

        public CountdownTime(long days, long hours, long minutes, long seconds) {
            this.days = days;
            this.hours = hours;
            this.minutes = minutes;
            this.seconds = seconds;
        }

        public long getDays() {
            return days;
        }

        public long getHours() {
            return hours;
        }

        public long getMinutes() {
            return minutes;
        }

        public long getSeconds() {
            return seconds;
        }

        @Override
        public String toString() {
            return String.format("%d يوم، %02d:%02d:%02d", days, hours, minutes, seconds);
        }
    }
}