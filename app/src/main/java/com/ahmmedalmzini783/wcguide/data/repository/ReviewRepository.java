package com.ahmmedalmzini783.wcguide.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ahmmedalmzini783.wcguide.data.model.Review;
import com.ahmmedalmzini783.wcguide.util.Resource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReviewRepository {

    private static final String REVIEWS_NODE = "reviews";
    private DatabaseReference reviewsRef;
    private static ReviewRepository instance;

    private ReviewRepository() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reviewsRef = database.getReference(REVIEWS_NODE);
    }

    public static synchronized ReviewRepository getInstance() {
        if (instance == null) {
            instance = new ReviewRepository();
        }
        return instance;
    }

    public LiveData<Resource<List<Review>>> getAllReviews() {
        MutableLiveData<Resource<List<Review>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        reviewsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Review> reviews = new ArrayList<>();
                for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                    Review review = reviewSnapshot.getValue(Review.class);
                    if (review != null) {
                        review.setId(reviewSnapshot.getKey());
                        reviews.add(review);
                    }
                }
                result.setValue(Resource.success(reviews));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                result.setValue(Resource.error(error.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<Review>>> getReviewsByLandmark(String landmarkId) {
        MutableLiveData<Resource<List<Review>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        reviewsRef.orderByChild("landmarkId").equalTo(landmarkId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<Review> reviews = new ArrayList<>();
                        for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                            Review review = reviewSnapshot.getValue(Review.class);
                            if (review != null && review.isApproved()) {
                                review.setId(reviewSnapshot.getKey());
                                reviews.add(review);
                            }
                        }
                        result.setValue(Resource.success(reviews));
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        result.setValue(Resource.error(error.getMessage(), null));
                    }
                });

        return result;
    }

    public LiveData<Resource<Void>> addReview(Review review) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        String reviewId = reviewsRef.push().getKey();
        if (reviewId != null) {
            review.setId(reviewId);
            reviewsRef.child(reviewId).setValue(review)
                    .addOnSuccessListener(aVoid -> result.setValue(Resource.success(null)))
                    .addOnFailureListener(e -> result.setValue(Resource.error(e.getMessage(), null)));
        } else {
            result.setValue(Resource.error("فشل في إنشاء معرف التقييم", null));
        }

        return result;
    }

    public LiveData<Resource<Void>> updateReview(Review review) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        if (review.getId() != null) {
            reviewsRef.child(review.getId()).setValue(review)
                    .addOnSuccessListener(aVoid -> result.setValue(Resource.success(null)))
                    .addOnFailureListener(e -> result.setValue(Resource.error(e.getMessage(), null)));
        } else {
            result.setValue(Resource.error("معرف التقييم مطلوب للتحديث", null));
        }

        return result;
    }

    public LiveData<Resource<Void>> deleteReview(String reviewId) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        reviewsRef.child(reviewId).removeValue()
                .addOnSuccessListener(aVoid -> result.setValue(Resource.success(null)))
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getMessage(), null)));

        return result;
    }

    public LiveData<Resource<Review>> getReviewById(String reviewId) {
        MutableLiveData<Resource<Review>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        reviewsRef.child(reviewId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Review review = snapshot.getValue(Review.class);
                if (review != null) {
                    review.setId(snapshot.getKey());
                    result.setValue(Resource.success(review));
                } else {
                    result.setValue(Resource.error("التقييم غير موجود", null));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                result.setValue(Resource.error(error.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Double>> getAverageRating(String landmarkId) {
        MutableLiveData<Resource<Double>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        reviewsRef.orderByChild("landmarkId").equalTo(landmarkId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        double totalRating = 0.0;
                        int count = 0;

                        for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                            Review review = reviewSnapshot.getValue(Review.class);
                            if (review != null && review.isApproved()) {
                                totalRating += review.getRating();
                                count++;
                            }
                        }

                        double average = count > 0 ? totalRating / count : 0.0;
                        result.setValue(Resource.success(average));
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        result.setValue(Resource.error(error.getMessage(), null));
                    }
                });

        return result;
    }
}
