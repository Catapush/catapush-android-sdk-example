package com.catapush.demo.catapush36integrationtest.app;

import com.catapush.library.Catapush;
import com.catapush.library.interfaces.Callback;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Catapush.init(this, new Callback<Boolean>() {
            @Override
            public void success(Boolean result) {
                Log.d("MyApp", "Catapush has been initialized");
            }

            @Override
            public void failure(Throwable t) {

            }
        });
    }
}
