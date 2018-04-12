VLog4J
======
[![Build Status](https://travis-ci.org/mkroetzsch/vlog4j.png?branch=master)](https://travis-ci.org/mkroetzsch/vlog4j)
[![Coverage Status](https://coveralls.io/repos/github/mkroetzsch/vlog4j/badge.svg?branch=master)](https://coveralls.io/github/mkroetzsch/vlog4j?branch=master)

A Java library based on the [VLog rule engine](https://github.com/karmaresearch/vlog)

Installation
------------

To build vlog4j from source, you need to install Maven and perform the following steps:

* In the directory ```./vlog-core/lib``` copy the jar to ```jvlog-local.jar``` (the current default is a Linux library there)
* Run ```mvn initialize```
* Run ```mvn install```
* If this fails, you can run the script build-vlog-library.sh to compile and install this jar from the latest online sources using your local compiler

Documentation
-------------

* [JavaDoc](https://mkroetzsch.github.io/vlog4j/) is available online and through the Maven packages.
