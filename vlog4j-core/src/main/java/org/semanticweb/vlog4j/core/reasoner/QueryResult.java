package org.semanticweb.vlog4j.core.reasoner;

import java.util.List;

import org.semanticweb.vlog4j.core.model.api.Term;

public class QueryResult {

	private final List<Term> terms;

	public QueryResult(List<Term> terms) {
		super();
		this.terms = terms;
	}

	public List<Term> getTerms() {
		// TODO immutable list?
		return this.terms;
	}

}
