package com.ahmmedalmzini783.wcguide.data.remote;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.ahmmedalmzini783.wcguide.data.model.Event;
import com.ahmmedalmzini783.wcguide.data.model.Place;
import com.ahmmedalmzini783.wcguide.data.model.Review;
import com.ahmmedalmzini783.wcguide.data.model.UserProfile;
import com.ahmmedalmzini783.wcguide.data.model.Banner;
import com.ahmmedalmzini783.wcguide.data.model.QuickInfo;
import com.ahmmedalmzini783.wcguide.util.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirebaseDataSource {
    private static final String TAG = "FirebaseDataSource";

    private final FirebaseAuth auth;
    private final DatabaseReference database;
    private final StorageReference storage;

    // Database references
    private final DatabaseReference eventsRef;
    private final DatabaseReference placesRef;
    private final DatabaseReference reviewsRef;
    private final DatabaseReference usersRef;
    private final DatabaseReference bannersRef;
    private final DatabaseReference quickInfoRef;

    public FirebaseDataSource() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();

        // Initialize references
        eventsRef = database.child("events");
        placesRef = database.child("places");
        reviewsRef = database.child("reviews");
        usersRef = database.child("users");
        bannersRef = database.child("banners");
        quickInfoRef = database.child("quickInfo");
    }

    // Authentication Methods
    public LiveData<Resource<FirebaseUser>> signInWithEmail(String email, String password) {
        MutableLiveData<Resource<FirebaseUser>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.setValue(Resource.success(auth.getCurrentUser()));
                    } else {
                        result.setValue(Resource.error(
                                task.getException() != null ?
                                        task.getException().getMessage() : "Sign in failed",
                                null
                        ));
                    }
                });

        return result;
    }

    public LiveData<Resource<FirebaseUser>> createUserWithEmail(String email, String password, String displayName) {
        MutableLiveData<Resource<FirebaseUser>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // Update profile with display name
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayName)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {
                                        if (profileTask.isSuccessful()) {
                                            // Create user profile in database
                                            createUserProfile(user, displayName);
                                            result.setValue(Resource.success(user));
                                        } else {
                                            result.setValue(Resource.error(
                                                    profileTask.getException() != null ?
                                                            profileTask.getException().getMessage() : "Profile update failed",
                                                    user
                                            ));
                                        }
                                    });
                        }
                    } else {
                        result.setValue(Resource.error(
                                task.getException() != null ?
                                        task.getException().getMessage() : "Registration failed",
                                null
                        ));
                    }
                });

        return result;
    }

    public void signOut() {
        auth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    // User Profile Methods
    private void createUserProfile(FirebaseUser user, String displayName) {
        UserProfile profile = new UserProfile();
        profile.setUid(user.getUid());
        profile.setDisplayName(displayName);
        profile.setEmail(user.getEmail());
        profile.setPhotoUrl(user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);

        usersRef.child(user.getUid()).setValue(profile);
    }

    public LiveData<Resource<UserProfile>> getUserProfile(String uid) {
        MutableLiveData<Resource<UserProfile>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile profile = snapshot.getValue(UserProfile.class);
                if (profile != null) {
                    result.setValue(Resource.success(profile));
                } else {
                    result.setValue(Resource.error("User profile not found", null));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.setValue(Resource.error(error.getMessage(), null));
            }
        });

        return result;
    }

    public Task<Void> updateUserProfile(UserProfile profile) {
        return usersRef.child(profile.getUid()).setValue(profile);
    }

    // Events Methods
    public LiveData<Resource<List<Event>>> getAllEvents() {
        MutableLiveData<Resource<List<Event>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Event> events = new ArrayList<>();
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    if (event != null) {
                        event.setId(eventSnapshot.getKey());
                        events.add(event);
                    }
                }
                result.setValue(Resource.success(events));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.setValue(Resource.error(error.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Event>> getEventById(String eventId) {
        MutableLiveData<Resource<Event>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        eventsRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event event = snapshot.getValue(Event.class);
                if (event != null) {
                    event.setId(snapshot.getKey());
                    result.setValue(Resource.success(event));
                } else {
                    result.setValue(Resource.error("Event not found", null));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.setValue(Resource.error(error.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<Event>>> getEventsByCountry(String country) {
        MutableLiveData<Resource<List<Event>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Query query = eventsRef.orderByChild("country").equalTo(country);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Event> events = new ArrayList<>();
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    if (event != null) {
                        event.setId(eventSnapshot.getKey());
                        events.add(event);
                    }
                }
                result.setValue(Resource.success(events));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.setValue(Resource.error(error.getMessage(), null));
            }
        });

        return result;
    }

    public Task<Void> addEvent(Event event) {
        String key = eventsRef.push().getKey();
        if (key != null) {
            event.setId(key);
            return eventsRef.child(key).setValue(event);
        }
        return null;
    }

    // Places Methods
    public LiveData<Resource<List<Place>>> getAllPlaces() {
        MutableLiveData<Resource<List<Place>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        placesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Place> places = new ArrayList<>();
                for (DataSnapshot placeSnapshot : snapshot.getChildren()) {
                    Place place = placeSnapshot.getValue(Place.class);
                    if (place != null) {
                        place.setId(placeSnapshot.getKey());
                        places.add(place);
                    }
                }
                result.setValue(Resource.success(places));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.setValue(Resource.error(error.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<Place>>> getPlacesByCountryAndKind(String country, String kind) {
        MutableLiveData<Resource<List<Place>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Query query = placesRef.orderByChild("country").equalTo(country);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Place> places = new ArrayList<>();
                for (DataSnapshot placeSnapshot : snapshot.getChildren()) {
                    Place place = placeSnapshot.getValue(Place.class);
                    if (place != null && (kind == null || kind.equals(place.getKind()))) {
                        place.setId(placeSnapshot.getKey());
                        places.add(place);
                    }
                }
                result.setValue(Resource.success(places));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.setValue(Resource.error(error.getMessage(), null));
            }
        });

        return result;
    }

    // Reviews Methods
    public LiveData<Resource<List<Review>>> getReviewsForTarget(String targetId, String targetKind) {
        MutableLiveData<Resource<List<Review>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        reviewsRef.child(targetId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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
            public void onCancelled(@NonNull DatabaseError error) {
                result.setValue(Resource.error(error.getMessage(), null));
            }
        });

        return result;
    }

    public Task<Void> addReview(Review review) {
        String key = reviewsRef.child(review.getTargetId()).push().getKey();
        if (key != null) {
            review.setId(key);
            return reviewsRef.child(review.getTargetId()).child(key).setValue(review);
        }
        return null;
    }

    // Banners Methods
    public LiveData<Resource<List<Banner>>> getBanners() {
        MutableLiveData<Resource<List<Banner>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        bannersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Banner> banners = new ArrayList<>();
                for (DataSnapshot bannerSnapshot : snapshot.getChildren()) {
                    Banner banner = bannerSnapshot.getValue(Banner.class);
                    if (banner != null) {
                        banner.setId(bannerSnapshot.getKey());
                        banners.add(banner);
                    }
                }
                result.setValue(Resource.success(banners));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.setValue(Resource.error(error.getMessage(), null));
            }
        });

        return result;
    }

    // Quick Info Methods
    public LiveData<Resource<QuickInfo>> getQuickInfo(String countryCode) {
        MutableLiveData<Resource<QuickInfo>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        quickInfoRef.child(countryCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                QuickInfo quickInfo = snapshot.getValue(QuickInfo.class);
                if (quickInfo != null) {
                    quickInfo.setCountryCode(snapshot.getKey());
                    result.setValue(Resource.success(quickInfo));
                } else {
                    result.setValue(Resource.error("Quick info not found", null));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.setValue(Resource.error(error.getMessage(), null));
            }
        });

        return result;
    }

    // Storage Methods
    public Task<UploadTask.TaskSnapshot> uploadImage(Uri imageUri, String path) {
        StorageReference imageRef = storage.child("images/" + path);
        return imageRef.putFile(imageUri);
    }

    public Task<Uri> getDownloadUrl(String path) {
        StorageReference imageRef = storage.child("images/" + path);
        return imageRef.getDownloadUrl();
    }

    // Favorites Methods
    public Task<Void> addToFavorites(String userId, String targetId, String targetKind) {
        return usersRef.child(userId)
                .child("favorites")
                .child(targetKind + "Ids")
                .child(targetId)
                .setValue(true);
    }

    public Task<Void> removeFromFavorites(String userId, String targetId, String targetKind) {
        return usersRef.child(userId)
                .child("favorites")
                .child(targetKind + "Ids")
                .child(targetId)
                .removeValue();
    }
}