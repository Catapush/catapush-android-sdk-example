package com.catapush.android.example;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDexApplication;

import com.catapush.android.example.managers.SampleCatapushStateManager;
import com.catapush.library.Catapush;
import com.catapush.library.exceptions.CatapushApiException;
import com.catapush.library.exceptions.CatapushAuthenticationError;
import com.catapush.library.exceptions.CatapushConnectionError;
import com.catapush.library.exceptions.PushServicesException;
import com.catapush.library.gms.CatapushGms;
import com.catapush.library.interfaces.Callback;
import com.catapush.library.interfaces.ICatapushEventDelegate;
import com.catapush.library.messages.CatapushMessage;
import com.catapush.library.notifications.NotificationTemplate;
import com.catapush.library.push.models.PushPlatformType;
import com.catapush.library.ui.CatapushUi;
import com.google.android.gms.common.GoogleApiAvailability;
import com.huawei.hms.api.HuaweiApiAvailability;

import java.io.IOException;
import java.util.Collections;

import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;

public class SampleApplication extends MultiDexApplication {

    // Constant sting ID for your notification channel.
    // The value may be truncated if it's too long.
    // See https://developer.android.com/training/notify-user/channels
    public static final String NOTIFICATION_CHANNEL_ID = "com.catapush.example.app.MESSAGES";

    @Override
    public void onCreate() {
        super.onCreate();

        setupRxErrorHandler(); // Required

        ICatapushEventDelegate catapushEventDelegate = new ICatapushEventDelegate() {
            @Override
            public void onRegistrationFailed(@NonNull CatapushAuthenticationError error) {
                Log.e(SampleApplication.class.getSimpleName(), "Error Message: " + error);
                if (error.getCause() instanceof CatapushApiException &&
                        ((CatapushApiException) error.getCause()).getReasonCode() == CatapushApiException.API_INTERNAL_ERROR) {
                    SampleCatapushStateManager.INSTANCE.processStatus(SampleCatapushStateManager.Status.API_ERROR);
                } else {
                    SampleCatapushStateManager.INSTANCE.processStatus(SampleCatapushStateManager.Status.AUTH_FAILED);
                }
            }

            @Override
            public void onConnecting() {
                Log.d(SampleApplication.class.getSimpleName(), "Connecting...");
                SampleCatapushStateManager.INSTANCE.processStatus(SampleCatapushStateManager.Status.CONNECTING);
            }

            @Override
            public void onConnected() {
                Log.d(SampleApplication.class.getSimpleName(), "Connected");
                SampleCatapushStateManager.INSTANCE.processStatus(SampleCatapushStateManager.Status.CONNECTED);
            }

            @Override
            public void onDisconnected(@NonNull CatapushConnectionError error) {
                Log.d(SampleApplication.class.getSimpleName(), "Disconnected: " + error.getReasonCode());
                SampleCatapushStateManager.INSTANCE.processStatus(SampleCatapushStateManager.Status.DISCONNECTED);
            }

            @Override
            public void onPushServicesError(@NonNull PushServicesException e) {
                if (PushPlatformType.GMS.name().equals(e.getPlatform()) && e.isUserResolvable()) {
                    // It's a GMS error and it's user resolvable: show a notification to the user
                    Log.w(SampleApplication.class.getSimpleName(), "GMS error: " + e.getErrorMessage());
                    GoogleApiAvailability gmsAvailability = GoogleApiAvailability.getInstance();
                    gmsAvailability.setDefaultNotificationChannelId(
                            SampleApplication.this, SampleApplication.NOTIFICATION_CHANNEL_ID);
                    gmsAvailability.showErrorNotification(SampleApplication.this, e.getErrorCode());
                } else if (PushPlatformType.HMS.name().equals(e.getPlatform()) && e.isUserResolvable()) {
                    // It's a HMS error and it's user resolvable: show a notification to the user
                    Log.w(SampleApplication.class.getSimpleName(), "HMS error: " + e.getErrorMessage());
                    HuaweiApiAvailability hmsAvailability = HuaweiApiAvailability.getInstance();
                    hmsAvailability.showErrorNotification(SampleApplication.this, e.getErrorCode());
                }
            }

            @Override
            public void onMessageReceived(@NonNull CatapushMessage message) {
                Log.d(SampleApplication.class.getSimpleName(), "Received message: " + message);
            }

            @Override
            public void onMessageReceivedConfirmed(@NonNull CatapushMessage message) {
                Log.d(SampleApplication.class.getSimpleName(), "Received message confirmed: " + message);
            }

            @Override
            public void onMessageOpened(@NonNull CatapushMessage message) {
                Log.d(SampleApplication.class.getSimpleName(), "Opened message: " + message);
            }

            @Override
            public void onMessageOpenedConfirmed(@NonNull CatapushMessage message) {
                Log.d(SampleApplication.class.getSimpleName(), "Opened message confirmed: " + message);
            }

            @Override
            public void onMessageSent(@NonNull CatapushMessage message) {
                Log.d(SampleApplication.class.getSimpleName(), "Message marked as sent");
            }

            @Override
            public void onMessageSentConfirmed(@NonNull CatapushMessage message) {
                Log.d(SampleApplication.class.getSimpleName(), "Message sent and delivered");
            }
        };

        // This is the Android system notification channel that will be used by the Catapush SDK
        // to notify the incoming messages since Android 8.0. It is important that the channel
        // is created before starting Catapush.
        // See https://developer.android.com/training/notify-user/channels
        NotificationManager nm = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
        if (nm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            // Customize your notification appearance here (Android >= 8.0)
            // it's possible to customize a channel only on creation
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 100, 300});
            channel.enableLights(true);
            channel.setLightColor(ContextCompat.getColor(this, R.color.primary));
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT)
                            .build());
            nm.createNotificationChannel(channel);
        }

        // This is the notification template that the Catapush SDK uses to build
        // the status bar notification shown to the user.
        // Some settings like vibration, lights, etc. are duplicated here because
        // before Android introduced notification channels (Android < 8.0) the
        // styling was made on a per-notification basis.
        int primaryColor = ContextCompat.getColor(this, R.color.primary);
        final NotificationTemplate template = new NotificationTemplate.Builder(NOTIFICATION_CHANNEL_ID)
                .swipeToDismissEnabled(false)
                .title("Your notification title!")
                .iconId(R.drawable.ic_stat_notify_default)
                .vibrationEnabled(true)
                .vibrationPattern(new long[]{100, 200, 100, 300})
                .soundEnabled(true)
                .soundResourceUri(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .circleColor(primaryColor)
                .ledEnabled(true)
                .ledColor(primaryColor)
                .ledOnMS(2000)
                .ledOffMS(1000)
                .build();

        Catapush.getInstance()
                .setNotificationIntent((catapushMessage, context) -> {
                    Log.d(SampleApplication.class.getSimpleName(), "Notification tapped: " + catapushMessage);
                    // This is the Activity you want to open when a notification is tapped:
                    Intent intent = new Intent(context, MainActivity.class);
                    // This is a unique URI set to the Intent to avoid its recycling for different
                    // Notifications when it's set as PendingIntent in the NotificationManager.
                    // There's no need to provide a valid scheme or path, it just need to be unique.
                    intent.setData(Uri.parse("catapush://message/" + catapushMessage.id()));
                    intent.putExtra("message", catapushMessage);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    // This PendingIntent will be set as "ContentIntent" in the local notification
                    // shown to the user in the Android notifications UI and launched on tap
                    return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                })
                .init(
                        this,
                        catapushEventDelegate,
                        Collections.singletonList(CatapushGms.INSTANCE),
                        template, // This is the main/default notification channel
                        null, // If you'd like to support more than one notification channel, pass the templates here
                        new Callback<Boolean>() {
                            @Override
                            public void success(Boolean response) {
                                CatapushUi.INSTANCE.init();
                                Log.d(SampleApplication.class.getCanonicalName(), "Catapush has been successfully initialized");
                            }

                            @Override
                            public void failure(@NonNull Throwable t) {
                                Log.d(SampleApplication.class.getCanonicalName(), "Catapush initialization error: " + t.getMessage());
                            }
                        }
                );
    }

    // See https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling
    private void setupRxErrorHandler() {
        RxJavaPlugins.setErrorHandler(e -> {
            if (e instanceof UndeliverableException) {
                // The exception was wrapped
                e = e.getCause();
            }
            if (e instanceof IOException) {
                // Fine, irrelevant network problem or API that throws on cancellation
                return;
            }
            if (e instanceof InterruptedException) {
                // Fine, some blocking code was interrupted by a dispose call
                return;
            }
            if ((e instanceof NullPointerException) || (e instanceof IllegalArgumentException)) {
                // That's likely a bug in the application
                Thread.currentThread().getUncaughtExceptionHandler()
                        .uncaughtException(Thread.currentThread(), e);
                return;
            }
            if (e instanceof IllegalStateException) {
                // That's a bug in RxJava or in a custom operator
                Thread.currentThread().getUncaughtExceptionHandler()
                        .uncaughtException(Thread.currentThread(), e);
                return;
            }
            // Log this to your analytics platform
            Log.e(SampleApplication.class.getCanonicalName(), e.getMessage());
        });
    }
}
