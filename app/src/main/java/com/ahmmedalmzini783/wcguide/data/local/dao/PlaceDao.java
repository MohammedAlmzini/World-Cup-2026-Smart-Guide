package com.ahmmedalmzini783.wcguide.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.lifecycle.LiveData;

import com.ahmmedalmzini783.wcguide.data.local.entity.PlaceEntity;

import java.util.List;

@Dao
public interface PlaceDao {

    @Query("SELECT * FROM places ORDER BY avgRating DESC")
    LiveData<List<PlaceEntity>> getAllPlaces();

    @Query("SELECT * FROM places WHERE id = :placeId")
    LiveData<PlaceEntity> getPlaceById(String placeId);

    @Query("SELECT * FROM places WHERE country = :country ORDER BY avgRating DESC")
    LiveData<List<PlaceEntity>> getPlacesByCountry(String country);

    @Query("SELECT * FROM places WHERE city = :city ORDER BY avgRating DESC")
    LiveData<List<PlaceEntity>> getPlacesByCity(String city);

    @Query("SELECT * FROM places WHERE kind = :kind ORDER BY avgRating DESC")
    LiveData<List<PlaceEntity>> getPlacesByKind(String kind);

    @Query("SELECT * FROM places WHERE country = :country AND kind = :kind ORDER BY avgRating DESC LIMIT :limit")
    LiveData<List<PlaceEntity>> getPlacesByCountryAndKind(String country, String kind, int limit);

    @Query("SELECT * FROM places WHERE city = :city AND kind = :kind ORDER BY avgRating DESC LIMIT :limit")
    LiveData<List<PlaceEntity>> getPlacesByCityAndKind(String city, String kind, int limit);

    @Query("SELECT * FROM places WHERE priceLevel <= :maxPriceLevel ORDER BY avgRating DESC")
    LiveData<List<PlaceEntity>> getPlacesByPriceLevel(int maxPriceLevel);

    @Query("SELECT * FROM places WHERE avgRating >= :minRating ORDER BY avgRating DESC")
    LiveData<List<PlaceEntity>> getPlacesByRating(float minRating);

    @Query("SELECT DISTINCT country FROM places ORDER BY country ASC")
    LiveData<List<String>> getAllCountries();

    @Query("SELECT DISTINCT city FROM places WHERE country = :country ORDER BY city ASC")
    LiveData<List<String>> getCitiesByCountry(String country);

    @Query("SELECT DISTINCT kind FROM places ORDER BY kind ASC")
    LiveData<List<String>> getAllKinds();

    // Location-based queries (simplified - in real app you'd use spatial functions)
    @Query("SELECT * FROM places WHERE lat BETWEEN :minLat AND :maxLat AND lng BETWEEN :minLng AND :maxLng ORDER BY avgRating DESC")
    LiveData<List<PlaceEntity>> getPlacesInBounds(double minLat, double maxLat, double minLng, double maxLng);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlace(PlaceEntity place);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlaces(List<PlaceEntity> places);

    @Update
    void updatePlace(PlaceEntity place);

    @Delete
    void deletePlace(PlaceEntity place);

    @Query("DELETE FROM places WHERE id = :placeId")
    void deletePlaceById(String placeId);

    @Query("DELETE FROM places")
    void deleteAllPlaces();

    @Query("SELECT COUNT(*) FROM places")
    int getPlaceCount();

    @Query("SELECT * FROM places WHERE lastUpdated < :threshold")
    List<PlaceEntity> getStalePlaces(long threshold);
}