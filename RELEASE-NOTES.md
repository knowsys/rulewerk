Rulewerk Release Notes
======================

Rulewerk v0.7.0
---------------

New features:
* New interactive Rulewerk shell for rule reasoning from the command line client
* Significant speedup in iterating over query results
* Support for using data from a Trident database, the recommended data source for large
  RDF graphs in VLog
* More features to control how Rulewerk imports RDF data using rulewerk-rdf module
* New class `LiteralQueryResultPrinter` for pretty-printing query results

Other improvements:
* Improved serialization of knowledge bases (using namespaces)
* Simple (non-IRI, namespace-less) predicate names can now include - and _
* Nulls in input data (aka "blank nodes") are now properly skolemized for VLog
* InMemoryGraphAnalysisExample now counts proper triangles using negation to avoid "triangles" where
  two or more edges are the same.

Breaking changes:
* The `RdfModelConverter` class from the rdf package is no longer static (and has more options)
* The `Serializer` class in the core package has been replaced by a new implementation
  with a completely different interface.
* The methods `getSerialization` that were present in most syntax objects have been removed. Use `toString()` instead for simple serializations, or invoke a custom Serializer.
* The `DataSource` interface requires a new method to be implemented.
* `@import`, `@import-relative`, and `@source` now treat relative paths as relative to the file they occur in, as opposed to the global working directory.

Rulewerk v0.6.0
---------------

Breaking changes:
* VLog4j is now called Rulewerk. Consequently, the groupId, artifact Ids, and package names
  of the project have changed.
* In the examples package, `ExamplesUtils.getQueryAnswerCount(queryString, reasoner)` does no
  longer exist. It can be replaced by
  `reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral(queryString)).getCount()`
* The `FileDataSource` constructor and those of child classes (`CsvFileDataSource`, `RdfFileDataSource`)
  now take the String path to a file instead of `File` object.
* The VLog backend has been moved to a new `rulewerk-vlog` module,
  changing several import paths. `Reasoner.getInstance()` is
  gone. Furthermore, `InMemoryDataSource` has become an abstract class,
  use `VLogInMemoryDataSource` where applicable.

New features:
* Counting query answers is more efficient now, using `Reasoner.countQueryAnswers()`
* All inferred facts can be serialized to a file using `Reasoner.writeInferences()`
* All inferred facts can be obtained as a Stream using `Reasoner.getInferences()`
* `Reasoner.getCorrectness()` returns the correctness result of the last reasoning task.
* Knowledge bases can be serialized to a file using `KnowlegdeBase.writeKnowledgeBase()`
* Rules files may import other rules files using `@import` and
  `@import-relative`, where the latter resolves relative IRIs using
  the current base IRI, unless the imported file explicitly specifies
  a different one.
* Named nulls of the form `_:name` are now allowed during parsing (but
  may not occur in rule bodies). They are renamed to assure that they
  are distinct on a per-file level.
* The parser allows custom directives to be implemented, and a certain
  set of delimiters allows for custom literal expressions.

Other improvements:
* Prefix declarations are now kept as part of the Knowledge Base and
  are used to abbreviate names when exporting inferences.

Bugfixes:
* Several reasoning errors in VLog (backend) have been discovered and fixed in the version used now

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
