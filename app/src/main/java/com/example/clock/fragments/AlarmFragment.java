package com.example.clock.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.clock.AlarmManagerBroadcastReceiver;
import com.example.clock.AlarmNotificationService;
import com.example.clock.R;

import java.util.Calendar;
import java.util.Locale;

public class AlarmFragment extends Fragment {

    private Button mButtonStart, mButtonStop;
    private TextView mTextAlarm;
    private AlarmManager mAlarmManager;
    private TimePickerDialog mTimePickerDialog;
    private Calendar mCalendar;
    private SharedPreferences mSharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alarm, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mButtonStart = getActivity().findViewById(R.id.button_start_alarm);
        mButtonStop = getActivity().findViewById(R.id.button_stop_alarm);
        mTextAlarm = getActivity().findViewById(R.id.text_alarm);
        mAlarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        mSharedPreferences = getContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
        mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(mSharedPreferences.getLong("alarm_time", mCalendar.getTimeInMillis()));

        updateTextAlarmTime();
        playBehaviourOfButtons();
        setTime();
    }

    @Override
    public void onResume() {
        super.onResume();

        // change style of buttons
        if (mSharedPreferences.getBoolean("alarm_running", false)) {
            mButtonStart.setVisibility(View.GONE);
            mButtonStop.setVisibility(View.VISIBLE);
        } else {
            mButtonStart.setVisibility(View.VISIBLE);
            mButtonStop.setVisibility(View.GONE);
        }
    }

    private void playBehaviourOfButtons() {
        mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButtonStart.setVisibility(View.GONE);
                mButtonStop.setVisibility(View.VISIBLE);

                startAlarm();
            }
        });
        mButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButtonStart.setVisibility(View.VISIBLE);
                mButtonStop.setVisibility(View.GONE);

                stopAlarm();
            }
        });
    }

    private void setTime() {
        mTextAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set new time for alarm
                if (!mSharedPreferences.getBoolean("alarm_running", false)) {
                    mTimePickerDialog = new TimePickerDialog(
                            getContext(),
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    mCalendar.set(Calendar.MINUTE, minute);

                                    updateTextAlarmTime();
                                }
                            },
                            mCalendar.get(Calendar.HOUR_OF_DAY),
                            mCalendar.get(Calendar.MINUTE),
                            true);

                    mTimePickerDialog.show();
                }
            }
        });
    }

    private void startAlarm() {
        // if the selected time is less than the current time we have to set alarm on the next day
        if (mCalendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
            mCalendar.setTimeInMillis(mCalendar.getTimeInMillis() + 86_400_000L);
        }

        // start an alarm
        Intent intent = new Intent(getContext(), AlarmManagerBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mAlarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    mCalendar.getTimeInMillis(),
                    pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mAlarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    mCalendar.getTimeInMillis(), 
                    pendingIntent);
        } else {
            mAlarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    mCalendar.getTimeInMillis(),
                    pendingIntent);
        }

        // create a notification about a running alarm
        Intent intentNotification = new Intent(getContext(), AlarmNotificationService.class);
        intentNotification.putExtra("alarm_time" , String.format(
                Locale.getDefault(),
                "%02d:%02d",
                mCalendar.get(Calendar.HOUR_OF_DAY),
                mCalendar.get(Calendar.MINUTE)));
        getActivity().startService(intentNotification);

        mSharedPreferences.edit().putBoolean("alarm_running", true).apply();
        mSharedPreferences.edit().putLong("alarm_time", mCalendar.getTimeInMillis()).apply();
    }

    private void stopAlarm() {
        // cancel the current alarm
        Intent intent = new Intent(getContext(), AlarmManagerBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mAlarmManager.cancel(pendingIntent);

        // hide a notification about a running alarm
        getActivity().stopService(new Intent(getContext(), AlarmNotificationService.class));

        mSharedPreferences.edit().putBoolean("alarm_running", false).apply();
    }

    private void updateTextAlarmTime() {
        mTextAlarm.setText(String.format(
                Locale.getDefault(),
                "%02d:%02d",
                mCalendar.get(Calendar.HOUR_OF_DAY),
                mCalendar.get(Calendar.MINUTE)));
    }

}
