package org.semanticweb.rulewerk.integrationtests.acyclicity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.parser.ParsingException;

public class JATest extends AcyclicityTest {

	@Test
	public void isJA() throws ParsingException {
		try (Reasoner r = this.getReasonerWithKbFromResource("JA-1.rls")) {
			assertTrue(r.isJA());
		}
	}

	@Test
	public void isNotJA() throws ParsingException {
		try (Reasoner r = this.getReasonerWithKbFromResource("MFA-1.rls")) {
			assertFalse(r.isJA());
		}
	}
}
