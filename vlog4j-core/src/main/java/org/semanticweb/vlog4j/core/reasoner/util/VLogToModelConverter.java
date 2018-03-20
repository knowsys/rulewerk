package org.semanticweb.vlog4j.core.reasoner.util;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.impl.ConstantImpl;
import org.semanticweb.vlog4j.core.model.validation.IllegalEntityNameException;
import org.semanticweb.vlog4j.core.reasoner.QueryResult;

public class VLogToModelConverter {

	public static QueryResult toQueryResult(String[] vlogQueryResult) {
		return new QueryResult(toGroundTermsArray(vlogQueryResult));
	}

	private static List<Term> toGroundTermsArray(String[] vlogGroundTerms) {
		// TODO support blanks
		final List<Term> groundTerms = new ArrayList<>();
		for (final String term : vlogGroundTerms) {
			Term groundTerm;
			try {
				groundTerm = new ConstantImpl(term);
				groundTerms.add(groundTerm);
			} catch (final IllegalEntityNameException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return groundTerms;
	}

}
