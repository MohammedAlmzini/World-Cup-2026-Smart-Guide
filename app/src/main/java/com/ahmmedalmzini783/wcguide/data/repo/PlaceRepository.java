package com.ahmmedalmzini783.wcguide.data.repo;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.ahmmedalmzini783.wcguide.data.local.AppDatabase;
import com.ahmmedalmzini783.wcguide.data.local.dao.PlaceDao;
import com.ahmmedalmzini783.wcguide.data.local.entity.PlaceEntity;
import com.ahmmedalmzini783.wcguide.data.model.Place;
import com.ahmmedalmzini783.wcguide.data.remote.FirebaseDataSource;
import com.ahmmedalmzini783.wcguide.util.Resource;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlaceRepository {
    private final PlaceDao placeDao;
    private final FirebaseDataSource firebaseDataSource;
    private final ExecutorService executor;
    private final Gson gson;

    public PlaceRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        placeDao = database.placeDao();
        firebaseDataSource = new FirebaseDataSource();
        executor = Executors.newFixedThreadPool(4);
        gson = new Gson();
    }

    public LiveData<Resource<List<Place>>> getAllPlaces() {
        MediatorLiveData<Resource<List<Place>>> result = new MediatorLiveData<>();

        // Load from cache first
        LiveData<List<PlaceEntity>> localData = placeDao.getAllPlaces();
        result.addSource(localData, entities -> {
            if (entities != null && !entities.isEmpty()) {
                executor.execute(() -> {
                    List<Place> places = convertEntitiesToPlaces(entities);
                    result.postValue(Resource.success(places));
                });
            }
        });

        // Then fetch from network
        LiveData<Resource<List<Place>>> remoteData = firebaseDataSource.getAllPlaces();
        result.addSource(remoteData, resource -> {
            if (resource != null && resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                executor.execute(() -> {
                    List<PlaceEntity> entities = convertPlacesToEntities(resource.getData());
                    placeDao.insertPlaces(entities);
                });
                result.setValue(resource);
            }
        });

        return result;
    }

    public LiveData<Resource<List<Place>>> getPlacesByCountryAndKind(String country, String kind, int limit) {
        MediatorLiveData<Resource<List<Place>>> result = new MediatorLiveData<>();

        // Load from cache
        LiveData<List<PlaceEntity>> localData = placeDao.getPlacesByCountryAndKind(country, kind, limit);
        result.addSource(localData, entities -> {
            if (entities != null) {
                executor.execute(() -> {
                    List<Place> places = convertEntitiesToPlaces(entities);
                    result.postValue(Resource.success(places));
                });
            }
        });

        // Fetch from network
        LiveData<Resource<List<Place>>> remoteData = firebaseDataSource.getPlacesByCountryAndKind(country, kind);
        result.addSource(remoteData, resource -> {
            if (resource != null && resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                executor.execute(() -> {
                    List<PlaceEntity> entities = convertPlacesToEntities(resource.getData());
                    placeDao.insertPlaces(entities);
                });
                result.setValue(resource);
            }
        });

        return result;
    }

    public LiveData<Resource<List<Place>>> getPlacesByCity(String city) {
        MediatorLiveData<Resource<List<Place>>> result = new MediatorLiveData<>();

        LiveData<List<PlaceEntity>> localData = placeDao.getPlacesByCity(city);
        result.addSource(localData, entities -> {
            if (entities != null) {
                executor.execute(() -> {
                    List<Place> places = convertEntitiesToPlaces(entities);
                    result.postValue(Resource.success(places));
                });
            }
        });

        return result;
    }

    public LiveData<Resource<List<Place>>> getPlacesByKind(String kind) {
        MediatorLiveData<Resource<List<Place>>> result = new MediatorLiveData<>();

        LiveData<List<PlaceEntity>> localData = placeDao.getPlacesByKind(kind);
        result.addSource(localData, entities -> {
            if (entities != null) {
                executor.execute(() -> {
                    List<Place> places = convertEntitiesToPlaces(entities);
                    result.postValue(Resource.success(places));
                });
            }
        });

        return result;
    }

    public LiveData<List<String>> getAllCountries() {
        return placeDao.getAllCountries();
    }

    public LiveData<List<String>> getCitiesByCountry(String country) {
        return placeDao.getCitiesByCountry(country);
    }

    public LiveData<List<String>> getAllKinds() {
        return placeDao.getAllKinds();
    }

    // Conversion methods
    private List<Place> convertEntitiesToPlaces(List<PlaceEntity> entities) {
        List<Place> places = new ArrayList<>();
        for (PlaceEntity entity : entities) {
            places.add(convertEntityToPlace(entity));
        }
        return places;
    }

    private Place convertEntityToPlace(PlaceEntity entity) {
        Place place = new Place();
        place.setId(entity.getId());
        place.setKind(entity.getKind());
        place.setName(entity.getName());
        place.setCountry(entity.getCountry());
        place.setCity(entity.getCity());
        place.setAddress(entity.getAddress());
        place.setLat(entity.getLat());
        place.setLng(entity.getLng());
        place.setAvgRating(entity.getAvgRating());
        place.setRatingCount(entity.getRatingCount());
        place.setPriceLevel(entity.getPriceLevel());
        place.setDescription(entity.getDescription());

        // Convert JSON strings back to lists
        if (entity.getImagesJson() != null) {
            Type listType = new TypeToken<List<String>>(){}.getType();
            place.setImages(gson.fromJson(entity.getImagesJson(), listType));
        }

        if (entity.getAmenitiesJson() != null) {
            Type listType = new TypeToken<List<String>>(){}.getType();
            place.setAmenities(gson.fromJson(entity.getAmenitiesJson(), listType));
        }

        return place;
    }

    private List<PlaceEntity> convertPlacesToEntities(List<Place> places) {
        List<PlaceEntity> entities = new ArrayList<>();
        for (Place place : places) {
            entities.add(convertPlaceToEntity(place));
        }
        return entities;
    }

    private PlaceEntity convertPlaceToEntity(Place place) {
        PlaceEntity entity = new PlaceEntity();
        entity.setId(place.getId());
        entity.setKind(place.getKind());
        entity.setName(place.getName());
        entity.setCountry(place.getCountry());
        entity.setCity(place.getCity());
        entity.setAddress(place.getAddress());
        entity.setLat(place.getLat());
        entity.setLng(place.getLng());
        entity.setAvgRating(place.getAvgRating());
        entity.setRatingCount(place.getRatingCount());
        entity.setPriceLevel(place.getPriceLevel());
        entity.setDescription(place.getDescription());
        entity.setLastUpdated(System.currentTimeMillis());

        // Convert lists to JSON strings
        if (place.getImages() != null) {
            entity.setImagesJson(gson.toJson(place.getImages()));
        }

        if (place.getAmenities() != null) {
            entity.setAmenitiesJson(gson.toJson(place.getAmenities()));
        }

        return entity;
    }
}