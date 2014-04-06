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
    port = 6
    timeout = 1
    thread = None

    # special packets
    packet_car_locked = ''
    packet_car_unlocked = ''
    packet_switch_radio_mode = 'f004684823f7'
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
        hex_dump = dump.encode('hex')
        print hex_dump
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

                # process packet data (and send to Android)
                self.process_packet(packet)
            except Exception as e:
                print "Error processing bus dump: " + e.message

    @staticmethod
    def process_packet(packet):
        """
        Process packet and determine message to send to Android
        """
        if not packet.is_valid():
            return False

        # check if android service ready
        if globals.android_service is not None:
            try:
                # check if 'Next Track' steering wheel command
                if packet.source_id == "50" and packet.destination_id == "68":
                    globals.android_service.send_command_to_android("NEXT")

                # TODO check if 'Mode' pressed from steering wheel

                # TODO check if 'GPS Location' text update received

                # send packet to android
                globals.android_service.send_packet_to_android(packet)

            except Exception as e:
                print e.message + "\n" + "Failed to send to android"
                return False

        return True

    def radio_toggle_mode(self):
        """
        Toggles 'Aux / Radio / CD-Player'
        """
        print "Writing to IBUS " + 'f004684823f7'.decode('hex')
        self.handle.write('f004684823f7'.decode('hex'))