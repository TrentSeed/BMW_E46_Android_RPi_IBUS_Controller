#!/usr/bin/env python
"""
The start_controller.py module serves as the main entry point to the connected car
application. When this module is run from the command line, a signal handler
will be attached to Ctrl+C so we can interrupt and exit the application.
After this step, a BaseController is instantiated, which will start the BaseService
initializations.

"""
import logging
import sys


LOGGER = logging.getLogger(__name__)


def start_controller():
    """
    Responsible for initializing a controller instance, which will subsequently initialize
    the necessary core services for the vehicle data bus and client devices e.g. bluetooth.
    """
    try:
        controller_type = sys.argv[1]
    except IndexError:
        controller_type = None

    LOGGER.info('connected car controller - version: %r', '1.0.0')

    # initiate the provided controller type
    LOGGER.info('starting controller: %r', controller_type)
    if controller_type == 'bmw-e46':
        from controllers.bmw.e46 import E46Controller
        E46Controller().start()
    else:
        LOGGER.error('controller not supported - %r', controller_type)


if __name__ == "__main__":
    log_format = '%(asctime)s - %(levelname)s - %(filename)s - %(lineno)s - %(message)s'
    logging.basicConfig(level=logging.DEBUG, format=log_format)
    start_controller()
