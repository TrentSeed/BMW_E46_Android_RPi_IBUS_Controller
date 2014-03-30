#!/usr/bin/env python
from ibus_service.ibus import IBUS
from android_service.android import ANDROID
import signal
import sys

# globals and configuration
ibus = None
android = None


def signal_handler_quit(signal, frame):
    """
    Invoked when 'Ctrl+C' is pressed
    """
    print "Stopping service..."
    if ibus is not None:
        ibus.destroy()
    if android is not None:
        android.destroy()
    sys.exit(0)


def start_services():
    """
    Main method of the Rasperry Pi service(s)
    """
    global android, ibus
    print "RasperryPi IBUS/Android Communication"
    print "Starting service...\n"
    signal.signal(signal.SIGINT, signal_handler_quit)

    # initialize ibus and android services
    try:
        ibus = IBUS()
        android = ANDROID()
    except Exception as e:
        print e.message + "\nFailed to start service(s)"

# start RPi services
start_services()