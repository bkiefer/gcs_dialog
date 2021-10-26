#!/usr/bin/env python
# -*- coding: utf-8 -*-

import rospy
from rospy_message_converter import message_converter
import socket
import os
import json
from body_tracker_msgs.msg import Skeleton


class AsrListener:
    # Connect the socket to the port where the server is listening
    server_address = (os.environ.get("ROS_IP"), 11666)
    BUFFER_SIZE = 20

    def callback(self, data):
        if data.transcript[0] != "":
            # open socket and send data
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.connect(self.server_address)
            try:
                dictionary = message_converter.convert_ros_message_to_dictionary(
                    data)
                dictionary["type"] = "Skeleton"
                sock.sendall(json.dumps(dictionary))
            finally:
                sock.close()

    def __init__(self):
        rospy.Subscriber("/D3/drz_vit/body_tracker/skeleton",
                         Skeleton, self.callback)


if __name__ == '__main__':
    rospy.logdebug("starting skeleton_listener")
    rospy.init_node("skeleton_listener")
    bridge = AsrListener()
    rospy.spin()
