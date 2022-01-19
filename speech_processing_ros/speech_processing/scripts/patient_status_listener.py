#!/usr/bin/env python
# -*- coding: utf-8 -*-

import rospy
from rospy_message_converter import message_converter
import socket
import os
import json
from gcs_msgs.msg import PatientStatus



class PatientStatusListener:
    # Connect the socket to the port where the server is listening
    server_address = (os.environ.get("ROS_IP"), 11666)
    BUFFER_SIZE = 20
    labels = ["patient_id", "age", "gesture", "gender", "are_eyes_open", "is_mouth_open",
     "has_moved", "has_moved_left_arm", "has_moved_left_hand", "has_moved_left_leg",
     "has_moved_right_arm", "has_moved_right_hand", "has_moved_right_leg", "gesture", "joint_position_head_confidence",
     "joint_position_left_elbow_confidence", "joint_position_left_hand_confidence", "joint_position_right_elbow_confidence",
     "joint_position_right_hand_confidence", "joint_position_left_ankle_confidence", "joint_position_right_ankle_confidence"]

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
                        if isinstance(v, dict):
                            v_dict = {}
                            for k1, v1 in v.items():
                                v_dict[k1] = round(v1, 2)
                                dict_2[k.replace("joint_position", "p").replace("left", "l").replace("right", "r").replace("ankle", "a").replace("elbow", "e").replace("hand", "h")] = v_dict
                        else:
                             dict_2[k.replace("joint_position", "p").replace("left", "l").replace("right", "r").replace("ankle", "a").replace("elbow", "e").replace("hand", "h")] = v
                dict_2["type"] = "PatientStatus"
                sock.sendall(json.dumps(dict_2))
            finally:
                sock.close()


    def __init__(self):
        rospy.Subscriber("/D3/drz_vit/patient_status",
                         PatientStatus, self.callback)


if __name__ == '__main__':
    rospy.logdebug("starting patient_status_listener")
    rospy.init_node("patient_status_listener")
    bridge = PatientStatusListener()
    rospy.spin()
