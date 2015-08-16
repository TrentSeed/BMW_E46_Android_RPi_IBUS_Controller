#!/usr/bin/env python
import struct
import time
import threading
import usb

"""
Android communication via USB.
Open Issues:
    - Claim interface issue (Windows) w/ libusb-win32 backend
    - Operation timed out (Ubuntu)
"""


class AndroidUSBService(object):

    # configuration (HTC One X)
    accessory_vendor_id = 0x04E8  # (HTC One X - 0x0BB4)  # (Galaxy S4 - 0x04E8)
    accessory_product_id = 0x6860  # (HTC One X - 0x0DFA)  # (Galaxy S4 - 0x6860)
    description = "BMW IBUS via Raspberry Pi"
    endpoint_in = None
    endpoint_out = None
    handle = None
    interface = None
    manufacturer = "Raspberry Pi Foundation"
    model_name = "Raspberry Pi"
    serial_number = "1337"
    url = "https://github.com/TrentSeed/BMW_E46_Android_RPi_IBUS_Controller/"
    version = "0.1"

    def __init__(self):
        """
        Initializes bi-directional communication with ANDROID via USB
        """
        self.handle = usb.core.find(idVendor=self.accessory_vendor_id)
        if self.handle is None:
            raise ValueError("Android device is not connected")

        # place device in accessory mode and listen for commands
        self.set_protocol()
        self.set_accessory_mode()
        self.set_endpoints()
        self.start_accessory_tasks()
        return

    def destroy(self):
        """
        Closes USB connection and resets handle
        """
        try:
            self.handle.reset()
            self.handle.close()
            self.handle = None
        except ValueError:
            self.handle = None

    def set_protocol(self):
        """
        Communicate with android device, set configuration,
        and determine supported Open Accessory Protocol version
        """
        try:
            self.handle.set_configuration()
        except usb.core.USBError as e:
            if e.errno == 16:
                print('Device already configured, should be OK')
            else:
                print('Configuration failed')
        ret = self.handle.ctrl_transfer(0xC0, 51, 0, 0, 2)
        protocol = ret[0]
        print('Protocol version: %i' % protocol)
        if protocol < 2:
            print('Android Open Accessory protocol v2 not supported')
        return

    def set_accessory_mode(self):
        """
        Communicate with Android device via USB and describe the
        Raspberry Pi accessory (refer to Open Accessory Protocol)
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

    def set_endpoints(self):
        """
        Get USB configuration and determine IN and OUT endpoints
        """
        # use new android accessory interface?
        self.handle = usb.core.find(idProduct=0x2D01, idVendor=0x18D1)  #?
        self.handle.set_configuration()                                 #?

        configuration = self.handle.get_active_configuration()
        interface_num = configuration[(0, 0)].bInterfaceNumber
        self.interface = usb.util.find_descriptor(configuration, bInterfaceNumber=interface_num)

        # determine endpoint out
        self.endpoint_out = usb.util.find_descriptor(
            self.interface, custom_match=lambda e:
            usb.util.endpoint_direction(e.bEndpointAddress) == usb.util.ENDPOINT_OUT
        )
        #self.endpoint_out = self.handle[0][(0, 0)][1]

        # determine endpoint in
        self.endpoint_in = usb.util.find_descriptor(
            self.interface, custom_match=lambda e:
            usb.util.endpoint_direction(e.bEndpointAddress) == usb.util.ENDPOINT_IN
        )
        #self.handle.detach_kernel_driver(self.interface)

    def start_accessory_tasks(self):
        """
        Performs regular communication with Android USB device
        """
        while True:
            self.perform_accessory_task()

    def perform_accessory_task(self):
        """
        Perform Android USB Accessory communication task
        """
        print "Performing accessory task..."
        time.sleep(5)

        for cfg in self.handle:
            print "Configuration: " + str(cfg.bConfigurationValue) + '\n'
            for interface in cfg:
                print '\tInterface: ' + str(interface.bInterfaceNumber) + ',' + str(interface.bAlternateSetting) + '\n'
                for ep in interface:
                    print '\t\tEndpoint: ' + str(ep.bEndpointAddress) + '\n'

        try:
            print "Writing data..."
            self.endpoint_out.write('test', timeout=0)
        except usb.core.USBError as e:
            print e

        #print self.endpoint_out.write('0x01')
        #print self.endpoint_in.read(size=1, timeout=10)
        # start usb writer thread
        #writer_thread = threading.Thread(target=self.writer, args=(ep_out, ))
        #writer_thread.start()

        # perform read until all bytes in
        #self.reader(ep_in)

        # close writer thread
        #writer_thread.join()

    def writer(self):
        """
        Send data bytes via USB connection to Android
        """
        while True:
            try:
                assert len(self.endpoint_out.write('test')) == len('test')
                time.sleep(0.5)
            except usb.core.USBError:
                print "Error in writer thread"
                break
            except TypeError:
                print "Error while writing " + str(type(self.endpoint_out))
                break

    def reader(self):
        """
        Read data bytes via USB connection from Android
        """
        while True:
            try:
                data = self.endpoint_in.read(size=1, timeout=0)
                print "Read value %d" % data[0]
            except usb.core.USBError:
                print "Failed to send IN transfer"
                break