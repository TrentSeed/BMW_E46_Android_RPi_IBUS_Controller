"""
The start_controller.py module serves as the main entry point to the connected car application.
When this module is run from the command line, a signal handler will be attached to Ctrl+C so
we can interrupt and exit the application.

Example
-------
    $ python3 start_controller.py bmw-e46

"""
import logging
import sys
import argparse


from controllers.factory import ControllerFactory
from version import __version__


LOGGER = logging.getLogger(__name__)


def start_controller(controller_type):
    """
    Responsible for initializing a controller instance, which will subsequently initialize
    the necessary core services for the vehicle data bus and client devices e.g. bluetooth.
    """
    LOGGER.info('connected car controller - version: %s', __version__)
    controller = ControllerFactory.create(controller_type)

    if not controller:
        LOGGER.error("[ERROR] invalid controller, exiting...")
        sys.exit(2)

    controller.start()


if __name__ == "__main__":
    # setup logging
    log_format = '%(asctime)s [%(levelname)s] [%(filename)s] [%(lineno)s] %(message)s'
    logging.basicConfig(level=logging.DEBUG, format=log_format)

    # parse command line arguments
    parser = argparse.ArgumentParser()
    parser.add_argument("controller_type", help="The name of the controller, for example 'bmw-e46'")
    args = parser.parse_args()

    # start the controller
    start_controller(args.controller_type)
