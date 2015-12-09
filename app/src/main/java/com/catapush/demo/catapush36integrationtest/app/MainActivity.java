package com.catapush.demo.catapush36integrationtest.app;

import com.catapush.demo.catapush36integrationtest.app.messages.MessageFragment;
import com.catapush.library.Catapush;
import com.catapush.library.notifications.Notification;

import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements TitleChange {

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

        if (!Catapush.getInstance().isRunning()) {
            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            startCatapush(sound);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Our app is open and we don't want status bar notification in this scenario
        Catapush.getInstance().showNotificationPopup(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Our app is not visible and we want status bar notification in this scenario
        Catapush.getInstance().showNotificationPopup(true);
    }

    public void startCatapush(Uri sound) {
        Notification notification = Notification.builder()
            .isSwipeToDismissEnabled(false)
            .contentTitle("CATAPUSH TEST")
            .iconId(R.drawable.ic_stat_notify)
            .isVibrationEnabled(true)
            .vibrationPattern(new long[]{100, 200, 100, 300})
            .isSoundEnabled(true)
            .soundResourceUri(sound)
            .isLedEnabled(true)
            .ledColor(0xFFFF0000)
            .ledOnMS(2000)
            .ledOffMS(1000)
            .build();

        Catapush.getInstance()
            .setPush(notification)
            .setLogging(true)
            .start("", "");
    }

    @Override
    public void set(String title) {
        setTitle(title);
    }
}
