package com.ahmmedalmzini783.wcguide.ui.hotels;

import android.app.Application;

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
    private LiveData<Resource<List<Place>>> allHotels;

    public AllHotelsViewModel(@NonNull Application application) {
        super(application);
        placeRepository = new PlaceRepository(application);
        loadAllHotels();
    }

    private void loadAllHotels() {
        // Load all hotels without limit
        allHotels = placeRepository.getAllHotelsByKind("hotel", Integer.MAX_VALUE);
    }

    public LiveData<Resource<List<Place>>> getAllHotels() {
        return allHotels;
    }

    public void refreshHotels() {
        loadAllHotels();
    }
}
