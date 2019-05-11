package com.example.clock.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.clock.R;
import com.example.clock.adapters.TabFragmentPagerAdapter;
import com.example.clock.fragments.AlarmFragment;
import com.example.clock.fragments.StopwatchFragment;
import com.example.clock.fragments.TimerFragment;
import com.example.clock.models.Tab;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_my);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        initTabs();

        onNewIntent(getIntent()); // if the activity was destroyed
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // cancel notifications
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (intent.getBooleanExtra("timer_cancel_notification", false)) {
            if (notificationManager != null) {
                notificationManager.cancel(2);
                viewPager.setCurrentItem(2); // go to the timer tab
                
            }
        }
        if (intent.getBooleanExtra("alarm_cancel_notification", false)) {
            if (notificationManager != null) {
                notificationManager.cancel(1);
                viewPager.setCurrentItem(0); // go to the alarm tab
            }
        }
        if (intent.getBooleanExtra("alarm_notification_tab", false)) {
            viewPager.setCurrentItem(0); // go to the alarm tab
        }

        // shortcut actions
        if (intent.getStringExtra("shortcut_action") != null) {
            switch (intent.getStringExtra("shortcut_action")) {
                case "alarm":
                    viewPager.setCurrentItem(0);
                    break;

                case "stopwatch":
                    viewPager.setCurrentItem(1);
                    break;

                case "timer":
                    viewPager.setCurrentItem(2);
            }
        }
    }

    private void initTabs() {
        TabLayout tabLayout = findViewById(R.id.tab_layout_my);
        viewPager = findViewById(R.id.view_pager_my);
        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getSupportFragmentManager());

        adapter.addNewTab(new Tab("Alarm", new AlarmFragment()));
        adapter.addNewTab(new Tab("Stopwatch", new StopwatchFragment()));
        adapter.addNewTab(new Tab("Timer", new TimerFragment()));

        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

}
