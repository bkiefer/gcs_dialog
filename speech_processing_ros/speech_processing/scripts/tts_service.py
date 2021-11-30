#!/usr/bin/env python
# -*- coding: utf-8 -*-

import rospy
from rospy_message_converter import message_converter
from gcs_msgs.srv import TtsService, TtsServiceResponse
from gcs_msgs.msg import TtsVolume
import os
import StringIO
import wave
from std_srvs.srv import Trigger, TriggerResponse
from marytts import MaryTTS
import pyaudio
import audioop


class ElevatorDialogueService:

    # Connect the socket to the port where the server is listening
    marytts = MaryTTS(os.environ.get("ROS_IP"))
    marytts.locale = "de"
    marytts.voice = "dfki-pavoque-neutral-hsmm"

    chunk = 512

    def __init__(self):
        rospy.logdebug("init tts_service")
        self.service = rospy.Service(
            "/mlt/tts_service", TtsService, self.callback)
        self.publisher = rospy.Publisher(
            '/tts_volume', TtsVolume, queue_size=1)
        self.mic_toggle_service = rospy.ServiceProxy(
            "/mlt/mic_toggle_service", Trigger)

    def callback(self, req):
        rospy.loginfo("Callback on " + req.message)
        if (req.message != ""):
            message = req.message.replace("ae", u"ä").replace("Ae", u"Ä").replace(
                "oe", u"ö").replace("Oe", u"Ö").replace("Ue", u"ü").replace("ue", u"ü")
            wavs = self.marytts.synth_wav(message)
            wf = wave.open(StringIO.StringIO(wavs))
            self.mic_toggle_service()
            self.playback(wf)
            self.mic_toggle_service()
        else:
            rospy.loginfo("ignoring empty input")
        rospy.loginfo("sending response")
        response = TtsServiceResponse(True)
        return response

    def playback(self, wf):
        try:
            p = pyaudio.PyAudio()
            # Open a .Stream object to write the WAV file to
            # 'output = True' indicates that the sound will be played rather than recorded
            stream = p.open(format=p.get_format_from_width(wf.getsampwidth()),
                            channels=wf.getnchannels(),
                            rate=wf.getframerate(),
                            output=True)
            # Read data in chunks
            data = wf.readframes(self.chunk)
            # Play the sound by writing the audio data to the stream
            while data != '':
                rms = audioop.rms(data, 2)
                message = TtsVolume()
                message.volume = rms
                self.publisher.publish(message)
                stream.write(data)
                data = wf.readframes(self.chunk)
            # Close and terminate the stream
        except:
            pass
        finally:
            stream.close()
            p.terminate()

            #status_message = TtsStatus();
            #status_message.status = 3;
            # self.status_publisher.publish(status_message)


if __name__ == "__main__":
    rospy.init_node("tts_service")
    service = ElevatorDialogueService()
    rospy.spin()
