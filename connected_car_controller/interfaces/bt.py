"""
The interfaces.bt module contains a BaseInterface implementation for BluetoothInterface.
"""
import json
import threading
import subprocess
import logging
from bluetooth import advertise_service, BluetoothSocket, RFCOMM, PORT_ANY, SERIAL_PORT_CLASS, SERIAL_PORT_PROFILE

from interfaces.base import BaseInterface


LOGGER = logging.getLogger(__name__)


class BluetoothInterface(BaseInterface):
    """The Bluetooth interface definition for communication with wireless client devices."""

    __interface_name__ = 'bluetooth'

    def __init__(self, controller):
        """
        Initializes a bluetooth service that may be consumed by a remote client.

        Arguments
        ---------
            controller : controllers.base.BaseController
                the parent controller that is instantiated this interface

        """
        super(BluetoothInterface, self).__init__()
        self.controller = controller
        self.client_sock = None
        self.client_info = None
        self.rfcomm_channel = None
        self.service_name = self.get_setting('service_name')
        self.service_uuid = self.get_setting('service_uuid')
        self.server_sock = None
        self.thread = None

    def connect(self):
        """Creates a new thread that listens for an incoming bluetooth RFCOMM connection."""
        LOGGER.info('creating thread for bluetooth interface...')
        self.thread = threading.Thread(target=self.listen_for_rfcomm_connection)
        self.thread.daemon = True
        self.thread.start()

    def listen_for_rfcomm_connection(self):
        """
        Starts bluetooth interfaces
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
        LOGGER.info('waiting for connection on RFCOMM channel %d' % self.rfcomm_channel)

        # accept received connection
        self.client_sock, self.client_info = self.server_sock.accept()
        self.state = self.__states__.STATE_CONNECTED
        LOGGER.info('accepted connection from %r', self.client_info)

        # start listening for data
        self.consume_bus()

    def disconnect(self):
        """
        Closes Bluetooth connection and resets handle
        """
        LOGGER.info('destroying bluetooth interface...')
        self.state = self.__states__.STATE_DISCONNECTING
        if self.client_sock and hasattr(self.client_sock, 'close'):
            self.client_sock.close()
            self.client_sock = None
        if self.server_sock and hasattr(self.server_sock, 'close'):
            self.server_sock.close()
            self.server_sock = None

        # reset the bluetooth interface
        self.thread = None
        self.perform_hci0_reset()
        self.state = self.__states__.STATE_READY

    @staticmethod
    def perform_hci0_reset():
        """Resets the bluetooth hci0 device via hciconfig command line interface."""
        try:
            LOGGER.info('performing hci0 down/up...')
            subprocess.Popen('sudo hciconfig hci0 down', shell=True).communicate()
            subprocess.Popen('sudo hciconfig hci0 up', shell=True).communicate()
            LOGGER.info('hci0 down/up has completed')
        except Exception as exception:
            LOGGER.exception("Failed to restart hci0 - %r", exception)

    def receive(self, data):
        """
        Processes received data from Bluetooth socket

        Arguments
        ---------
            data : basestring
                the data received from the bluetooth connection

        """
        try:
            packet = json.loads(data)
            LOGGER.info('received packet via bluetooth: %r', packet['data'])

            # invoke bound method (if set)
            if self.receive_hook and hasattr(self.receive_hook, '__call__'):
                self.receive_hook(packet['data'].decode('hex'))

        except Exception as exception:
            LOGGER.exception('error: %r', exception)

    def send(self, data):
        """
        Sends data via Bluetooth socket connection

        Arguments
        ---------
            data : basestring
                the data to be sent via this interface

        """
        if self.state != self.__states__.STATE_CONNECTED:
            LOGGER.error('error: send() was called but state is not connected')
            return False

        try:
            LOGGER.info('sending IBUSPacket(s)...')
            packets = []
            for packet in data:
                packets.append(packet.as_serializable_dict())

            # encapsulate ibus packets and send
            data = {"data": json.dumps(packets)}  # TODO : is an inner json.dumps necessary?
            LOGGER.info(data)
            self.client_sock.send(json.dumps(data))
        except Exception as exception:
            # socket was closed, graceful restart
            LOGGER.exception('bt send: %r', exception.message)
            self.reconnect()

    def consume_bus(self):
        """
        Start listening for incoming data
        """
        try:
            LOGGER.info('starting to listen for bluetooth data...')
            read_buffer_length = self.get_setting('read_buffer_length', int)

            while self.client_sock:
                data = self.client_sock.recv(read_buffer_length)
                if len(data) > 0:
                    self.receive(data)

        except Exception as exception:
            LOGGER.exception('android device was disconnected - %r', exception)
            self.reconnect()
