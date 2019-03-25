package com.trentseed.bmw_rpi_ibus_controller.common;

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

    // special packets
    public static final String KEY_INSERTED = "4405bf7404028c";
    public static final String KEY_REMOTE_LOCK = "3f05000c340103";
    public static final String KEY_REMOTE_UNLOCK = "3f05000c030134";
    public static final String RADIO_POLLING_CD_REQUEST = "6803180172";
    public static final String RADIO_POLLING_CD_RESPONSE = "180468020076";

    // track GPS data toggle
    public static boolean gpsIsCityToggle = true;
    public static String gpsStreet = "STREET";
    public static String gpsCity = "CITY";

    /**
     * Processes a received IBUSPacket (this is extracted from original BlueBusPacket)
     * BMW --(IBUS USB)--> RaspberryPi --(Bluetooth)--> Android
     * @param ibPacket
     */
    public static void processPacket(IBUSPacket ibPacket){
        // add packet to local buffer (IBUS activity displays buffer)
        int max_buffer = 20;
        BluetoothInterface.mArrayListIBUSActivity.add(0, ibPacket);
        if(BluetoothInterface.mArrayListIBUSActivity.size() > max_buffer){
            BluetoothInterface.mArrayListIBUSActivity.remove(BluetoothInterface.mArrayListIBUSActivity.size()-1);
        }

        // check for special packets
//        if(ibPacket.raw.equals(KEY_INSERTED)){
//            // special packet - key inserted
//            Log.d("BMW", "Key was inserted now...");
//            wakeScreen();
//        }else if(ibPacket.raw.equals(KEY_REMOTE_LOCK)){
//            // special packet - car locked
//            Log.d("BMW", "Car was locked now...");
//        }else if(ibPacket.raw.equals(KEY_REMOTE_UNLOCK)){
//            // special packet - car unlocked
//            Log.d("BMW", "Car was unlocked now...");
//            wakeScreen();
//        }else if(ibPacket.raw.equals(RADIO_POLLING_CD_REQUEST)){
//            // special packet - radio polling CD changer
//            Log.d("BMW", "Radio polling CD changer now...");
//            sendCDChangerPollResponse();
//        }

        // perform activity specific actions
//        if(BluetoothInterface.mActivity instanceof ActivityMain){
//            ((ActivityMain) BluetoothInterface.mActivity).receivedIBUSPacket(ibPacket);
//        }else if(BluetoothInterface.mActivity instanceof ActivityIBUS){
//            ((ActivityIBUS) BluetoothInterface.mActivity).receivedIBUSPacket(ibPacket);
//        }else if(BluetoothInterface.mActivity instanceof ActivityRadio){
//
//        }else if(BluetoothInterface.mActivity instanceof ActivityStatus){
//
//        }else if(BluetoothInterface.mActivity instanceof ActivityWindows){
//
//        }
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
        bbPacket.data = "f004684823f7" + "f0046848a377" + "680b3ba562014120464d412095";
        try {
            if(BluetoothInterface.isConnected()){
                BluetoothInterface.mBluetoothOutputStream.write(new Gson().toJson(bbPacket).getBytes());
                Log.d("BMW", "Writing toggle mode message...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void volumeUp(){
        BlueBusPacket bbPacket = new BlueBusPacket();
        bbPacket.type = 1;
        bbPacket.data = "50046832111f";
        try {
            if(BluetoothInterface.isConnected()){
                BluetoothInterface.mBluetoothOutputStream.write(new Gson().toJson(bbPacket).getBytes());
                Log.d("BMW", "Writing vol up message...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void volumeDown(){
        BlueBusPacket bbPacket = new BlueBusPacket();
        bbPacket.type = 1;
        bbPacket.data = "50046832101e";
        try {
            if(BluetoothInterface.isConnected()){
                BluetoothInterface.mBluetoothOutputStream.write(new Gson().toJson(bbPacket).getBytes());
                Log.d("BMW", "Writing vol down message...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void turnInteriorLightsOff(){
        BlueBusPacket bbPacket = new BlueBusPacket();
        bbPacket.type = 1;
        bbPacket.data = "3f05000c010136";
        try {
            if(BluetoothInterface.isConnected()){
                BluetoothInterface.mBluetoothOutputStream.write(new Gson().toJson(bbPacket).getBytes());
                Log.d("BMW", "Writing turn off interior lights message...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void turnInteriorLightsOn(){
        BlueBusPacket bbPacket = new BlueBusPacket();
        bbPacket.type = 1;
        bbPacket.data = "3f05000c600157";
        try {
            if(BluetoothInterface.isConnected()){
                BluetoothInterface.mBluetoothOutputStream.write(new Gson().toJson(bbPacket).getBytes());
                Log.d("BMW", "Writing turn on interior lights message...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void openTrunk(){
        BlueBusPacket bbPacket = new BlueBusPacket();
        bbPacket.type = 1;
        bbPacket.data = "3f05000c020135";
        try {
            if(BluetoothInterface.isConnected()){
                BluetoothInterface.mBluetoothOutputStream.write(new Gson().toJson(bbPacket).getBytes());
                Log.d("BMW", "Writing open trunk message...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void pressAccept(){
        BlueBusPacket bbPacket = new BlueBusPacket();
        bbPacket.type = 1;
        bbPacket.data = "f0043b480582" + "f0043b488502";
        try {
            if(BluetoothInterface.isConnected()){
                BluetoothInterface.mBluetoothOutputStream.write(new Gson().toJson(bbPacket).getBytes());
                Log.d("BMW", "Writing press accept message...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void toggleHazards(){
        BlueBusPacket bbPacket = new BlueBusPacket();
        bbPacket.type = 1;
        bbPacket.data = "0004bf7602cf";
        try {
            if(BluetoothInterface.isConnected()){
                BluetoothInterface.mBluetoothOutputStream.write(new Gson().toJson(bbPacket).getBytes());
                Log.d("BMW", "Writing toggle hazards message...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void toggleRadioPower(){
        BlueBusPacket bbPacket = new BlueBusPacket();
        bbPacket.type = 1;
        bbPacket.data = "f004684806d2";
        try {
            if(BluetoothInterface.isConnected()){
                BluetoothInterface.mBluetoothOutputStream.write(new Gson().toJson(bbPacket).getBytes());
                Log.d("BMW", "Writing toggle radio power message...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void windowDriverFront(boolean down){
        BlueBusPacket bbPacket = new BlueBusPacket();
        bbPacket.type = 1;
        if(down) bbPacket.data = "3F05000C520165";
        else bbPacket.data = "3F05000C530164";
        try {
            if(BluetoothInterface.isConnected()){
                BluetoothInterface.mBluetoothOutputStream.write(new Gson().toJson(bbPacket).getBytes());
                Log.d("BMW", "Writing window message...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void windowDriverRear(boolean down){
        BlueBusPacket bbPacket = new BlueBusPacket();
        bbPacket.type = 1;
        if(down) bbPacket.data = "3F05000C410176";
        else bbPacket.data = "3F05000C420175";
        try {
            if(BluetoothInterface.isConnected()){
                BluetoothInterface.mBluetoothOutputStream.write(new Gson().toJson(bbPacket).getBytes());
                Log.d("BMW", "Writing window message...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void windowPassengerFront(boolean down){
        BlueBusPacket bbPacket = new BlueBusPacket();
        bbPacket.type = 1;
        if(down) bbPacket.data = "3F05000C540163";
        else bbPacket.data = "3F05000C550162";
        try {
            if(BluetoothInterface.isConnected()){
                BluetoothInterface.mBluetoothOutputStream.write(new Gson().toJson(bbPacket).getBytes());
                Log.d("BMW", "Writing window message...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void windowPassengerRear(boolean down){
        BlueBusPacket bbPacket = new BlueBusPacket();
        bbPacket.type = 1;
        if(down) bbPacket.data = "3F05000C440173";
        else bbPacket.data = "3F05000C430174";
        try {
            if(BluetoothInterface.isConnected()){
                BluetoothInterface.mBluetoothOutputStream.write(new Gson().toJson(bbPacket).getBytes());
                Log.d("BMW", "Writing window message...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void moveDriverSeat(boolean forward){
        BlueBusPacket bbPacket = new BlueBusPacket();
        bbPacket.type = 1;
        if(forward) bbPacket.data = "3F06720C01010047";
        else bbPacket.data = "3F06720C01020044";
        try {
            if(BluetoothInterface.isConnected()){
                BluetoothInterface.mBluetoothOutputStream.write(new Gson().toJson(bbPacket).getBytes());
                Log.d("BMW", "Writing driver seat message...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void moveDriverSeatUpperPortion(boolean forward){
        BlueBusPacket bbPacket = new BlueBusPacket();
        bbPacket.type = 1;
        if(forward) bbPacket.data = "3f06720c01400006";
        else bbPacket.data = "3f06720c018000c6";
        try {
            if(BluetoothInterface.isConnected()){
                BluetoothInterface.mBluetoothOutputStream.write(new Gson().toJson(bbPacket).getBytes());
                Log.d("BMW", "Writing upper driver seat message...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void lockCar(){
        BlueBusPacket bbPacket = new BlueBusPacket();
        bbPacket.type = 1;
        bbPacket.data = "3F05000C340103";
        try {
            if(BluetoothInterface.isConnected()){
                BluetoothInterface.mBluetoothOutputStream.write(new Gson().toJson(bbPacket).getBytes());
                Log.d("BMW", "Writing lock car message...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void unlockCar(){
        BlueBusPacket bbPacket = new BlueBusPacket();
        bbPacket.type = 1;
        bbPacket.data = "3F05000C030134";
        try {
            if(BluetoothInterface.isConnected()){
                BluetoothInterface.mBluetoothOutputStream.write(new Gson().toJson(bbPacket).getBytes());
                Log.d("BMW", "Writing unlock car message...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void toggleSunroof(boolean open){
        BlueBusPacket bbPacket = new BlueBusPacket();
        bbPacket.type = 1;
        if(open) bbPacket.data = "3F05000C7E0149";
        else bbPacket.data = "3F05000C7F0148";
        try {
            if(BluetoothInterface.isConnected()){
                BluetoothInterface.mBluetoothOutputStream.write(new Gson().toJson(bbPacket).getBytes());
                Log.d("BMW", "Writing toggle sunroof message...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sendCDChangerPollResponse(){
        BlueBusPacket bbPacket = new BlueBusPacket();
        bbPacket.type = 1;
        bbPacket.data = RADIO_POLLING_CD_RESPONSE;
        try {
            if(BluetoothInterface.isConnected()){
                BluetoothInterface.mBluetoothOutputStream.write(new Gson().toJson(bbPacket).getBytes());
                Log.d("BMW", "Writing radio polling CD response...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}