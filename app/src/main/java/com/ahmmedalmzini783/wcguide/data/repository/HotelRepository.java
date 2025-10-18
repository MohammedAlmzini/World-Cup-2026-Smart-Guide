package com.ahmmedalmzini783.wcguide.data.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.ahmmedalmzini783.wcguide.data.model.Hotel;
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

public class HotelRepository {
    private DatabaseReference databaseReference;
    private static final String HOTELS_NODE = "hotels";
    private static final String TAG = "HotelRepository";

    public HotelRepository() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(HOTELS_NODE);
    }

    public LiveData<Resource<List<Hotel>>> getAllHotels() {
        MutableLiveData<Resource<List<Hotel>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Log.d(TAG, "Fetching all hotels from Firebase");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Hotel> hotels = new ArrayList<>();
                Log.d(TAG, "Firebase response received. Children count: " + dataSnapshot.getChildrenCount());
                
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Hotel hotel = snapshot.getValue(Hotel.class);
                    if (hotel != null) {
                        Log.d(TAG, "Hotel found: " + hotel.getName());
                        hotels.add(hotel);
                    }
                }
                Log.d(TAG, "Total hotels loaded: " + hotels.size());
                result.setValue(Resource.success(hotels));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error fetching hotels: " + databaseError.getMessage());
                result.setValue(Resource.error(databaseError.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Hotel>> getHotelById(String hotelId) {
        MutableLiveData<Resource<Hotel>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        databaseReference.child(hotelId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Hotel hotel = dataSnapshot.getValue(Hotel.class);
                if (hotel != null) {
                    result.setValue(Resource.success(hotel));
                } else {
                    result.setValue(Resource.error("Hotel not found", null));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.setValue(Resource.error(databaseError.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<Hotel>>> getHotelsByCity(String city) {
        MutableLiveData<Resource<List<Hotel>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Query query = databaseReference.orderByChild("city").equalTo(city);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Hotel> hotels = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Hotel hotel = snapshot.getValue(Hotel.class);
                    if (hotel != null) {
                        hotels.add(hotel);
                    }
                }
                result.setValue(Resource.success(hotels));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.setValue(Resource.error(databaseError.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<Hotel>>> getHotelsByCountry(String country) {
        MutableLiveData<Resource<List<Hotel>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Log.d(TAG, "Fetching hotels for country: " + country);

        Query query = databaseReference.orderByChild("country").equalTo(country);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Hotel> hotels = new ArrayList<>();
                Log.d(TAG, "Firebase response for country " + country + ". Children count: " + dataSnapshot.getChildrenCount());
                
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Hotel hotel = snapshot.getValue(Hotel.class);
                    if (hotel != null) {
                        Log.d(TAG, "Hotel found for " + country + ": " + hotel.getName());
                        hotels.add(hotel);
                    }
                }
                Log.d(TAG, "Total hotels loaded for " + country + ": " + hotels.size());
                result.setValue(Resource.success(hotels));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error fetching hotels for country " + country + ": " + databaseError.getMessage());
                result.setValue(Resource.error(databaseError.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<Hotel>>> getHotelsByMultipleCountries(String[] countries) {
        MutableLiveData<Resource<List<Hotel>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Log.d(TAG, "Fetching hotels for multiple countries: " + java.util.Arrays.toString(countries));

        // For simplicity, we'll get all hotels and filter by countries
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Hotel> hotels = new ArrayList<>();
                Log.d(TAG, "Firebase response for multiple countries. Children count: " + dataSnapshot.getChildrenCount());
                
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Hotel hotel = snapshot.getValue(Hotel.class);
                    if (hotel != null) {
                        Log.d(TAG, "Processing hotel: " + hotel.getName() + " from country: " + hotel.getCountry());
                        // Check if hotel's country is in the list of accepted countries
                        boolean countryFound = false;
                        for (String country : countries) {
                            if (country.equalsIgnoreCase(hotel.getCountry())) {
                                Log.d(TAG, "Hotel accepted for " + country + ": " + hotel.getName());
                                hotels.add(hotel);
                                countryFound = true;
                                break;
                            }
                        }
                        if (!countryFound) {
                            Log.d(TAG, "Hotel country '" + hotel.getCountry() + "' not in accepted list: " + java.util.Arrays.toString(countries));
                        }
                    } else {
                        Log.w(TAG, "Hotel is null for snapshot: " + snapshot.getKey());
                    }
                }
                Log.d(TAG, "Total hotels loaded for multiple countries: " + hotels.size());
                result.setValue(Resource.success(hotels));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error fetching hotels for multiple countries: " + databaseError.getMessage());
                result.setValue(Resource.error(databaseError.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Void>> addHotel(Hotel hotel) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        // Generate ID if not provided
        if (hotel.getId() == null || hotel.getId().isEmpty()) {
            hotel.setId(UUID.randomUUID().toString());
        }

        // Set timestamps
        long currentTime = System.currentTimeMillis();
        hotel.setCreatedAt(currentTime);
        hotel.setUpdatedAt(currentTime);

        databaseReference.child(hotel.getId()).setValue(hotel)
                .addOnSuccessListener(aVoid -> result.setValue(Resource.success(null)))
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getMessage(), null)));

        return result;
    }

    public LiveData<Resource<Void>> updateHotel(Hotel hotel) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        // Update timestamp
        hotel.setUpdatedAt(System.currentTimeMillis());

        databaseReference.child(hotel.getId()).setValue(hotel)
                .addOnSuccessListener(aVoid -> result.setValue(Resource.success(null)))
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getMessage(), null)));

        return result;
    }

    public LiveData<Resource<Void>> deleteHotel(String hotelId) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        databaseReference.child(hotelId).removeValue()
                .addOnSuccessListener(aVoid -> result.setValue(Resource.success(null)))
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getMessage(), null)));

        return result;
    }

    public LiveData<Resource<List<Hotel>>> searchHotels(String query) {
        MutableLiveData<Resource<List<Hotel>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Hotel> hotels = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Hotel hotel = snapshot.getValue(Hotel.class);
                    if (hotel != null && 
                        (hotel.getName().toLowerCase().contains(query.toLowerCase()) ||
                         hotel.getCity().toLowerCase().contains(query.toLowerCase()) ||
                         hotel.getCountry().toLowerCase().contains(query.toLowerCase()))) {
                        hotels.add(hotel);
                    }
                }
                result.setValue(Resource.success(hotels));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.setValue(Resource.error(databaseError.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<Hotel>>> getTopRatedHotels(int limit) {
        MutableLiveData<Resource<List<Hotel>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Query query = databaseReference.orderByChild("rating").limitToLast(limit);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Hotel> hotels = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Hotel hotel = snapshot.getValue(Hotel.class);
                    if (hotel != null) {
                        hotels.add(0, hotel); // Add at beginning for descending order
                    }
                }
                result.setValue(Resource.success(hotels));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.setValue(Resource.error(databaseError.getMessage(), null));
            }
        });

        return result;
    }
}
