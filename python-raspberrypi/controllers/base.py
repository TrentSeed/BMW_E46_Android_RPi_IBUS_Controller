"""
The controllers.base module contains logic common to all BaseController implementations.
"""
import logging
import sys
import signal
import time


LOGGER = logging.getLogger(__name__)


class ControllerStates(object):
    """The various states that an interface can experience."""
    STATE_STOPPED = 0
    STATE_RUNNING = 1


class BaseController(object):
    """The base class for implementing a controller type."""

    __controller_name__ = None
    __states__ = ControllerStates

    def __init__(self):
        self.state = self.__states__.STATE_STOPPED

    def start(self):
        """Invoked when the controller is starting."""
        raise NotImplementedError

    def stop(self):
        """Invoked when the controller should stop."""
        raise NotImplementedError

    def restart(self):
        """Restarts the controller to establish fresh interface connections."""
        LOGGER.info('restarting the controller...')
        self.stop()
        self.start()

    @staticmethod
    def loop_until_ctrl_c():
        """Performs an infinite loop, waiting for "Ctrl-C" to exit the application."""
        LOGGER.info('press "ctrl-c" to exit the application...')
        signal.signal(signal.SIGINT, lambda x, y: sys.exit(0))
        while True:
            time.sleep(1)


