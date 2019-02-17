package com.example.clock.helpers;

import android.content.Context;
import android.os.Build;
import android.os.PowerManager;

public class ScreenWakeup {

    public static void screenWakeUp(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

            if (!powerManager.isInteractive()){
                PowerManager.WakeLock wl = powerManager.newWakeLock(
                        PowerManager.FULL_WAKE_LOCK |
                                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                                PowerManager.ON_AFTER_RELEASE,
                        "com.example.clock:screen_lock");
                wl.acquire(10_000);
                PowerManager.WakeLock wl_cpu = powerManager.newWakeLock(
                        PowerManager.PARTIAL_WAKE_LOCK,
                        "com.example.clock:screen_lock");
                wl_cpu.acquire(10_000);
            }
        }
    }

}
