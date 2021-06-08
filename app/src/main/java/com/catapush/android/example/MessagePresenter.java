package com.catapush.android.example;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.catapush.library.messages.CatapushMessage;

public class MessagePresenter implements MessageContract.Presenter {

    private MessageContract.MessageView messageView;
    private MessageContract.MainView mainView;

    public MessagePresenter(MessageContract.MessageView messageView) {
        this.messageView = messageView;
    }

    public void setMainView(@Nullable MessageContract.MainView mainView) {
        this.mainView = mainView;
    }

    public void setMessageView(@Nullable MessageContract.MessageView messageView) {
        this.messageView = messageView;
    }

    @Override
    public void onViewTitleChanged(String title) {
        if (mainView != null) {
            mainView.setViewTitle(title);
        }
    }

    @Override
    public void onMessageDeleted(@NonNull CatapushMessage message) {
        if (messageView != null) {
            messageView.showUndeleteSnackBar(message.previewText());
        }
    }

    @Override
    public void onPickAttachmentRequest(@NonNull String mimeType) {
        if (mainView != null) {
            mainView.openFilePicker(mimeType);
        }
    }

    @Override
    public void onAttachmentPicked(@NonNull Uri attachmentUri) {
        if (messageView != null) {
            messageView.attachFile(attachmentUri);
        }
    }
}
