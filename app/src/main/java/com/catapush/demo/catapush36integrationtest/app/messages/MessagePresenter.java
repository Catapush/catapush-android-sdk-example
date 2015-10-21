package com.catapush.demo.catapush36integrationtest.app.messages;

import com.catapush.demo.catapush36integrationtest.app.communications.Actions;
import com.catapush.library.Catapush;
import com.catapush.library.interfaces.Callback;
import com.catapush.library.storage.models.IPMessage;

import org.parceler.Parcels;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.List;

public class MessagePresenter {

    private MessageView mView;

    public MessagePresenter(Context context, MessageView view) {
        mView = view;

        IntentFilter filter = new IntentFilter();
        filter.addAction(Actions.ACTION_IPMESSAGE_RECEIVED);
        filter.addCategory(context.getPackageName());
        context.registerReceiver(mReceiver, filter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (Actions.ACTION_IPMESSAGE_RECEIVED.equals(action)) {
                onMessageReceived(intent);
            }
        }
    };

    private void onMessageReceived(Intent intent) {
        mView.addMessage(Parcels.<IPMessage>unwrap(intent.getParcelableExtra("ipmessage")));
    }

    public void getMessages() {
        Catapush.getInstance().getAllMessagesAsList(new Callback<List<IPMessage>>() {
            @Override
            public void success(List<IPMessage> messages) {
                mView.setMessages(messages);
            }

            @Override
            public void failure(Throwable throwable) {

            }
        });
    }
}
