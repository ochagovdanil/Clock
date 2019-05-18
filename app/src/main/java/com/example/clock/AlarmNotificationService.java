package com.example.clock;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.example.clock.activities.MainActivity;

public class AlarmNotificationService extends Service {

    private static final int ALARM_RUNNING_NOTIFICATION_ID = 3;
    private static final String CHANNEL_ID = "foreground_clock_channel_id";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Clock notifications (foreground alarm event)",
                    NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableVibration(false);
            notificationChannel.enableLights(false);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("alarm_notification_tab", true); // go to the alarm tab
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(
                this,
                3,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this,
                CHANNEL_ID);
        builder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Alarm was set [" + intent.getStringExtra("alarm_time") + "]")
                .setTicker("Alarm")
                .setContentIntent(notificationPendingIntent)
                .setSmallIcon(R.drawable.ic_stat_notify_alarm);

        startForeground(ALARM_RUNNING_NOTIFICATION_ID, builder.build());

        return START_REDELIVER_INTENT;
    }

}
