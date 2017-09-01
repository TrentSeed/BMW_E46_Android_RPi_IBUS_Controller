#!/bin/bash
# Executes the core application for the vehicle controllers. This process
# is run in the background, and does not block the terminal. All output is
# redirected to '/dev/null'
echo running 'python start.py...'
nohup python start_controller.py bmw-e46 2>/dev/null 1>/dev/null &
