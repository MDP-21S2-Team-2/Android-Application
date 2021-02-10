package com.example.mdpapplication.ui.communication;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CommunicationViewModel extends ViewModel {

    private MutableLiveData<String> receivedStringsText;

    public CommunicationViewModel() {
        receivedStringsText = new MutableLiveData<>();
        receivedStringsText.setValue("");
    }

    public LiveData<String> getText() {
        Log.d("CommunicationViewModel", "Returning view model received strings: " + receivedStringsText.getValue());
        return receivedStringsText;
    }

    public void updateReceivedStringsText(String newReceivedString) {
        receivedStringsText.setValue(newReceivedString + "\n" + receivedStringsText.getValue());
        Log.d("CommunicationViewModel", "Updated view model received strings: " + receivedStringsText.getValue());
    }
}
