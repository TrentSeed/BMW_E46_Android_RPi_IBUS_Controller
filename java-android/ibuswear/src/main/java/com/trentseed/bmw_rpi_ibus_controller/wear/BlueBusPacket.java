package com.trentseed.bmw_rpi_ibus_controller.wear;

import com.google.gson.Gson;

/**
 * Bluetooth "packet" that is sent between Android and Raspberry Pi
 */
public class BlueBusPacket {
	
	// class variables
	public final static int TYPE_PACKET = 0;
	public final static int TYPE_COMMAND = 1;
	
	// instance variables
	int type;
	String data;
	
	public BlueBusPacket(){ }
	
	public BlueBusPacket(int packet_type, String data){
		this.type = packet_type;
		this.data = data;
	}
	
	public boolean isTypePacket(){
		return this.type == BlueBusPacket.TYPE_PACKET;
	}
	
	public boolean isTypeCommand(){
		return this.type == BlueBusPacket.TYPE_COMMAND;
	}
	
	public IBUSPacket[] getIBUSPackets(){
		if(this.isTypePacket() == false) return null;
		try{
			IBUSPacket[] ibPackets = new Gson().fromJson(this.data, IBUSPacket[].class);
			return ibPackets;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
