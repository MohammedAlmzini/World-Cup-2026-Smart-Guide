package com.ahmmedalmzini783.wcguide.data.repo;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.ahmmedalmzini783.wcguide.data.local.AppDatabase;
import com.ahmmedalmzini783.wcguide.data.local.dao.PlaceDao;
import com.ahmmedalmzini783.wcguide.data.local.entity.PlaceEntity;
import com.ahmmedalmzini783.wcguide.data.model.Banner;
import com.ahmmedalmzini783.wcguide.data.model.Hotel;
import com.ahmmedalmzini783.wcguide.data.model.Place;
import com.ahmmedalmzini783.wcguide.data.model.QuickInfo;
import com.ahmmedalmzini783.wcguide.data.model.Restaurant;
import com.ahmmedalmzini783.wcguide.data.remote.FirebaseDataSource;
import com.ahmmedalmzini783.wcguide.data.repository.HotelRepository;
import com.ahmmedalmzini783.wcguide.data.repository.RestaurantRepository;
import com.ahmmedalmzini783.wcguide.util.Resource;
import com.ahmmedalmzini783.wcguide.util.HotelPlaceConverter;
import com.ahmmedalmzini783.wcguide.util.RestaurantPlaceConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlaceRepository {
    private final PlaceDao placeDao;
    private final FirebaseDataSource firebaseDataSource;
    private final HotelRepository hotelRepository;
    private final RestaurantRepository restaurantRepository;
    private final ExecutorService executor;
    private final Gson gson;
    private static final String TAG = "PlaceRepository";

    public PlaceRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        placeDao = database.placeDao();
        firebaseDataSource = new FirebaseDataSource();
        hotelRepository = new HotelRepository();
        restaurantRepository = new RestaurantRepository();
        executor = Executors.newFixedThreadPool(4);
        gson = new Gson();
    }

    public LiveData<Resource<List<Place>>> getAllPlaces() {
        MediatorLiveData<Resource<List<Place>>> result = new MediatorLiveData<>();

        // Load from cache first
        LiveData<List<PlaceEntity>> localData = placeDao.getAllPlaces();
        result.addSource(localData, entities -> {
            if (entities != null && !entities.isEmpty()) {
                executor.execute(() -> {
                    List<Place> places = convertEntitiesToPlaces(entities);
                    result.postValue(Resource.success(places));
                });
            }
        });

        // Then fetch from network
        LiveData<Resource<List<Place>>> remoteData = firebaseDataSource.getAllPlaces();
        result.addSource(remoteData, resource -> {
            if (resource != null && resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                executor.execute(() -> {
                    List<PlaceEntity> entities = convertPlacesToEntities(resource.getData());
                    placeDao.insertPlaces(entities);
                });
                result.setValue(resource);
            }
        });

        return result;
    }

    public LiveData<Resource<List<Place>>> getPlacesByCountryAndKind(String country, String kind, int limit) {
        MediatorLiveData<Resource<List<Place>>> result = new MediatorLiveData<>();

        // If requesting hotels, get data from new Hotel repository
        if ("hotel".equals(kind)) {
            Log.d(TAG, "Fetching hotels for country: " + country + " with limit: " + limit);
            // Get hotels from hotel repository
            LiveData<Resource<List<Hotel>>> hotelData = hotelRepository.getHotelsByCountry(country);
            result.addSource(hotelData, hotelResource -> {
                if (hotelResource != null) {
                    Log.d(TAG, "Hotel resource status: " + hotelResource.getStatus());
                    if (hotelResource.getStatus() == Resource.Status.LOADING) {
                        result.setValue(Resource.loading(null));
                    } else if (hotelResource.getStatus() == Resource.Status.SUCCESS && hotelResource.getData() != null) {
                        // Convert hotels to places and apply limit
                        List<Hotel> hotels = hotelResource.getData();
                        Log.d(TAG, "Converting " + hotels.size() + " hotels to places");
                        List<Place> places = HotelPlaceConverter.convertHotelsToPlaces(hotels);
                        
                        // Apply limit
                        if (places.size() > limit) {
                            places = places.subList(0, limit);
                        }
                        
                        Log.d(TAG, "Final places count after limit: " + places.size());
                        result.setValue(Resource.success(places));
                    } else if (hotelResource.getStatus() == Resource.Status.ERROR) {
                        Log.e(TAG, "Error loading hotels: " + hotelResource.getMessage());
                        result.setValue(Resource.error(hotelResource.getMessage(), null));
                    }
                }
            });
            
            return result;
        }
        
        // If requesting restaurants, get data from Restaurant repository
        if ("restaurant".equals(kind)) {
            Log.d(TAG, "Fetching restaurants for country: " + country + " with limit: " + limit);
            // Get restaurants from restaurant repository
            LiveData<Resource<List<Restaurant>>> restaurantData = restaurantRepository.getRestaurantsByCountry(country);
            result.addSource(restaurantData, restaurantResource -> {
                if (restaurantResource != null) {
                    Log.d(TAG, "Restaurant resource status: " + restaurantResource.getStatus());
                    if (restaurantResource.getStatus() == Resource.Status.LOADING) {
                        result.setValue(Resource.loading(null));
                    } else if (restaurantResource.getStatus() == Resource.Status.SUCCESS && restaurantResource.getData() != null) {
                        // Convert restaurants to places and apply limit
                        List<Restaurant> restaurants = restaurantResource.getData();
                        Log.d(TAG, "Converting " + restaurants.size() + " restaurants to places");
                        List<Place> places = RestaurantPlaceConverter.convertRestaurantsToPlaces(restaurants);
                        
                        // Apply limit
                        if (places.size() > limit) {
                            places = places.subList(0, limit);
                        }
                        
                        Log.d(TAG, "Final places count after limit: " + places.size());
                        result.setValue(Resource.success(places));
                    } else if (restaurantResource.getStatus() == Resource.Status.ERROR) {
                        Log.e(TAG, "Error loading restaurants: " + restaurantResource.getMessage());
                        result.setValue(Resource.error(restaurantResource.getMessage(), null));
                    }
                }
            });
            
            return result;
        }
        
        // For non-hotel/non-restaurant places, use original logic
        // Load from cache
        LiveData<List<PlaceEntity>> localData = placeDao.getPlacesByCountryAndKind(country, kind, limit);
        result.addSource(localData, entities -> {
            if (entities != null) {
                executor.execute(() -> {
                    List<Place> places = convertEntitiesToPlaces(entities);
                    result.postValue(Resource.success(places));
                });
            }
        });

        // Fetch from network
        LiveData<Resource<List<Place>>> remoteData = firebaseDataSource.getPlacesByCountryAndKind(country, kind);
        result.addSource(remoteData, resource -> {
            if (resource != null && resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                executor.execute(() -> {
                    List<PlaceEntity> entities = convertPlacesToEntities(resource.getData());
                    placeDao.insertPlaces(entities);
                });
                result.setValue(resource);
            }
        });

        return result;
    }

    public LiveData<Resource<List<Place>>> getAllHotels() {
        MediatorLiveData<Resource<List<Place>>> result = new MediatorLiveData<>();
        
        Log.d(TAG, "Fetching all hotels from Firebase without country filtering");
        
        // Get all hotels from HotelRepository without country filtering
        LiveData<Resource<List<Hotel>>> hotelData = hotelRepository.getAllHotels();
        result.addSource(hotelData, hotelResource -> {
            if (hotelResource != null) {
                Log.d(TAG, "All hotels resource status: " + hotelResource.getStatus());
                if (hotelResource.getStatus() == Resource.Status.LOADING) {
                    result.setValue(Resource.loading(null));
                } else if (hotelResource.getStatus() == Resource.Status.SUCCESS && hotelResource.getData() != null) {
                    // Convert hotels to places
                    List<Hotel> hotels = hotelResource.getData();
                    Log.d(TAG, "Converting " + hotels.size() + " hotels to places");
                    List<Place> places = HotelPlaceConverter.convertHotelsToPlaces(hotels);
                    
                    Log.d(TAG, "Final places count: " + places.size());
                    result.setValue(Resource.success(places));
                } else if (hotelResource.getStatus() == Resource.Status.ERROR) {
                    Log.e(TAG, "Error loading all hotels: " + hotelResource.getMessage());
                    result.setValue(Resource.error(hotelResource.getMessage(), null));
                }
            }
        });
        
        return result;
    }

    public LiveData<Resource<List<Place>>> getAllHotelsByKind(String kind, int limit) {
        MediatorLiveData<Resource<List<Place>>> result = new MediatorLiveData<>();

        // If requesting hotels, get hotels from multiple World Cup countries
        if ("hotel".equals(kind)) {
            Log.d(TAG, "Fetching hotels from World Cup countries with limit: " + limit);
            
            // World Cup 2026 countries: USA, Canada, Mexico, Qatar (and others)
            String[] worldCupCountries = {"usa", "USA", "United States", "Qatar", "Canada", "Mexico", "قطر", "الولايات المتحدة"};
            
            // Get hotels from multiple countries
            LiveData<Resource<List<Hotel>>> hotelData = hotelRepository.getHotelsByMultipleCountries(worldCupCountries);
            result.addSource(hotelData, hotelResource -> {
                if (hotelResource != null) {
                    Log.d(TAG, "Multi-country hotels resource status: " + hotelResource.getStatus());
                    if (hotelResource.getStatus() == Resource.Status.LOADING) {
                        result.setValue(Resource.loading(null));
                    } else if (hotelResource.getStatus() == Resource.Status.SUCCESS && hotelResource.getData() != null) {
                        // Convert hotels to places and apply limit
                        List<Hotel> hotels = hotelResource.getData();
                        Log.d(TAG, "Converting " + hotels.size() + " hotels from multiple countries to places");
                        List<Place> places = HotelPlaceConverter.convertHotelsToPlaces(hotels);
                        
                        // Apply limit
                        if (places.size() > limit) {
                            places = places.subList(0, limit);
                        }
                        
                        Log.d(TAG, "Final places count after limit: " + places.size());
                        result.setValue(Resource.success(places));
                    } else if (hotelResource.getStatus() == Resource.Status.ERROR) {
                        Log.e(TAG, "Error loading hotels from multiple countries: " + hotelResource.getMessage());
                        result.setValue(Resource.error(hotelResource.getMessage(), null));
                    }
                }
            });
            
            return result;
        }
        
        // For non-hotel places, use original logic to get all places of this kind
        LiveData<List<PlaceEntity>> localData = placeDao.getPlacesByKind(kind);
        result.addSource(localData, entities -> {
            if (entities != null) {
                executor.execute(() -> {
                    List<Place> places = convertEntitiesToPlaces(entities);
                    // Apply limit
                    if (places.size() > limit) {
                        places = places.subList(0, limit);
                    }
                    result.postValue(Resource.success(places));
                });
            }
        });

        return result;
    }

    public LiveData<Resource<List<Place>>> getAllRestaurantsByKind(String kind, int limit) {
        MediatorLiveData<Resource<List<Place>>> result = new MediatorLiveData<>();

        // If requesting restaurants, get restaurants from multiple World Cup countries
        if ("restaurant".equals(kind)) {
            Log.d(TAG, "Fetching restaurants from World Cup countries with limit: " + limit);
            
            // World Cup 2026 countries: USA, Canada, Mexico, Qatar (and others) - with various case variations
            String[] worldCupCountries = {"usa", "USA", "United States", "united states", "US", "us", "Qatar", "qatar", "قطر", "Canada", "canada", "Mexico", "mexico", "الولايات المتحدة"};
            
            Log.d(TAG, "Loading restaurants from countries: " + java.util.Arrays.toString(worldCupCountries));
            
            // Get restaurants from multiple countries
            LiveData<Resource<List<Restaurant>>> restaurantData = restaurantRepository.getRestaurantsByMultipleCountries(worldCupCountries);
            result.addSource(restaurantData, restaurantResource -> {
                if (restaurantResource != null) {
                    Log.d(TAG, "Multi-country restaurants resource status: " + restaurantResource.getStatus());
                    if (restaurantResource.getStatus() == Resource.Status.LOADING) {
                        result.setValue(Resource.loading(null));
                    } else if (restaurantResource.getStatus() == Resource.Status.SUCCESS && restaurantResource.getData() != null) {
                        // Convert restaurants to places and apply limit
                        List<Restaurant> restaurants = restaurantResource.getData();
                        Log.d(TAG, "Converting " + restaurants.size() + " restaurants from multiple countries to places");
                        List<Place> places = RestaurantPlaceConverter.convertRestaurantsToPlaces(restaurants);
                        
                        // Apply limit
                        if (places.size() > limit) {
                            places = places.subList(0, limit);
                        }
                        
                        Log.d(TAG, "Final places count after limit: " + places.size());
                        result.setValue(Resource.success(places));
                    } else if (restaurantResource.getStatus() == Resource.Status.ERROR) {
                        Log.e(TAG, "Error loading restaurants from multiple countries: " + restaurantResource.getMessage());
                        result.setValue(Resource.error(restaurantResource.getMessage(), null));
                    }
                }
            });
            
            return result;
        }
        
        // For non-restaurant places, use original logic to get all places of this kind
        LiveData<List<PlaceEntity>> localData = placeDao.getPlacesByKind(kind);
        result.addSource(localData, entities -> {
            if (entities != null) {
                executor.execute(() -> {
                    List<Place> places = convertEntitiesToPlaces(entities);
                    // Apply limit
                    if (places.size() > limit) {
                        places = places.subList(0, limit);
                    }
                    result.postValue(Resource.success(places));
                });
            }
        });

        return result;
    }

    public LiveData<Resource<List<Place>>> getPlacesByCity(String city) {
        MediatorLiveData<Resource<List<Place>>> result = new MediatorLiveData<>();

        LiveData<List<PlaceEntity>> localData = placeDao.getPlacesByCity(city);
        result.addSource(localData, entities -> {
            if (entities != null) {
                executor.execute(() -> {
                    List<Place> places = convertEntitiesToPlaces(entities);
                    result.postValue(Resource.success(places));
                });
            }
        });

        return result;
    }

    public LiveData<Resource<List<Place>>> getPlacesByKind(String kind) {
        MediatorLiveData<Resource<List<Place>>> result = new MediatorLiveData<>();

        LiveData<List<PlaceEntity>> localData = placeDao.getPlacesByKind(kind);
        result.addSource(localData, entities -> {
            if (entities != null) {
                executor.execute(() -> {
                    List<Place> places = convertEntitiesToPlaces(entities);
                    result.postValue(Resource.success(places));
                });
            }
        });

        return result;
    }

    public LiveData<List<String>> getAllCountries() {
        return placeDao.getAllCountries();
    }

    public LiveData<List<String>> getCitiesByCountry(String country) {
        return placeDao.getCitiesByCountry(country);
    }

    public LiveData<List<String>> getAllKinds() {
        return placeDao.getAllKinds();
    }

    // Conversion methods
    private List<Place> convertEntitiesToPlaces(List<PlaceEntity> entities) {
        List<Place> places = new ArrayList<>();
        for (PlaceEntity entity : entities) {
            places.add(convertEntityToPlace(entity));
        }
        return places;
    }

    private Place convertEntityToPlace(PlaceEntity entity) {
        Place place = new Place();
        place.setId(entity.getId());
        place.setKind(entity.getKind());
        place.setName(entity.getName());
        place.setCountry(entity.getCountry());
        place.setCity(entity.getCity());
        place.setAddress(entity.getAddress());
        place.setLat(entity.getLat());
        place.setLng(entity.getLng());
        place.setAvgRating(entity.getAvgRating());
        place.setRatingCount(entity.getRatingCount());
        place.setPriceLevel(entity.getPriceLevel());
        place.setDescription(entity.getDescription());

        // Convert JSON strings back to lists
        if (entity.getImagesJson() != null) {
            Type listType = new TypeToken<List<String>>(){}.getType();
            place.setImages(gson.fromJson(entity.getImagesJson(), listType));
        }

        if (entity.getAmenitiesJson() != null) {
            Type listType = new TypeToken<List<String>>(){}.getType();
            place.setAmenities(gson.fromJson(entity.getAmenitiesJson(), listType));
        }

        return place;
    }

    private List<PlaceEntity> convertPlacesToEntities(List<Place> places) {
        List<PlaceEntity> entities = new ArrayList<>();
        for (Place place : places) {
            entities.add(convertPlaceToEntity(place));
        }
        return entities;
    }

    private PlaceEntity convertPlaceToEntity(Place place) {
        PlaceEntity entity = new PlaceEntity();
        entity.setId(place.getId());
        entity.setKind(place.getKind());
        entity.setName(place.getName());
        entity.setCountry(place.getCountry());
        entity.setCity(place.getCity());
        entity.setAddress(place.getAddress());
        entity.setLat(place.getLat());
        entity.setLng(place.getLng());
        entity.setAvgRating(place.getAvgRating());
        entity.setRatingCount(place.getRatingCount());
        entity.setPriceLevel(place.getPriceLevel());
        entity.setDescription(place.getDescription());
        entity.setLastUpdated(System.currentTimeMillis());

        // Convert lists to JSON strings
        if (place.getImages() != null) {
            entity.setImagesJson(gson.toJson(place.getImages()));
        }

        if (place.getAmenities() != null) {
            entity.setAmenitiesJson(gson.toJson(place.getAmenities()));
        }

        return entity;
    }

    // Banners
    public LiveData<Resource<List<Banner>>> getBanners() {
        return firebaseDataSource.getBanners();
    }

    // Quick Info
    public LiveData<Resource<List<QuickInfo>>> getQuickInfo() {
        return firebaseDataSource.getQuickInfo();
    }
}