package com.ahmmedalmzini783.wcguide.ui.restaurants;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.ahmmedalmzini783.wcguide.data.model.Place;
import com.ahmmedalmzini783.wcguide.data.repo.PlaceRepository;
import com.ahmmedalmzini783.wcguide.util.Resource;

import java.util.List;

public class AllRestaurantsViewModel extends AndroidViewModel {
    private static final String TAG = "AllRestaurantsViewModel";
    
    private final PlaceRepository placeRepository;

    public AllRestaurantsViewModel(@NonNull Application application) {
        super(application);
        placeRepository = new PlaceRepository(application);
        Log.d(TAG, "AllRestaurantsViewModel created");
    }

    public LiveData<Resource<List<Place>>> getAllRestaurants() {
        Log.d(TAG, "getAllRestaurants() called");
        // Load all restaurants without limit - this will trigger fresh data load each time
        return placeRepository.getAllRestaurantsByKind("restaurant", Integer.MAX_VALUE);
    }

    public void refreshRestaurants() {
        Log.d(TAG, "refreshRestaurants() called");
        // The LiveData will automatically refresh when observed again
    }
}
