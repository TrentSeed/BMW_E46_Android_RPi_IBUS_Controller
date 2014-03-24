#!/usr/bin/env python


class ANDROID():

    # configuration
    handle = None

    def __init__(self):
        """
        Initializes bi-directional communication with ANDROID via USB
        """
        # TODO interface with Android via Accessory Protocol
        return

    def destroy(self):
        """
        Closes USB connection and resets handle
        """
        try:
            self.handle.close()
            self.handle = None
        except Exception:
            self.handle = None