package com.ahmmedalmzini783.wcguide.ui.home;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ahmmedalmzini783.wcguide.data.model.Banner;
import com.ahmmedalmzini783.wcguide.data.model.Place;
import com.ahmmedalmzini783.wcguide.data.model.QuickInfo;
import com.ahmmedalmzini783.wcguide.data.model.GooglePlaceResult;
import com.ahmmedalmzini783.wcguide.data.repo.PlaceRepository;
import com.ahmmedalmzini783.wcguide.data.repo.GooglePlacesRepository;
import com.ahmmedalmzini783.wcguide.util.Resource;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.ArrayList;

public class HomeViewModel extends AndroidViewModel {

    private final PlaceRepository placeRepository;
    private final GooglePlacesRepository googlePlacesRepository;

    // LiveData for different sections
    private LiveData<Resource<List<Banner>>> banners;
    private LiveData<Resource<List<Place>>> attractions;
    private MediatorLiveData<Resource<List<GooglePlaceResult>>> hotels;
    private MediatorLiveData<Resource<List<GooglePlaceResult>>> restaurants;
    private LiveData<Resource<List<QuickInfo>>> quickInfo;
    
    // Current data lists
    private final List<GooglePlaceResult> currentHotels = new ArrayList<>();
    private final List<GooglePlaceResult> currentRestaurants = new ArrayList<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        placeRepository = new PlaceRepository(application);
        googlePlacesRepository = new GooglePlacesRepository(application);

        // Initialize MediatorLiveData
        hotels = new MediatorLiveData<>();
        restaurants = new MediatorLiveData<>();

        initializeData();
    }

    private void initializeData() {
        Log.d("HomeViewModel", "Initializing data...");
        
        // Load banners from Firebase
        banners = placeRepository.getBanners();
        Log.d("HomeViewModel", "Banners LiveData initialized");

        // Load attractions (limit to 10) - for multiple World Cup countries
        attractions = placeRepository.getPlacesByCountryAndKind("Qatar", "attraction", 10);
        Log.d("HomeViewModel", "Attractions LiveData initialized");

        // Load hotels from Google Places API for multiple countries
        loadPlacesForAllCountries("hotel");
        Log.d("HomeViewModel", "Hotels LiveData initialized");

        // Load restaurants from Google Places API for multiple countries
        loadPlacesForAllCountries("restaurant");
        Log.d("HomeViewModel", "Restaurants LiveData initialized");

        // Load quick info for all countries
        quickInfo = placeRepository.getQuickInfo();
        Log.d("HomeViewModel", "QuickInfo LiveData initialized");
    }

    public LiveData<Resource<List<Banner>>> getBanners() {
        return banners;
    }

    public LiveData<Resource<List<Place>>> getAttractions() {
        return attractions;
    }

    public LiveData<Resource<List<GooglePlaceResult>>> getHotels() {
        return hotels;
    }

    public LiveData<Resource<List<GooglePlaceResult>>> getRestaurants() {
        return restaurants;
    }

    public LiveData<Resource<List<QuickInfo>>> getQuickInfo() {
        return quickInfo;
    }

    /**
     * Load places for all World Cup countries
     */
    private void loadPlacesForAllCountries(String placeType) {
        Log.d("HomeViewModel", "Loading places for type: " + placeType);
        
        MediatorLiveData<Resource<List<GooglePlaceResult>>> targetLiveData = 
            placeType.equals("hotel") ? hotels : restaurants;
        
        // Use the same method as GooglePlacesViewModel
        LiveData<Resource<List<GooglePlaceResult>>> source = googlePlacesRepository.getAllPlacesForWorldCup(placeType);
        
        targetLiveData.addSource(source, resource -> {
            Log.d("HomeViewModel", "Received resource for " + placeType + ": " + resource.getStatus());
            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                List<GooglePlaceResult> newPlaces = resource.getData();
                Log.d("HomeViewModel", "Got " + newPlaces.size() + " places for " + placeType);
                
                // Store the data directly in current lists
                List<GooglePlaceResult> currentPlaces = getCurrentPlacesList(placeType);
                currentPlaces.clear();
                currentPlaces.addAll(newPlaces);
                
                // Sort by rating (highest first)
                currentPlaces.sort((a, b) -> Float.compare(b.getRating(), a.getRating()));
                
                Log.d("HomeViewModel", "Final places count for " + placeType + ": " + currentPlaces.size());
                targetLiveData.setValue(Resource.success(new ArrayList<>(currentPlaces)));
            } else if (resource.getStatus() == Resource.Status.ERROR) {
                Log.e("HomeViewModel", "Error loading places for " + placeType + ": " + resource.getMessage());
                targetLiveData.setValue(Resource.error(resource.getMessage(), getCurrentPlaces(placeType)));
            } else if (resource.getStatus() == Resource.Status.LOADING) {
                Log.d("HomeViewModel", "Loading places for " + placeType);
                targetLiveData.setValue(Resource.loading(getCurrentPlaces(placeType)));
            }
        });
    }

    /**
     * Get current places list based on type
     */
    private List<GooglePlaceResult> getCurrentPlaces(String placeType) {
        switch (placeType.toLowerCase()) {
            case "hotel":
                return new ArrayList<>(currentHotels);
            case "restaurant":
                return new ArrayList<>(currentRestaurants);
            default:
                return new ArrayList<>();
        }
    }

    /**
     * Merge new places results with existing data
     */
    private void mergePlacesResults(List<GooglePlaceResult> newPlaces, String placeType) {
        List<GooglePlaceResult> currentPlaces = getCurrentPlacesList(placeType);
        
        // Remove duplicates based on placeId
        for (GooglePlaceResult newPlace : newPlaces) {
            boolean exists = false;
            for (GooglePlaceResult existingPlace : currentPlaces) {
                if (existingPlace.getPlaceId().equals(newPlace.getPlaceId())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                currentPlaces.add(newPlace);
            }
        }
        
        // Sort by rating (highest first)
        currentPlaces.sort((a, b) -> Float.compare(b.getRating(), a.getRating()));
    }

    /**
     * Get current places list reference
     */
    private List<GooglePlaceResult> getCurrentPlacesList(String placeType) {
        switch (placeType.toLowerCase()) {
            case "hotel":
                return currentHotels;
            case "restaurant":
                return currentRestaurants;
            default:
                return new ArrayList<>();
        }
    }

    public void refreshData() {
        // Trigger data refresh
        initializeData();
    }
}