package com.catapush.example.app.communications;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.catapush.example.app.SampleApplication;
import com.catapush.example.app.managers.SampleCatapushStateManager;
import com.catapush.library.CatapushTwoWayReceiver;
import com.catapush.library.exceptions.CatapushApiException;
import com.catapush.library.exceptions.CatapushAuthenticationError;
import com.catapush.library.exceptions.PushServicesException;
import com.catapush.library.messages.CatapushMessage;
import com.catapush.library.push.models.PushPlatformType;
import com.google.android.gms.common.GoogleApiAvailability;
import com.huawei.hms.api.HuaweiApiAvailability;

public class SampleReceiver extends CatapushTwoWayReceiver {

    @Override
    public void onConnecting(@NonNull Context context) {
        Log.d(SampleReceiver.class.getSimpleName(), "Connecting...");
        SampleCatapushStateManager.INSTANCE.processStatus(SampleCatapushStateManager.Status.CONNECTING);
    }

    @Override
    public void onConnected(@NonNull Context context) {
        Log.d(SampleReceiver.class.getSimpleName(), "Connected");
        SampleCatapushStateManager.INSTANCE.processStatus(SampleCatapushStateManager.Status.CONNECTED);
    }

    @Override
    public void onMessageReceived(@NonNull CatapushMessage catapushMessage, @NonNull Context context) {
        Log.d(SampleReceiver.class.getSimpleName(), "Received Message: " + catapushMessage);
    }

    @Override
    public void onMessageOpened(@NonNull CatapushMessage catapushMessage, @NonNull Context context) {
        Log.d(SampleReceiver.class.getSimpleName(), "Opened Message: " + catapushMessage);
    }

    @Override
    public void onMessageSent(@NonNull CatapushMessage catapushMessage, @NonNull Context context) {
        Log.d(SampleReceiver.class.getSimpleName(), "Message marked as sent");
    }

    @Override
    public void onMessageSentConfirmed(@NonNull CatapushMessage catapushMessage, @NonNull Context context) {
        Log.d(SampleReceiver.class.getSimpleName(), "Message sent and delivered");
    }

    @Override
    public void onRegistrationFailed(@NonNull CatapushAuthenticationError error, @NonNull Context context) {
        Log.e(SampleReceiver.class.getSimpleName(), "Error Message: " + error);
        if (error.getCause() instanceof CatapushApiException &&
                ((CatapushApiException) error.getCause()).getReasonCode() == CatapushApiException.API_INTERNAL_ERROR) {
            SampleCatapushStateManager.INSTANCE.processStatus(SampleCatapushStateManager.Status.API_ERROR);
        } else {
            SampleCatapushStateManager.INSTANCE.processStatus(SampleCatapushStateManager.Status.AUTH_FAILED);
        }
    }

    @Override
    public void onDisconnected(int errorCode, @NonNull Context context) {
        Log.d(SampleReceiver.class.getSimpleName(), "Disconnected: " + errorCode);
        SampleCatapushStateManager.INSTANCE.processStatus(SampleCatapushStateManager.Status.DISCONNECTED);
    }

    @Override
    public void onPushServicesError(@NonNull PushServicesException e, @NonNull Context context) {
        if (PushPlatformType.GMS.name().equals(e.getPlatform()) && e.isUserResolvable()) {
            // It's a GMS error and it's user resolvable: show a notification to the user
            Log.w(SampleReceiver.class.getSimpleName(), "GMS error: " + e.getErrorMessage());
            GoogleApiAvailability gmsAvailability = GoogleApiAvailability.getInstance();
            gmsAvailability.setDefaultNotificationChannelId(
                    context, SampleApplication.NOTIFICATION_CHANNEL_ID);
            gmsAvailability.showErrorNotification(context, e.getErrorCode());
        } else if (PushPlatformType.HMS.name().equals(e.getPlatform()) && e.isUserResolvable()) {
            // It's a HMS error and it's user resolvable: show a notification to the user
            Log.w(SampleReceiver.class.getSimpleName(), "HMS error: " + e.getErrorMessage());
            HuaweiApiAvailability hmsAvailability = HuaweiApiAvailability.getInstance();
            hmsAvailability.showErrorNotification(context, e.getErrorCode());
        }
    }

}
