package com.ahmmedalmzini783.wcguide.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ahmmedalmzini783.wcguide.data.model.Landmark;
import com.ahmmedalmzini783.wcguide.util.Resource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LandmarkRepository {

    private DatabaseReference databaseReference;
    private static final String LANDMARKS_NODE = "landmarks";

    public LandmarkRepository() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(LANDMARKS_NODE);
    }

    public LiveData<Resource<List<Landmark>>> getLandmarks() {
        MutableLiveData<Resource<List<Landmark>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Landmark> landmarks = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Landmark landmark = snapshot.getValue(Landmark.class);
                    if (landmark != null) {
                        landmark.setId(snapshot.getKey());
                        if (landmark.isActive()) { // Only show active landmarks
                            landmarks.add(landmark);
                        }
                    }
                }
                result.setValue(Resource.success(landmarks));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.setValue(Resource.error(databaseError.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Landmark>> getLandmarkById(String landmarkId) {
        MutableLiveData<Resource<Landmark>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        databaseReference.child(landmarkId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Landmark landmark = dataSnapshot.getValue(Landmark.class);
                if (landmark != null) {
                    landmark.setId(dataSnapshot.getKey());
                    result.setValue(Resource.success(landmark));
                } else {
                    result.setValue(Resource.error("المعلم غير موجود", null));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.setValue(Resource.error(databaseError.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Void>> addLandmark(Landmark landmark) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        String landmarkId = databaseReference.push().getKey();
        if (landmarkId != null) {
            landmark.setId(landmarkId);
            landmark.setTimestamp(System.currentTimeMillis());
            landmark.setActive(true);

            databaseReference.child(landmarkId).setValue(landmark)
                    .addOnSuccessListener(aVoid -> {
                        result.setValue(Resource.success(null));
                    })
                    .addOnFailureListener(e -> {
                        result.setValue(Resource.error(e.getMessage(), null));
                    });
        } else {
            result.setValue(Resource.error("فشل في إنشاء معرف المعلم", null));
        }

        return result;
    }

    public LiveData<Resource<Void>> updateLandmark(Landmark landmark) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        if (landmark.getId() != null) {
            databaseReference.child(landmark.getId()).setValue(landmark)
                    .addOnSuccessListener(aVoid -> {
                        result.setValue(Resource.success(null));
                    })
                    .addOnFailureListener(e -> {
                        result.setValue(Resource.error(e.getMessage(), null));
                    });
        } else {
            result.setValue(Resource.error("معرف المعلم مفقود", null));
        }

        return result;
    }

    public LiveData<Resource<Void>> deleteLandmark(String landmarkId) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        // Instead of deleting, mark as inactive
        databaseReference.child(landmarkId).child("active").setValue(false)
                .addOnSuccessListener(aVoid -> {
                    result.setValue(Resource.success(null));
                })
                .addOnFailureListener(e -> {
                    result.setValue(Resource.error(e.getMessage(), null));
                });

        return result;
    }

    public LiveData<Resource<List<Landmark>>> searchLandmarks(String query) {
        MutableLiveData<Resource<List<Landmark>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Landmark> landmarks = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Landmark landmark = snapshot.getValue(Landmark.class);
                    if (landmark != null && landmark.isActive()) {
                        landmark.setId(snapshot.getKey());
                        // Search in name, description, or address
                        if (landmark.getName().toLowerCase().contains(query.toLowerCase()) ||
                            landmark.getDescription().toLowerCase().contains(query.toLowerCase()) ||
                            landmark.getAddress().toLowerCase().contains(query.toLowerCase())) {
                            landmarks.add(landmark);
                        }
                    }
                }
                result.setValue(Resource.success(landmarks));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.setValue(Resource.error(databaseError.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<Landmark>>> getLatestLandmarks(int limit) {
        MutableLiveData<Resource<List<Landmark>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        databaseReference.orderByChild("timestamp").limitToLast(limit)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Landmark> landmarks = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Landmark landmark = snapshot.getValue(Landmark.class);
                    if (landmark != null) {
                        landmark.setId(snapshot.getKey());
                        if (landmark.isActive()) { // Only show active landmarks
                            landmarks.add(0, landmark); // Add to beginning to get newest first
                        }
                    }
                }
                result.setValue(Resource.success(landmarks));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.setValue(Resource.error(databaseError.getMessage(), null));
            }
        });

        return result;
    }
}
