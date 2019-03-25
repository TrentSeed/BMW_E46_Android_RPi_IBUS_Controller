"""
The e46.py module is responsible for BMW E46 vehicles.

Car Production: March 1998 - February 2005
Reference: https://en.wikipedia.org/wiki/BMW_3_Series_(E46)

"""
import logging

from controllers.base import BaseController

from interfaces.ibus import IBUSInterface
from interfaces.bt import BluetoothInterface


LOGGER = logging.getLogger(__name__)


class E46Controller(BaseController):
    """Controller definition for BMW E46 Vehicles"""

    __controller_name__ = 'bmw-e46'

    def __init__(self):
        super(E46Controller, self).__init__()
        self.bluetooth = BluetoothInterface(self)  # interface available for clients
        self.ibus = IBUSInterface(self)  # interface connected to the vehicle

        # bind interfaces to each other
        self.bluetooth.bind_receive(self.ibus.send)
        self.ibus.bind_receive(self.bluetooth.send)

    def start(self):
        """Invoked when the controller is starting."""
        LOGGER.info('starting the controller services...')

        # connect to the necessary interfaces
        self.ibus.connect()
        self.bluetooth.connect()
        self.state = self.__states__.STATE_RUNNING
        LOGGER.info('all services have been started')

        # run until "Ctrl-C" is pressed
        self.loop_until_ctrl_c()

    def stop(self):
        """Invoked when the controller should stop."""
        LOGGER.info('ending the controller services...')
        self.state = self.__states__.STATE_STOPPED
        self.ibus.disconnect()
        self.bluetooth.disconnect()

