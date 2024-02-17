# Purpose

This module handles the natural language interaction part of the Glasgow Coma Scale (GCS) signs-of-life module of the DRZ project. The main communication is handled using MQTT messages, including ASR output, sensor and control signals.

# Installation

## Prerequisites

`git`, Java 11 and maven

## clone from repository


    git clone git@mlt-gitlab.sb.dfki.de:willms/drz_sign_of_life_module.git


## compile and build the docker image `drz/gcs_dialog_manager`

    mvn -U clean
    ./compile
    mvn install
    ./build_docker.sh

## create a logs/ folder (for detailed logs) and start the docker image

    mkdir logs/ 2>/dev/null
    ./start_docker.sh
