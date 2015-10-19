package com.catapush.demo.catapush36integrationtest.app;

import com.catapush.demo.catapush36integrationtest.app.messages.MessageFragment;
import com.catapush.library.Catapush;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MessageFragment())
                    .commit();
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
}
