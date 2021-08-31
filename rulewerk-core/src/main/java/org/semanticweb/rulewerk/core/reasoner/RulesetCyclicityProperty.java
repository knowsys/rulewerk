package org.semanticweb.rulewerk.core.reasoner;
//TODO javadoc

public interface RulesetCyclicityProperty {

	static enum Type {
		CYCLIC, ACYCLIC
	}
	 
	Type getType();

	String name();
}
