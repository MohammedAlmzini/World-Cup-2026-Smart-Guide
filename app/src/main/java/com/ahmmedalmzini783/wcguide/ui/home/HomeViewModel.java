package com.ahmmedalmzini783.wcguide.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ahmmedalmzini783.wcguide.data.model.Banner;
import com.ahmmedalmzini783.wcguide.data.model.Place;
import com.ahmmedalmzini783.wcguide.data.model.QuickInfo;
import com.ahmmedalmzini783.wcguide.data.repo.PlaceRepository;
import com.ahmmedalmzini783.wcguide.util.Resource;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final PlaceRepository placeRepository;

    // LiveData for different sections
    private LiveData<Resource<List<Banner>>> banners;
    private LiveData<Resource<List<Place>>> attractions;
    private LiveData<Resource<List<Place>>> hotels;
    private LiveData<Resource<List<Place>>> restaurants;
    private LiveData<Resource<List<QuickInfo>>> quickInfo;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        placeRepository = new PlaceRepository(application);

        initializeData();
    }

    private void initializeData() {
        // Load banners from Firebase
        banners = placeRepository.getBanners();

        // Load attractions (limit to 10) - for multiple World Cup countries
        attractions = placeRepository.getPlacesByCountryAndKind("Qatar", "attraction", 10);

        // Load hotels (limit to 10) - for multiple World Cup countries including USA, Qatar, etc.
        hotels = placeRepository.getAllHotelsByKind("hotel", 10);

        // Load restaurants (limit to 10) - for multiple World Cup countries including USA, Qatar, etc.
        restaurants = placeRepository.getAllRestaurantsByKind("restaurant", 10);

        // Load quick info for all countries
        quickInfo = placeRepository.getQuickInfo();
    }

    public LiveData<Resource<List<Banner>>> getBanners() {
        return banners;
    }

    public LiveData<Resource<List<Place>>> getAttractions() {
        return attractions;
    }

    public LiveData<Resource<List<Place>>> getHotels() {
        return hotels;
    }

    public LiveData<Resource<List<Place>>> getRestaurants() {
        return restaurants;
    }

    public LiveData<Resource<List<QuickInfo>>> getQuickInfo() {
        return quickInfo;
    }

    public void refreshData() {
        // Trigger data refresh
        initializeData();
    }
}