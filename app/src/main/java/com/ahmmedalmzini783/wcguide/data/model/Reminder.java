package com.ahmmedalmzini783.wcguide.data.model;

import java.util.Objects;

public class Reminder {
    private String id;
    private String eventId;
    private long timeMillis; // When to trigger the reminder

    public Reminder() {
        // Default constructor required for Firebase
    }

    public Reminder(String id, String eventId, long timeMillis) {
        this.id = id;
        this.eventId = eventId;
        this.timeMillis = timeMillis;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public long getTimeMillis() { return timeMillis; }
    public void setTimeMillis(long timeMillis) { this.timeMillis = timeMillis; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reminder reminder = (Reminder) o;
        return Objects.equals(id, reminder.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Reminder{" +
                "id='" + id + '\'' +
                ", eventId='" + eventId + '\'' +
                ", timeMillis=" + timeMillis +
                '}';
    }
}