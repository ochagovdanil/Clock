package com.example.clock;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

public class ChannelIdApp extends Application {

    public static final String CHANNEL_ID = "clock_channel_id";

    @Override
    public void onCreate() {
        super.onCreate();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Clock notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

}
