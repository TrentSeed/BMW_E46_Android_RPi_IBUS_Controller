#!/usr/bin/env python
import serial
import globals
from packet import IBUSPacket
import threading


class IBUSService():

    # configuration
    baudrate = 9600
    handle = None
    parity = serial.PARITY_EVEN
    #port = 6  # ThinkPad T410
    port = '/dev/ttyUSB0'  # Raspberry Pi
    timeout = 1
    thread = None

    # special packets
    packet_car_locked = ''
    packet_car_unlocked = ''
    packet_switch_mode_down = 'f004684823f7'
    packet_switch_mode_down_extra_sec = 'f004684863b7'
    packet_switch_mode_release = 'f0046848A377'
    packet_press_fm = 'f004684831e5'
    packet_volume_up = '50046832111f'
    packet_volume_down = '50046832101e'
    packet_radio_power = 'f004684806d2'
    packet_gps_location_update = ''
    packet_steering_wheel_ctl_mode = ''

    def __init__(self):
        """
        Initializes bi-directional communication with IBUS adapter via USB
        """
        self.handle = serial.Serial(self.port, parity=self.parity, timeout=self.timeout, stopbits=1)
        self.thread = threading.Thread(target=self.start)
        self.thread.daemon = True
        self.thread.start()
        return

    def start(self):
        """
        Starts listen service
        """
        while True:
            data = self.handle.read(9999)
            if len(data) > 0:
                self.process_bus_dump(data)

    def destroy(self):
        """
        Closes serial connection and resets handle
        """
        try:
            print "Destroying IBUS service..."
            self.handle.close()
            self.handle = None
            self.thread = None
        except TypeError:
            self.handle = None
            self.thread = None

    def process_bus_dump(self, dump, index=0):
        """
        Processes bytes received from serial and parse packets

        ---------------------------------------------
        | Source ID | Length | Dest Id | Data | XOR |
        ---------------------------------------------
                             | ------ Length -------|

        """
        packets = []
        hex_dump = dump.encode('hex')
        print "Hex Dump: " + hex_dump
        while index < len(hex_dump):
            try:
                # construct packet while reading
                current_packet = ""

                # extract source id
                source_id = hex_dump[index:(index+2)]
                current_packet += source_id
                index += 2

                # extract length info
                length = hex_dump[index:(index+2)]
                current_packet += length
                total_length_data = int(length, 16) - 4
                total_length_hex_chars = total_length_data * 2
                index += 2

                # extract destination id
                destination_id = hex_dump[index:(index+2)]
                current_packet += destination_id
                index += 2

                # extract inner data
                data = hex_dump[index:(index+total_length_hex_chars)]
                current_packet += data
                index += total_length_hex_chars

                # extract xor checksum
                xor = hex_dump[index:(index+2)]
                current_packet += xor
                index += 2

                # confirm full packet exists
                expected_packet_length = (2 + 2 + 2 + total_length_hex_chars + 2)
                if current_packet.__len__() != expected_packet_length:
                    return False

                # create packet
                packet = IBUSPacket(source_id=source_id, length=total_length_data, destination_id=destination_id,
                                    data=data, xor_checksum=xor, raw=current_packet)

                # add packet if valid
                if packet.is_valid():
                    packets.append(packet)

            except Exception as e:
                print "Error processing bus dump: " + e.message

            # process packets data (and send to Android)
            self.process_packets(packets)

    @staticmethod
    def process_packets(packets):
        """
        Process packets [] and determine message to send to Android
        """
        # check if android service ready
        if globals.android_service is not None:
            try:
                # send packets to android
                globals.android_service.send_packets_to_android(packets)
            except Exception as e:
                print "Error: " + e.message + "\nFailed to send packets to android"
                return False

        return True

    def radio_toggle_mode(self):
        """
        Toggles 'Aux / Radio / CD-Player'
        """
        toggle_radio_packets = 'f004684823f7f0046848a377680b3ba562014120464d412095'
        print "Writing to IBUS: hex = " + toggle_radio_packets
        self.handle.write(toggle_radio_packets.decode('hex'))