package org.semanticweb.rulewerk.integrationtests.vlogissues;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.parser.ParsingException;

public class RulewerkIssue175IT extends VLogIssue {
	@Test
	public void issue175_full_succeeds() throws ParsingException, IOException {
		try (final Reasoner reasoner = getReasonerWithKbFromResource("rulewerk/175.rls")) {
			reasoner.reason();
			try (QueryResultIterator result = reasoner.answerQuery(Expressions.makePositiveLiteral("VANDALISMRESERVEDENTITIESSUPPREL0",
					Expressions.makeAbstractConstant("VANDALISMRESERVEDENTITIESSUPPRULE50")), false)) {
				assertTrue(result.hasNext());
			}
		}
	}

	@Test
	public void issue175_minimal_succeeds() throws ParsingException, IOException {
		try (final Reasoner reasoner = getReasonerWithKbFromResource("rulewerk/175-minimal.rls")) {
			reasoner.reason();
			try (QueryResultIterator result = reasoner.answerQuery(Expressions.makePositiveLiteral("VANDALISMRESERVEDENTITIESSUPPREL0",
					Expressions.makeAbstractConstant("VANDALISMRESERVEDENTITIESSUPPRULE50")), false)) {
				assertTrue(result.hasNext());
			}
		}
	}
}
