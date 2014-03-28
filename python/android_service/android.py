#!/usr/bin/env python
import struct
import time
import threading
import usb


class ANDROID():

    # configuration (Nexus 7)
    accessory_vendor_id = 0x17EF
    accessory_product_id = 0x480F
    description = "BMW IBUS via Raspberry Pi"
    handle = None
    manufacturer = "Nexus-RPi"
    model_name = "Raspberry Pi"
    serial_number = "1337"
    url = "http://www.trentseed.com/BMW_E46_IBUS_APP.apk"
    version = "0.1"

    def __init__(self):
        """
        Initializes bi-directional communication with ANDROID via USB
        """
        while True:
            # perform accessory communication
            self.accessory_task()

    def destroy(self):
        """
        Closes USB connection and resets handle
        """
        try:
            self.handle.close()
            self.handle = None
        except TypeError:
            self.handle = None

    def accessory_task(self):
        """
        Perform Android USB Accessory communication task
        """
        self.handle = usb.core.find(idVendor=self.accessory_vendor_id)

        # check if device found
        if self.handle is None:
            raise ValueError("No compatible device found")

        # place device in accessory mode
        self.send_accessory_info()

        # android 'accessory connected' intent
        self.handle.set_configuration()
        time.sleep(1)
        configuration = self.handle.get_active_configuration()
        interface_num = configuration[(0, 0)].bInterfaceNumber
        interface = usb.util.find_descriptor(configuration, bInterfaceNumber=interface_num)

        # determine endpoint out
        ep_out = usb.util.find_descriptor(
            interface, custom_match=lambda e:
            usb.util.endpoint_direction(e.bEndpointAddress) == usb.util.ENDPOINT_OUT
        )
        print str(type(ep_out))

        # determine endpoint in
        ep_in = usb.util.find_descriptor(
            interface, custom_match=lambda e:
            usb.util.endpoint_direction(e.bEndpointAddress) == usb.util.ENDPOINT_IN
        )
        print str(type(ep_in))

        # start usb writer thread
        writer_thread = threading.Thread(target=self.writer, args=(ep_out, ))
        writer_thread.start()

        # perform read until all bytes in
        self.reader(ep_in)

        # close writer thread
        writer_thread.join()

    @staticmethod
    def writer(ep_out):
        """
        Send data bytes via USB connection
        """
        while True:
            try:
                length = ep_out.write([0], timeout=0)
                print("%d bytes written" % length)
                time.sleep(0.5)
            except usb.core.USBError:
                print("Error in writer thread")
                break
            except TypeError:
                print "Error while writing " + str(type(ep_out))
                break

    @staticmethod
    def reader(ep_in):
        """
        Read data bytes via USB connection
        """
        while True:
            try:
                data = ep_in.read(size=1, timeout=0)
                print("Read value %d" % data[0])
            except usb.core.USBError:
                print("Failed to send IN transfer")
                break

    def send_accessory_info(self):
        """
        Communicate with Android device via USB and describe the
        Raspberry Pi accessory
        """
        # read version info
        version = self.handle.ctrl_transfer(
            usb.util.CTRL_TYPE_VENDOR | usb.util.CTRL_IN,
            51, 0, 0, 2)
        print("Version is: %d" % struct.unpack('<H', version))

        # provide manufacturer info
        assert self.handle.ctrl_transfer(
            usb.util.CTRL_TYPE_VENDOR | usb.util.CTRL_OUT,
            52, 0, 0, self.manufacturer) == len(self.manufacturer)

        # provide model name info
        assert self.handle.ctrl_transfer(
            usb.util.CTRL_TYPE_VENDOR | usb.util.CTRL_OUT,
            52, 0, 1, self.model_name) == len(self.model_name)

        # provide description info
        assert self.handle.ctrl_transfer(
            usb.util.CTRL_TYPE_VENDOR | usb.util.CTRL_OUT,
            52, 0, 2, self.description) == len(self.description)

        # provide version info
        assert self.handle.ctrl_transfer(
            usb.util.CTRL_TYPE_VENDOR | usb.util.CTRL_OUT,
            52, 0, 3, self.version) == len(self.version)

        # provide url info
        assert self.handle.ctrl_transfer(
            usb.util.CTRL_TYPE_VENDOR | usb.util.CTRL_OUT,
            52, 0, 4, self.url) == len(self.url)

        # provide serial number info
        assert self.handle.ctrl_transfer(
            usb.util.CTRL_TYPE_VENDOR | usb.util.CTRL_OUT,
            52, 0, 5, self.serial_number) == len(self.serial_number)

        # provide end of info
        self.handle.ctrl_transfer(
            usb.util.CTRL_TYPE_VENDOR | usb.util.CTRL_OUT,
            53, 0, 0, None)

        time.sleep(1)