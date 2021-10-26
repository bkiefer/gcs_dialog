#!/usr/bin/env python

import rospy
from rospy_message_converter import json_message_converter, message_converter
import socket
from gcs_msgs.srv import TtsService
import os
import json
from threading import Thread


class TtsPublisher:
    local_address = (os.environ.get("ROS_IP"), 11113)
    BUFFER_SIZE = 20  # Usually 1024, but we need quick response

    def __init__(self):
        rospy.loginfo("init bridge")
        self.tts_service = rospy.ServiceProxy("/mlt/tts_service", TtsService)
        tcpServer = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        tcpServer.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        tcpServer.bind(self.local_address)
        threads = []
        while True:
            tcpServer.listen(4)
            rospy.loginfo(
                "Multithreaded Python server : Waiting for connections from TCP clients...")
            (conn, (ip, port)) = tcpServer.accept()
            newthread = self.ClientThread(ip, port, conn, self.tts_service)
            newthread.start()
            threads.append(newthread)
        for t in threads:
            t.join()

    # Multithreaded Python server : TCP Server Socket Thread Pool
    class ClientThread(Thread):

        def __init__(self, ip, port, conn, tts_service):
            Thread.__init__(self)
            self.ip = ip
            self.port = port
            self.conn = conn
            self.tts_service = tts_service
            self.server_address = (os.environ.get("DIA_IP"), 11666)
            rospy.loginfo(
                "[+] New server socket thread started for " + ip + ":" + str(port))

        def run(self):
            data = self.conn.recv(2048)
            self.conn.send("")  # echo
            message = json_message_converter.convert_json_to_ros_message(
                'intuitiv_msgs/Tts', data)
            try:
                result = self.tts_service(message.message)
            except rospy.ServiceException:
                return

            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.connect(self.server_address)
            try:
                rospy.loginfo(result)
                dictionary = dict()
                dictionary["type"] = "tts_done"
                sock.sendall(json.dumps(dictionary))
            finally:
                sock.close()


if __name__ == '__main__':
    rospy.init_node('ttsPublisher')
    bridge = TtsPublisher()
    rospy.spin()
