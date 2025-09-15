package com.ahmmedalmzini783.wcguide.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ahmmedalmzini783.wcguide.data.model.LandmarkRating;
import com.ahmmedalmzini783.wcguide.util.Resource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LandmarkRatingRepository {

    private static final String LANDMARK_RATINGS_NODE = "landmark_ratings";
    private DatabaseReference landmarkRatingsRef;
    private static LandmarkRatingRepository instance;

    private LandmarkRatingRepository() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        landmarkRatingsRef = database.getReference(LANDMARK_RATINGS_NODE);
    }

    public static synchronized LandmarkRatingRepository getInstance() {
        if (instance == null) {
            instance = new LandmarkRatingRepository();
        }
        return instance;
    }

    public LiveData<Resource<List<LandmarkRating>>> getAllLandmarkRatings() {
        MutableLiveData<Resource<List<LandmarkRating>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        landmarkRatingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<LandmarkRating> ratings = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LandmarkRating rating = snapshot.getValue(LandmarkRating.class);
                    if (rating != null) {
                        rating.setId(snapshot.getKey());
                        ratings.add(rating);
                    }
                }
                result.setValue(Resource.success(ratings));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.setValue(Resource.error(databaseError.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<LandmarkRating>> getLandmarkRatingByLandmarkId(String landmarkId) {
        MutableLiveData<Resource<LandmarkRating>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        landmarkRatingsRef.orderByChild("landmarkId").equalTo(landmarkId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        LandmarkRating rating = null;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            rating = snapshot.getValue(LandmarkRating.class);
                            if (rating != null) {
                                rating.setId(snapshot.getKey());
                                break; // Get the first (should be only one)
                            }
                        }
                        result.setValue(Resource.success(rating));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        result.setValue(Resource.error(databaseError.getMessage(), null));
                    }
                });

        return result;
    }

    public LiveData<Resource<Void>> addLandmarkRating(LandmarkRating rating) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        String key = landmarkRatingsRef.push().getKey();
        if (key != null) {
            rating.setId(key);
            landmarkRatingsRef.child(key).setValue(rating)
                    .addOnSuccessListener(aVoid -> result.setValue(Resource.success(null)))
                    .addOnFailureListener(e -> result.setValue(Resource.error(e.getMessage(), null)));
        } else {
            result.setValue(Resource.error("فشل في إنشاء معرف للتقييم", null));
        }

        return result;
    }

    public LiveData<Resource<Void>> updateLandmarkRating(LandmarkRating rating) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        if (rating.getId() != null) {
            landmarkRatingsRef.child(rating.getId()).setValue(rating)
                    .addOnSuccessListener(aVoid -> result.setValue(Resource.success(null)))
                    .addOnFailureListener(e -> result.setValue(Resource.error(e.getMessage(), null)));
        } else {
            result.setValue(Resource.error("معرف التقييم مفقود", null));
        }

        return result;
    }

    public LiveData<Resource<Void>> deleteLandmarkRating(String ratingId) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        landmarkRatingsRef.child(ratingId).removeValue()
                .addOnSuccessListener(aVoid -> result.setValue(Resource.success(null)))
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getMessage(), null)));

        return result;
    }

    public LiveData<Resource<Void>> addOrUpdateLandmarkRating(LandmarkRating rating) {
        // First check if rating exists for this landmark
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        landmarkRatingsRef.orderByChild("landmarkId").equalTo(rating.getLandmarkId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Update existing rating
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                rating.setId(snapshot.getKey());
                                landmarkRatingsRef.child(rating.getId()).setValue(rating)
                                        .addOnSuccessListener(aVoid -> result.setValue(Resource.success(null)))
                                        .addOnFailureListener(e -> result.setValue(Resource.error(e.getMessage(), null)));
                                break;
                            }
                        } else {
                            // Add new rating
                            String key = landmarkRatingsRef.push().getKey();
                            if (key != null) {
                                rating.setId(key);
                                landmarkRatingsRef.child(key).setValue(rating)
                                        .addOnSuccessListener(aVoid -> result.setValue(Resource.success(null)))
                                        .addOnFailureListener(e -> result.setValue(Resource.error(e.getMessage(), null)));
                            } else {
                                result.setValue(Resource.error("فشل في إنشاء معرف للتقييم", null));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        result.setValue(Resource.error(databaseError.getMessage(), null));
                    }
                });

        return result;
    }
}
