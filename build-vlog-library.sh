#!/bin/sh
# Script to build unreleased snapshots of karmaresearch/vlog into rulewerk-base jar on Unix-like systems

if [ -f "./local_builds/jvlog.jar" ]
then
	echo "Using cached VLog JAR."
else
	echo "Building new VLog JAR."
    	mkdir -p local_builds
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

mkdir -p rulewerk-vlog/lib
cp local_builds/jvlog.jar rulewerk-vlog/lib/jvlog-local.jar
mvn initialize -Pdevelopment
