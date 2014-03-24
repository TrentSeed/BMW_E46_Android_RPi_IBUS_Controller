package com.trentseed.bmw_rpi_ibus_controller;

/**
 * Navigation class handles core communication with Raspberry Pi.
 * @author Trent
 *
 */
public class Navigation {

	// supported commands
	public static final String COMMAND_TRACK_NEXT = "TRACK_NEXT";
	public static final String COMMAND_TRACK_PREV = "TRACK_PREV";
	public static final String COMMAND_MODE = "MODE";
	public static final String COMMAND_RADIO_FM = "FM";
	public static final String COMMAND_RADIO_AM = "AM";
	public static final String COMMAND_RADIO_PRESET = "PRESET";
	
	/**
	 * Sends a command to the RaspberryPi
	 * @param command
	 * @return
	 */
	public static boolean sendCommand(String command){
		return false;
	}
	
	/**
	 * Processes a received command from RaspberryPi
	 * @param command
	 */
	public static void processCommand(String command){
		
	}
	
}
