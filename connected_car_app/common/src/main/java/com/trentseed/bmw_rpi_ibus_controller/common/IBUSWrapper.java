package com.trentseed.bmw_rpi_ibus_controller.common;

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

    // special packets
    public static final String KEY_INSERTED = "4405bf7404028c";
    public static final String KEY_REMOTE_LOCK = "3f05000c340103";
    public static final String KEY_REMOTE_UNLOCK = "3f05000c030134";
    public static final String RADIO_POLLING_CD_REQUEST = "6803180172";
    public static final String RADIO_POLLING_CD_RESPONSE = "180468020076";

    /**
     * Processes a received IBUSPacket (this is extracted from original ControllerMessage)
     * BMW --(IBUS USB)--> RaspberryPi --(Bluetooth)--> Android
     */
    public static void processPacket(IBUSPacket ibPacket){
        // add packet to local buffer (IBUS activity displays buffer)
        int max_buffer = 20;
        BluetoothInterface.mArrayListIBUSActivity.add(0, ibPacket);
        if(BluetoothInterface.mArrayListIBUSActivity.size() > max_buffer){
            BluetoothInterface.mArrayListIBUSActivity.remove(
                BluetoothInterface.mArrayListIBUSActivity.size()-1
            );
        }

        // check for special packets
        if(ibPacket.raw.equals(KEY_INSERTED)){
            // special packet - key inserted
            Log.d("BMW", "Key was inserted now...");
        }else if(ibPacket.raw.equals(KEY_REMOTE_LOCK)){
            // special packet - car locked
            Log.d("BMW", "Car was locked now...");
        }else if(ibPacket.raw.equals(KEY_REMOTE_UNLOCK)){
            // special packet - car unlocked
            Log.d("BMW", "Car was unlocked now...");
        }else if(ibPacket.raw.equals(RADIO_POLLING_CD_REQUEST)){
            // special packet - radio polling CD changer
            Log.d("BMW", "Radio polling CD changer now...");
            sendCDChangerPollResponse();
        }
    }

    /**
     * Sends a ControllerMessage object to the RaspberryPi over Bluetooth.
     * @param data - the ControllerMessage instance to send
     */
    private static void sendMessage(ControllerMessage data){
        try {
            if(BluetoothInterface.isConnected()){
                Log.d("BMW", "writing bytes to output stream...");
                byte[] bytes = new Gson().toJson(data).getBytes();
                BluetoothInterface.mBluetoothOutputStream.write(bytes);
            }else{
                Log.d("BMW", "cannot write bytes, not connected.");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Advance to next audio track (i.e. Google Music). This can be called when
     * detecting a particular packet, such as the steering wheel audio controls.
     */
    public static void nextTrack(Activity thisActivity){
        Log.d("BMW", "ATTEMPTING TO GO TO NEXT TRACK");
        Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
        synchronized (thisActivity) {
            i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT));
            thisActivity.sendOrderedBroadcast(i, null);

            i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT));
            thisActivity.sendOrderedBroadcast(i, null);
        }
    }

    public static void toggleMode(Activity thisActivity){
        ControllerMessage msg = new ControllerMessage();
        msg.data = "f004684823f7" + "f0046848a377" + "680b3ba562014120464d412095";
        IBUSWrapper.sendMessage(msg);
    }

    public static void volumeUp(){
        ControllerMessage msg = new ControllerMessage();
        msg.data = "50046832111f";
        IBUSWrapper.sendMessage(msg);
    }

    public static void volumeDown(){
        ControllerMessage msg = new ControllerMessage();
        msg.data = "50046832101e";
        IBUSWrapper.sendMessage(msg);
    }

    public static void turnInteriorLightsOff(){
        ControllerMessage msg = new ControllerMessage();
        msg.data = "3f05000c010136";
        IBUSWrapper.sendMessage(msg);
    }

    public static void turnInteriorLightsOn(){
        ControllerMessage msg = new ControllerMessage();
        msg.data = "3f05000c600157";
        IBUSWrapper.sendMessage(msg);
    }

    public static void openTrunk(){
        ControllerMessage msg = new ControllerMessage();
        msg.data = "3f05000c020135";
        IBUSWrapper.sendMessage(msg);
    }

    public static void pressAccept(){
        ControllerMessage msg = new ControllerMessage();
        msg.data = "f0043b480582" + "f0043b488502";
        IBUSWrapper.sendMessage(msg);
    }

    public static void toggleHazards(){
        ControllerMessage msg = new ControllerMessage();
        msg.data = "0004bf7602cf";
        IBUSWrapper.sendMessage(msg);
    }

    public static void toggleRadioPower(){
        ControllerMessage msg = new ControllerMessage();
        msg.data = "f004684806d2";
        IBUSWrapper.sendMessage(msg);
    }

    public static void windowDriverFront(boolean down){
        ControllerMessage msg = new ControllerMessage();
        if(down) msg.data = "3F05000C520165";
        else msg.data = "3F05000C530164";
        IBUSWrapper.sendMessage(msg);
    }

    public static void windowDriverRear(boolean down){
        ControllerMessage msg = new ControllerMessage();
        if(down) msg.data = "3F05000C410176";
        else msg.data = "3F05000C420175";
        IBUSWrapper.sendMessage(msg);
    }

    public static void windowPassengerFront(boolean down){
        ControllerMessage msg = new ControllerMessage();
        if(down) msg.data = "3F05000C540163";
        else msg.data = "3F05000C550162";
        IBUSWrapper.sendMessage(msg);
    }

    public static void windowPassengerRear(boolean down){
        ControllerMessage msg = new ControllerMessage();
        if(down) msg.data = "3F05000C440173";
        else msg.data = "3F05000C430174";
        IBUSWrapper.sendMessage(msg);
    }

    public static void moveDriverSeat(boolean forward){
        ControllerMessage msg = new ControllerMessage();
        if(forward) msg.data = "3F06720C01010047";
        else msg.data = "3F06720C01020044";
        IBUSWrapper.sendMessage(msg);
    }

    public static void moveDriverSeatUpperPortion(boolean forward){
        ControllerMessage msg = new ControllerMessage();
        if(forward) msg.data = "3f06720c01400006";
        else msg.data = "3f06720c018000c6";
        IBUSWrapper.sendMessage(msg);
    }

    public static void lockCar(){
        ControllerMessage msg = new ControllerMessage();
        msg.data = "3F05000C340103";
        IBUSWrapper.sendMessage(msg);
    }

    public static void unlockCar(){
        ControllerMessage msg = new ControllerMessage();
        msg.data = "3F05000C030134";
        IBUSWrapper.sendMessage(msg);
    }

    public static void toggleSunroof(boolean open){
        ControllerMessage msg = new ControllerMessage();
        if(open) msg.data = "3F05000C7E0149";
        else msg.data = "3F05000C7F0148";
        IBUSWrapper.sendMessage(msg);
    }

    private static void sendCDChangerPollResponse(){
        ControllerMessage msg = new ControllerMessage();
        msg.data = RADIO_POLLING_CD_RESPONSE;
        IBUSWrapper.sendMessage(msg);
    }

}