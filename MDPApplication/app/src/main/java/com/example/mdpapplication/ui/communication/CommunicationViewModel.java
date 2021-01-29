package com.example.mdpapplication.ui.communication;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CommunicationViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CommunicationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is communication fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}