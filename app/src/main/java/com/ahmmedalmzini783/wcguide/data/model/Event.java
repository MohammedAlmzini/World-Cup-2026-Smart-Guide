package com.ahmmedalmzini783.wcguide.data.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Event implements Serializable {
    private String id;
    private String title;
    private String description;
    private String location;
    private String type; // "celebration" أو "match" أو "general"
    private Date date;
    private String imageUrl;
    private boolean isFavorite;
    private String country;
    private String city;
    private String venueName;
    private long startUtc;
    private long endUtc;
    private int capacity;
    private String ticketUrl;
    private double lat;
    private double lng;
    
    // خصائص إضافية للاحتفالات
    private List<String> activities; // الأنشطة التي ستقام
    private List<String> performers; // المؤدون في الحفل
    private String duration; // مدة الحفل
    private String celebrationType; // نوع الاحتفال
    
    // خصائص إضافية للمباريات
    private String homeTeam; // الفريق الأول
    private String awayTeam; // الفريق الثاني
    private String homeTeamFlag; // رابط علم الفريق الأول
    private String awayTeamFlag; // رابط علم الفريق الثاني
    private String referee; // حكم المباراة
    private String matchType; // نوع المباراة
    private String group; // المجموعة
    private Integer homeScore; // نتيجة الفريق الأول
    private Integer awayScore; // نتيجة الفريق الثاني
    private String matchStatus; // حالة المباراة
    
    // خصائص العد التنازلي
    private boolean hasCountdown;
    private long createdAt;
    private long updatedAt;
    
    // خاصية الفعالية المميزة
    private boolean isFeatured;

    public Event() {
        // Required empty constructor for Firebase
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.hasCountdown = false;
        this.matchStatus = "scheduled";
    }

    public Event(String id, String title, String description, String location, String type, Date date, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.date = date;
        this.imageUrl = imageUrl;
        this.isFavorite = false;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.hasCountdown = false;
        this.matchStatus = "scheduled";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
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

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public long getStartUtc() {
        return startUtc;
    }

    public void setStartUtc(long startUtc) {
        this.startUtc = startUtc;
    }

    public long getEndUtc() {
        return endUtc;
    }

    public void setEndUtc(long endUtc) {
        this.endUtc = endUtc;
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

    // Getters and Setters للخصائص الجديدة
    public List<String> getActivities() {
        return activities;
    }

    public void setActivities(List<String> activities) {
        this.activities = activities;
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

    public String getCelebrationType() {
        return celebrationType;
    }

    public void setCelebrationType(String celebrationType) {
        this.celebrationType = celebrationType;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public String getHomeTeamFlag() {
        return homeTeamFlag;
    }

    public void setHomeTeamFlag(String homeTeamFlag) {
        this.homeTeamFlag = homeTeamFlag;
    }

    public String getAwayTeamFlag() {
        return awayTeamFlag;
    }

    public void setAwayTeamFlag(String awayTeamFlag) {
        this.awayTeamFlag = awayTeamFlag;
    }

    public String getReferee() {
        return referee;
    }

    public void setReferee(String referee) {
        this.referee = referee;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Integer getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(Integer homeScore) {
        this.homeScore = homeScore;
    }

    public Integer getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(Integer awayScore) {
        this.awayScore = awayScore;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
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

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }

    public String getFormattedDate() {
        if (date == null) return "تاريخ غير محدد";
        
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return dateFormat.format(date);
        } catch (Exception e) {
            return date.toString();
        }
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

    /**
     * الحصول على عنوان الحدث حسب النوع
     */
    public String getEventTitle() {
        if ("match".equals(type) && homeTeam != null && awayTeam != null) {
            return homeTeam + " ضد " + awayTeam;
        }
        return title;
    }

    /**
     * الحصول على النتيجة النهائية للمباراة
     */
    public String getFinalScore() {
        if (!"match".equals(type) || homeScore == null || awayScore == null) return "";
        return homeScore + " - " + awayScore;
    }

    /**
     * فحص ما إذا كان الحدث مباراة
     */
    public boolean isMatch() {
        return "match".equals(type);
    }

    /**
     * فحص ما إذا كان الحدث احتفال
     */
    public boolean isCelebration() {
        return "celebration".equals(type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NonNull
    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", type='" + type + '\'' +
                ", date=" + date +
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

        public String toArabicString() {
            StringBuilder sb = new StringBuilder();
            if (days > 0) {
                sb.append(days).append(" يوم");
                if (hours > 0 || minutes > 0 || seconds > 0) {
                    sb.append("، ");
                }
            }
            if (hours > 0) {
                sb.append(hours).append(" ساعة");
                if (minutes > 0 || seconds > 0) {
                    sb.append("، ");
                }
            }
            if (minutes > 0) {
                sb.append(minutes).append(" دقيقة");
                if (seconds > 0) {
                    sb.append("، ");
                }
            }
            if (seconds > 0 || sb.length() == 0) {
                sb.append(seconds).append(" ثانية");
            }
            return sb.toString();
        }
    }
}