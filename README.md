Rulewerk
======
[![Rulewerk Test Status](https://github.com/knowsys/rulewerk/workflows/Rulewerk%20Tests/badge.svg?branch=master)](https://github.com/knowsys/rulewerk/actions?query=workflow:Rulewerk+Tests)
[![Coverage Status](https://coveralls.io/repos/github/knowsys/rulewerk/badge.svg?branch=master)](https://coveralls.io/github/knowsys/rulewerk?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.semanticweb.rulewerk/rulewerk-parent/badge.svg)](http://search.maven.org/#search|ga|1|g%3A%22org.semanticweb.rulewerk%22)

Rulewerk is a Java library based on the [VLog rule engine](https://github.com/karmaresearch/vlog).

***Note:*** The recent [Nemo rule engine](https://github.com/knowsys/nemo) also implements support for a Rulewerk-like Datalog-dialect, and might be a good choice for some projects using Rulewerk. Currently, there is no Rulewerk-integration of Nemo yet, but many Rulewerk progams will work in Nemo (or can easily be adapted), whereas Nemo tends to have more features (e.g., arithmetic built-ins and datatype support).

Installation
------------

The current release of Rulewerk is version [0.9.0](https://github.com/knowsys/rulewerk/releases/tag/v0.9.0). The easiest way of using the library is with Maven. Maven users must add the following dependency to the dependencies in their pom.xml file:

```
<dependency>
	<groupId>org.semanticweb.rulewerk</groupId>
	<artifactId>rulewerk-core</artifactId>
	<version>0.9.0</version>
</dependency>
```

Previous to version `0.6.0`, *rulewerk* project name was *vlog4j*. Older versions released under name *vlog4j* have `<groupId>org.semanticweb.vlog4j</groupId>` and `<artifactId>vlog4j-core</artifactId>`, the latest version being version `0.5.0`.


You need to use Java 1.8 or above. Available source modules include:

* **rulewerk-core**: essential data models for rules and facts, and essential reasoner functionality
* **rulewerk-parser**: support for processing knowledge bases in [Rulewerk syntax](https://github.com/knowsys/rulewerk/wiki/Rule-syntax-grammar)
* **rulewerk-graal**: support for converting rules, facts and queries from [Graal](http://graphik-team.github.io/graal/) API objects and [DLGP](http://graphik-team.github.io/graal/doc/dlgp) files
* **rulewerk-rdf**: support for reading from RDF files in Java (not required for loading RDF directly during reasoning)
* **rulewerk-owlapi**: support for converting rules from OWL ontology, loaded with the OWL API
* **rulewerk-client**: stand-alone application that builds a [command-line client](https://github.com/knowsys/rulewerk/wiki/Standalone-client) for Rulewerk.
* **rulewerk-commands**: support for running commands, as done by the client
* **rulewerk-vlog**: support for using [VLog](https://github.com/karmaresearch/vlog) as a reasoning backend for Rulewerk.

Test module **rulewerk-integrationtests** contains integration tests that verify the correctness of the backend reasoners for various complex reasoning problems.

<a name="anchor-build-vlog">The released **rulewerk-vlog** packages use [`vlog-java`](https://search.maven.org/search?q=a:vlog-java), which packages system-dependent [VLog](https://github.com/karmaresearch/vlog) binaries for Linux, macOS, and Windows, and should work out of the box with current versions of these systems (for Linux, you will need at least libstdc++-v3.4.22; for macOS, you will need at least macOS 10.14). In case of problems, or if you are using the current development version, own binaries can be compiled as follows:
* (Optional) It is recommended to increase the version of `vlog-java` (in `rulewerk-vlog/pom.xml`) before executing the next steps.
* Delete (if existing) previous local builds (`local_builds` directory).
* Run [build-vlog-library.sh](https://github.com/knowsys/rulewerk/blob/master/build-vlog-library.sh) or execute the commands in this file manually. This will compile a local jar file on your system, copy it to ```rulewerk-vlog/lib/jvlog-local.jar```, and install the new jar in your local Maven repository in place of the distributed version of `vlog-java`.
* Run ```mvn install``` to test if the setup works.</a>



Documentation
-------------

* The module **rulewerk-examples** includes short example programs that demonstrate various features and use cases
* The GitHub project **[Rulewerk Example](https://github.com/knowsys/rulewerk-example)** shows how to use Rulewerk in own Maven projects and can be used as a skeleton for own projects
* [JavaDoc](https://knowsys.github.io/rulewerk/) is available online and through the Maven packages.
* A Rulewerk [Wiki](https://github.com/knowsys/rulewerk/wiki) is available online, with detailed information about rulewerk usage, the supported rule language [examples](https://github.com/knowsys/rulewerk/wiki/Rule-syntax-by-examples) and [grammar](https://github.com/knowsys/rulewerk/wiki/Rule-syntax-grammar), and related publications.
* You can contact developers and other users about usage and or development on our [support channel](https://matrix.to/#/#rulewerk-support:tu-dresden.de).

Development
-----------

* Pull requests are welcome.
* We largely follow [Java Programming Style Guidelines published by Petroware](https://petroware.no/javastyle.html). The main exception are the names of private members, which do not usually end in underscores in our code.

* The master branch may require a development version of [VLog](https://github.com/karmaresearch/vlog).
Use the script `build-vlog-library.sh` [as shown here](#anchor-build-vlog) to create and install it on your machine. This will compile and install `vlog-java`dependency with the current code of [VLog](https://github.com/karmaresearch/vlog) master branch.



* Users of Eclipse should install the [JavaCC Eclipse Plug-in](https://marketplace.eclipse.org/content/javacc-eclipse-plug) to generate the parser sources. After [installing](https://marketplace.eclipse.org/content/javacc-eclipse-plug/help) the plugin, right-click on the file `JavaCCParser.jj` in `org.semanticweb.rulewerk.parser.javacc`, and select "compile with javacc". This step needs to be repeated when the file changes.
* To build the standalone client jar, run `mvn install -Pclient`. This generates `standalone-rulewerk-client-[VERSION].jar` in `rulewerk-client/target`.
* The CI setup is [documented here](https://github.com/knowsys/rulewerk/wiki/CI-Setup).
