package com.catapush.example.app;

import com.catapush.library.Catapush;
import com.catapush.library.interfaces.Callback;

import android.app.Application;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

public class MyApplication extends MultiDexApplication {

    private static final String NOTIFICATION_CHANNEL_ID = "your.app.channel_id";
    private static final String NOTIFICATION_CHANNEL_NAME = "your.app.channel_name";

    @Override
    public void onCreate() {
        super.onCreate();

        Catapush.getInstance().init(this, NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME,
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
}
