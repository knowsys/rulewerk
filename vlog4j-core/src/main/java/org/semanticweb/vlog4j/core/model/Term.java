package org.semanticweb.vlog4j.core.model;

public interface Term {

	String getName();
	
	TermType getType();
	
	boolean isVariable();
	
	boolean isConstant();
}
