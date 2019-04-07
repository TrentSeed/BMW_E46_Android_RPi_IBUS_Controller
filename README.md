<p align="center"><img src="https://s3-us-west-1.amazonaws.com/connected-car-static-files/connected_car_logo_padded_blue.png" width="70%" style=""></p>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
![Python](https://img.shields.io/badge/python-v3.6+-blue.svg)
![Dependencies](https://img.shields.io/librariesio/github/TrentSeed/BMW_E46_Android_RPi_IBUS_Controller.svg)
[![GitHub Issues](https://img.shields.io/github/issues/TrentSeed/BMW_E46_Android_RPi_IBUS_Controller.svg)](https://github.com/TrentSeed/BMW_E46_Android_RPi_IBUS_Controller/issues)
![Last Updated](https://img.shields.io/github/last-commit/TrentSeed/BMW_E46_Android_RPi_IBUS_Controller.svg)
![Contributions welcome](https://img.shields.io/badge/contributions-welcome-orange.svg)
<br/><br/>

"Appify" your E46 BMW with a Raspberry Pi and your Smartphone! This repository contains the IBUS controller, as well as the Android application which supports smart phones and wearables. This is to be used with the IBUS USB interface which can be acquired from [Reslers.de](http://www.reslers.de/IBUS/), or from Amazon/eBay.

Blog Series on Medium:
- [Connecting a BMW to the Internet: Part One](https://medium.com/design-and-tech-co/connecting-a-bmw-to-the-internet-part-one-fbe18a54121d)
- [Connecting a BMW to the Internet: Part Two](https://medium.com/@trentseed/connecting-a-bmw-to-the-internet-part-two-1ee2ea44d4a2)
- [Connecting a BMW to the Internet: Part Three](https://medium.com/@trentseed/connecting-a-bmw-to-the-internet-part-three-89fb322b1dcb)

## Overview 

There are three main components of the solution:
* Android Tablet - Primary front-end device for interacting with the car
* Raspberry Pi - Mini-PC that handles communication between the IBUS adapter and the Android device
* IBUS USB Adapter - USB adapter that provides USB/UART interface from physical wire in car

![Android_RPi_IBUS_Overview](https://s3-us-west-1.amazonaws.com/connected-car-static-files/connected_car_overview2.png)

There is also an Android Wear (4.4W) component that allows you to control your vehicle from your smart watch!
* Android Wear Device - Secondary device that provides quick interactions with the car

![Android_WEAR_IBUS_Overview](https://s3-us-west-1.amazonaws.com/connected-car-static-files/IBUS+Wear+UI+On+G+Watch.png)

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
* Pair Android device with Raspberry Pi via Bluetooth

#### 2a. Running with Docker
* Install docker and docker-compose
	* `curl -fsSL get.docker.com -o get-docker.sh && sh get-docker.sh`
	* `pip install docker-compose`
* Download project and run with docker-compose
	* `git clone https://github.com/TrentSeed/BMW_E46_Android_RPi_IBUS_Controller controller`
	* `cd controller/connected_car_controller/`
	* `docker-compose up --build -d`

#### 2b. Running without Docker
* Install packages
	* `apt-get install build-essential bluez bluez-tools libbluetooth-dev`
* Download project
	* `git clone https://github.com/TrentSeed/BMW_E46_Android_RPi_IBUS_Controller controller`
	* `cd controller/connected_car_controller/`
* Install python modules:
	* `pip install -r requirements.txt`
* Run the project: `python3 start_controller.py bmw-e46`

### 3. Android Mobile / Tablet
* Update `BluetoothInterface.remoteBluetoothAddress` of 'connected_car_app/common' with RPi Bluetooth address
* Build Android project `connected_car_app/` via Android Studio
* Install the applicaton
	* You can build and run in Android Studio by connecting your devices
	* You can also install by transfering the built APKs 
		* `connected_car_app/app/build/outputs/apk/debug/app-debug.apk`
		* `connected_car_app/ibuswear/build/outputs/apk/debug/ibuswear-debug.apk`

## How To Get Started
* Install the prerequisites above
* Ensure Android device is paired with Raspberry Pi via Bluetooth
* Run the controller as daemon: `docker-compose up -d`
* Launch Connected Car App on your Android device or wearable