#!/bin/sh
save_image() {
    docker save -o "$1".tar "$2"
    rm "$1".tar.gz
    gzip -9 "$1".tar
}


#if test -n "$1"; then
save_image gcs_dialogue drz/gcs_dialog_manager
#fi
