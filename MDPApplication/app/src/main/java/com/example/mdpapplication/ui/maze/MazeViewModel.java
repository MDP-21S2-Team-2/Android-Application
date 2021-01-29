package com.example.mdpapplication.ui.maze;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MazeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MazeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is maze fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}