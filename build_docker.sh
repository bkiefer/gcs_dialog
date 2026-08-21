#!/bin/bash
. utils.sh

docker build -f Dockerfile -t drz/gcs_dialog_manager:$(pom_version) .
