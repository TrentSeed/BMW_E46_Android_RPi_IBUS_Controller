package com.trentseed.bmw_rpi_ibus_controller.common;

import com.google.gson.Gson;

/**
 * Data structure that encapsulates ibus packets which are sent between Android and
 * Raspberry Pi controller.
 */
public class ControllerMessage {

    String data;

    public ControllerMessage(){}

    public IBUSPacket[] getIBUSPackets(){
        try{
            return new Gson().fromJson(this.data, IBUSPacket[].class);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
