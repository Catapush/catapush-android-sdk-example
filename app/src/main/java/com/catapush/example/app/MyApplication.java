package com.catapush.example.app;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.catapush.library.Catapush;
import com.catapush.library.interfaces.Callback;

import java.io.IOException;

import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;

public class MyApplication extends MultiDexApplication {

    // Constant sting ID for your notification channel.
    // The value may be truncated if it's too long.
    // See https://developer.android.com/training/notify-user/channels
    private static final String NOTIFICATION_CHANNEL_ID = "your.app.channel_id";

    @Override
    public void onCreate() {
        super.onCreate();

        setupRxErrorHandler(); // Required

        // Localized channel name, visible in the device system settings of your app, under
        // notification preferences.
        // The recommended maximum length is 40 characters.
        // See https://developer.android.com/training/notify-user/channels
        final String channelName = getString(R.string.catapush_notification_channel_name);

        // Required Catapush instance initialization
        Catapush.getInstance().init(this, NOTIFICATION_CHANNEL_ID, channelName,
                new Callback<Boolean>() {
                    @Override
                    public void success(Boolean response) {
                        Log.d(MyApplication.class.getCanonicalName(), "Catapush has been successfully initialized");
                    }

                    @Override
                    public void failure(Throwable t) {

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
