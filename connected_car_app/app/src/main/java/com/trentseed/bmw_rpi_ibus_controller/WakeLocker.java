package com.trentseed.bmw_rpi_ibus_controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;

/** 
 * This class handles device sleep/wake lock features. Utilized
 * by GCMIntentService.
 */
public abstract class WakeLocker {
	
	//core variables
    private static PowerManager.WakeLock wakeLock;
    private final static String tag = "WakeLock";
 
    /** 
     * Acquire handle and wake device
     * @param context
     */
	@SuppressWarnings("deprecation")
	@SuppressLint("Wakelock")
	public static void acquire(Context context) {
        if (wakeLock != null) wakeLock.release();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, tag);
        wakeLock.acquire();
    }
 
    /** 
     * Release handle, allowing device to sleep
     */
    public static void release() {
        if (wakeLock != null){
        	wakeLock.release(); 
        	wakeLock = null;
        }
    }
}