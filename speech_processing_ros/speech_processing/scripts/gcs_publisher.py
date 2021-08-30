#!/usr/bin/env python
# -*- coding: utf-8 -*-

import rospy
from rospy_message_converter import json_message_converter
import socket
import os
import sys
import json
import roslib
import re
from std_msgs.msg import String
from gcs_msgs.msg import GCS
from threading import Thread
from SocketServer import ThreadingMixIn


class GCSPublisher:
    local_address = (os.environ.get("DIA_IP"), 11114)
    BUFFER_SIZE = 20  # Usually 1024, but we need quick response

    def __init__(self):
        print("init bridge at " + str(self.local_address))
        self.publisher = rospy.Publisher(
            '/gcs', GCS, queue_size=1)
        tcpServer = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        tcpServer.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        tcpServer.bind(self.local_address)
        threads = []
        while True:
            tcpServer.listen(4)
            print(
                "Multithreaded Python server : Waiting for connections from TCP clients...")
            (conn, (ip, port)) = tcpServer.accept()
            newthread = self.ClientThread(ip, port, conn, self.publisher)
            newthread.start()
            threads.append(newthread)
        for t in threads:
            t.join()

    # Multithreaded Python server : TCP Server Socket Thread Pool
    class ClientThread(Thread):

        def __init__(self, ip, port, conn, publisher):
            Thread.__init__(self)
            self.ip = ip
            self.port = port
            self.conn = conn
            self.pub = publisher
            print("[+] New server socket thread started for " +
                  ip + ":" + str(port))

        def run(self):
            data = self.conn.recv(2048)
            print("Server received data:", data)
            self.conn.send("Echo")  # echo
            jsonData = json.loads(data)
            message = json_message_converter.convert_json_to_ros_message(
                'gcs_msgs/GCS', data)
            print(message)
            self.pub.publish(message)


if __name__ == '__main__':
    print("init ros")
    rospy.init_node('gcsPublisher')
    bridge = GCSPublisher()
    print("rospy.spin")
    rospy.spin()
