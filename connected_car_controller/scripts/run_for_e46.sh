#!/bin/bash
# Executes the core application for the vehicle controller. This process
# is run in the background, and does not block the terminal. All output is
# redirected to '/dev/null'
echo running 'nohup python start_controller.py bmw-e46 2>/dev/null 1>/dev/null &'
nohup python start_controller.py bmw-e46 2>/dev/null 1>/dev/null &
