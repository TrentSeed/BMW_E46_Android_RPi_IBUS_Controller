"""
The interfaces.base module contains logic common to all BaseInterface implementations.
"""
import logging
import os
from ConfigParser import SafeConfigParser


LOGGER = logging.getLogger(__name__)


class InterfaceStates(object):
    """The various states that an interface can experience."""
    STATE_READY = 0
    STATE_CONNECTING = 1
    STATE_CONNECTED = 2
    STATE_DISCONNECTING = 3


class BaseInterface(object):
    """The base class for implementing an interface type."""

    __interface_name__ = None
    __states__ = InterfaceStates

    def __init__(self):
        self.state = self.__states__.STATE_READY
        self.send_hook = None
        self.receive_hook = None

    def get_setting(self, setting, setting_type=basestring):
        """
        Returns a specified setting from etc/config.ini.

        Parameters
        ----------
            setting : basestring
                the key of the setting to return under the [interfaces.{__interface_name__}] section
            setting_type : type
                [optional] the data type for the setting e.g. int or bool. default is basestring.

        Returns
        -------
            basestring
                string value of the setting

        """
        path = os.path.join(
            os.path.dirname(os.path.abspath(__file__)), '..', 'etc', 'config.ini'
        )
        parser = SafeConfigParser()
        parser.read(path)
        section = 'interfaces.{}'.format(self.__interface_name__)

        # return setting value using the specified setting_type
        if setting_type is int:
            return parser.getint(section, setting)
        elif setting_type is bool:
            return parser.getboolean(section, setting)
        elif setting_type is float:
            return parser.getfloat(section, setting)
        else:
            return parser.get(section, setting)

    def connect(self):
        """Invoked when the interface is performing a connection"""
        raise NotImplementedError

    def disconnect(self):
        """Invoked when the interface is being destroyed"""
        raise NotImplementedError

    def send(self, data):
        """Invoked when data should be sent over the interface

        Arguments
        ---------
            data : basestring
                the data to be sent via this interface

        """
        raise NotImplementedError

    def receive(self, data):
        """Invoked when data is received from the interface

        Arguments
        ---------
            data : basestring
                incoming data received from this interface

        """
        raise NotImplementedError

    def consume_bus(self):
        """Responsible for initiating a loop and reading from the bus."""
        raise NotImplementedError

    def reconnect(self):
        """Performs a reconnect job to establish a fresh connection"""
        LOGGER.info('restarting interface...')
        self.disconnect()
        self.connect()

    def bind_receive(self, method):
        """Used to bind a method that will be called every time the interface receives data.

        Arguments
        ---------
            method : function
                a method to bind and invoke each time the interface receives data

        """
        if not hasattr(method, '__call__'):
            LOGGER.error('error in bind_receive: method must be callable!')
            return

        self.receive_hook = method

    def bind_send(self, method):
        """Used to bind a method that will be called every time the interface sends data.

        Arguments
        ---------
            method : function
                a method to bind and invoke each time the interface sends data

        """
        if not hasattr(method, '__call__'):
            LOGGER.error('error in bind_send: method must be callable!')
            return

        self.send_hook = method
