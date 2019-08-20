VLog4j Release Notes
====================

VLog4j v0.4.0
-------------

Breaking changes:
* IRIs loaded from RDF inputs no longer include surrounding < > in their string identifier

New features:
* New own syntax for rules, facts, and data sources to create knowledge bases from files or strings in Java
* New and updated example programs to illustrate use of syntax

Bugfixes:
* ...


VLog4j v0.3.0
-------------

New features:
* Support for Graal data structures (conversion from Graal model to VLog model objects)
* Stratified negation: rule bodies are conjunctions of positive or negated literals
* SPARQL-based data sources: load remote data from SPARQL endpoints
* Acyclicity and cyclicity checks: JA, RJA, MFA, RMFA, RFC, as well as a generic method that checks whether given set or rules and fact predicates are acyclic, cyclic, or undetermined

VLog4j v0.2.0
-------------

New features:
* supporting File data sources of N-Triples format (.nt file extension) 
* supporting g-zipped data source files (.csv.gz, .nt.gz)

VLog4j v0.1.0
-------------

Initial release.

New features:
* Essential data models for rules and facts, and essential reasoner functionality
* support for reading from RDF files
* support for converting rules from OWL ontology, loaded with the OWL API

