package com.ahmmedalmzini783.wcguide.ui.admin;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ahmmedalmzini783.wcguide.data.model.Restaurant;
import com.ahmmedalmzini783.wcguide.data.repository.RestaurantRepository;
import com.ahmmedalmzini783.wcguide.util.Resource;

import java.util.List;

public class RestaurantViewModel extends AndroidViewModel {
    private final RestaurantRepository restaurantRepository;
    private final MutableLiveData<String> operationResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public RestaurantViewModel(@NonNull Application application) {
        super(application);
        restaurantRepository = new RestaurantRepository();
    }

    public LiveData<Resource<List<Restaurant>>> getAllRestaurants() {
        return restaurantRepository.getAllRestaurants();
    }

    public LiveData<Resource<Restaurant>> getRestaurantById(String restaurantId) {
        return restaurantRepository.getRestaurantById(restaurantId);
    }

    public LiveData<Resource<List<Restaurant>>> getRestaurantsByCity(String city) {
        return restaurantRepository.getRestaurantsByCity(city);
    }

    public LiveData<Resource<List<Restaurant>>> getRestaurantsByCountry(String country) {
        return restaurantRepository.getRestaurantsByCountry(country);
    }

    public LiveData<Resource<List<Restaurant>>> getRestaurantsByCuisineType(String cuisineType) {
        return restaurantRepository.getRestaurantsByCuisineType(cuisineType);
    }

    public LiveData<Resource<List<Restaurant>>> searchRestaurants(String query) {
        return restaurantRepository.searchRestaurants(query);
    }

    public void addRestaurant(Restaurant restaurant) {
        isLoading.setValue(true);
        restaurantRepository.addRestaurant(restaurant).observeForever(resource -> {
            isLoading.setValue(false);
            if (resource.getStatus() == Resource.Status.SUCCESS) {
                operationResult.setValue("تم إضافة المطعم بنجاح");
            } else if (resource.getStatus() == Resource.Status.ERROR) {
                operationResult.setValue("فشل في إضافة المطعم: " + resource.getMessage());
            }
        });
    }

    public void updateRestaurant(Restaurant restaurant) {
        isLoading.setValue(true);
        restaurantRepository.updateRestaurant(restaurant).observeForever(resource -> {
            isLoading.setValue(false);
            if (resource.getStatus() == Resource.Status.SUCCESS) {
                operationResult.setValue("تم تحديث المطعم بنجاح");
            } else if (resource.getStatus() == Resource.Status.ERROR) {
                operationResult.setValue("فشل في تحديث المطعم: " + resource.getMessage());
            }
        });
    }

    public void deleteRestaurant(String restaurantId) {
        isLoading.setValue(true);
        restaurantRepository.deleteRestaurant(restaurantId).observeForever(resource -> {
            isLoading.setValue(false);
            if (resource.getStatus() == Resource.Status.SUCCESS) {
                operationResult.setValue("تم حذف المطعم بنجاح");
            } else if (resource.getStatus() == Resource.Status.ERROR) {
                operationResult.setValue("فشل في حذف المطعم: " + resource.getMessage());
            }
        });
    }

    public LiveData<String> getOperationResult() {
        return operationResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void clearOperationResult() {
        operationResult.setValue(null);
    }
}
