#!/bin/sh
# Script to build the necessary VLog jar on Unix-like systems

if [ -f "./local_builds/jvlog.jar" ]
then
	echo "Using cached VLog JAR."
else
	echo "Building new VLog JAR."
	export CC=gcc-5 && export CXX=g++-5
	mkdir local_builds
	rm -rf build-vlog
	mkdir build-vlog
	cd build-vlog
	git clone https://github.com/karmaresearch/vlog.git
	cd vlog
	# git pull
	mkdir build
	cd build
	cmake -DJAVA=1 -DSPARQL=1 ..
	make
	cp jvlog.jar ../../../local_builds/jvlog.jar
	cd ../../..
fi

cp local_builds/jvlog.jar vlog4j-core/lib/jvlog-local.jar
mvn initialize
