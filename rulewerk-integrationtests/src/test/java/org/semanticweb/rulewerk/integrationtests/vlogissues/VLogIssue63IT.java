package org.semanticweb.rulewerk.integrationtests.vlogissues;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.parser.ParsingException;

public class VLogIssue63IT extends VLogIssue {

	@Ignore
	@Test
	public void test() throws ParsingException, IOException {
		try (final Reasoner reasoner = getReasonerWithKbFromResource("vlog/63.rls")) {
			reasoner.reason();
		}
	}
}
