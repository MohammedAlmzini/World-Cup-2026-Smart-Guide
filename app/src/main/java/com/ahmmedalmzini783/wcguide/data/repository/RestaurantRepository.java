package com.ahmmedalmzini783.wcguide.data.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.ahmmedalmzini783.wcguide.data.model.Restaurant;
import com.ahmmedalmzini783.wcguide.util.Resource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RestaurantRepository {
    private DatabaseReference databaseReference;
    private static final String RESTAURANTS_NODE = "restaurants";
    private static final String TAG = "RestaurantRepository";

    public RestaurantRepository() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(RESTAURANTS_NODE);
    }

    public LiveData<Resource<List<Restaurant>>> getAllRestaurants() {
        MutableLiveData<Resource<List<Restaurant>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Log.d(TAG, "Fetching all restaurants from Firebase");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Restaurant> restaurants = new ArrayList<>();
                Log.d(TAG, "Firebase response received. Children count: " + dataSnapshot.getChildrenCount());
                
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Restaurant restaurant = snapshot.getValue(Restaurant.class);
                    if (restaurant != null) {
                        Log.d(TAG, "Restaurant found: " + restaurant.getName());
                        restaurants.add(restaurant);
                    }
                }
                Log.d(TAG, "Total restaurants loaded: " + restaurants.size());
                result.setValue(Resource.success(restaurants));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error fetching restaurants: " + databaseError.getMessage());
                result.setValue(Resource.error(databaseError.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Restaurant>> getRestaurantById(String restaurantId) {
        MutableLiveData<Resource<Restaurant>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        databaseReference.child(restaurantId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                if (restaurant != null) {
                    result.setValue(Resource.success(restaurant));
                } else {
                    result.setValue(Resource.error("Restaurant not found", null));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.setValue(Resource.error(databaseError.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<Restaurant>>> getRestaurantsByCity(String city) {
        MutableLiveData<Resource<List<Restaurant>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Query query = databaseReference.orderByChild("city").equalTo(city);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Restaurant> restaurants = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Restaurant restaurant = snapshot.getValue(Restaurant.class);
                    if (restaurant != null) {
                        restaurants.add(restaurant);
                    }
                }
                result.setValue(Resource.success(restaurants));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.setValue(Resource.error(databaseError.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<Restaurant>>> getRestaurantsByCountry(String country) {
        MutableLiveData<Resource<List<Restaurant>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Query query = databaseReference.orderByChild("country").equalTo(country);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Restaurant> restaurants = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Restaurant restaurant = snapshot.getValue(Restaurant.class);
                    if (restaurant != null) {
                        restaurants.add(restaurant);
                    }
                }
                result.setValue(Resource.success(restaurants));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.setValue(Resource.error(databaseError.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<Restaurant>>> getRestaurantsByCuisineType(String cuisineType) {
        MutableLiveData<Resource<List<Restaurant>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Query query = databaseReference.orderByChild("cuisineType").equalTo(cuisineType);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Restaurant> restaurants = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Restaurant restaurant = snapshot.getValue(Restaurant.class);
                    if (restaurant != null) {
                        restaurants.add(restaurant);
                    }
                }
                result.setValue(Resource.success(restaurants));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.setValue(Resource.error(databaseError.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<Restaurant>>> getRestaurantsByMultipleCountries(String[] countries) {
        MutableLiveData<Resource<List<Restaurant>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Log.d(TAG, "Fetching restaurants for multiple countries");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Restaurant> restaurants = new ArrayList<>();
                Log.d(TAG, "Firebase response for multiple countries. Children count: " + dataSnapshot.getChildrenCount());
                
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        Restaurant restaurant = snapshot.getValue(Restaurant.class);
                        if (restaurant != null) {
                            Log.d(TAG, "Restaurant parsed: " + restaurant.getName() + " from country: " + restaurant.getCountry());
                            // Check if restaurant's country is in the list of accepted countries
                            boolean countryFound = false;
                            for (String country : countries) {
                                if (country.equalsIgnoreCase(restaurant.getCountry())) {
                                    Log.d(TAG, "Restaurant accepted for " + country + ": " + restaurant.getName());
                                    restaurants.add(restaurant);
                                    countryFound = true;
                                    break;
                                }
                            }
                            if (!countryFound) {
                                Log.d(TAG, "Restaurant country '" + restaurant.getCountry() + "' not in accepted list");
                            }
                        } else {
                            Log.w(TAG, "Restaurant is null for snapshot: " + snapshot.getKey());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing restaurant from snapshot: " + snapshot.getKey(), e);
                    }
                }
                Log.d(TAG, "Total restaurants loaded for multiple countries: " + restaurants.size());
                result.setValue(Resource.success(restaurants));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error fetching restaurants for multiple countries: " + databaseError.getMessage());
                result.setValue(Resource.error(databaseError.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<Restaurant>>> searchRestaurants(String query) {
        MutableLiveData<Resource<List<Restaurant>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Restaurant> restaurants = new ArrayList<>();
                String searchQuery = query.toLowerCase();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Restaurant restaurant = snapshot.getValue(Restaurant.class);
                    if (restaurant != null) {
                        // Search in name, description, cuisine type, city, and country
                        if (restaurant.getName().toLowerCase().contains(searchQuery) ||
                            restaurant.getDescription().toLowerCase().contains(searchQuery) ||
                            restaurant.getCuisineType().toLowerCase().contains(searchQuery) ||
                            restaurant.getCity().toLowerCase().contains(searchQuery) ||
                            restaurant.getCountry().toLowerCase().contains(searchQuery)) {
                            restaurants.add(restaurant);
                        }
                    }
                }
                result.setValue(Resource.success(restaurants));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.setValue(Resource.error(databaseError.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Void>> addRestaurant(Restaurant restaurant) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        // Generate ID if not provided
        if (restaurant.getId() == null || restaurant.getId().isEmpty()) {
            restaurant.setId(UUID.randomUUID().toString());
        }

        // Set timestamps
        long currentTime = System.currentTimeMillis();
        restaurant.setCreatedAt(currentTime);
        restaurant.setUpdatedAt(currentTime);

        databaseReference.child(restaurant.getId()).setValue(restaurant)
                .addOnSuccessListener(aVoid -> result.setValue(Resource.success(null)))
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getMessage(), null)));

        return result;
    }

    public LiveData<Resource<Void>> updateRestaurant(Restaurant restaurant) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        // Update timestamp
        restaurant.setUpdatedAt(System.currentTimeMillis());

        databaseReference.child(restaurant.getId()).setValue(restaurant)
                .addOnSuccessListener(aVoid -> result.setValue(Resource.success(null)))
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getMessage(), null)));

        return result;
    }

    public LiveData<Resource<Void>> deleteRestaurant(String restaurantId) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        databaseReference.child(restaurantId).removeValue()
                .addOnSuccessListener(aVoid -> result.setValue(Resource.success(null)))
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getMessage(), null)));

        return result;
    }
}
