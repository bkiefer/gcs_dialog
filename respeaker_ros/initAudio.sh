#!/bin/sh

export RESPEAKER=$( cat /proc/asound/cards | grep -P "\d+ (?=\[ArrayUAC10)" -o)
echo "defaults.pcm.card $RESPEAKER" > /etc/asound.conf
echo "defaults.ctl.card $RESPEAKER" >> /etc/asound.conf
echo "defaults.pcm.device 0" >> /etc/asound.conf