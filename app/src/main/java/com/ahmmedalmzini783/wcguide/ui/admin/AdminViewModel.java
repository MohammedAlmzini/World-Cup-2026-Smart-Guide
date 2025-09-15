package com.ahmmedalmzini783.wcguide.ui.admin;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ahmmedalmzini783.wcguide.data.model.Banner;
import com.ahmmedalmzini783.wcguide.data.remote.FirebaseDataSource;
import com.ahmmedalmzini783.wcguide.util.Resource;

import java.util.List;

public class AdminViewModel extends AndroidViewModel {

    private final FirebaseDataSource firebaseDataSource;
    private final MutableLiveData<String> operationResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public AdminViewModel(@NonNull Application application) {
        super(application);
        firebaseDataSource = new FirebaseDataSource();
    }

    public LiveData<Resource<List<Banner>>> getBanners() {
        return firebaseDataSource.getBanners();
    }

    public void addBanner(Banner banner) {
        isLoading.setValue(true);
        firebaseDataSource.addBanner(banner, new FirebaseDataSource.OnCompleteListener<Void>() {
            @Override
            public void onComplete(boolean success, String message) {
                isLoading.setValue(false);
                if (success) {
                    operationResult.setValue("تم إضافة الإعلان بنجاح");
                } else {
                    operationResult.setValue("فشل في إضافة الإعلان: " + message);
                }
            }
        });
    }

    public void updateBanner(Banner banner) {
        isLoading.setValue(true);
        firebaseDataSource.updateBanner(banner, new FirebaseDataSource.OnCompleteListener<Void>() {
            @Override
            public void onComplete(boolean success, String message) {
                isLoading.setValue(false);
                if (success) {
                    operationResult.setValue("تم تحديث الإعلان بنجاح");
                } else {
                    operationResult.setValue("فشل في تحديث الإعلان: " + message);
                }
            }
        });
    }

    public void deleteBanner(String bannerId) {
        isLoading.setValue(true);
        firebaseDataSource.deleteBanner(bannerId, new FirebaseDataSource.OnCompleteListener<Void>() {
            @Override
            public void onComplete(boolean success, String message) {
                isLoading.setValue(false);
                if (success) {
                    operationResult.setValue("تم حذف الإعلان بنجاح");
                } else {
                    operationResult.setValue("فشل في حذف الإعلان: " + message);
                }
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
