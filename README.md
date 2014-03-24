BMW E46 - Android RasperryPi IBUS Controller
=====

IBUS interface for my E46 BMW written in Python & Java
This is to be used with the USB interface which can be acquired from [Reslers.de](http://www.reslers.de/IBUS/)

## Overview
There are 2 main components:
**Android** - Front-end user interface for navigation
**Raspberry Pi** - Interfaces with IBUS via USB and Android via USB

### Architecture
In progress

## Pre-Requisites
* python, python-setuptools
	* `apt-get install python python-setuptools`
* **Python modules:** pyserial
	* `easy_install pyserial`

## How to use
* Install the prerequisites above
* Plug in IBUS USB device
* Plug in Android USB device
* Run: `./start.py`
