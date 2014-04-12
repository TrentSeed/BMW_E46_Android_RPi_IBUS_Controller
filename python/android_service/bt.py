#!/usr/bin/env python
import globals
from bluetooth import *
from packet import BlueBUSPacket
from ibus_service.packet import IBUSPacket
import json
import time
import threading


class AndroidBluetoothService():

    # configuration
    #bluetooth_address = "5C:AC:4C:C8:E2:7E"  # ThinkPad T410
    bluetooth_address = "00:02:72:CC:EF:3C"  # Asus USB-BT400
    client_sock = None
    client_info = None
    conn_established = False
    rfcomm_channel = None
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
        return

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
        advertise_service(self.server_sock, "BMWPythonServer",
                          service_id=self.service_uuid,
                          service_classes=[self.service_uuid, SERIAL_PORT_CLASS],
                          profiles=[SERIAL_PORT_PROFILE])
        print("Waiting for connection on RFCOMM channel %d" % self.rfcomm_channel)

        # accept received connection
        self.client_sock, self.client_info = self.server_sock.accept()
        self.conn_established = True
        print("Accepted connection from ", self.client_info)

        # start listening for data
        self.start_listening()

        # debug only - send test packets over Bluetooth
        #self.start_debug_sending()
        return

    def destroy(self):
        """
        Closes Bluetooth connection and resets handle
        """
        try:
            print "Destroying BLUETOOTH service..."
            self.conn_established = False
            self.client_sock.close()
            self.server_sock.close()
            self.client_sock = None
            self.server_sock = None
            self.thread = None
        except ValueError:
            self.client_sock = None
            self.server_sock = None
            self.thread = None

    def send_packets_to_android(self, ibus_packets):
        """
        Sends data via Bluetooth socket connection
        """
        if self.conn_established:
            try:
                print ("Sending encapsulated IBUSPacket(s)...")
                packets = []
                for ibus_packet in ibus_packets:
                    packets.append(ibus_packet.as_dict())

                # encapsulate IBUS packet in BlueBUSPacket
                packet = BlueBUSPacket(packet_type=BlueBUSPacket.TYPE_PACKET,
                                       data=json.dumps(packets))

                # serialize BlueBusPacket and send
                json_data = json.dumps(packet.as_dict())
                print json_data
                self.client_sock.send(json_data)

                # packet sent successfully
                #print packet
                return True
            except Exception as e:
                # socket was closed, graceful restart
                print "Error: " + e.message
                globals.restart_services()
                return False

    def send_command_to_android(self, command):
        """
        Sends data via Bluetooth socket connection
        """
        try:
            print ("Sending message...")
            # encapsulate command in BlueBUSPacket
            packet = BlueBUSPacket(packet_type=BlueBUSPacket.TYPE_COMMAND,
                                   data=command)

            # serialize BlueBusPacket and send
            json_data = json.dumps(packet.__dict__())
            self.client_sock.send(json_data)
            #print packet
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
                data = self.client_sock.recv(2048)
                if len(data) > 0:
                    self.process_data_from_android(data)
        except IOError:
            print ("Android device was disconnected...")
            globals.restart_services()
            pass

    def start_debug_sending(self):
        """
        Starts DEBUG mode cycle where a sample IBUSPacket object
        sent via Bluetooth (encapsulated in BlueBUSPacket object)
        """
        test_length = 10
        while True:
            # create test packet
            packets = []
            test_packet = IBUSPacket(source_id="f0", length=str(test_length), destination_id="68",
                                     data="32", xor_checksum="ff", raw="f0"+str(test_length)+"ff00")
            packets.append(test_packet)
            self.send_packets_to_android(packets)
            test_length += 1
            time.sleep(5)

    @staticmethod
    def process_data_from_android(data):
        """
        Processes received data from Bluetooth socket
        """
        # inflate JSON data to BlueBUSPacket
        parsed_json = json.loads(data)
        packet = BlueBUSPacket(packet_type=parsed_json['type'],
                               data=parsed_json['data'])
        print("Received BlueBusPacket from Android [" + str(len(packet.data)) + "]")

        # check if command to perform (i.e. write to IBUS)
        globals.ibus_service.write_to_ibus(packet.data.decode('hex'))

        return
        """
        try:
            globals.ibus_service.radio_toggle_mode()
        except Exception:
            print "Action Failed - Unable to toggle radio mode"
        return
        """
