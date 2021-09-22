#!/usr/bin/env python
# -*- coding: utf-8 -*-

import rospy
from rospy_message_converter import message_converter
import socket
import os
import json
from speech_recognition_msgs.msg import SpeechRecognitionCandidates

class AsrListener:
    # Connect the socket to the port where the server is listening
    server_address = (os.environ.get("DIA_IP"), 11666)
    BUFFER_SIZE = 10

    def callback(self, data):
        if data.transcript[0] != "":
            #open socket and send data
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.connect(self.server_address)
            try:
                dictionary = message_converter.convert_ros_message_to_dictionary(data)
                dictionary["type"] = "SpeechRecognitionCandidates"
                sock.sendall(json.dumps(dictionary))
            finally:
                sock.close()



    def __init__(self):
        rospy.Subscriber("speech_to_text", SpeechRecognitionCandidates, self.callback)

if __name__ == '__main__':
    rospy.logdebug("starting asr_listener")
    rospy.init_node("asr_listener")
    bridge = AsrListener()
    rospy.spin()
