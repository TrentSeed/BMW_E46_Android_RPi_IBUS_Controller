"""
The bmw.base module contains logic common to all BMWController implementations.
"""
import logging

from controllers.base import BaseController


LOGGER = logging.getLogger(__name__)


class BMWController(BaseController):
    """Base class for any BMW vehicle controller implementations."""

    __controller_name__ = 'bmw-base'

    def __init__(self):
        super(BMWController, self).__init__()

    def start(self):
        """Invoked when the controller is starting."""
        raise NotImplementedError

    def stop(self):
        """Invoked when the controller should stop."""
        raise NotImplementedError
