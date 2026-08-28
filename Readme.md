# Purpose

This module handles the natural language interaction part of the Glasgow Coma Scale (GCS) signs-of-life module of the DRZ project. The main communication is handled using MQTT messages, including ASR output, sensor and control signals.

# Installation

## Prerequisites

`git`, Java 11 and maven

## clone from repository

    git clone git@github.com:bkiefer/gcs_dialog.git

## build the supporting modules (ASR and TTS) and download their models

    ./build_modules.sh -a

## compile and build the docker image `drz/gcs_dialog_manager`

    mvn -U clean
    ./install.sh
    ./build_docker.sh

This will also pull in the GraVE project as a submodule, the automata editor can also be started from there.

# Start the docker image in isolation

    ./start_docker.sh

# Start the whole pipeline using docker compose

    ./run_pipeline

The pipeline can be shut down completely by executing

    docker compose down

in this directory.
