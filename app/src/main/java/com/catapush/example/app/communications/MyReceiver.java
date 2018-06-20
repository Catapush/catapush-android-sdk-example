package com.catapush.example.app.communications;

import com.catapush.example.app.MainActivity;
import com.catapush.library.CatapushTwoWayReceiver;
import com.catapush.library.exceptions.CatapushAuthenticationError;
import com.catapush.library.messages.CatapushMessage;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

public class MyReceiver extends CatapushTwoWayReceiver {

    @Override
    public void onConnecting(Context context) {
        Log.d(MyReceiver.class.getCanonicalName(), "Connecting...");
    }

    @Override
    public void onConnected(Context context) {
        Log.d(MyReceiver.class.getCanonicalName(), "Connected");
    }

    @Override
    public void onMessageReceived(@NonNull CatapushMessage catapushMessage, Context context) {
        Log.d(MyReceiver.class.getCanonicalName(), "Received Message: " + catapushMessage);
    }

    @Override
    public void onMessageOpened(@NonNull CatapushMessage catapushMessage, Context context) {
        Log.d(MyReceiver.class.getCanonicalName(), "Opened Message: " + catapushMessage);
    }

    @Override
    public void onMessageSent(@NonNull CatapushMessage catapushMessage, Context context) {
        Log.d(MyReceiver.class.getCanonicalName(), "Message marked as sent");
    }

    @Override
    public void onMessageSentConfirmed(@NonNull CatapushMessage catapushMessage, Context context) {
        Log.d(MyReceiver.class.getCanonicalName(), "Message sent and delivered");
    }

    @Override
    public void onNotificationClicked(@NonNull CatapushMessage catapushMessage, Context context) {
        Log.d(MyReceiver.class.getCanonicalName(), "Notification clicked: " + catapushMessage);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    @Override
    public void onReloginNotificationClicked(Context context) {
        Log.d(MyReceiver.class.getCanonicalName(), "Authentication lost. Need to relogin");
    }

    @Override
    public void onRegistrationFailed(@NonNull CatapushAuthenticationError error, Context context) {
        Log.e(MyReceiver.class.getCanonicalName(), "Error Message: " + error);
    }

    @Override
    public void onDisconnected(int errorCode, Context context) {
        Log.d(MyReceiver.class.getCanonicalName(), "Disconnected: " + errorCode);
    }
}
