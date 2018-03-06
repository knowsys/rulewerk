package org.semanticweb.vlog4j.core.model;

import java.util.Set;

import org.semanticweb.vlog4j.core.validation.RuleValidator;
import org.semanticweb.vlog4j.core.validation.RuleVariableValidationException;

public class Rule {
	
	private final Atom[] body;
	private final Atom[] head;
	private final Set<Variable> existentiallyQuantifiedVariables;
	
	public Rule(Atom[] body, Atom[] head, Set<Variable> existentiallyQuantifiedVariables)
			throws RuleVariableValidationException {
		super();
		this.body = body;
		this.head = head;
		this.existentiallyQuantifiedVariables = existentiallyQuantifiedVariables;
		
		RuleValidator.checkRuleVariables(this);
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
