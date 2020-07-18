package com.example.PassControll.AllPassesActivity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AllPassesContentViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AllPassesContentViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}