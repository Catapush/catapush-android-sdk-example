package com.catapush.example.app.communications;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.catapush.example.app.MainActivity;
import com.catapush.example.app.managers.SampleCatapushStateManager;
import com.catapush.library.CatapushTwoWayReceiver;
import com.catapush.library.exceptions.CatapushApiException;
import com.catapush.library.exceptions.CatapushAuthenticationError;
import com.catapush.library.messages.CatapushMessage;

public class SampleReceiver extends CatapushTwoWayReceiver {

    @Override
    public void onConnecting(@NonNull Context context) {
        Log.d(SampleReceiver.class.getCanonicalName(), "Connecting...");
        SampleCatapushStateManager.INSTANCE.processStatus(SampleCatapushStateManager.Status.CONNECTING);
    }

    @Override
    public void onConnected(@NonNull Context context) {
        Log.d(SampleReceiver.class.getCanonicalName(), "Connected");
        SampleCatapushStateManager.INSTANCE.processStatus(SampleCatapushStateManager.Status.CONNECTED);
    }

    @Override
    public void onMessageReceived(@NonNull CatapushMessage catapushMessage, @NonNull Context context) {
        Log.d(SampleReceiver.class.getCanonicalName(), "Received Message: " + catapushMessage);
    }

    @Override
    public void onMessageOpened(@NonNull CatapushMessage catapushMessage, @NonNull Context context) {
        Log.d(SampleReceiver.class.getCanonicalName(), "Opened Message: " + catapushMessage);
    }

    @Override
    public void onMessageSent(@NonNull CatapushMessage catapushMessage, @NonNull Context context) {
        Log.d(SampleReceiver.class.getCanonicalName(), "Message marked as sent");
    }

    @Override
    public void onMessageSentConfirmed(@NonNull CatapushMessage catapushMessage, @NonNull Context context) {
        Log.d(SampleReceiver.class.getCanonicalName(), "Message sent and delivered");
    }

    @Override
    public void onNotificationClicked(@NonNull CatapushMessage catapushMessage, @NonNull Context context) {
        Log.d(SampleReceiver.class.getCanonicalName(), "Notification clicked: " + catapushMessage);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    @Override
    public void onRegistrationFailed(@NonNull CatapushAuthenticationError error, @NonNull Context context) {
        Log.e(SampleReceiver.class.getCanonicalName(), "Error Message: " + error);
        if (error.getCause() instanceof CatapushApiException &&
                ((CatapushApiException) error.getCause()).getReasonCode() == CatapushApiException.API_INTERNAL_ERROR) {
            SampleCatapushStateManager.INSTANCE.processStatus(SampleCatapushStateManager.Status.API_ERROR);
        } else {
            SampleCatapushStateManager.INSTANCE.processStatus(SampleCatapushStateManager.Status.AUTH_FAILED);
        }
    }

    @Override
    public void onDisconnected(int errorCode, @NonNull Context context) {
        Log.d(SampleReceiver.class.getCanonicalName(), "Disconnected: " + errorCode);
        SampleCatapushStateManager.INSTANCE.processStatus(SampleCatapushStateManager.Status.DISCONNECTED);
    }

}
