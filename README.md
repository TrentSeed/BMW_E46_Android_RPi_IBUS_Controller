BMW E46 - Android, Raspberry Pi, IBUS Controller
=====

IBUS interface for my E46 BMW written in Python & Java. This is to be used with the IBUS USB interface which can be acquired from [Reslers.de](http://www.reslers.de/IBUS/), or from Amazon/eBay.

## Overview
There are 3 main components of the solution:<br />
**Android Tablet** - Primary front-end device for interacting with the car<br />
**Raspberry Pi** - Mini-PC that handles communication between the IBUS adapter and the Android device<br />
**IBUS USB Adapter** - USB adapter that provides USB/UART interface from physical wire in car<br />

### Architecture
![Android_RPi_IBUS_Overview](http://trentseed.com/img/projects/bmw_raspberrypi_android_ibus_overview.jpg)

## Hardware Pre-Requisites
### 1. IBUS USB Adapter
* The IBUS USB adapter must be physically connected to the 12V, GND, & IBUS wires attached to the [now disconnected] OEM Navigation Head Unit harness.

## Software Pre-Requisites
### 1. Raspberry Pi
* Install Raspbian (or another OS that supports python)
    * One tutorial [can be found here](http://computers.tutsplus.com/articles/how-to-flash-an-sd-card-for-raspberry-pi--mac-53600)
* Install *python*, *python-setuptools*
	* `apt-get install python python-setuptools`
* Install python module: *pyserial*
	* `easy_install pyserial`
* Copy `python/` contents to RPi

### 2. Android
* Build Android project `android/`
* Install `android/bin/BMW_RPi_IBUS_Controller.apk`

## How To Get Started
* Install the prerequisites above
* Plug IBUS USB device into Raspberry Pi
* Plug Android USB device into Raspberry Pi
* Run: `python/start.py`
