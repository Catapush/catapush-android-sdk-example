package com.catapush.demo.catapush36integrationtest.app.messages;

import com.catapush.demo.catapush36integrationtest.app.communications.Actions;
import com.catapush.demo.catapush36integrationtest.app.R;
import com.catapush.library.Catapush;
import com.catapush.library.notifications.Notification;
import com.catapush.library.storage.models.IPMessage;

import org.parceler.Parcels;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;

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
        mView.setMessage(Parcels.<IPMessage>unwrap(intent.getParcelableExtra("ipmessage")));
    }

    public void startCatapush(Uri sound) {
        Notification notification = Notification.builder()
                .isSwipeToDismissEnabled(false)
                .contentTitle("CATAPUSH TEST")
                .iconId(R.drawable.ic_stat_notify)
                .isVibrationEnabled(true)
                .vibrationPattern(new long[]{100, 200, 100, 300})
                .isSoundEnabled(true)
                .soundResourceUri(sound)
                .isLedEnabled(true)
                .ledColor(0xFFFF0000)
                .ledOnMS(2000)
                .ledOffMS(1000)
                .build();

        Catapush.getInstance()
                .setPush(notification)
                .setLogging(true)
                .start("test-android", "test-android");
    }
}
