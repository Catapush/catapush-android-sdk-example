package com.catapush.example.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDexApplication;

import com.catapush.library.Catapush;
import com.catapush.library.interfaces.Callback;
import com.catapush.library.notifications.NotificationTemplate;

import java.io.IOException;

import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;

public class MyApplication extends MultiDexApplication {

    // Constant sting ID for your notification channel.
    // The value may be truncated if it's too long.
    // See https://developer.android.com/training/notify-user/channels
    private static final String NOTIFICATION_CHANNEL_ID = "com.catapush.example.app.MESSAGES";

    @Override
    public void onCreate() {
        super.onCreate();

        setupRxErrorHandler(); // Required

        // This is the Android system notification channel that will be used by the Catapush SDK
        // to notify the incoming messages since Android 8.0. It is important that the channel
        // is created before starting Catapush.
        // See https://developer.android.com/training/notify-user/channels
        NotificationManager nm = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
        if (nm != null) {
            String channelName = getString(R.string.catapush_notification_channel_name);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = nm.getNotificationChannel(NOTIFICATION_CHANNEL_ID);
                boolean shouldCreateOrUpdate = (channel == null || !channelName.contentEquals(channel.getName()));
                if (shouldCreateOrUpdate) {
                    if (channel == null) {
                        channel = new NotificationChannel(
                                NOTIFICATION_CHANNEL_ID,
                                channelName,
                                NotificationManager.IMPORTANCE_HIGH);
                        // Customize your notification appearance here (Android >= 8.0)
                        channel.enableVibration(true);
                        channel.setVibrationPattern(new long[]{100, 200, 100, 300});
                        channel.enableLights(true);
                        channel.setLightColor(ContextCompat.getColor(this, R.color.primary));
                    }
                    nm.createNotificationChannel(channel);
                }
            }
        }

        // Required Catapush instance initialization, must be done in Application.onCreate()
        Catapush.getInstance().init(this, NOTIFICATION_CHANNEL_ID,
                new Callback<Boolean>() {
                    @Override
                    public void success(Boolean response) {
                        Log.d(MyApplication.class.getCanonicalName(), "Catapush has been successfully initialized");

                        // This is the notification template that the Catapush SDK uses to build
                        // the status bar notification shown to the user.
                        // Some settings like vibration, lights, etc. are duplicated here since
                        // before Android introduced notification channels (Android < 8.0) the
                        // styling was made on a per-notification basis.
                        final NotificationTemplate template = NotificationTemplate.builder()
                                .swipeToDismissEnabled(false)
                                .title("Your notification title!")
                                .vibrationEnabled(true)
                                .vibrationPattern(new long[]{100, 200, 100, 300})
                                .soundEnabled(true)
                                .soundResourceUri(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                                .circleColor(ContextCompat.getColor(MyApplication.this, R.color.primary))
                                .ledEnabled(true)
                                .ledColor(Color.BLUE)
                                .ledOnMS(2000)
                                .ledOffMS(1000)
                                .build();

                        Catapush.getInstance().setNotificationTemplate(template);
                    }

                    @Override
                    public void failure(@NonNull Throwable t) {
                        Log.d(MyApplication.class.getCanonicalName(), "Catapush initialization error: " + t.getMessage());
                    }
                });
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
            Log.e(MyApplication.class.getCanonicalName(), e.getMessage());
        });
    }
}
