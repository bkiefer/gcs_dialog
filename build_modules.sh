#!/bin/bash
#set -x
logfile="`pwd`/BUILD`date -Iseconds|sed 's/[: ]/_/g'`.log"
exec &> >(tee "$logfile")

. $(dirname $0)/utils.sh

GREEN='\e[42m\e[1;30m'
YELLOW='\e[93m'
RED='\e[31m'
NC='\033[0m' # No Color

export UPDATE=""

function _exitOnError {
    printf "${RED}ERROR during build or model download $1 ${NC}\n";
    exit 1;
}

function _reportSuccess {
    printf "${GREEN}$1 successfully built${NC}\n";
}

toml_version() {
    path="."
    if test -n "$1"; then path="$1"; fi
    grep version "$path"/pyproject.toml | sed 's/version *= *"\([^"]*\)".*/\1/'
}

create_env_file() {
    (
    echo "ASR_VERSION='`toml_version whisper-gstreamer`'"
    echo "TTS_VERSION='`toml_version mqtt-tts`'"
    echo "GCS_VERSION='`pom_version`'"
    ) > .env
}

build_asr() {
    # ASR and speaker identification
    cd "$script_dir"/whisper-gstreamer
    ./build_docker.sh || _exitOnError "asr"
    # download silero and vosk model
    ./download-models-vosk.sh || _exitOnError "asr"
    cd "$script_dir"
    _reportSuccess "asr"
}

build_tts() {
    # Build docker for TTS
    cd "$script_dir"/mqtt-tts
    tts_model="tts_models/de/thorsten/tacotron2-DDC"
    ./build_docker.sh $UPDATE || _exitOnError "tts"
    # model_download.sh needs the docker image built before
    ./coqui_dld_model.sh "$tts_model" || _exitOnError "tts"
    cd "$script_dir"
    _reportSuccess "tts"
}

build_dialog() {
    mvn -U clean
    ./install.sh -n
    ./build_docker.sh
}

while getopts aub: c
do
    case $c in
        a)  all="true";;
        u)  UPDATE="-u" ;;
        b)  build="$OPTARG" ;;
        *)  echo "Usage: $0 [-<a>ll] [-<u>pdate_repo] [module1, module2 ...]

update will pull the git repository and all subrepositories recursively
module must be one of 'asr', 'tts', or 'dialog'
"
    esac
done
shift `expr $OPTIND - 1`

if test -n "$UPDATE" ; then # check out and update all modules
   ./update_repo.sh
fi

create_env_file

if test "$all" = "true"; then
    build_asr && build_tts && build_dialog
else
    for mod; do
        build_$mod || exit
    done
fi
