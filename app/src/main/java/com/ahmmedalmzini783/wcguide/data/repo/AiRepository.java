package com.ahmmedalmzini783.wcguide.data.repo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ahmmedalmzini783.wcguide.data.model.AiResponse;
import com.ahmmedalmzini783.wcguide.data.remote.AiApiClient;
import com.ahmmedalmzini783.wcguide.util.Resource;

import java.util.List;

public class AiRepository {
    private final AiApiClient aiApiClient;

    public AiRepository() {
        aiApiClient = new AiApiClient();
    }

    public LiveData<Resource<String>> askQuestion(String question) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        aiApiClient.askQuestion(question, new AiApiClient.ApiCallback<String>() {
            @Override
            public void onSuccess(String response) {
                result.setValue(Resource.success(response));
            }

            @Override
            public void onError(String error) {
                result.setValue(Resource.error(error, null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<AiResponse.DailyPlanItem>>> generateDailyPlan(String city, int availableHours, String interests) {
        MutableLiveData<Resource<List<AiResponse.DailyPlanItem>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        aiApiClient.generateDailyPlan(city, availableHours, interests, new AiApiClient.ApiCallback<List<AiResponse.DailyPlanItem>>() {
            @Override
            public void onSuccess(List<AiResponse.DailyPlanItem> planItems) {
                result.setValue(Resource.success(planItems));
            }

            @Override
            public void onError(String error) {
                result.setValue(Resource.error(error, null));
            }
        });

        return result;
    }

    public LiveData<Resource<AiResponse.TranslationResponse>> translateText(String text, String targetLanguage) {
        MutableLiveData<Resource<AiResponse.TranslationResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        aiApiClient.translateText(text, targetLanguage, new AiApiClient.ApiCallback<AiResponse.TranslationResponse>() {
            @Override
            public void onSuccess(AiResponse.TranslationResponse response) {
                result.setValue(Resource.success(response));
            }

            @Override
            public void onError(String error) {
                result.setValue(Resource.error(error, null));
            }
        });

        return result;
    }
}