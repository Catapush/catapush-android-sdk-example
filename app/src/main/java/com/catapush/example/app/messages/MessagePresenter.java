package com.catapush.example.app.messages;

import com.catapush.example.app.communications.Actions;
import com.catapush.library.Catapush;
import com.catapush.library.interfaces.Callback;
import com.catapush.library.messages.CatapushMessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.List;

public class MessagePresenter {

    private MessageView view;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (Actions.ACTION_IPMESSAGE_RECEIVED.equals(action)) {
                onMessageReceived(intent);
            }
        }
    };

    public MessagePresenter(Context context, MessageView view) {
        this.view = view;

        IntentFilter filter = new IntentFilter();
        filter.addAction(Actions.ACTION_IPMESSAGE_RECEIVED);
        filter.addCategory(context.getPackageName());
        context.registerReceiver(receiver, filter);
    }

    private void onMessageReceived(Intent intent) {
        getMessages();
    }

    public void getMessages() {
        Catapush.getInstance().getMessagesAsList(new Callback<List<CatapushMessage>>() {
            @Override
            public void success(List<CatapushMessage> catapushMessages) {
                view.setMessages(catapushMessages);
            }

            @Override
            public void failure(Throwable throwable) {

            }
        });
    }

    public BroadcastReceiver getReceiver() {
        return receiver;
    }
}
