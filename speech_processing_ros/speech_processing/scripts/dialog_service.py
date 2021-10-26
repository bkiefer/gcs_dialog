#!/usr/bin/env python
# -*- coding: utf-8 -*-

import rospy
from rospy_message_converter import message_converter
from rospy_message_converter import json_message_converter
from gcs_msgs.srv import DialogService, DialogServiceResponse
import os
import json
import socket


class DialogueService:

    # Connect the socket to the port where the server is listening
    server_address = (os.environ.get("ROS_IP"), 11666)
    BUFFER_SIZE = 20

    def __init__(self):
        rospy.logdebug("init tts_service")
        self.service = rospy.Service(
            "/mlt/dialog_service", DialogService, self.callback)



    def callback(self, req):
        #send trigger to DialogueSystem
        #open socket and send data
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.setblocking(True)
        sock.connect(self.server_address)
        self.data = ""
        try:
            dictionary = message_converter.convert_ros_message_to_dictionary(req)
            dictionary["type"] = "GCSService"
            sock.sendall(json.dumps(dictionary))
            #wait for answer
            while 1:
                self.data = sock.recv(1024)
                if(len(self.data)>0):
                    rospy.logdebug("Server received data:", self.data)
                    break;
                else:
                    self.sleep(100)
        finally:
            # send response
            sock.close()
        response = json_message_converter.convert_json_to_ros_message(
            'gcs_msgs/GCS', self.data)
        return response




if __name__ == "__main__":
    rospy.init_node("gcs_service")
    service = DialogueService()
    rospy.spin()
