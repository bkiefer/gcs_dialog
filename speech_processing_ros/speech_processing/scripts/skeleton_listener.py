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
    labels = ["body_id", "tracking_status", "gesture", "position2D", "centerOfMass", "joint_position_head",
     "joint_position_left_shoulder", "joint_position_left_elbow", "joint_position_left_hand", "joint_position_right_shoulder",
     "joint_position_right_elbow", "joint_position_right_hand"]

    def callback(self, data):
        if data:
            # open socket and send data
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.connect(self.server_address)
            try:
                dictionary = message_converter.convert_ros_message_to_dictionary(
                data)
                dict_2 = {}
                for k, v in  dictionary.items():
                    rospy.loginfo(v)
                    rospy.loginfo(type(v))
                    if k in labels :
                        if k isinstance(v, dict):
                            v_dict = {}
                            for k1, v1 in v.items():
                                v_dict[k1] = round(v1, 2)
                                dict_2[k.replace("joint_position", "p").replace("left", "l").replace("right", "r").replace("shoulder", "s").replace("elbow", "e").replace("hand", "h")] = v_dict
                        else:
                             dict_2[k.replace("joint_position", "p").replace("left", "l").replace("right", "r").replace("shoulder", "s").replace("elbow", "e").replace("hand", "h")]] = v
                dict_2["type"] = "Skeleton"
                sock.sendall(json.dumps(dict_2))
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
