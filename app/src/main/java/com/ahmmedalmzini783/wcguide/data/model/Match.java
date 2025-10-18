package com.ahmmedalmzini783.wcguide.data.model;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.Objects;

/**
 * فئة المباريات - تشمل تفاصيل المباريات في كأس العالم
 */
public class Match {
    private String id;
    private String homeTeam; // الفريق الأول
    private String awayTeam; // الفريق الثاني
    private String homeTeamFlag; // رابط علم الفريق الأول
    private String awayTeamFlag; // رابط علم الفريق الثاني
    private Date matchDate; // موعد المباراة
    private String stadium; // ملعب المباراة
    private String referee; // حكم المباراة
    private int stadiumCapacity; // سعة الملعب من المتفرجين
    private String location; // موقع الملعب
    private double lat;
    private double lng;
    private String matchType; // نوع المباراة (دور المجموعات، الدور الثاني، إلخ)
    private String group; // المجموعة (في حالة دور المجموعات)
    private boolean isImportant; // هل المباراة مهمة
    private String description; // وصف المباراة
    private String ticketUrl; // رابط حجز التذاكر
    private boolean hasCountdown; // هل يظهر العد التنازلي
    
    // نتيجة المباراة (في حالة انتهاء المباراة)
    private Integer homeScore;
    private Integer awayScore;
    private String matchStatus; // حالة المباراة (لم تبدأ، جارية، انتهت)
    
    private long createdAt;
    private long updatedAt;

    public Match() {
        // Required empty constructor for Firebase
    }

    public Match(String id, String homeTeam, String awayTeam, Date matchDate, String stadium) {
        this.id = id;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.matchDate = matchDate;
        this.stadium = stadium;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.hasCountdown = false;
        this.matchStatus = "scheduled"; // مجدولة
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Date getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(Date matchDate) {
        this.matchDate = matchDate;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public String getReferee() {
        return referee;
    }

    public void setReferee(String referee) {
        this.referee = referee;
    }

    public int getStadiumCapacity() {
        return stadiumCapacity;
    }

    public void setStadiumCapacity(int stadiumCapacity) {
        this.stadiumCapacity = stadiumCapacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public boolean isImportant() {
        return isImportant;
    }

    public void setImportant(boolean important) {
        isImportant = important;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTicketUrl() {
        return ticketUrl;
    }

    public void setTicketUrl(String ticketUrl) {
        this.ticketUrl = ticketUrl;
    }

    public boolean isHasCountdown() {
        return hasCountdown;
    }

    public void setHasCountdown(boolean hasCountdown) {
        this.hasCountdown = hasCountdown;
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
     * فحص ما إذا كانت المباراة تحتاج لعد تنازلي (أقل من 10 أيام)
     */
    public boolean shouldShowCountdown() {
        if (matchDate == null) return false;
        
        long currentTime = System.currentTimeMillis();
        long matchTime = matchDate.getTime();
        long diffInMillis = matchTime - currentTime;
        long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);
        
        return diffInDays >= 0 && diffInDays <= 10;
    }

    /**
     * حساب الوقت المتبقي للمباراة
     */
    public CountdownTime getCountdownTime() {
        if (matchDate == null) return null;
        
        long currentTime = System.currentTimeMillis();
        long matchTime = matchDate.getTime();
        long diffInMillis = matchTime - currentTime;
        
        if (diffInMillis <= 0) return null;
        
        long days = diffInMillis / (24 * 60 * 60 * 1000);
        long hours = (diffInMillis % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
        long minutes = (diffInMillis % (60 * 60 * 1000)) / (60 * 1000);
        long seconds = (diffInMillis % (60 * 1000)) / 1000;
        
        return new CountdownTime(days, hours, minutes, seconds);
    }

    /**
     * الحصول على اسم المباراة
     */
    public String getMatchTitle() {
        return homeTeam + " ضد " + awayTeam;
    }

    /**
     * الحصول على النتيجة النهائية
     */
    public String getFinalScore() {
        if (homeScore == null || awayScore == null) return "";
        return homeScore + " - " + awayScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return Objects.equals(id, match.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NonNull
    @Override
    public String toString() {
        return "Match{" +
                "id='" + id + '\'' +
                ", homeTeam='" + homeTeam + '\'' +
                ", awayTeam='" + awayTeam + '\'' +
                ", matchDate=" + matchDate +
                ", stadium='" + stadium + '\'' +
                ", matchStatus='" + matchStatus + '\'' +
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