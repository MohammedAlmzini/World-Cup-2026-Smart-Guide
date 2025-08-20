package com.ahmmedalmzini783.wcguide.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.lifecycle.LiveData;

import com.ahmmedalmzini783.wcguide.data.local.entity.EventEntity;

import java.util.List;

@Dao
public interface EventDao {

    @Query("SELECT * FROM events ORDER BY startUtc ASC")
    LiveData<List<EventEntity>> getAllEvents();

    @Query("SELECT * FROM events WHERE id = :eventId")
    LiveData<EventEntity> getEventById(String eventId);

    @Query("SELECT * FROM events WHERE country = :country ORDER BY startUtc ASC")
    LiveData<List<EventEntity>> getEventsByCountry(String country);

    @Query("SELECT * FROM events WHERE city = :city ORDER BY startUtc ASC")
    LiveData<List<EventEntity>> getEventsByCity(String city);

    @Query("SELECT * FROM events WHERE type = :type ORDER BY startUtc ASC")
    LiveData<List<EventEntity>> getEventsByType(String type);

    @Query("SELECT * FROM events WHERE startUtc BETWEEN :startTime AND :endTime ORDER BY startUtc ASC")
    LiveData<List<EventEntity>> getEventsByDateRange(long startTime, long endTime);

    @Query("SELECT * FROM events WHERE country = :country AND city = :city AND type = :type ORDER BY startUtc ASC")
    LiveData<List<EventEntity>> getFilteredEvents(String country, String city, String type);

    @Query("SELECT * FROM events WHERE startUtc > :currentTime ORDER BY startUtc ASC LIMIT :limit")
    LiveData<List<EventEntity>> getUpcomingEvents(long currentTime, int limit);

    @Query("SELECT DISTINCT country FROM events ORDER BY country ASC")
    LiveData<List<String>> getAllCountries();

    @Query("SELECT DISTINCT city FROM events WHERE country = :country ORDER BY city ASC")
    LiveData<List<String>> getCitiesByCountry(String country);

    @Query("SELECT DISTINCT type FROM events ORDER BY type ASC")
    LiveData<List<String>> getAllTypes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEvent(EventEntity event);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEvents(List<EventEntity> events);

    @Update
    void updateEvent(EventEntity event);

    @Delete
    void deleteEvent(EventEntity event);

    @Query("DELETE FROM events WHERE id = :eventId")
    void deleteEventById(String eventId);

    @Query("DELETE FROM events")
    void deleteAllEvents();

    @Query("SELECT COUNT(*) FROM events")
    int getEventCount();

    @Query("SELECT * FROM events WHERE lastUpdated < :threshold")
    List<EventEntity> getStaleEvents(long threshold);
}