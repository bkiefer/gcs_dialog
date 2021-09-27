FROM ros:melodic-robot

ENV ROS_DISTRO melodic
ENV GITLAB_TOKEN oBAR1D9YTtTu1bqSo1ze

RUN apt-key adv --keyserver 'hkp://keyserver.ubuntu.com:80' --recv-key C1CF6E31E6BADE8868B172B4F42ED6FBAB17C654

# INSTALL STUFF #######################################################################################################
RUN apt -q -qq update && \
  DEBIAN_FRONTEND=noninteractive apt install -y --no-install-recommends \
  apt-utils \
  software-properties-common \
  nano \
  python-pip \
  python3.6\
  python3-pip\
  python3-setuptools \
  python-setuptools \
  portaudio19-dev \
  python-pyaudio \
  python3-pyaudio \
  # not sure about this
  libusb-1.0 \
  usbutils \
  unzip \
  alsa-utils \
  wget \
  apt-utils \
  alsa-base  \
  libpulse0 \
  software-properties-common \
  libasound-dev \
  libportaudio2 \
  libportaudiocpp0 \
  python-pip

RUN apt -q -qq update && \
  DEBIAN_FRONTEND=noninteractive apt install -y --allow-unauthenticated --no-install-recommends \
  python-rosinstall \
  python-catkin-tools \
  python-catkin-pkg

RUN rosdep update

#Install python dependencies for speechrec
RUN pip install py-marytts pyaudio
#Pip wheel setup
RUN pip install wheel && pip3 install wheel
#Install python dependencies for speechrec
RUN pip install --upgrade setuptools
RUN pip install pyusb click gdown pyyaml
RUN pip3 install --upgrade setuptools
RUN pip3 install deepspeech==0.9.3 pyyaml
RUN pip3 install deepspeech-tflite

# INTUITIV SOFTWARE SETUP AND INSTALL ##################################################################################

# clone drivers for respeaker
RUN  mkdir -p /catkin_ws/src && cd /catkin_ws/src  && git clone https://github.com/jsk-ros-pkg/jsk_3rdparty.git && git clone https://github.com/locusrobotics/catkin_virtualenv.git &&  git clone https://github.com/uos/rospy_message_converter.git
RUN cd /catkin_ws/src/rospy_message_converter/ && python ./setup.py install
RUN  gdown  https://drive.google.com/uc?id=1xZ0IN4DyzQkoGaD5OqiJYr-V3QC2Mspz && unzip -q de-DE.zip && rm de-DE.zip

# Clean up
RUN cd /catkin_ws/src && cp -r jsk_3rdparty/respeaker_ros . && rm -r ./jsk_3rdparty
COPY speech_processing speech_processing
RUN mv speech_processing /catkin_ws/src
COPY gcs_msgs gcs_msgs
RUN mv gcs_msgs /catkin_ws/src
RUN mv /bin/sh /bin/sh_tmp && ln -s /bin/bash /bin/sh

# Install Respeaker ROS
RUN apt -q -qq update; source /opt/ros/${ROS_DISTRO}/setup.bash; cd /catkin_ws; rosdep install --from-paths /catkin_ws/src -i -r -n -y; catkin config --init; catkin build catkin_virtualenv respeaker_ros speech_processing gcs_msgs

#SETUP
RUN apt autoremove
RUN touch /root/.bashrc &&  echo "source /catkin_ws/devel/setup.bash \n" >> /root/.bashrc
RUN rm -rf /var/lib/apt/lists/*

#ENTRYPOINT ["/ros_entrypoint.sh"]
CMD ["bash"]
