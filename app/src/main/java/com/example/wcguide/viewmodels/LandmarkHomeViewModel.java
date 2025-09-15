package com.example.wcguide.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ahmmedalmzini783.wcguide.data.model.Landmark;
import com.ahmmedalmzini783.wcguide.data.repository.LandmarkRepository;
import com.ahmmedalmzini783.wcguide.util.Resource;

import java.util.List;

public class LandmarkHomeViewModel extends ViewModel {
    
    private final LandmarkRepository repository;
    
    public LandmarkHomeViewModel(LandmarkRepository repository) {
        this.repository = repository;
    }
    
    public LiveData<Resource<List<Landmark>>> getAllLandmarks() {
        return repository.getLandmarks();
    }
    
    public LiveData<Resource<List<Landmark>>> getLatestLandmarks(int limit) {
        return repository.getLatestLandmarks(limit);
    }
    
    public LiveData<Resource<Landmark>> getLandmarkById(String landmarkId) {
        return repository.getLandmarkById(landmarkId);
    }
    
    public LiveData<Resource<List<Landmark>>> searchLandmarks(String query) {
        return repository.searchLandmarks(query);
    }
    
    public static class Factory implements ViewModelProvider.Factory {
        private final LandmarkRepository repository;
        
        public Factory(LandmarkRepository repository) {
            this.repository = repository;
        }
        
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(LandmarkHomeViewModel.class)) {
                return (T) new LandmarkHomeViewModel(repository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
