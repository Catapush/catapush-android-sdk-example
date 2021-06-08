package com.catapush.android.example;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.catapush.android.example.messages.MessagingViewModel;


public class SampleViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.equals(MessagingViewModel.class)) {
            //noinspection unchecked
            return (T) new MessagingViewModel();
        }
        return null;
    }

}

