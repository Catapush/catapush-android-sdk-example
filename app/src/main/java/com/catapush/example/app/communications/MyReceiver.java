package com.catapush.example.app.communications;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.catapush.example.app.MainActivity;
import com.catapush.library.CatapushTwoWayReceiver;
import com.catapush.library.exceptions.CatapushAuthenticationError;
import com.catapush.library.messages.CatapushMessage;

public class MyReceiver extends CatapushTwoWayReceiver {

    @Override
    public void onConnecting(@NonNull Context context) {
        Log.d(MyReceiver.class.getCanonicalName(), "Connecting...");
    }

    @Override
    public void onConnected(@NonNull Context context) {
        Log.d(MyReceiver.class.getCanonicalName(), "Connected");
    }

    @Override
    public void onMessageReceived(@NonNull CatapushMessage catapushMessage, @NonNull Context context) {
        Log.d(MyReceiver.class.getCanonicalName(), "Received Message: " + catapushMessage);
    }

    @Override
    public void onMessageOpened(@NonNull CatapushMessage catapushMessage, @NonNull Context context) {
        Log.d(MyReceiver.class.getCanonicalName(), "Opened Message: " + catapushMessage);
    }

    @Override
    public void onMessageSent(@NonNull CatapushMessage catapushMessage, @NonNull Context context) {
        Log.d(MyReceiver.class.getCanonicalName(), "Message marked as sent");
    }

    @Override
    public void onMessageSentConfirmed(@NonNull CatapushMessage catapushMessage, @NonNull Context context) {
        Log.d(MyReceiver.class.getCanonicalName(), "Message sent and delivered");
    }

    @Override
    public void onNotificationClicked(@NonNull CatapushMessage catapushMessage, @NonNull Context context) {
        Log.d(MyReceiver.class.getCanonicalName(), "Notification clicked: " + catapushMessage);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    @Override
    public void onRegistrationFailed(@NonNull CatapushAuthenticationError error, @NonNull Context context) {
        Log.e(MyReceiver.class.getCanonicalName(), "Error Message: " + error);
    }

    @Override
    public void onDisconnected(int errorCode, @NonNull Context context) {
        Log.d(MyReceiver.class.getCanonicalName(), "Disconnected: " + errorCode);
    }

}
