package com.catapush.android.example.messages;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.PagingData;

import com.catapush.library.Catapush;
import com.catapush.library.messages.CatapushMessage;

import io.reactivex.Flowable;
import kotlinx.coroutines.CoroutineScope;

public class MessagingViewModel extends ViewModel {
    public Flowable<PagingData<CatapushMessage>> messageList;

    public MessagingViewModel init() {
        if (messageList == null) {
            CoroutineScope thisScope = ViewModelKt.getViewModelScope(this);
            messageList = Catapush.getInstance().getMessagesWithoutChannelAsPagingDataFlowable(thisScope);
        }
        return this;
    }
}
