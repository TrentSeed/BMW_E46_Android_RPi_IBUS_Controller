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
    timeout = 3
    thread = None

    def __init__(self):
        """
        Initializes bi-directional communication with IBUS adapter via USB
        """
        self.handle = serial.Serial(self.port, parity=self.parity, timeout=self.timeout)
        self.thread = threading.Thread(target=self.start)
        self.thread.daemon = True
        self.thread.start()
        return

    def start(self):
        """
        Starts listen service
        """
        while True:
            data = self.handle.read(2048)
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
        except TypeError:
            self.handle = None

    def process_bus_dump(self, dump, index=0):
        """
        Processes bytes received from serial and parse packets

        ---------------------------------------------
        | Source ID | Length | Dest Id | Data | XOR |
        ---------------------------------------------
                             | ------ Length -------|

        """
        hex_dump = dump.encode('hex')

        while index < len(hex_dump):
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

    @staticmethod
    def process_packet(packet):
        """
        Process packet and determine message to send to Android
        """
        if not packet.is_valid():
            return False

        try:
            # print packet to console
            print packet

            # check if 'Next Track' steering wheel command
            if packet.source_id == "50" and packet.destination_id == "68":
                globals.android_service.send_command_to_android("NEXT")

            # TODO check if 'Mode' pressed from steering wheel

            # TODO check if 'GPS Location' text update received

            # send packet to android
            globals.android_service.send_packet_to_android(packet)

        except Exception as e:
            print e.message + "\n" + "Failed to send to android"

        return True