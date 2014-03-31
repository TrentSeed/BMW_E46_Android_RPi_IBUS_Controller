#!/usr/bin/env python
import globals
from ibus_service.ibus import IBUS_SERVICE
from android_service.android import ANDROID_SERVICE
import signal
import sys
import time


def signal_handler_quit(signal, frame):
    """
    Invoked when 'Ctrl+C' is pressed
    """
    print "Stopping services..."
    if globals.ibus_service is not None:
        globals.ibus_service.destroy()
    if globals.android_service is not None:
        globals.android_service.destroy()
    sys.exit(0)


def start_services():
    """
    Main method of the Rasperry Pi service(s)
    """
    print "RasperryPi IBUS/Android Communication\n"
    print "Starting services..."
    signal.signal(signal.SIGINT, signal_handler_quit)

    # initialize ibus and android services
    try:
        print "Initializing IBUS service...."
        globals.ibus_service = IBUS_SERVICE()
        print "Initializing BLUETOOTH service...."
        globals.android_service = ANDROID_SERVICE()
        print "\n\nAll services running...\n"
    except Exception as e:
        print e.message + "\n\nFailed to start service(s)"

    # until "Ctrl-C" is pressed
    while True:
        time.sleep(1)

# start RPi services
start_services()