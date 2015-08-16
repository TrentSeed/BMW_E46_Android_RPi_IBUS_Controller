#!/usr/bin/env python
import globals
from bluetooth import *
from ibus_service.packet import IBUSPacket
import json
import time
import threading


class AndroidBluetoothService(object):

    # configuration
    bluetooth_address = "5C:F3:70:6A:9E:12"  # Asus USB-BT400
    client_sock = None
    client_info = None
    conn_established = False
    rfcomm_channel = None
    service_name = "BMWPythonServer"
    service_uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"
    server_sock = None
    thread = None

    def __init__(self):
        """
        Initializes bi-directional communication with ANDROID via Bluetooth
        """
        self.thread = threading.Thread(target=self.start)
        self.thread.daemon = True
        self.thread.start()

    def start(self):
        """
        Starts bluetooth service
        """
        # prepare bluetooth server
        self.server_sock = BluetoothSocket(RFCOMM)
        self.server_sock.bind(("", PORT_ANY))
        self.server_sock.listen(1)
        self.rfcomm_channel = self.server_sock.getsockname()[1]

        # start listening for incoming connections
        advertise_service(self.server_sock, self.service_name,
                          service_id=self.service_uuid,
                          service_classes=[self.service_uuid, SERIAL_PORT_CLASS],
                          profiles=[SERIAL_PORT_PROFILE])
        print "Waiting for connection on RFCOMM channel %d" % self.rfcomm_channel

        # accept received connection
        self.client_sock, self.client_info = self.server_sock.accept()
        self.conn_established = True
        print "Accepted connection from " + str(self.client_info)

        # start listening for data
        self.start_listening()
        # self.start_debug_sending()  # DEBUG ONLY

    def destroy(self):
        """
        Closes Bluetooth connection and resets handle
        """
        try:
            print "Destroying BLUETOOTH service..."
            self.client_sock.close()
            self.server_sock.close()
        except Exception:
            pass

        self.conn_established = False
        self.client_sock = None
        self.server_sock = None
        self.thread = None

    def send_packets_to_android(self, ibus_packets):
        """
        Sends data via Bluetooth socket connection
        """
        if not self.conn_established:
            return False

        try:
            print ("Sending encapsulated IBUSPacket(s)...")
            packets = []
            for ibus_packet in ibus_packets:
                packets.append(ibus_packet.as_dict())

            # encapsulate IBUS packets and send
            data = {"data": json.dumps(packets)}
            if globals.debug:
                print data
            self.client_sock.send(data)
            return True

        except Exception as e:
            # socket was closed, graceful restart
            print "Error: " + e.message
            globals.restart_bluetooth()
            return False

    def start_listening(self):
        """
        Start listening for incoming data
        """
        try:
            print "Starting to listen for data..."
            while True:
                data = self.client_sock.recv(2048)
                if len(data) > 0:
                    self.process_data_from_android(data)
        except (IOError, Exception):
            print ("Android device was disconnected...")
            globals.restart_bluetooth()

    def start_debug_sending(self):
        """
        Starts DEBUG mode cycle where a test IBUS packet is sent every 5 seconds
        """
        while True:
            test_packet = IBUSPacket(source_id="f0", length="10", destination_id="68",
                                     data="32", xor_checksum="ff", raw="f0106842ff")
            self.send_packets_to_android([test_packet])
            time.sleep(5)

    @staticmethod
    def process_data_from_android(data):
        """
        Processes received data from Bluetooth socket
        """
        try:
            packet = json.loads(data)
            print "Received BlueBusPacket from Android [" + str(len(packet['data'])) + "]"
            globals.ibus_service.write_to_ibus(packet.data.decode('hex'))
        except Exception as e:
            print "Error: " + e.message
