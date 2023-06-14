package com.catapush.android.example;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.catapush.android.example.managers.SampleCatapushStateManager;
import com.catapush.android.example.messages.MessageFragment;
import com.catapush.library.Catapush;
import com.catapush.library.interfaces.RecoverableErrorCallback;
import com.catapush.library.messages.CatapushMessage;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity
        extends AppCompatActivity
        implements MessageContract.MainView, SampleCatapushStateManager.CatapushStarter {

    private static final int REQUEST_FILE_PICKER = 2021;

    private MessagePresenter presenter;

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), result -> {
                if (result) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }
    );

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
        // Start monitoring Catapush status to keep it connected
        SampleCatapushStateManager.INSTANCE.init(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            showPostNotificationsPermissionDialog();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Our app is not visible and we want status bar notification in this scenario
        Catapush.getInstance().resumeNotifications();
        // Stop to monitor the Catapush status to avoid starting it while in the background
        SampleCatapushStateManager.INSTANCE.dispose();
    }

    public void startCatapush() {
        // TODO Add your user configuration in strings.xml file
        Catapush.getInstance()
                .setUser(getString(R.string.catapush_username), getString(R.string.catapush_password))
                .start(new RecoverableErrorCallback<Boolean>() {
                    @Override
                    public void success(Boolean aBoolean) {
                        Log.d(MainActivity.class.getCanonicalName(), "Catapush is starting");
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

    @TargetApi(Build.VERSION_CODES.TIRAMISU)
    private void showPostNotificationsPermissionDialog() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.post_notifications_title))
                    .setMessage(getString(R.string.post_notifications_message));

            builder.setPositiveButton(getString(R.string.post_notifications_settings_action), (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                dialog.dismiss();
            });

            builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.dismiss());

            builder.show();
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // The registered ActivityResultCallback gets the result of this request
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

}
