package com.example.clock;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.example.clock.activities.MainActivity;
import com.example.clock.helpers.ScreenWakeup;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    private static final int ALARM_EXPIRED_NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        // hide a notification about a running alarm
        context.stopService(new Intent(context, AlarmNotificationService.class));

        // turn the screen on to show the notification
        ScreenWakeup.screenWakeUp(context);

        // show a notification about an expired alarm
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(
                context,
                1,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context,
                ChannelIdApp.CHANNEL_ID);
        builder.setAutoCancel(true)
                .setUsesChronometer(true)
                .setContentTitle("Clock")
                .setContentText("Alarm was expired!")
                .setTicker("Alarm")
                .setContentIntent(notificationPendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                .setSmallIcon(R.drawable.ic_stat_notify_clock);

        Notification notification = builder.build();
        notification.flags = notification.flags | Notification.FLAG_INSISTENT;

        if (notificationManager != null) {
            notificationManager.notify(ALARM_EXPIRED_NOTIFICATION_ID,  notification);
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "pref",
                Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("alarm_running", false).apply();
    }

}