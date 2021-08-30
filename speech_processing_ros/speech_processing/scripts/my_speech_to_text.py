#!/usr/bin/env python3.6
# -*- coding: utf-8 -*-
# Author: Christian Willms <christian.willms@dfki.de>


import rospy
import sys
import wave
import time
import numpy as np
import io
import audioop
from timeit import default_timer as timer
try:
    #import speech_recognition as SR
    import deepspeech as DS
except ImportError as e:
    raise ImportError(str(e) + '\nplease try "pip install deepspeech"')

from audio_common_msgs.msg import AudioData
from speech_recognition_msgs.msg import SpeechRecognitionCandidates


class MyAudioData(object):
    """
    Creates a new ``AudioData`` instance, which represents mono audio data.

    The raw audio data is specified by ``frame_data``, which is a sequence of bytes representing audio samples. This is the frame data structure used by the PCM WAV format.

    The width of each sample, in bytes, is specified by ``sample_width``. Each group of ``sample_width`` bytes represents a single audio sample.

    The audio data is assumed to have a sample rate of ``sample_rate`` samples per second (Hertz).

    Usually, instances of this class are obtained from ``recognizer_instance.record`` or ``recognizer_instance.listen``, or in the callback for ``recognizer_instance.listen_in_background``, rather than instantiating them directly.
    """
    def __init__(self, frame_data, sample_rate, sample_width):
        assert sample_rate > 0, "Sample rate must be a positive integer"
        assert sample_width % 1 == 0 and 1 <= sample_width <= 4, "Sample width must be between 1 and 4 inclusive"
        self.frame_data = frame_data
        self.sample_rate = sample_rate
        self.sample_width = int(sample_width)

    def get_segment(self, start_ms=None, end_ms=None):
        """
        Returns a new ``AudioData`` instance, trimmed to a given time interval. In other words, an ``AudioData`` instance with the same audio data except starting at ``start_ms`` milliseconds in and ending ``end_ms`` milliseconds in.

        If not specified, ``start_ms`` defaults to the beginning of the audio, and ``end_ms`` defaults to the end.
        """
        assert start_ms is None or start_ms >= 0, "``start_ms`` must be a non-negative number"
        assert end_ms is None or end_ms >= (0 if start_ms is None else start_ms), "``end_ms`` must be a non-negative number greater or equal to ``start_ms``"
        if start_ms is None:
            start_byte = 0
        else:
            start_byte = int((start_ms * self.sample_rate * self.sample_width) // 1000)
        if end_ms is None:
            end_byte = len(self.frame_data)
        else:
            end_byte = int((end_ms * self.sample_rate * self.sample_width) // 1000)
        return AudioData(self.frame_data[start_byte:end_byte], self.sample_rate, self.sample_width)

    def get_raw_data(self, convert_rate=None, convert_width=None):
        """
        Returns a byte string representing the raw frame data for the audio represented by the ``AudioData`` instance.

        If ``convert_rate`` is specified and the audio sample rate is not ``convert_rate`` Hz, the resulting audio is resampled to match.

        If ``convert_width`` is specified and the audio samples are not ``convert_width`` bytes each, the resulting audio is converted to match.

        Writing these bytes directly to a file results in a valid `RAW/PCM audio file <https://en.wikipedia.org/wiki/Raw_audio_format>`__.
        """
        assert convert_rate is None or convert_rate > 0, "Sample rate to convert to must be a positive integer"
        assert convert_width is None or (convert_width % 1 == 0 and 1 <= convert_width <= 4), "Sample width to convert to must be between 1 and 4 inclusive"

        raw_data = self.frame_data

        # make sure unsigned 8-bit audio (which uses unsigned samples) is handled like higher sample width audio (which uses signed samples)
        if self.sample_width == 1:
            raw_data = audioop.bias(raw_data, 1, -128)  # subtract 128 from every sample to make them act like signed samples

        # resample audio at the desired rate if specified
        if convert_rate is not None and self.sample_rate != convert_rate:
            raw_data, _ = audioop.ratecv(raw_data, self.sample_width, 1, self.sample_rate, convert_rate, None)

        # convert samples to desired sample width if specified
        if convert_width is not None and self.sample_width != convert_width:
            if convert_width == 3:  # we're converting the audio into 24-bit (workaround for https://bugs.python.org/issue12866)
                raw_data = audioop.lin2lin(raw_data, self.sample_width, 4)  # convert audio into 32-bit first, which is always supported
                try: audioop.bias(b"", 3, 0)  # test whether 24-bit audio is supported (for example, ``audioop`` in Python 3.3 and below don't support sample width 3, while Python 3.4+ do)
                except audioop.error:  # this version of audioop doesn't support 24-bit audio (probably Python 3.3 or less)
                    raw_data = b"".join(raw_data[i + 1:i + 4] for i in range(0, len(raw_data), 4))  # since we're in little endian, we discard the first byte from each 32-bit sample to get a 24-bit sample
                else:  # 24-bit audio fully supported, we don't need to shim anything
                    raw_data = audioop.lin2lin(raw_data, self.sample_width, convert_width)
            else:
                raw_data = audioop.lin2lin(raw_data, self.sample_width, convert_width)

        # if the output is 8-bit audio with unsigned samples, convert the samples we've been treating as signed to unsigned again
        if convert_width == 1:
            raw_data = audioop.bias(raw_data, 1, 128)  # add 128 to every sample to make them act like unsigned samples again

        return raw_data

    def get_wav_data(self, convert_rate=None, convert_width=None):
        """
        Returns a byte string representing the contents of a WAV file containing the audio represented by the ``AudioData`` instance.
        If ``convert_width`` is specified and the audio samples are not ``convert_width`` bytes each, the resulting audio is converted to match.
        If ``convert_rate`` is specified and the audio sample rate is not ``convert_rate`` Hz, the resulting audio is resampled to match.
        Writing these bytes directly to a file results in a valid `WAV file <https://en.wikipedia.org/wiki/WAV>`__.
        """
        raw_data = self.get_raw_data(convert_rate, convert_width)
        sample_rate = self.sample_rate if convert_rate is None else convert_rate
        sample_width = self.sample_width if convert_width is None else convert_width

        # generate the WAV file contents
        with io.BytesIO() as wav_file:
            wav_writer = wave.open(wav_file, "wb")
            output = wave.open("recording" + time.time()  +".wav", 'wb')
            try:  # note that we can't use context manager, since that was only added in Python 3.4
                wav_writer.setframerate(sample_rate)
                output.setframerate(sample_rate)
                wav_writer.setsampwidth(sample_width)
                output.setsampwidth(sample_width)
                wav_writer.setnchannels(1)
                output.setnchannels(1)
                wav_writer.writeframes(raw_data)
                output.writeframes(raw_data)
                wav_data = wav_file.getvalue()
            finally:  # make sure resources are cleaned up
                wav_writer.close()
                output.close()
        return wav_data


class SpeechToText(object):
    def __init__(self):
        print("Python version")
        print(sys.version)
        # format of input audio data
        self.sample_rate = rospy.get_param("~sample_rate", 16000)
        self.sample_width = rospy.get_param("~sample_width", 2)
        self.store_wav = rospy.get_param("~store_wav", False)
        # language of STT service
        self.language = rospy.get_param("~language", "de-DE")
        # ignore voice input while the robot is speaking
        self.self_cancellation = rospy.get_param("~self_cancellation", True)
        self._beam_width = rospy.get_param('~beam_width', 500)
        self._publish_if_empty = rospy.get_param('~publish_if_empty', False)
        self._lm_alpha = rospy.get_param('~lm_alpha', 0.931289039105002)
        self._lm_beta = rospy.get_param('~lm_beta', 1.1834137581510284)
        # time to assume as SPEAKING after tts service is finished
        self.tts_tolerance = rospy.Duration.from_sec(
            rospy.get_param("~tts_tolerance", 1.0))
        self.is_canceling = False
        # Load DeepSpeech model
        #model = rospy.get_param("~model", "/home/chwi02/Downloads/release_v0.7.4/output_graph.pb")
        #model = rospy.get_param("~model", "/home/chwi02/Projects/intuitiv_ros_modules/data/deepspeech-data/de-DE/release_v0.9.0/output_graph.pbmm")
        model = rospy.get_param("~tflite", "/de-DE/release_v0.9.0/output_graph_from_koh-osug.tflite")
        scorer = rospy.get_param("~scrorer", "/de-DE/release_v0.9.0//kenlm.scorer")
        #scorer = rospy.get_param("~scrorer", "/home/chwi02/Projects/intuitiv_ros_modules/data/deepspeech-data/de-DE/release_v0.9.0/kenlm.scorer")
        # tflite
        print('Initializing model...')
        print("ARGS.model: %s", model)
        self.dsModel = DS.Model(model)
        self.desired_sample_rate = self.dsModel.sampleRate()

        self.dsModel.enableExternalScorer(scorer)
        #self.stream_context = dsModel.createStream()
        self.dsModel.setBeamWidth(self._beam_width)
        self.dsModel.setScorerAlphaBeta(self._lm_alpha, self._lm_beta)
        #print("ARGS.scorer: %s", scorer)
        self.pub_speech = rospy.Publisher(
            "speech_to_text", SpeechRecognitionCandidates, queue_size=1)
        self.sub_audio = rospy.Subscriber("/speech_audio", AudioData, self.audio_cb)


    def audio_cb(self, msg):

        if self.is_canceling:
            rospy.loginfo("Speech is cancelled")
            return
        data = MyAudioData(msg.data, self.sample_rate, self.sample_width)
        if(self.store_wav):
            wave_data = data.get_wave_data(16000, 2)
        if data is not None:
            try:
                rospy.loginfo("Waiting for result %d" % len(data.get_raw_data()))
                result = self.foo(self.dsModel, data, self.sample_rate)
                if (result[0] != ""):
                    msg = SpeechRecognitionCandidates(transcript=[result[0]])
                    self.pub_speech.publish(msg)
            except Exception as e:
                rospy.logerr("Failed to recognize: %s" % str(e))

    def foo(self, ds, audio, fs):
        inference_time = 0.0
        #audio_length = len(audio) * (1 / fs)
        audio_length = len(audio.get_raw_data())
        # Run Deepspeech
        rospy.loginfo('Running inference...')
        inference_start = timer()
        raw_data = audio.get_raw_data(convert_rate=self.desired_sample_rate, convert_width=2)
        output = ds.stt(np.frombuffer(raw_data, np.int16))
        inference_end = timer() - inference_start
        inference_time += inference_end
        rospy.loginfo('Inference took %0.3fs for %0.3fs audio file.' % (inference_end, audio_length))

        return [output, inference_time]


if __name__ == '__main__':
    rospy.init_node("speech_to_text")
    stt = SpeechToText()
    rospy.spin()
