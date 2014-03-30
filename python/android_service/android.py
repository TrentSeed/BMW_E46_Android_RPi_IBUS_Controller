#!/usr/bin/env python
from bluetooth import *


class ANDROID():

    # configuration
    bluetooth_address = "10:3B:59:4E:83:77"
    client_sock = None
    client_info = None
    handle = None
    service_uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"
    server_sock = None

    def __init__(self):
        """
        Initializes bi-directional communication with ANDROID via Bluetooth
        """
        print "Initializing BLUETOOTH service...."
        self.handle = None

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
        print("Waiting for connection on RFCOMM channel %d" % port)

        # accept received connection
        self.client_sock, self.client_info = self.server_sock.accept()
        print("Accepted connection from ", self.client_info)

        # start listening for data
        #self.start_listening()
        return

    def destroy(self):
        """
        Closes Bluetooth connection and resets handle
        """
        try:
            print "Destroying BLUETOOTH service..."
            self.handle.close()
            self.client_sock.close()
            self.server_sock.close()
            self.handle = None
            self.client_sock = None
            self.server_sock = None
        except ValueError:
            self.handle = None
            self.client_sock = None
            self.server_sock = None

    def send(self, data):
        """
        Sends data via Bluetooth socket connection
        """
        try:
            print ("Sending message...")
            self.client_sock.send(data)
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

    @staticmethod
    def process_data_in(data):
        """
        Processes received data from Bluetooth socket
        """
        print("Received [%s]" % data)
        return
