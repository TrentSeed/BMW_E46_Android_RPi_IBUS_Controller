"""
This module contains the implementation for IBUSInterface.
"""
import logging
import time
import threading
import serial
import binascii


from interfaces.base import BaseInterface


LOGGER = logging.getLogger(__name__)


class IBUSInterface(BaseInterface):
    """The IBUS interface definition for communication with BMW vehicles."""

    __interface_name__ = 'ibus'

    def __init__(self, controller):
        """
        Initializes bi-directional communication with IBUS adapter via USB

        Arguments
        ---------
            controller : controllers.base.BaseController
                the parent controller that instantiated this interface

        """
        super(IBUSInterface, self).__init__()
        self.controller = controller
        self.baudrate = self.get_setting('baudrate', int)
        self.handle = None
        self.parity = serial.PARITY_EVEN
        self.port = self.get_setting('port')
        self.timeout = self.get_setting('timeout', int)
        self.thread = None

    def connect(self):
        """Create daemon thread to establish serial communication."""
        LOGGER.info('creating thread for %s interface...', self.__interface_name__)
        self.thread = threading.Thread(target=self.listen_for_serial_connection)
        self.thread.daemon = True
        self.thread.start()

    def listen_for_serial_connection(self):
        """
        Connect to the vehicle via serial communication.
        """
        # initialize serial port connection
        try:
            self.state = self.__states__.STATE_CONNECTING
            self.handle = serial.Serial(
                port=self.port,
                baudrate=self.baudrate,
                parity=self.parity,
                timeout=self.timeout,
                stopbits=1
            )
            self.state = self.__states__.STATE_CONNECTED
        except serial.serialutil.SerialException:
            LOGGER.exception('failed to establish serial connection, retrying in 10 seconds...')
            time.sleep(10)
            self.state = self.__states__.STATE_READY
            self.listen_for_serial_connection()

        # start listening for data
        self.consume_bus()

    def consume_bus(self):
        """Starts an infinite loop on the thread that will continue to read from the bus."""
        read_buffer_length = self.get_setting('read_buffer_length', int)

        try:
            while self.handle:
                data = self.handle.read(read_buffer_length)
                if len(data) > 0:
                    self.receive(data)
        except Exception:
            LOGGER.exception('exception consuming bus, retrying connection in 5 seconds...')
            time.sleep(5)
            self.reconnect()

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
        LOGGER.info('bus dump: hex: %r', data.hex())

        packets = []
        bus_dump = bytearray(data)
        packet = bytearray()

        def is_packet_complete():
            """Identifies if packet is complete, i.e. is the size of its length byte.

            Returns
            -------
                bool
                    True if the packet bytearray is a "full/complete" IBUS packet

            """
            if len(packet) == 0:  # no source id
                return False
            elif len(packet) == 1:  # no length
                return False
            elif len(packet) == 2:  # no destination id
                return False

            # length = ord(packet[1])  # e.g. '\x04' -> 4
            length = packet[1]
            entire_length = length + 2  # the source_id and length bytes
            if len(packet) == entire_length:
                return True

            return False

        # process each byte that was received
        for index, byte in enumerate(bus_dump):  # index is an int, byte is a str
            packet.append(byte)

            if is_packet_complete():
                packet = IBUSPacket(packet)
                if packet.is_valid():
                    packets.append(packet)
                else:
                    LOGGER.error('invalid packet : %r', packet)

                packet = bytearray()  # reset packet

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

        self.handle.write(bytes.fromhex(data))


class IBUSPacket(dict):
    """
    The ibus.packet module represents an IBUS packet that would be processed
    from the connected BUS.

    IBUS Packet
    -----------
        ----------------------------------------------------
        | Source ID | Length | Destination Id | Data | XOR |
        ----------------------------------------------------
                             | ---------- Length ----------|

    """

    def __init__(self, bytes):
        """
        Initializes packet object.

        Parameters
        ----------
            bytes : bytearray
                the bytearray representation of the ibus packet e.g. '\xFF\x03\x01\x01'

        """
        super(IBUSPacket, self).__init__()
        self['source_id'] = bytes[0]
        self['length'] = bytes[1]
        self['destination_id'] = bytes[2]

        data_start = 3
        data_end = data_start + self['length'] - 2

        self['data'] = bytes[data_start:data_end]
        self['xor_checksum'] = bytes[-1:]
        self['raw'] = bytes
        self['timestamp'] = int(time.time())

    def is_valid(self):
        """Verifies packet information & XOR checksum.

        Returns
        -------
            bool
                True if the packet is valid, False if the packet is invalid

        """
        return self['xor_checksum'] == self.calculate_xor_checksum()

    @staticmethod
    def get_device_name(device_id):
        """Returns the nice human-readable device name for provided device id.

        E.g. 0x50 returns 'MFL Multi FunctionalSteering Wheel Buttons'.

        Arguments
        ---------
            device_id : int
                return the device description for the provided hex code

        Returns
        -------
            basestring
                the name of the device, or "Unknown" if an unrecognized id

        """
        if not isinstance(device_id, int):
            raise TypeError('get_device_name: device_id must be an int')

        device_names = {
            0x00: "Broadcast",
            0x18: "CDW - CDC CD-Player",
            0x30: "?????",
            0x3b: "NAV Navigation/Video Module",
            0x3f: "?????",
            0x43: "Menu Screen",
            0x44: "?????",
            0x50: "MFL Multi Functional Steering Wheel Buttons",
            0x60: "PDC Park Distance Control",
            0x68: "RAD Radio",
            0x6a: "DSP Digital Sound Processor",
            0x7f: "?????",
            0x80: "IKE Instrument Kombi Electronics",
            0xa8: "?????",
            0xbb: "TV Module",
            0xbf: "LCM Light Control Module",
            0xc0: "MID Multi-Information Display Buttons",
            0xc8: "TEL Telephone",
            0xd0: "Navigation Location",
            0xe7: "OBC Text Bar",
            0xe8: "?????",
            0xed: "Lights, Wipers, Seat Memory",
            0xf0: "BMB Board Monitor Buttons",
            0xff: "Broadcast",
        }

        try:
            return device_names[device_id]
        except KeyError:
            return "Unknown"

    def as_serializable_dict(self):
        """The current android application requires a particular JSON
        structure.

        Python bytearray objects are not JSON serializable, so this function
        will return the expected object, and hexlifies the bytearrays.

        Returns
        -------
            dict
                a dictionary of json serializable values

        """
        return {
            'source_id': binascii.hexlify(self['source_id']),
            'length': binascii.hexlify(self['length']),
            'destination_id': binascii.hexlify(self['destination_id']),
            'data': binascii.hexlify(self['data']),
            'xor_checksum': binascii.hexlify(self['xor_checksum']),
            'raw': binascii.hexlify(self['raw']),
            'timestamp': self['timestamp']
        }

    def calculate_xor_checksum(self):
        """Calculates XOR value for packet.

        This is used to verify that the scanned value matches the actual calculated value.

        Returns
        -------
            str
                the checksum value for this packet instance e.g. '\xff'

        """
        checksum = 0

        for key in self['raw'][:-1]:
            checksum = checksum ^ key

        return chr(checksum)
