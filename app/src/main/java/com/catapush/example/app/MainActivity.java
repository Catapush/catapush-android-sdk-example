package com.catapush.example.app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.catapush.example.app.managers.SampleCatapushStateManager;
import com.catapush.example.app.messages.MessageFragment;
import com.catapush.library.Catapush;
import com.catapush.library.interfaces.RecoverableErrorCallback;
import com.catapush.library.messages.CatapushMessage;

public class MainActivity
        extends AppCompatActivity
        implements MessageContract.MainView, SampleCatapushStateManager.CatapushStarter {

    private static final int REQUEST_FILE_PICKER = 2021;

    private MessagePresenter presenter;

    public void setPresenter(@Nullable MessagePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MessageFragment())
                    .commit();
        }

        SampleCatapushStateManager.INSTANCE.init(this);

        if (getIntent().hasExtra("message")) {
            handleCatapushMessageIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.hasExtra("message")) {
            handleCatapushMessageIntent(intent);
        } else {
            super.onNewIntent(intent);
        }
    }

    private void handleCatapushMessageIntent(Intent intent) {
        CatapushMessage message = intent.getParcelableExtra("message");
        Log.d(MainActivity.class.getSimpleName(), "Notification tapped event received for message: " + message);
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
    public void setViewTitle(String title) {
        setTitle(title);
    }

    @Override
    public void openFilePicker(@NonNull String mimeType) {
        final Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] { mimeType });
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        try {
            startActivityForResult(intent, REQUEST_FILE_PICKER);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No picker apps available.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FILE_PICKER) {
            if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                Uri uri = data.getData();
                Exception e = null;
                try {
                    getContentResolver().takePersistableUriPermission(
                            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (SecurityException se) {
                    e = se;
                }
                if (e == null && presenter != null) {
                    presenter.onAttachmentPicked(uri);
                    return; // Success!
                }
            }
            // Error!
            Toast.makeText(this, "Pick action failed!", Toast.LENGTH_LONG).show();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
