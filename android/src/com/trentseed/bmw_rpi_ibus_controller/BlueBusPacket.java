package com.trentseed.bmw_rpi_ibus_controller;

/**
 * Bluetooth "packet" that is sent between Android and Raspberry Pi
 *
 */
public class BlueBusPacket {
	
	// class variables
	public final static int TYPE_PACKET = 0;
	public final static int TYPE_COMMAND = 1;
	
	// instance variables
	int type;
	String data;
	
	public BlueBusPacket(int packet_type, String data){
		this.type = packet_type;
		this.data = data;
	}
}
