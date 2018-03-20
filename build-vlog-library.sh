#!/bin/sh
# Script to build the necessary VLog jar on Unix-like systems

rm -rf build-vlog
mkdir build-vlog
cd build-vlog
git clone https://github.com/karmaresearch/vlog.git
cd vlog
# git pull
mkdir build
cd build
cmake -DJAVA=1 ..
make
cp jvlog.jar ../../../vlog4j-core/lib/jvlog.jar
