VLog4j
======
[![Build Status](https://travis-ci.org/knowsys/vlog4j.png?branch=master)](https://travis-ci.org/knowsys/vlog4j)
[![Coverage Status](https://coveralls.io/repos/github/knowsys/vlog4j/badge.svg?branch=master)](https://coveralls.io/github/knowsys/vlog4j?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.semanticweb.vlog4j/vlog4j-parent/badge.svg)](http://search.maven.org/#search|ga|1|g%3A%22org.semanticweb.vlog4j%22)

A Java library based on the [VLog rule engine](https://github.com/karmaresearch/vlog)

Installation
------------

The current release of VLog4j is version 0.3.0. The easiest way of using the library is with Maven. Maven users must add the following dependency to the dependencies in their pom.xml file:

```
<dependency>
	<groupId>org.semanticweb.vlog4j</groupId>
	<artifactId>vlog4j-core</artifactId>
	<version>0.3.0</version>
</dependency>
```

You need to use Java 1.8 or above. Available modules include:

* **vlog4j-core**: essential data models for rules and facts, and essential reasoner functionality
* **vlog4j-graal**: support for converting rules, facts and queries from [Graal](http://graphik-team.github.io/graal/) API objects and [DLGP](http://graphik-team.github.io/graal/doc/dlgp) files
* **vlog4j-rdf**: support for reading from RDF files
* **vlog4j-owlapi**: support for converting rules from OWL ontology, loaded with the OWL API

The released packages use vlog4j-base, which packages system-dependent binaries for Linux, MacOS, and Windows, and should work out of the box with current versions of these systems. In case of problems, or if you are using the current development version, own binaries can be compiled as follows:

* Run [build-vlog-library.sh](https://github.com/mkroetzsch/vlog4j/blob/master/build-vlog-library.sh) or execute the commands in this file manually. This will compile a local jar file on your system, copy it to ```./vlog4j-core/lib/jvlog-local.jar```, and install the new jar locally in Maven in place of the distributed version of vlog4j-base.
* Run ```mvn install``` to test if the setup works

Documentation
-------------

* The module **vlog4j-examples** includes short example programs that demonstrate some common use cases
* [JavaDoc](https://knowsys.github.io/vlog4j/) is available online and through the Maven packages.
