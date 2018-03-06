package org.semanticweb.vlog4j.core.model;

public class Atom {

	private final String predicateName;

	private final Term[] arguments;

	public Atom(String predicateName, Term[] arguments) {
		super();
		this.predicateName = predicateName;
		this.arguments = arguments;
	}

	public String getPredicateName() {
		return predicateName;
	}

	public Term[] getArguments() {
		return arguments;
	}

	// TODO: toString, which format?
	
}
