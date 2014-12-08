

class BlueBUSPacket():

    # class variables
    TYPE_PACKET = 0
    TYPE_COMMAND = 1

    # instance variables
    type = None
    data = None

    def __init__(self, packet_type, data):
        """
        Initializes packet object
        ---------------------------------------
        | { "type": "1"; "data": "{...}"; } |
        ---------------------------------------
        """
        self.type = packet_type
        self.data = data
        return

    def as_dict(self):
        """
        Returns dict() representation of object
        """
        return dict(type=self.type,
                    data=self.data)

    def is_valid(self):
        """
        Determines if packet is valid
        """
        valid_packets = (BlueBUSPacket.TYPE_PACKET, BlueBUSPacket.TYPE_COMMAND)
        return self.type in valid_packets

    def __str__(self):
        """
        Human-readable string representing packet data
        """

        return "BlueBusPacket\nType = " + self.get_type_name(self.type) + "\n"\
               + "Data = " + str(self.data) + "\n"

    def get_ibus_packets(self):
        """
        Returns encapsulated IBUSPacket(s)
        """
        if not self.is_type_ibus_packet():
            return False

        # TODO create IBUSPacket object(s)

    def is_type_ibus_packet(self):
        """
        Determines if encapsulated IBUSPacket type
        """
        return self.type == "0"

    def is_type_command(self):
        """
        Determines if command type
        """
        return self.type == "1"

    @staticmethod
    def get_type_name(packet_type):
        """
        Returns BlueBUSPacket type description
        """
        packet_type = str(packet_type)
        packet_types = {
            "0": "Packet (Encapsulated IBUS Packet)",
            "1": "Command (Proprietary Command)",
        }
        try:
            return packet_types[packet_type]
        except KeyError:
            return "Unknown"