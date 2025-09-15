package com.ahmmedalmzini783.wcguide.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ahmmedalmzini783.wcguide.data.model.Review;
import com.ahmmedalmzini783.wcguide.data.repository.ReviewRepository;
import com.ahmmedalmzini783.wcguide.util.Resource;

import java.util.List;

public class ReviewViewModel extends ViewModel {
    
    private ReviewRepository reviewRepository;
    private MutableLiveData<List<Review>> allReviewsLiveData;
    private MutableLiveData<List<Review>> landmarkReviewsLiveData;
    private MutableLiveData<Float> averageRatingLiveData;
    private MutableLiveData<Boolean> isLoadingLiveData;
    private MutableLiveData<String> errorMessageLiveData;

    public ReviewViewModel() {
        reviewRepository = ReviewRepository.getInstance();
        allReviewsLiveData = new MutableLiveData<>();
        landmarkReviewsLiveData = new MutableLiveData<>();
        averageRatingLiveData = new MutableLiveData<>();
        isLoadingLiveData = new MutableLiveData<>();
        errorMessageLiveData = new MutableLiveData<>();
    }

    public LiveData<List<Review>> getAllReviews() {
        isLoadingLiveData.setValue(true);
        
        LiveData<Resource<List<Review>>> resourceLiveData = reviewRepository.getAllReviews();
        
        // Transform Resource<List<Review>> to List<Review>
        MutableLiveData<List<Review>> result = new MutableLiveData<>();
        
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

    public LiveData<List<Review>> getReviewsByLandmark(String landmarkId) {
        isLoadingLiveData.setValue(true);
        
        LiveData<Resource<List<Review>>> resourceLiveData = reviewRepository.getReviewsByLandmark(landmarkId);
        
        MutableLiveData<List<Review>> result = new MutableLiveData<>();
        
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

    public LiveData<Float> getAverageRating(String landmarkId) {
        LiveData<Resource<Double>> resourceLiveData = reviewRepository.getAverageRating(landmarkId);
        
        MutableLiveData<Float> result = new MutableLiveData<>();
        
        resourceLiveData.observeForever(resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case SUCCESS:
                        Double rating = resource.getData();
                        result.setValue(rating != null ? rating.floatValue() : 0.0f);
                        break;
                    case ERROR:
                        errorMessageLiveData.setValue(resource.getMessage());
                        break;
                }
            }
        });
        
        return result;
    }

    public LiveData<Resource<Void>> addReview(Review review) {
        isLoadingLiveData.setValue(true);
        
        LiveData<Resource<Void>> resourceLiveData = reviewRepository.addReview(review);
        
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

    public LiveData<Resource<Void>> updateReview(Review review) {
        isLoadingLiveData.setValue(true);
        
        LiveData<Resource<Void>> resourceLiveData = reviewRepository.updateReview(review);
        
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

    public LiveData<Resource<Void>> deleteReview(String reviewId) {
        isLoadingLiveData.setValue(true);
        
        LiveData<Resource<Void>> resourceLiveData = reviewRepository.deleteReview(reviewId);
        
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
