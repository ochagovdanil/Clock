package com.example.clock.fragments;

import android.animation.ObjectAnimator;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.clock.ChannelIdApp;
import com.example.clock.R;
import com.example.clock.activities.MainActivity;
import com.example.clock.helpers.ScreenWakeup;
import com.example.clock.ui.ViewAnimations;

import java.util.Locale;

public class TimerFragment extends Fragment {

    private static final int TIMER_EXPIRED_NOTIFICATION_ID = 2;

    private static boolean sRunning = false;
    private static long sStartTime = 60_000; // 1 minute by default
    private static long sTimeLeft = sStartTime;

    private Button mButtonStart, mButtonStopResume, mButtonReset;
    private TextView mTextTimer;
    private CountDownTimer mCountDownTimer;
    private ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timer, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mButtonReset = getActivity().findViewById(R.id.button_reset_timer);
        mButtonStart = getActivity().findViewById(R.id.button_start_timer);
        mButtonStopResume = getActivity().findViewById(R.id.button_stop_resume_timer);
        mTextTimer = getActivity().findViewById(R.id.text_timer);
        mProgressBar = getActivity().findViewById(R.id.progress_bar_timer);

        playBehaviourOfButtons();
        setTime();
        updateTimerText();
    }

    private void playBehaviourOfButtons() {
        mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start timer
                mButtonStart.setVisibility(View.GONE);
                mButtonStopResume.setVisibility(View.VISIBLE);
                mButtonStopResume.setTextColor(getResources().getColor(R.color.colorRed));
                mButtonReset.setVisibility(View.VISIBLE);
                ViewAnimations.divergeTwoViewsHorizontal(
                        getContext(),
                        mButtonStopResume,
                        mButtonReset);

                startTimer();
            }
        });
        mButtonStopResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mButtonStopResume.getText().toString().equals("Resume")) {
                    // resume timer
                    mButtonStopResume.setText(R.string.button_stop);
                    mButtonStopResume.setTextColor(getResources().getColor(R.color.colorRed));

                    startTimer();
                } else {
                    // pause timer
                    mButtonStopResume.setText(R.string.button_resume);
                    mButtonStopResume.setTextColor(getResources().getColor(R.color.colorGreen));

                    pauseTimer();
                }
            }
        });
        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // reset timer
                ViewAnimations.comeCloserTwoViewsHorizontal(
                        getContext(),
                        mButtonStopResume,
                        mButtonReset);
                mButtonStart.setVisibility(View.VISIBLE);
                mButtonStopResume.setVisibility(View.GONE);
                mButtonStopResume.setText(R.string.button_stop);
                mButtonStopResume.setTextColor(getResources().getColor(R.color.colorRed));
                mButtonReset.setVisibility(View.GONE);

                resetTimer();
            }
        });
    }

    // set new time for timer via the alert dialog
    private void setTime() {
        mTextTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sRunning && mButtonStart.getVisibility() == View.VISIBLE) {
                    Bundle bundle = new Bundle();
                    bundle.putLong("timer", sStartTime);
                    TimerDialogFragment dialog = new TimerDialogFragment();
                    dialog.setArguments(bundle);

                    dialog.show(getFragmentManager(), "TimerDialogFragment");

                    dialog.setPositiveButtonClickListener(
                        new TimerDialogFragment.OnPositiveButtonClickListener() {
                            @Override
                            public void onPositiveClickListener(long fullTime) {
                                sStartTime = sTimeLeft = fullTime;
                                updateTimerText();
                            }
                        });
                }
            }
        });
    }

    private void startTimer() {
        if (!sRunning) {
            mProgressBar.setMax((int) sStartTime);
            mProgressBar.setProgress((int) sTimeLeft);

            mCountDownTimer = new CountDownTimer(sTimeLeft, 1_000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // play the animation for the progress bar
                    ObjectAnimator objectAnimator = ObjectAnimator.ofInt(
                            mProgressBar,
                            "progress",
                            mProgressBar.getProgress(),
                            (int) millisUntilFinished);
                    objectAnimator.setDuration(300);
                    objectAnimator.setInterpolator(new LinearInterpolator());
                    objectAnimator.start();

                    mProgressBar.setProgress((int) millisUntilFinished);

                    sTimeLeft = millisUntilFinished;
                    updateTimerText();
                }

                @Override
                public void onFinish() {
                    sTimeLeft = sStartTime = 60_000;
                    sRunning = false;
                    mProgressBar.setMax((int) sStartTime);
                    mProgressBar.setProgress((int) sStartTime);
                    ViewAnimations.comeCloserTwoViewsHorizontal(
                            getContext(),
                            mButtonStopResume,
                            mButtonReset);
                    mButtonStart.setVisibility(View.VISIBLE);
                    mButtonStopResume.setVisibility(View.GONE);
                    mButtonStopResume.setText(R.string.button_stop);
                    mButtonStopResume.setTextColor(getResources().getColor(R.color.colorRed));
                    mButtonReset.setVisibility(View.GONE);
                    updateTimerText();

                    showNotificationAboutExpired();
                }
            }.start();

            sRunning = true;
        }
    }

    private void pauseTimer() {
        if (sRunning) {
            mCountDownTimer.cancel();
            sRunning = false;
        }
    }

    private void resetTimer() {
        mCountDownTimer.cancel();
        sTimeLeft = sStartTime = 60_000;
        sRunning = false;
        updateTimerText();
        mProgressBar.setMax((int) sStartTime);
        mProgressBar.setProgress((int) sStartTime);
    }

    private void updateTimerText() {
        int seconds = (int) sTimeLeft / 1_000 % 60;
        int minutes = (int) sTimeLeft / 1_000 / 60 % 60;
        int hours = (int) sTimeLeft / 1_000 / 60 / 60 % 24;

        String timeLeftFormatted = String.format(
                Locale.getDefault(),
                "%02d:%02d:%02d",
                hours,
                minutes,
                seconds);

        mTextTimer.setText(timeLeftFormatted);
    }

    private void showNotificationAboutExpired() {
        // turn the screen on to show the notification
        ScreenWakeup.screenWakeUp(getContext());

        // create the channel id
        NotificationManager notificationManager =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(getContext(), MainActivity.class);
        notificationIntent.putExtra("timer_cancel_notification", true);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(
                getContext(),
                2,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteViews = new RemoteViews(
                getContext().getPackageName(),
                R.layout.partial_notification);
        remoteViews.setTextViewText(
                R.id.cancel_button_notification,
                getString(R.string.timer_notification));
        remoteViews.setOnClickPendingIntent(
                R.id.cancel_button_notification,
                notificationPendingIntent);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getContext(),
                ChannelIdApp.CHANNEL_ID);
        builder.setAutoCancel(true)
                .setCustomContentView(remoteViews)
                .setTicker("Timer")
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.drawable.ic_stat_notify_timer);

        Notification notification = builder.build();
        notification.flags = notification.flags | Notification.FLAG_INSISTENT;

        if (notificationManager != null) {
            notificationManager.notify(TIMER_EXPIRED_NOTIFICATION_ID, notification);
        }
    }

}