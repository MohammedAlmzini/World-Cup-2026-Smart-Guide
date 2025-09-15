package com.ahmmedalmzini783.wcguide.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ahmmedalmzini783.wcguide.data.model.LandmarkRating;
import com.ahmmedalmzini783.wcguide.data.repository.LandmarkRatingRepository;
import com.ahmmedalmzini783.wcguide.util.Resource;

import java.util.List;

public class LandmarkRatingViewModel extends ViewModel {
    
    private LandmarkRatingRepository repository;
    private MutableLiveData<List<LandmarkRating>> allRatingsLiveData;
    private MutableLiveData<LandmarkRating> landmarkRatingLiveData;
    private MutableLiveData<Boolean> isLoadingLiveData;
    private MutableLiveData<String> errorMessageLiveData;

    public LandmarkRatingViewModel() {
        repository = LandmarkRatingRepository.getInstance();
        allRatingsLiveData = new MutableLiveData<>();
        landmarkRatingLiveData = new MutableLiveData<>();
        isLoadingLiveData = new MutableLiveData<>();
        errorMessageLiveData = new MutableLiveData<>();
    }

    public LiveData<List<LandmarkRating>> getAllLandmarkRatings() {
        isLoadingLiveData.setValue(true);
        
        LiveData<Resource<List<LandmarkRating>>> resourceLiveData = repository.getAllLandmarkRatings();
        
        MutableLiveData<List<LandmarkRating>> result = new MutableLiveData<>();
        
        resourceLiveData.observeForever(resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case SUCCESS:
                        result.setValue(resource.getData());
                        isLoadingLiveData.setValue(false);
                        break;
                    case ERROR:
                        errorMessageLiveData.setValue(resource.getMessage());
                        isLoadingLiveData.setValue(false);
                        break;
                    case LOADING:
                        isLoadingLiveData.setValue(true);
                        break;
                }
            }
        });
        
        return result;
    }

    public LiveData<LandmarkRating> getLandmarkRatingByLandmarkId(String landmarkId) {
        isLoadingLiveData.setValue(true);
        
        LiveData<Resource<LandmarkRating>> resourceLiveData = repository.getLandmarkRatingByLandmarkId(landmarkId);
        
        MutableLiveData<LandmarkRating> result = new MutableLiveData<>();
        
        resourceLiveData.observeForever(resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case SUCCESS:
                        result.setValue(resource.getData());
                        isLoadingLiveData.setValue(false);
                        break;
                    case ERROR:
                        errorMessageLiveData.setValue(resource.getMessage());
                        isLoadingLiveData.setValue(false);
                        break;
                    case LOADING:
                        isLoadingLiveData.setValue(true);
                        break;
                }
            }
        });
        
        return result;
    }

    public LiveData<Resource<Void>> addLandmarkRating(LandmarkRating rating) {
        isLoadingLiveData.setValue(true);
        
        LiveData<Resource<Void>> resourceLiveData = repository.addLandmarkRating(rating);
        
        resourceLiveData.observeForever(resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case SUCCESS:
                        isLoadingLiveData.setValue(false);
                        break;
                    case ERROR:
                        errorMessageLiveData.setValue(resource.getMessage());
                        isLoadingLiveData.setValue(false);
                        break;
                }
            }
        });
        
        return resourceLiveData;
    }

    public LiveData<Resource<Void>> updateLandmarkRating(LandmarkRating rating) {
        isLoadingLiveData.setValue(true);
        
        LiveData<Resource<Void>> resourceLiveData = repository.updateLandmarkRating(rating);
        
        resourceLiveData.observeForever(resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case SUCCESS:
                        isLoadingLiveData.setValue(false);
                        break;
                    case ERROR:
                        errorMessageLiveData.setValue(resource.getMessage());
                        isLoadingLiveData.setValue(false);
                        break;
                }
            }
        });
        
        return resourceLiveData;
    }

    public LiveData<Resource<Void>> addOrUpdateLandmarkRating(LandmarkRating rating) {
        isLoadingLiveData.setValue(true);
        
        LiveData<Resource<Void>> resourceLiveData = repository.addOrUpdateLandmarkRating(rating);
        
        resourceLiveData.observeForever(resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case SUCCESS:
                        isLoadingLiveData.setValue(false);
                        break;
                    case ERROR:
                        errorMessageLiveData.setValue(resource.getMessage());
                        isLoadingLiveData.setValue(false);
                        break;
                }
            }
        });
        
        return resourceLiveData;
    }

    public LiveData<Resource<Void>> deleteLandmarkRating(String ratingId) {
        isLoadingLiveData.setValue(true);
        
        LiveData<Resource<Void>> resourceLiveData = repository.deleteLandmarkRating(ratingId);
        
        resourceLiveData.observeForever(resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case SUCCESS:
                        isLoadingLiveData.setValue(false);
                        break;
                    case ERROR:
                        errorMessageLiveData.setValue(resource.getMessage());
                        isLoadingLiveData.setValue(false);
                        break;
                }
            }
        });
        
        return resourceLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessageLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Clean up any resources if needed
    }
}
