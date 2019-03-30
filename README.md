<p align="center"><img src="https://s3-us-west-1.amazonaws.com/connected-car-static-files/connected_car_logo_padded.png" width="70%" style=""></p>

"Appify" your E46 BMW with a Raspberry Pi and your Smartphone! This repository contains the IBUS controller, as well as the Android application which supports smart phones and wearables. This is to be used with the IBUS USB interface which can be acquired from [Reslers.de](http://www.reslers.de/IBUS/), or from Amazon/eBay.

Blog Series on Medium:
- [Connecting a BMW to the Internet: Part One](https://medium.com/design-and-tech-co/connecting-a-bmw-to-the-internet-part-one-fbe18a54121d)
- [Connecting a BMW to the Internet: Part Two](https://medium.com/@trentseed/connecting-a-bmw-to-the-internet-part-two-1ee2ea44d4a2)

## Overview 

There are three main components of the solution:
**Android Tablet** - Primary front-end device for interacting with the car
**Raspberry Pi** - Mini-PC that handles communication between the IBUS adapter and the Android device
**IBUS USB Adapter** - USB adapter that provides USB/UART interface from physical wire in car

![Android_RPi_IBUS_Overview](https://s3-us-west-1.amazonaws.com/connected-car-static-files/connected_car_overview.jpg)

There is also an Android Wear (4.4W) component that allows you to control your vehicle from your smart watch!
**Android Wear Device** - Secondary device that provides quick interactions with the car

![Android_WEAR_IBUS_Overview](https://s3-us-west-1.amazonaws.com/connected-car-static-files/Android_Wear_Connected_BMW_App_UI.jpg)

## Pre-Requisites
### 1. Car Installation
* Remove OEM Navigation Head Unit from BMW
* Tap the 12V (red) wire, the GND (black) wire, and the IBUS (red/white/yellow) wire
* Connect IBUS USB adapter to BMW (using 12V, GND, IBUS)
* Install USB 12V - 5V adapter (using 12V, GND)
* Install Raspberry Pi in the dash (and power w/ 5V from adapter)
* Connect IBUS USB adapter to Raspberry Pi via USB cable
* Connect Ethernet cable to Raspberry Pi (and wire to glove box)

### 2. Raspberry Pi 3
* Install Raspbian (or another OS that supports python)
    * One tutorial [can be found here](http://computers.tutsplus.com/articles/how-to-flash-an-sd-card-for-raspberry-pi--mac-53600)
* Install python modules: *pyserial*, *libbluetooth-dev*, *pybluz*
	* `pip install -r requirements.txt`
	* `apt-get install libbluetooth-dev`
	* [`pip install pybluz`](https://code.google.com/p/pybluez/wiki/Documentation)
* Install Bluetooth stack and confirm dongle/adapter is supported
    * One tutorial [can be found here](http://plugable.com/2013/12/10/using-the-plugable-usb-bt4le-bluetooth-adapter-with-the-raspberry-pi)
    * Follow the ['accepted answer' instructions](http://stackoverflow.com/questions/14618277/rfcomm-without-pairing-using-pybluez-on-debian/14827036#14827036) to fix a known bug
* Copy `connected_car_controller/` contents to RPi

### 3. Android Mobile / Tablet
* Update `BluetoothInterface.remoteBluetoothAddress` of 'connected_car_app/app' with RPi Bluetooth address
* Update `BluetoothInterface.remoteBluetoothAddress` of 'connected_car_app/ibuswear' with RPi Bluetooth address
* Build Android project `connected_car_app/` via Android Studio
* Install `connected_car_app/app/build/outputs/apk/debug/app-debug.apk` to mobile/tablet device
* Install `connected_car_app/ibuswear/build/outputs/apk/debug/ibuswear-debug.apk` to smart watch

## How To Get Started
* Install the prerequisites above
* Pair Android device with Raspberry Pi via Bluetooth
* Run as Daemon: `nohup python start_controller.py bmw-e46 2>/dev/null 1>/dev/null &`
* Launch Connected Car App on your Android device or wearable