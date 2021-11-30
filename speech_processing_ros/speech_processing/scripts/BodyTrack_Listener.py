#!/usr/bin/env python
# -*- coding: utf-8 -*-

import rospy
from rospy_message_converter import message_converter
import socket
import os
import json
from body_tracker_msgs.msg import BodyTrackerArray


class AsrListener:
    # Connect the socket to the port where the server is listening
    server_address = (os.environ.get("DIA_IP"), 11666)
    BUFFER_SIZE = 20
    labels = ["body_id", "tracking_status", "gesture", "face_found", "age", "gender", "name", "angry", "suprise", "happy", "neutral", "position2d", "position3d", "face_center"]

    def callback(self, data):
        if len(data.detected_list) > 0:
            # open socket and send data
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.connect(self.server_address)
            try:
               dictionary = message_converter.convert_ros_message_to_dictionary(
                               data.detected_list[0])
               dict_2 = {}
               for k, v in  dictionary.items():
                   rospy.loginfo(v)
                   rospy.loginfo(type(v))
                   if k  in labels:
                       if isinstance(v, dict):
                           v_dict = {}
                           for k1, v1 in v.items():
                               v_dict[k1] = round(v1, 2)
                               dict_2[k] = v_dict
                       else:
                           dict_2[k] = v
               dict_2["type"] = "BodyTrack"
               sock.sendall(json.dumps(dict_2))
            finally:
                sock.close()

    def __init__(self):
        rospy.Subscriber("/D3/drz_vit/body_tracker_array/position",
                         BodyTrackerArray, self.callback)


if __name__ == '__main__':
    rospy.logdebug("starting bodyTracker listener")
    rospy.init_node("body_tracker_listener")
    bridge = AsrListener()
    rospy.spin()
