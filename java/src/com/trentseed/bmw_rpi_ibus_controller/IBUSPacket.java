package com.trentseed.bmw_rpi_ibus_controller;

/**
 * IBUS "packet" that is sent between BMW and Raspberry Pi
 */
public class IBUSPacket {
	
	String source_id;
	String length;
	String destination_id;
	String data;
	String xor_checksum;
	String raw;
	String timestamp;
	
	public IBUSPacket(){ }
	
	public String getSourceName(){
		return getDeviceName(this.source_id);
	}
	
	public String getDestinationName(){
		return getDeviceName(this.destination_id);
	}
	
	public String getDeviceName(String device_id){
		if(device_id.equals("00")) return "Broadcast 00";
		else if(device_id.equals("18")) return "CDW - CDC CD-Player";
		else if(device_id.equals("3b")) return "NAV Navigation/Video Module";
		else if(device_id.equals("43")) return "Menu Screen";
		else if(device_id.equals("50")) return "MFL Steering Wheel Controls";
		else if(device_id.equals("60")) return "PDC Park Distance Control";
		else if(device_id.equals("68")) return "RAD Radio";
		else if(device_id.equals("6a")) return "DSP Digital Sound Processor";
		else if(device_id.equals("80")) return "IKE Instrument Kombi Electronics";
		else if(device_id.equals("bb")) return "TV Module";
		else if(device_id.equals("bf")) return "LCM Light Control Module";
		else if(device_id.equals("c0")) return "MID Multi-Information Display Buttons";
		else if(device_id.equals("c8")) return "TEL Telephone";
		else if(device_id.equals("d0")) return "Navigation Location";
		else if(device_id.equals("e7")) return "OBC Text Bar";
		else if(device_id.equals("ed")) return "Lights, Wipers, Seat Memory";
		else if(device_id.equals("f0")) return "BMB Board Monitor Buttons";
		else if(device_id.equals("ff")) return "Broadcast FF";
		else return "Unknown";
	}

}
