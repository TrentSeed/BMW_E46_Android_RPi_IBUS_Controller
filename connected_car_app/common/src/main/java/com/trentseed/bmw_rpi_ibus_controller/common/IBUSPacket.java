package com.trentseed.bmw_rpi_ibus_controller.common;

import java.text.Normalizer;

/**
 * IBUS "packet" that is sent between BMW and Raspberry Pi
 */
public class IBUSPacket {

    public String source_id;
    public String length;
    public String destination_id;
    public String data;
    public String xor_checksum;
    public String raw;
    public String timestamp;

    public IBUSPacket(){ }

    public String getSourceName(){
        return getDeviceName(this.source_id);
    }

    public String getDestinationName(){
        return getDeviceName(this.destination_id);
    }

    private String getDeviceName(String device_id){
        switch (device_id) {
            case "00":
                return "Broadcast 00";
            case "18":
                return "CDW - CDC CD-Player";
            case "3b":
                return "NAV Navigation/Video Module";
            case "43":
                return "Menu Screen";
            case "50":
                return "MFL Steering Wheel Controls";
            case "60":
                return "PDC Park Distance Control";
            case "68":
                return "RAD Radio";
            case "6a":
                return "DSP Digital Sound Processor";
            case "80":
                return "IKE Instrument Kombi Electronics";
            case "bb":
                return "TV Module";
            case "bf":
                return "LCM Light Control Module";
            case "c0":
                return "MID Multi-Information Display Buttons";
            case "c8":
                return "TEL Telephone";
            case "d0":
                return "Navigation Location";
            case "e7":
                return "OBC Text Bar";
            case "ed":
                return "Lights, Wipers, Seat Memory";
            case "f0":
                return "BMB Board Monitor Buttons";
            case "ff":
                return "Broadcast FF";
            default:
                return "Unknown";
        }
    }

    /**
     * Parses hex string and returns ASCII character representation
     * http://www.mkyong.com/java/how-to-convert-hex-to-ascii-in-java/
     * @return String
     */
    public String getAsciiFromRaw(){
        StringBuilder sb = new StringBuilder();
        for( int i=0; i<raw.length()-1; i+=2 ){
            String output = raw.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char)decimal);
        }
        String normalized = Normalizer.normalize(sb.toString(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        normalized = normalized.replace("#E", "");  // extra data to remove
        normalized = normalized.replaceAll("[,;]", "");  // extra data to remove
        normalized = normalized.replaceAll("[a-z]", "");  // extra data to remove
        return normalized.replace(" CAP", " CA");  // extra data to remove
    }

}
