# Purpose

This module handles the natural language interaction part of the Glasgow Coma Scale (GCS) signs-of-life module of the DRZ project. The main communication is handled using MQTT messages, including ASR output, sensor and control signals.

# Installation

## Prerequisites

`git`, Java 11 and maven

## clone from repository

    git clone git@github.com:bkiefer/gcs_dialog.git

## build all modules (asr, tts and dialog) and download necessary models

    ./build_modules.sh -a

# Start the whole pipeline using docker compose

    ./run_pipeline

The pipeline can be shut down completely by executing

    docker compose down

in this directory.

# Build and start the dialogue manager in isolation

## To compile and build the docker image `drz/gcs_dialog_manager`

This is included in the build step above, it's only necessary for troubleshooting or development.

    mvn -U clean
    ./install.sh
    ./build_docker.sh

It will also pull in the GraVE project as a submodule, the automata editor can also be started from there to make modifications.

# Start the docker image in isolation

    ./start_docker.sh
