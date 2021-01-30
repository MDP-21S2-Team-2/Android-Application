package com.example.mdpapplication.ui.communication;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CommunicationViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CommunicationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Enter your persistent communication string 1 here");
    }

    public LiveData<String> getText() {
        return mText;
    }
}