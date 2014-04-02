package com.trentseed.bmw_rpi_ibus_controller;

import java.io.IOException;

import android.util.Log;
import com.google.gson.Gson;

/**
 * Thread that handles Bluetooth RFCOMM channel reading from Raspberry Pi
 */
class ConnectedThread extends Thread {
 
    public ConnectedThread() { }
 
    public void run() {
        int bytes = 0;
        
        while (true) {
            try {
            	byte[] buffer = new byte[1024];
                bytes = BluetoothInterface.mBluetoothInputStream.read(buffer);
                if(bytes > 0){
                	// read data, inflate to BlueBusPacket, extract IBUSPacket
                	Log.d("BMW", "Data In = " + new String(buffer).trim());
                	String strBuffer = new String(buffer).trim();
                	BlueBusPacket bbPacket = new Gson().fromJson(strBuffer, BlueBusPacket.class);
                	final IBUSPacket ibPacket = bbPacket.getIBUSPacket();
                	
                	//perform command
                	BluetoothInterface.mActivity.runOnUiThread(new Runnable() {
                	    @Override
                	    public void run() {
                	    	IBUSWrapper.processPacket(ibPacket);
                	    }
                	});
                }
            } catch (IOException e) {
            	Log.d("BMW", "Error reading: " + String.valueOf(e.getLocalizedMessage()));
                break;
            }
        }
    }
}
