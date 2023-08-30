#!/bin/sh
#set -x
here=`pwd`
scriptdir=`dirname $0`
cd "$scriptdir"
. ./dependencies.sh

getversions() {
    mvn dependency:list | grep '[]]    ' | sed 's/.*\]    //' |
        sort | uniq |
        gawk -v names="$1" -F ':' \
             -e '{ version[$2]=$4; }' \
             -e 'END { split(names, n, " ");
                       for (i in n) print n[i] ":" version[n[i]]; }'
}

for cmd in $prereqs; do
    if test -z "`type -all $cmd 2>/dev/null`" ; then
        toinstall="$toinstall $cmd"
    fi
done
if test -n "$toinstall"; then
    echo "Install ${toinstall} first"
    exit 1
fi
versions=$(getversions "$githubdeps")

mkdir locallibs
cd locallibs
here=`pwd`
# Clone the given modules into the locallibs directory and put them into your
# local .m2/repository
for name in $githubdeps; do
    version=$(echo "$versions" | gawk -v name="$name" -F ':' '{ if (name == $1) print $2; }')
    if test -d $name; then
        cd $name
        git pull
    else
        git clone https://github.com/bkiefer/$name.git
        cd $name
    fi
    #if test \! "$name" = "$ver"; then
    git checkout "$version"
    #fi
    if test -f install_locallibs.sh; then
        ./install_locallibs.sh
    fi
    mvn -q install
    cd "$here"
done
cd ..
