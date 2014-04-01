package com.trentseed.bmw_rpi_ibus_controller;

import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;

/**
 * Navigation class handles core communication with Raspberry Pi.
 * @author Trent
 *
 */
public class IBUSWrapper {

	// supported commands
	public static final String COMMAND_TRACK_NEXT = "TRACK_NEXT";
	public static final String COMMAND_TRACK_PREV = "TRACK_PREV";
	public static final String COMMAND_MODE = "MODE";
	public static final String COMMAND_RADIO_FM = "FM";
	public static final String COMMAND_RADIO_AM = "AM";
	public static final String COMMAND_RADIO_PRESET = "PRESET";
	
	/**
	 * Sends a command to the RaspberryPi
	 * @param command
	 * @return
	 */
	public static boolean sendCommand(String command){
		return false;
	}
	
	/**
	 * Processes a received command from RaspberryPi
	 * @param command
	 */
	public static void processCommand(String command){
		
	}
	
	/**
	 * Advance to next audio track (i.e. Google Music)
	 * @param thisActivity
	 */
	public static void nextTrack(Activity thisActivity){
		Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
		synchronized (thisActivity) {
		            i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT));
		            thisActivity.sendOrderedBroadcast(i, null);

		            i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT));
		            thisActivity.sendOrderedBroadcast(i, null);
		 }
	}
	
}
