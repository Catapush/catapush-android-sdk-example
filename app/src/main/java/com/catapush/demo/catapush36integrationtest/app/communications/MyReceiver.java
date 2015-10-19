package com.catapush.demo.catapush36integrationtest.app.communications;

import com.catapush.demo.catapush36integrationtest.app.MainActivity;
import com.catapush.library.CatapushReceiver;
import com.catapush.library.storage.models.IPMessage;

import org.parceler.Parcels;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends CatapushReceiver {

    public MyReceiver() {
    }

    @Override
    public void onMessageReceived(IPMessage message, Context context) {
        Log.d("MyApp", "Received Message: " + message);

        Intent intent = new Intent(Actions.ACTION_IPMESSAGE_RECEIVED);
        intent.putExtra("ipmessage", Parcels.wrap(IPMessage.class, message));
        intent.addCategory(context.getPackageName());
        context.sendBroadcast(intent);
    }

    @Override
    public void onMessageOpened(IPMessage msg, Context context) {
        Log.d("MyApp", "Opened Message: " + msg);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    @Override
    public void onRegistrationFailed(int errorCode, Context context) {
        Log.e("MyApp", "Error code: " + errorCode);
    }

    @Override
    public void onConnected(Context context) {
        Log.d("MyApp", "Connection successful!");
    }

    @Override
    public void onConnecting(Context context) {
        Log.d("MyApp", "Reconnection attempt..");
    }

    @Override
    public void onDisconnected(int errorCode, Context context) {
        Log.d("MyApp", "Disconnection due: " + errorCode);
    }
}