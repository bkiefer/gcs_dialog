#!/bin/bash
#set -x
. $(dirname $0)/utils.sh
cd "$script_dir"

silentmvn="-q -ntp"
grave_dir="GraVE"
# check out and update all modules
git pull --recurse-submodules
git submodule update --init --recursive --remote
$grave_dir/install.sh -n
./compile "$@"
