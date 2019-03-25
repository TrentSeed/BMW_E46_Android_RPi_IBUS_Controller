package com.trentseed.bmw_rpi_ibus_controller.common;

import com.google.gson.Gson;

/**
 * Bluetooth "packet" that is sent between Android and Raspberry Pi
 */
public class BlueBusPacket {

    // class variables
    private final static int TYPE_PACKET = 0;
    private final static int TYPE_COMMAND = 1;

    // instance variables
    int type;
    String data;

    public BlueBusPacket(){ }

    public BlueBusPacket(int packet_type, String data){
        this.type = packet_type;
        this.data = data;
    }

    private boolean isTypePacket(){
        return this.type == BlueBusPacket.TYPE_PACKET;
    }

    private boolean isTypeCommand(){
        return this.type == BlueBusPacket.TYPE_COMMAND;
    }

    public IBUSPacket[] getIBUSPackets(){
        if(!this.isTypePacket()) return null;
        try{
            return new Gson().fromJson(this.data, IBUSPacket[].class);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
