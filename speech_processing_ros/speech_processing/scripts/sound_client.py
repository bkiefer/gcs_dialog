#!/usr/bin/env python
# -*- coding: utf-8 -*-

import rospy
from rospy_message_converter import message_converter
import socket
import os
import json
from std_msgs.msg import String



class SoundClientNode:
    # Connect the socket to the port where the server is listening
    server_address = (os.environ.get("ROS_IP"), 11666)
    BUFFER_SIZE = 20

    def callback(self, data):
        if data:
            #define stream chunk
            chunk = 1024
            #open a wav format music
            f = wave.open(r"./pain.wav","rb")
            #instantiate PyAudio
            p = pyaudio.PyAudio()
            #open stream
            stream = p.open(format = p.get_format_from_width(f.getsampwidth()),
                            channels = f.getnchannels(),
                            rate = f.getframerate(),
                            output = True)
            #read data
            data = f.readframes(chunk)

            #play stream
            while data:
                stream.write(data)
                data = f.readframes(chunk)

            #stop stream
            stream.stop_stream()
            stream.close()

            #close PyAudio
            p.terminate()


    def __init__(self):
        rospy.Subscriber("/D3/drz_vit/sound",
                         String, self.callback)


if __name__ == '__main__':
    rospy.logdebug("starting sound client node")
    rospy.init_node("sound_client_node")
    bridge = SoundClientNode()
    rospy.spin()
