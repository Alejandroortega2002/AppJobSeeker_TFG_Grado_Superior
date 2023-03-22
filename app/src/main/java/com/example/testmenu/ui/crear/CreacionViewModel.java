package com.example.testmenu.ui.crear;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CreacionViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CreacionViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Crear fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}