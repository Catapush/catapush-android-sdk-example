package com.catapush.example.app;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.catapush.library.messages.CatapushMessage;

public interface MessageContract {

    interface MainView {

        void setViewTitle(String title);

        void openFilePicker(@NonNull String mimeType);

    }

    interface MessageView {

        void showUndeleteSnackBar(String messagePreview);

        void attachFile(@NonNull Uri attachmentUri);

    }

    interface Presenter {

        void onViewTitleChanged(String title);

        void onMessageDeleted(@NonNull CatapushMessage message);

        void onPickAttachmentRequest(@NonNull String mimeType);

        void onAttachmentPicked(@NonNull Uri attachmentUri);

    }

}
