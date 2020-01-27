VLog4j Release Notes
====================

VLog4j v0.5.0
-------------

Breaking changes:
* The data model for rules has been refined and changed:
  * Instead of Constant, specific types of constants are used to capture abtract and data values
  * Instead of Variable, ExistentialVariable and UniversalVariable now indicate quantification
  * Blank was renamed to NamedNull to avoid confusion with RDF blank nodes
  * Methods to access terms now use Java Streams and are unified across syntactic objects
* Data source declarations now use brackets to denote arity, e.g., `@source predicate[2]: load-csv()`

New features:
* New module vlog4j-client provides a stand-alone command line client jar for VLog4j
* A wiki for VLog4j use and related publications has been created: https://github.com/knowsys/vlog4j/wiki
* The parser behaviour for data source declarations and certain datatype literals can be customised.

Other improvements:
* Data model is better aligned with syntax supported by parser
* Java object Statements (rules, facts, datasource declarations) String representation is parseable
* OWL API dependency has been upgraded from 4.5.1 to latest (5.1.11)
* SL4J dependency has been upgraded from 1.7.10 to latest (1.7.28)
* Cobertura test coverage tool has been replaced by JaCoCo

Bugfixes:
* Acyclicity checks work again without calling reason() first (issue #128)
* in vlog4j-owlapi, class expressions of type ObjectMaxCardinality are not allowed in superclasses (issue #104)
* in vlog4j-owlapi, class expressions of type ObjectOneOf are only allowed as subclasses in axioms of type subClassOf (issue  #20)
* When parsing syntactic fragment such as Facts or Literals, the parser now enforces that all input is consumed.

VLog4j v0.4.0
-------------

Breaking changes:
* The Reasoner interface has changed (knowledge base and related methods moved to KnowledgeBase)
* The EdbIdbSeparation is obsolete and does no longer exist
* IRIs loaded from RDF inputs no longer include surrounding < > in their string identifier
* A new interface Fact has replaced the overly general PositiveLiteral in many places

New features:
* New own syntax for rules, facts, and data sources to create knowledge bases from files or strings in Java
* Input predicates can now be used with multiple sources and in rule heads (no more EDB-IDB distinction)
* New InMemoryDataSource for efficient in-memory fact loading
* New KnowledgeBase class separates facts, data sources, and rules from the actual Reasoner
* Modifications to the knowledge base are taken into account by the reasoner
* New and updated example programs to illustrate use of syntax

Other improvements:
* Query results now indicate their guaranteed correctness (example: answers can be incomplete when setting a timeout)
* Faster and more memory-efficient loading of facts
* Better error reporting; improved use of exceptions
* Better logging, especially on the INFO level
* Better code structure and testing

Bugfixes:
* Several reasoning errors in VLog (backend) have been discovered and fixed in the version used now


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
