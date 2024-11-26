package com.wongcoco.thinkwapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Map;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Map<String, Object>> userData = new MutableLiveData<>();

    public void setUserData(Map<String, Object> data) {
        userData.setValue(data);
    }

    public LiveData<Map<String, Object>> getUserData() {
        return userData;
    }
}
