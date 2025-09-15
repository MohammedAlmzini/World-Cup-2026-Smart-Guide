package com.ahmmedalmzini783.wcguide.ui.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ahmmedalmzini783.wcguide.data.model.Review;
import com.ahmmedalmzini783.wcguide.data.repository.ReviewRepository;
import com.ahmmedalmzini783.wcguide.util.Resource;

import java.util.List;

public class ReviewViewModel extends ViewModel {

    private ReviewRepository reviewRepository;

    public ReviewViewModel() {
        reviewRepository = ReviewRepository.getInstance();
    }

    public LiveData<Resource<List<Review>>> getAllReviews() {
        return reviewRepository.getAllReviews();
    }

    public LiveData<Resource<List<Review>>> getReviewsByLandmark(String landmarkId) {
        return reviewRepository.getReviewsByLandmark(landmarkId);
    }

    public LiveData<Resource<Void>> addReview(Review review) {
        return reviewRepository.addReview(review);
    }

    public LiveData<Resource<Void>> updateReview(Review review) {
        return reviewRepository.updateReview(review);
    }

    public LiveData<Resource<Void>> deleteReview(String reviewId) {
        return reviewRepository.deleteReview(reviewId);
    }

    public LiveData<Resource<Review>> getReviewById(String reviewId) {
        return reviewRepository.getReviewById(reviewId);
    }

    public LiveData<Resource<Double>> getAverageRating(String landmarkId) {
        return reviewRepository.getAverageRating(landmarkId);
    }
}
