package com.catapush.example.app.communications;

import com.catapush.example.app.MainActivity;
import com.catapush.library.CatapushReceiver;
import com.catapush.library.exceptions.CatapushAuthenticationError;
import com.catapush.library.messages.CatapushMessage;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends CatapushReceiver {

    @Override
    public void onConnected(Context context) {
        Log.d("MyApp", "Connected");
    }

    @Override
    public void onMessageReceived(CatapushMessage catapushMessage, Context context) {
        Log.d("MyApp", "Received Message: " + catapushMessage);

        Intent intent = new Intent(Actions.ACTION_IPMESSAGE_RECEIVED);
        intent.putExtra("ipmessage", catapushMessage);
        intent.addCategory(context.getPackageName());
        context.sendBroadcast(intent);
    }

    @Override
    public void onMessageOpened(CatapushMessage catapushMessage, Context context) {
        Log.d("MyApp", "Opened Message: " + catapushMessage);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    @Override
    public void onNotificationClicked(CatapushMessage catapushMessage, Context context) {
        Log.d("MyApp", "Notification clicked: " + catapushMessage);
    }

    @Override
    public void onRegistrationFailed(CatapushAuthenticationError error, Context context) {
        Log.e("MyApp", "Error Message: " + error.getReason());
    }

    @Override
    public void onDisconnected(int errorCode, Context context) {
        Log.d("MyApp", "Disconnected: " + errorCode);
    }

    @Override
    public void onConnecting(Context context) {
        Log.d("MyApp", "Connecting...");
    }
}
