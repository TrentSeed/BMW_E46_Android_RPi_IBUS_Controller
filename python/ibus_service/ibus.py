#!/usr/bin/env python

import serial
from packet import IBUSPacket


class IBUS():

    # configuration
    baudrate = 9600
    handle = None
    parity = serial.PARITY_EVEN
    port = 6
    timeout = 3

    def __init__(self):
        """
        Initializes bi-directional communication with IBUS adapter via USB
        """
        self.handle = serial.Serial(self.port, parity=self.parity, timeout=self.timeout)

        while True:
            data = self.handle.read(9999)
            if len(data) > 0:
                self.process_bus_dump(data)

        return

    def destroy(self):
        """
        Closes serial connection and resets handle
        """
        try:
            self.handle.close()
            self.handle = None
        except Exception:
            self.handle = None

    @staticmethod
    def process_bus_dump(dump, index=0):
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
            packet = IBUSPacket(source_id=source_id, length=total_length_data,
                                destination_id=destination_id, data=data, xor_checksum=xor, raw=current_packet)
            print packet

            # send to android if valid
            # TODO if packet.is_valid():
