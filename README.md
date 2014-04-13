BMW E46 - Android, Raspberry Pi, IBUS Controller
=====

IBUS interface for my E46 BMW written in Python & Java. This is to be used with the IBUS USB interface which can be acquired from [Reslers.de](http://www.reslers.de/IBUS/), or from Amazon/eBay.

## Overview
There are three main components of the solution:<br />
**Android Tablet** - Primary front-end device for interacting with the car<br />
**Raspberry Pi** - Mini-PC that handles communication between the IBUS adapter and the Android device<br />
**IBUS USB Adapter** - USB adapter that provides USB/UART interface from physical wire in car<br />

![Android_RPi_IBUS_Overview](http://trentseed.com/img/projects/bmw_raspberrypi_android_ibus_overview_v2.jpg)

## Pre-Requisites
### 1. Car Installation
* Remove OEM Navigation Head Unit from BMW
* Tap the 12V (red) wire, the GND (black) wire, and the IBUS (red/white/yellow) wire
* Connect IBUS USB adapter to BMW (using 12V, GND, IBUS)
* Install USB 12V - 5V adapter (using 12V, GND)
* Install Raspberry Pi in the dash (and power w/ 5V from adapter)
* Connect IBUS USB adapter to Raspberry Pi via USB cable
* Connect Bluetooth USB dongle to Raspberry Pi
* Connect Ethernet cable to Raspberry Pi (and wire to glove box)

### 2. Raspberry Pi
* Install Raspbian (or another OS that supports python)
    * One tutorial [can be found here](http://computers.tutsplus.com/articles/how-to-flash-an-sd-card-for-raspberry-pi--mac-53600)
* Install *python*, *python-setuptools*
	* `apt-get install python python-setuptools`
* Install python modules: *pyserial*, *libbluetooth-dev*, *pybluz*
	* `easy_install pyserial`
	* `apt-get install libbluetooth-dev`
	* [`pip install pybluz`](https://code.google.com/p/pybluez/wiki/Documentation)
* Install Bluetooth stack and confirm dongle/adapter is supported
    * One tutorial [can be found here](http://plugable.com/2013/12/10/using-the-plugable-usb-bt4le-bluetooth-adapter-with-the-raspberry-pi)
    * Follow the ['accepted answer' instructions](http://stackoverflow.com/questions/14618277/rfcomm-without-pairing-using-pybluez-on-debian/14827036#14827036) to fix a known bug
* Copy `python/` contents to RPi
* Update `python/android_service/bt.py` with RPi Bluetooth address

### 3. Android
* Update `BluetoothInterface.remoteBluetoothAddress` with RPi Bluetooth address
* Build Android project `android/`
* Install `android/bin/BMW_RPi_IBUS_Controller.apk`

## How To Get Started
* Install the prerequisites above
* Pair Android device with Raspberry Pi via Bluetooth
* Run as Daemon: `nohup python start.py 2>/dev/null 1>/dev/null &`
* Launch Android IBUS app