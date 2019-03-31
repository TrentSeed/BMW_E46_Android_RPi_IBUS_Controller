package com.trentseed.bmw_rpi_ibus_controller.common;

import java.io.IOException;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * Thread that handles Bluetooth RFCOMM channel reading from Raspberry Pi
 */
class ConnectedThread extends Thread {

    /**
     * Starts a loop responsible for continuously reading bytes from the
     * Bluetooth input stream, and invoking IBUSWrapper.processPacket().
     */
    public void run() {

        // start a loop that reads from the bluetooth input stream
        while (true) {
            try {
                // read bytes from bluetooth input stream
                byte[] buffer = new byte[2048];  // TODO: from config
                int bytes = BluetoothInterface.mBluetoothInputStream.read(buffer);
                if(bytes == 0) {
                    continue;
                }

                // create String from data and validate JSON structure
                Log.d("BMW", "Data In = " + new String(buffer).trim());
                String strBuffer = new String(buffer).trim();
                if(!isJSONValid(strBuffer)) {
                    continue;
                }

                // parse received data
                ControllerMessage msg = new Gson().fromJson(strBuffer, ControllerMessage.class);
                for(final IBUSPacket ibPacket : msg.getIBUSPackets()){
                    BluetoothInterface.mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            IBUSWrapper.processPacket(ibPacket);
                        }
                    });
                }

            } catch (IOException e) {
                Log.d("BMW", "Error reading: " + String.valueOf(e.getMessage()));
                break;
            }
        }
    }

    /**
     * Returns true or false is provided string input is JSON parsable.
     */
    private boolean isJSONValid(String json) {
        try {
            new JsonParser().parse(json);
            return true;
        } catch (JsonSyntaxException jse) {
            return false;
        }
    }
}

