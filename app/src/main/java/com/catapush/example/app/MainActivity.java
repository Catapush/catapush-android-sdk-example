package com.catapush.example.app;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.catapush.example.app.managers.SampleCatapushStateManager;
import com.catapush.example.app.messages.MessageFragment;
import com.catapush.library.Catapush;
import com.catapush.library.interfaces.RecoverableErrorCallback;

public class MainActivity
        extends AppCompatActivity
        implements TitleChange, SampleCatapushStateManager.CatapushStarter {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            MessageFragment messageFragment = new MessageFragment();
            messageFragment.setTitleChanger(this);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, messageFragment)
                    .commit();
        }

        SampleCatapushStateManager.INSTANCE.init(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Our app is open and we don't want status bar notification in this scenario
        Catapush.getInstance().pauseNotifications();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Our app is not visible and we want status bar notification in this scenario
        Catapush.getInstance().resumeNotifications();
    }

    @Override
    protected void onDestroy() {
        SampleCatapushStateManager.INSTANCE.dispose();
        super.onDestroy();
    }

    public void startCatapush() {
        // TODO Add your user configuration in strings.xml file
        Catapush.getInstance()
                .setUser(getString(R.string.catapush_username), getString(R.string.catapush_password))
                .start(new RecoverableErrorCallback<Boolean>() {
                    @Override
                    public void success(Boolean aBoolean) {
                        Log.d(MainActivity.class.getCanonicalName(), "Catapush has been successfully started");
                    }
                    @Override
                    public void warning(@NonNull Throwable throwable) {
                        Log.d(MainActivity.class.getCanonicalName(), "Catapush warning: " + throwable.getMessage());
                    }
                    @Override
                    public void failure(@NonNull Throwable throwable) {
                        Log.d(MainActivity.class.getCanonicalName(), "Catapush can't be started: " + throwable.getMessage());
                    }
                });
    }

    @Override
    public void set(String title) {
        setTitle(title);
    }
}
