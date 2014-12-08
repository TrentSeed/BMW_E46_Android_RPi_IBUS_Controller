#!/usr/bin/env python
import globals
import signal
import sys
import time


def signal_handler_quit(signal, frame):
    """
    Invoked when 'Ctrl+C' is pressed
    """
    print "Ending services..."
    globals.stop_services()
    sys.exit(0)


def main():
    """
    Start core services
    """
    print "BMW IBUS/RasperryPi/Android Controller"
    print "Starting services..."
    signal.signal(signal.SIGINT, signal_handler_quit)
    globals.start_services()

    # run until "Ctrl-C" is pressed
    while True:
        time.sleep(1)


if __name__ == "__main__":
    main()