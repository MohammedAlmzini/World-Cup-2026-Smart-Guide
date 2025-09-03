package com.ahmmedalmzini783.wcguide.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ahmmedalmzini783.wcguide.data.model.Banner;
import com.ahmmedalmzini783.wcguide.data.model.Place;
import com.ahmmedalmzini783.wcguide.data.model.QuickInfo;
import com.ahmmedalmzini783.wcguide.data.repo.PlaceRepository;
import com.ahmmedalmzini783.wcguide.util.Resource;

import java.util.ArrayList;
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
        // Create temporary promotional banners
        banners = createTemporaryBanners();

        // Load attractions (limit to 10)
        attractions = placeRepository.getPlacesByCountryAndKind("US", "attraction", 10);

        // Load hotels (limit to 10)
        hotels = placeRepository.getPlacesByCountryAndKind("US", "hotel", 10);

        // Load restaurants (limit to 10)
        restaurants = placeRepository.getPlacesByCountryAndKind("US", "restaurant", 10);

        // TODO: Load quick info for all countries
    }

    private LiveData<Resource<List<Banner>>> createTemporaryBanners() {
        List<Banner> tempBanners = new ArrayList<>();
        
        // Banner 1: World Cup Tickets
        Banner banner1 = new Banner(
            "temp_banner_1",
            "احجز تذاكر كأس العالم 2026 الآن!",
            "https://images.unsplash.com/photo-1574629810360-7efbbe195018?w=800&h=400&fit=crop",
            "app://tickets/worldcup2026"
        );
        
        // Banner 2: Hotel Booking
        Banner banner2 = new Banner(
            "temp_banner_2", 
            "خصومات خاصة على الفنادق - احجز الآن!",
            "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&h=400&fit=crop",
            "app://hotels/special_offer"
        );
        
        // Banner 3: Travel Guide
        Banner banner3 = new Banner(
            "temp_banner_3",
            "دليل السفر الشامل لكأس العالم 2026",
            "https://images.unsplash.com/photo-1552832230-c0197dd311b5?w=800&h=400&fit=crop", 
            "app://guide/travel_tips"
        );
        
        tempBanners.add(banner1);
        tempBanners.add(banner2);
        tempBanners.add(banner3);
        
        return new MutableLiveData<>(Resource.success(tempBanners));
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