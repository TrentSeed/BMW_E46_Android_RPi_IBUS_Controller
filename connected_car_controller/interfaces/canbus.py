"""
This module contains the implementation for CanBusInterface.

TODO: complete implementation
"""
import logging
import threading
import serial


from interfaces.base import BaseInterface


LOGGER = logging.getLogger(__name__)


class CanBusInterface(BaseInterface):
    """The CAN (Controller Area Network) bus interface definition for communication with vehicles."""

    __interface_name__ = 'canbus'

    def __init__(self, controller):
        """
        Initializes bi-directional communication with CAN bus adapter via USB

        Arguments
        ---------
            controller : controllers.base.BaseController
                the parent controller that instantiated this interface

        """
        super(CanBusInterface, self).__init__()
        self.controller = controller
        self.baudrate = self.get_setting('baudrate', int)
        self.handle = None
        self.port = self.get_setting('port')
        self.timeout = self.get_setting('timeout', int)
        self.thread = None

    def connect(self):
        """Connect to the vehicle via serial communication."""
        if not self.state == self.__states__.STATE_READY:
            LOGGER.error('error: interface is not STATE_READY')
            raise Exception('invalid interface state: %r', self.state)

        # initialize serial port connection
        try:
            self.state = self.__states__.STATE_CONNECTING
            self.handle = serial.Serial(self.port, self.baudrate, timeout=self.timeout)
            self.state = self.__states__.STATE_CONNECTED
        except serial.serialutil.SerialException:
            LOGGER.exception('failed to establish serial connection')
            return False

        # launch new thread for continuous processing of the bus
        LOGGER.info('creating thread for %s interface...', self.__interface_name__)
        self.thread = threading.Thread(target=self.consume_bus)
        self.thread.daemon = True
        self.thread.start()

    def consume_bus(self):
        """Starts an infinite loop on the thread that will continue to read from the bus."""
        read_buffer_length = self.get_setting('read_buffer_length', int)

        while self.handle:
            data = self.handle.read(read_buffer_length)
            if len(data) > 0:
                self.receive(data)

    def disconnect(self):
        """Closes serial connection and resets the handle."""
        try:
            LOGGER.info('destroying %s interface...', self.__interface_name__)
            self.state = self.__states__.STATE_DISCONNECTING
            if self.handle and hasattr(self.handle, 'close'):
                self.handle.close()
        except Exception as exception:
            LOGGER.exception('exception during %s disconnect - %r', self.__interface_name__, exception)
        finally:
            self.state = self.__states__.STATE_READY
            self.handle = None
            self.thread = None

    def receive(self, data):
        """
        Processes bytes received from the interface.

        Arguments
        ---------
            data : basestring
                the bytes retrieved from the bus, encoded as a basestring

        """
        LOGGER.info('bus dump: hex: %r', data.encode('hex'))

        # TODO process packets
        packets = []

        # invoke bound method (if set)
        if self.receive_hook and hasattr(self.receive_hook, '__call__'):
            self.receive_hook(packets)

    def send(self, data):
        """
        Writes the provided hex packet(s) to the bus

        Parameters
        ----------
            data : basestring
                the data to be sent via this interface

        """
        if self.state != self.__states__.STATE_CONNECTED:
            LOGGER.error('error: send() was called but state is not connected')
            return False

        if not self.handle or not hasattr(self.handle, 'write'):
            LOGGER.error('cannot write to %s interface', self.__interface_name__)
            self.reconnect()
            return

        self.handle.write(data)
