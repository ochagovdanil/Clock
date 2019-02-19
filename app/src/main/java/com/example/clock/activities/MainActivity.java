package com.example.clock.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.clock.R;
import com.example.clock.adapters.TabFragmentPagerAdapter;
import com.example.clock.fragments.AlarmFragment;
import com.example.clock.fragments.StopwatchFragment;
import com.example.clock.fragments.TimerFragment;
import com.example.clock.models.Tab;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_my);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        initTabs();
    }

    @Override
    protected void onResume() {
        super.onResume();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (getIntent().getBooleanExtra("timer_cancel_notification", false)) {
            if (notificationManager != null) {
                notificationManager.cancel(2);
            }
        }
        if (getIntent().getBooleanExtra("alarm_cancel_notification", false)) {
            if (notificationManager != null) {
                notificationManager.cancel(1);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_main_about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
            return true;
        }

        return false;
    }

    private void initTabs() {
        TabLayout tabLayout = findViewById(R.id.tab_layout_my);
        ViewPager viewPager = findViewById(R.id.view_pager_my);
        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getSupportFragmentManager());

        adapter.addNewTab(new Tab("Alarm", new AlarmFragment()));
        adapter.addNewTab(new Tab("Stopwatch", new StopwatchFragment()));
        adapter.addNewTab(new Tab("Timer", new TimerFragment()));

        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

}
