package com.ahmmedalmzini783.wcguide.ui.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ahmmedalmzini783.wcguide.data.model.Landmark;
import com.ahmmedalmzini783.wcguide.data.repository.LandmarkRepository;
import com.ahmmedalmzini783.wcguide.util.Resource;

import java.util.List;

public class LandmarkViewModel extends ViewModel {

    private LandmarkRepository repository;
    private MutableLiveData<Resource<List<Landmark>>> landmarksLiveData;

    public LandmarkViewModel() {
        repository = new LandmarkRepository();
        landmarksLiveData = new MutableLiveData<>();
    }

    public LiveData<Resource<List<Landmark>>> getLandmarksLiveData() {
        return landmarksLiveData;
    }

    public void loadLandmarks() {
        landmarksLiveData.setValue(Resource.loading(null));
        repository.getLandmarks().observeForever(resource -> {
            landmarksLiveData.setValue(resource);
        });
    }

    public LiveData<Resource<Void>> addLandmark(Landmark landmark) {
        return repository.addLandmark(landmark);
    }

    public LiveData<Resource<Void>> updateLandmark(Landmark landmark) {
        return repository.updateLandmark(landmark);
    }

    public LiveData<Resource<Void>> deleteLandmark(String landmarkId) {
        return repository.deleteLandmark(landmarkId);
    }

    public LiveData<Resource<Landmark>> getLandmarkById(String landmarkId) {
        return repository.getLandmarkById(landmarkId);
    }
}
