package com.ahmmedalmzini783.wcguide.ui.admin;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ahmmedalmzini783.wcguide.data.model.Hotel;
import com.ahmmedalmzini783.wcguide.data.repository.HotelRepository;
import com.ahmmedalmzini783.wcguide.util.Resource;

import java.util.List;

public class HotelViewModel extends AndroidViewModel {
    private final HotelRepository hotelRepository;
    private final MutableLiveData<String> operationResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public HotelViewModel(@NonNull Application application) {
        super(application);
        hotelRepository = new HotelRepository();
    }

    public LiveData<Resource<List<Hotel>>> getAllHotels() {
        return hotelRepository.getAllHotels();
    }

    public LiveData<Resource<Hotel>> getHotelById(String hotelId) {
        return hotelRepository.getHotelById(hotelId);
    }

    public LiveData<Resource<List<Hotel>>> getHotelsByCity(String city) {
        return hotelRepository.getHotelsByCity(city);
    }

    public LiveData<Resource<List<Hotel>>> getHotelsByCountry(String country) {
        return hotelRepository.getHotelsByCountry(country);
    }

    public LiveData<Resource<List<Hotel>>> searchHotels(String query) {
        return hotelRepository.searchHotels(query);
    }

    public void addHotel(Hotel hotel) {
        isLoading.setValue(true);
        hotelRepository.addHotel(hotel).observeForever(resource -> {
            isLoading.setValue(false);
            if (resource.getStatus() == Resource.Status.SUCCESS) {
                operationResult.setValue("تم إضافة الفندق بنجاح");
            } else if (resource.getStatus() == Resource.Status.ERROR) {
                operationResult.setValue("فشل في إضافة الفندق: " + resource.getMessage());
            }
        });
    }

    public void updateHotel(Hotel hotel) {
        isLoading.setValue(true);
        hotelRepository.updateHotel(hotel).observeForever(resource -> {
            isLoading.setValue(false);
            if (resource.getStatus() == Resource.Status.SUCCESS) {
                operationResult.setValue("تم تحديث الفندق بنجاح");
            } else if (resource.getStatus() == Resource.Status.ERROR) {
                operationResult.setValue("فشل في تحديث الفندق: " + resource.getMessage());
            }
        });
    }

    public void deleteHotel(String hotelId) {
        isLoading.setValue(true);
        hotelRepository.deleteHotel(hotelId).observeForever(resource -> {
            isLoading.setValue(false);
            if (resource.getStatus() == Resource.Status.SUCCESS) {
                operationResult.setValue("تم حذف الفندق بنجاح");
            } else if (resource.getStatus() == Resource.Status.ERROR) {
                operationResult.setValue("فشل في حذف الفندق: " + resource.getMessage());
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
