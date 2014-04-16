import time
from ibus_service.ibus import IBUSService
from android_service.bt import AndroidBluetoothService

# global services
android_service = None
debug = False
ibus_service = None


def start_services(auto_retry=True):
    """
    Main method of the Rasperry Pi service(s)
    """
    # initialize ibus and android services
    global ibus_service, android_service
    try:
        print "Initializing IBUS service...."
        ibus_service = IBUSService()
        print "Initializing BLUETOOTH service...."
        android_service = AndroidBluetoothService()
        print "All services running..."
    except Exception as e:
        if auto_retry:
            print "Error: " + e.message + "\n" + "Failed to start, trying again in 5 seconds..."
            time.sleep(5)
            print "Restarting now...\n"
            start_services()
        return


def restart_services():
    """
    Destroys existing IBUS and Android communication and re-initializes
    """
    print "Restarting services..."
    stop_services()
    start_services()


def stop_services():
    """
    Destroys IBUS and Android services
    """
    if ibus_service is not None:
        ibus_service.destroy()
    if android_service is not None:
        android_service.destroy()