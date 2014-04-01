#!/usr/bin/env python
from bluetooth import *
from packet import BlueBUSPacket
from ibus_service.packet import IBUSPacket
import globals
import json
import time


class AndroidBluetoothService():

    # configuration
    bluetooth_address = "10:3B:59:4E:83:77"
    client_sock = None
    client_info = None
    service_uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"
    server_sock = None

    def __init__(self):
        """
        Initializes bi-directional communication with ANDROID via Bluetooth
        """
        # prepare bluetooth server
        self.server_sock = BluetoothSocket(RFCOMM)
        self.server_sock.bind(("", PORT_ANY))
        self.server_sock.listen(1)
        port = self.server_sock.getsockname()[1]

        # start listening for incoming connections
        advertise_service(self.server_sock, "PythonServer",
                          service_id=self.service_uuid,
                          service_classes=[self.service_uuid, SERIAL_PORT_CLASS],
                          profiles=[SERIAL_PORT_PROFILE])
        print("Waiting for connection on RFCOMM channel %d\n" % port)

        # accept received connection
        self.client_sock, self.client_info = self.server_sock.accept()
        print("Accepted connection from ", self.client_info)

        # debug only - send test packets over Bluetooth
        self.start_debug_sending()

        # start listening for data
        #self.start_listening()
        return

    def destroy(self):
        """
        Closes Bluetooth connection and resets handle
        """
        try:
            print "Destroying BLUETOOTH service..."
            self.client_sock.close()
            self.server_sock.close()
            self.client_sock = None
            self.server_sock = None
        except ValueError:
            self.client_sock = None
            self.server_sock = None

    def send_packet(self, ibus_packet):
        """
        Sends data via Bluetooth socket connection
        """
        try:
            print ("Sending message...")
            # encapsulate IBUS packet in BlueBUSPacket
            packet = BlueBUSPacket(packet_type=BlueBUSPacket.TYPE_PACKET,
                                   data=ibus_packet.__json__())
            self.client_sock.send(json.dumps(packet.__json__()))
            print packet
            return True
        except Exception as e:
            print e
            return False

    def send_command(self, command):
        """
        Sends data via Bluetooth socket connection
        """
        try:
            print ("Sending message...")
            # encapsulate command in BlueBUSPacket
            packet = BlueBUSPacket(packet_type=BlueBUSPacket.TYPE_COMMAND,
                                   data=command)
            self.client_sock.send(json.dumps(packet.__json__()))
            print packet
            return True
        except Exception as e:
            print e
            return False

    def start_listening(self):
        """
        Start listening for incoming data
        """
        try:
            print("Starting to listen for data...")
            while True:
                data = self.client_sock.recv(1024)
                if len(data) == 0:
                    break
                self.process_data_in(data)
        except IOError:
            pass

    def start_debug_sending(self):
        """
        Starts DEBUG mode cycle where a sample IBUSPacket object
        sent via Bluetooth (encapsulated in BlueBUSPacket object)
        """
        while True:
            # create test packet
            test_packet = IBUSPacket(source_id="D0", length="02", destination_id="68",
                                     data="32", xor_checksum="FF", raw="D0026832FF")
            self.send_packet(test_packet)
            time.sleep(5)

    @staticmethod
    def process_data_in(data):
        """
        Processes received data from Bluetooth socket
        """
        # encapsulate data in BlueBUSPacket
        parsed_json = json.loads(data)
        packet = BlueBUSPacket(packet_type=parsed_json['type'],
                               data=parsed_json['data'])
        print("Received [%s]" % packet)
        return
