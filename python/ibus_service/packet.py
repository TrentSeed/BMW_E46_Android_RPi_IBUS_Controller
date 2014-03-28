

class IBUSPacket():

    source_id = None
    length = None
    destination_id = None
    data = None
    xor_checksum = None
    raw = None

    def __init__(self, source_id, length, destination_id, data, xor_checksum, raw = None):
        """
        Initializes packet object
        """
        self.source_id = source_id
        self.length = length
        self.destination_id = destination_id
        self.data = data
        self.xor_checksum = xor_checksum
        self.raw = raw
        return

    def is_valid(self):
        """
        Verifies packet information & XOR checksum
        """
        if self.source_id is None or self.destination_id is None \
                or self.data is None or self.xor_checksum is None:
            return False

        # TODO XOR checksum calculation

        return True

    def __str__(self):
        """
        Human-readable string representing packet data
        """
        try:
            return "Raw = " + self.raw + "\n"\
                   + "Source = " + self.get_device_name(self.source_id) + "\n"\
                   + "Destination = " + self.get_device_name(self.destination_id) + "\n"\
                   + "Data = " + self.data.decode("hex") + "\n"
        except TypeError:
            return "Raw = " + self.raw + "\n"\
                   + "Source = " + self.get_device_name(self.source_id) + "\n"\
                   + "Destination = " + self.get_device_name(self.destination_id) + "\n"\
                   + "Data = " + self.data + "\n"

    @staticmethod
    def get_device_name(device_id):
        """
        Returns device name for provided id
        i.e. 50 - MFL Multi Functional Steering Wheel Buttons
        """
        device_names = {
            "00": "Broadcast",
            "18": "CDW - CDC CD-Player",
            "30": "?????",
            "3b": "NAV Navigation/Video Module",
            "3f": "?????",
            "43": "Menu Screen",
            "44": "?????",
            "50": "MFL Multi Functional Steering Wheel Buttons",
            "60": "PDC Park Distance Control",
            "68": "RAD Radio",
            "6a": "DSP Digital Sound Processor",
            "7f": "?????",
            "80": "IKE Instrument Kombi Electronics",
            "a8": "?????",
            "bb": "TV Module",
            "bf": "LCM Light Control Module",
            "c0": "MID Multi-Information Display Buttons",
            "c8": "TEL Telephone",
            "d0": "Navigation Location",
            "e7": "OBC Text Bar",
            "e8": "?????",
            "ed": "Lights, Wipers, Seat Memory",
            "f0": "BMB Board Monitor Buttons",
            "ff": "Broadcast",
        }
        try:
            return device_names[device_id]
        except KeyError:
            return "Unknown"