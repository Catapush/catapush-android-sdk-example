package com.catapush.android.example.messages;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.catapush.library.Catapush;
import com.catapush.library.messages.CatapushMessage;

public class MessagingViewModel extends ViewModel {
    public LiveData<PagedList<CatapushMessage>> messageList;

    public MessagingViewModel init() {
        if (messageList == null) {
            DataSource.Factory<Integer, CatapushMessage> dataSource
                    = Catapush.getInstance().getMessagesWithoutChannelAsDataSourceFactory();
            messageList = new LivePagedListBuilder<>(dataSource, 50).build();
        }
        return this;
    }
}
