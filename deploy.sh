# remove old version
rm -rf drz_sign_of_life_module
# get new version
unzip drz_sign_of_life_module.zip
rm drz_sign_of_life_module.zip
cd drz_sign_of_life_module
# get and install dependencies
gdown https://drive.google.com/uc\?id\=1eZIN8uHyr8VBTkKZVEh6ssviZ0wSD9Wf
unzip common_lib.zip
rm common_lib.zip
# install dependencies
sh ./install.sh
# .compile java module
sh ./compile
# deploy ros_msgs and speech processing nodes
cd  /home/drz-projekt/catkin_ws/src/
rm  -r gcs_msgs
rm -r speech_processing
rm -r respeaker_ros
ln -s /home/drz-projekt/dfki/drz_sign_of_life_module/speech_processing_ros/gcs_msgs gcs_msgs
ln -s /home/drz-projekt/dfki/drz_sign_of_life_module/speech_processing_ros/speech_processing speech_processing
ln -s /home/drz-projekt/dfki/drz_sign_of_life_module/respeaker_ros respeaker_ros