package com.catapush.example.app.messages;

import com.catapush.library.messages.CatapushMessage;

public class MessagePresenter {

    private MessageView view;

    public MessagePresenter(MessageView view) {
        this.view = view;
    }

    public void onMessageDeleted(CatapushMessage message) {
        view.showUndeleteSnackBar(message.previewText());
    }

}
