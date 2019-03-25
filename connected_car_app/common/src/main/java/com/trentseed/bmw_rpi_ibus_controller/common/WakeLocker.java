package com.trentseed.bmw_rpi_ibus_controller.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;

import java.lang.NullPointerException;

/** 
 * This class handles device sleep/wake lock features. Utilized
 * by GCMIntentService.
 */
public abstract class WakeLocker {
	
    private static PowerManager.WakeLock wakeLock;
    private final static String tag = "app:WakeLock";
 
    /** 
     * Acquire handle and wake device
     * @param context
     */
	@SuppressWarnings("deprecation")
	@SuppressLint("Wakelock")
	public static void acquire(Context context) {
        if (wakeLock != null) release();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        try {
            wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                    PowerManager.ON_AFTER_RELEASE, tag);
            wakeLock.acquire();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }
 
    /** 
     * Release handle, allowing device to sleep
     */
    private static void release() {
        if (wakeLock != null){
        	wakeLock.release(); 
        	wakeLock = null;
        }
    }
}