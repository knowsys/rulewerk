package org.semanticweb.vlog4j.core.model;

import java.util.Set;

public class Rule {
	
	private final Atom[] body;
	private final Atom[] head;
	private final Set<Variable> existentiallyQuantifiedVariables;
	
	public Rule(Atom[] body, Atom[] head, Set<Variable> existentiallyQuantifiedVariables) {
		super();
		this.body = body;
		this.head = head;
		this.existentiallyQuantifiedVariables = existentiallyQuantifiedVariables;
	}

	public Atom[] getBody() {
		return body;
	}

	public Atom[] getHead() {
		return head;
	}

	public Set<Variable> getExistentiallyQuantifiedVariables() {
		return existentiallyQuantifiedVariables;
	}

}
