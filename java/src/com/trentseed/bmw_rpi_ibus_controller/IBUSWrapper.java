package com.trentseed.bmw_rpi_ibus_controller;

import java.io.IOException;

import com.google.gson.Gson;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

/**
 * Navigation class handles core communication with Raspberry Pi.
 * @author Trent
 *
 */
public class IBUSWrapper {

	// bluetooth proprietary commands
	public static final String COMMAND_TRACK_NEXT = "NEXT";

	// bmw device identifiers
	public static final String DEVICE_BROADCAST_00 = "00";
	public static final String DEVICE_CDW_CD_PLAYER = "18";
	public static final String DEVICE_NAVIGATION_VIDEO_MODULE = "3b";
	public static final String DEVICE_MENU_SCREEN = "43";
	public static final String DEVICE_MFL_STEERING_WHEEL_BTNS = "50";
	public static final String DEVICE_PDC_PARK_DISTANCE_CTRL = "60";
	public static final String DEVICE_RADIO = "68";
	public static final String DEVICE_DSP_DIGITAL_SOUND_PROCESSOR = "6a";
	public static final String DEVICE_IKE_KOMBI_ELECTRONICS = "80";
	public static final String DEVICE_TV_MODULE = "bb";
	public static final String DEVICE_LCM_LIGHT_CONTROL_MODULE = "bf";
	public static final String DEVICE_MID_MULTI_INFO_DISP_BTNS = "c0";
	public static final String DEVICE_TELEPHONE = "c8";
	public static final String DEVICE_NAVIGATION_LOCATION = "d0";
	public static final String DEVICE_OBC_TEXT_BAR = "e7";
	public static final String DEVICE_LIGHTS_WIPERS_SEATS = "ed";
	public static final String DEVICE_BMB_BOARD_MONITOR_BTNS = "f0";
	public static final String DEVICE_BROADCAST_FF = "ff";
	
	/**
	 * Sends a command to the RaspberryPi
	 * @param command
	 * @return
	 */
	public static boolean sendPacket(IBUSPacket ibPacket){
		// TODO encapsulate and send packet via bluetooth
		return false;
	}
	
	/**
	 * Processes a received IBUSPacket (this is extracted from original BlueBusPacket)
	 * BMW --(USB)--> RaspberryPi --(Bluetooth)--> Android
	 * @param command
	 */
	public static void processPacket(IBUSPacket ibPacket){
		// perform activity specific actions
		if(BluetoothInterface.mActivity instanceof ActivityIBUS){
			((ActivityIBUS) BluetoothInterface.mActivity).receivedIBUSPacket(ibPacket);
			
		}else if(BluetoothInterface.mActivity instanceof ActivityRadio){
			
		}else if(BluetoothInterface.mActivity instanceof ActivityStatus){
			
		}else if(BluetoothInterface.mActivity instanceof ActivityWindows){
			
		}
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
	
	/**
	 * Toggles the 'Mode' (i.e. Radio, AUX, CD-Player, iPod, etc.)
	 * @param thisActivity
	 */
	public static void toggleMode(Activity thisActivity){
		BlueBusPacket bbPacket = new BlueBusPacket();
		bbPacket.type = 1;
		bbPacket.data = "TOGGLE_MODE";
		try {
			if(BluetoothInterface.isConnected()){
				BluetoothInterface.mBluetoothOutputStream.write(new Gson().toJson(bbPacket).getBytes());
				Log.d("BMW", "Writing message...");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
