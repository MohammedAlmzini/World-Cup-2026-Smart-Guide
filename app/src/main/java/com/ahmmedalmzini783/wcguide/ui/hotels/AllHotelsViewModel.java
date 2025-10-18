package com.ahmmedalmzini783.wcguide.ui.hotels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ahmmedalmzini783.wcguide.data.model.Place;
import com.ahmmedalmzini783.wcguide.data.repo.PlaceRepository;
import com.ahmmedalmzini783.wcguide.util.Resource;

import java.util.List;

public class AllHotelsViewModel extends AndroidViewModel {
    private static final String TAG = "AllHotelsViewModel";
    
    private final PlaceRepository placeRepository;

    public AllHotelsViewModel(@NonNull Application application) {
        super(application);
        placeRepository = new PlaceRepository(application);
        Log.d(TAG, "AllHotelsViewModel created");
    }

    public LiveData<Resource<List<Place>>> getAllHotels() {
        Log.d(TAG, "getAllHotels() called");
        // Use getAllHotels() directly from HotelRepository to get all hotels without country filtering
        return placeRepository.getAllHotels();
    }

    public void refreshHotels() {
        // The LiveData will automatically refresh when observed again
    }
}
