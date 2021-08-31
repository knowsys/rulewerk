package org.semanticweb.rulewerk.integrationtests.acyclicity;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.parser.ParsingException;

public class MFATest extends AcyclicityTest {
	
	@Test
	public void isMFA_datalog() throws ParsingException {
		try (Reasoner r = this.getReasonerWithKbFromResource("datalog.rls")) {
			assertTrue(r.isMFA());
		}
	}

	@Test
	public void isMFA_JA_1() throws ParsingException
	{
		try (Reasoner r = this.getReasonerWithKbFromResource("JA-1.rls")) {
			assertTrue(r.isMFA());
		}
	}

	@Test
	public void isMFA_MFA_1() throws ParsingException
	{
		try (Reasoner r = this.getReasonerWithKbFromResource("MFA-1.rls")) {
			assertTrue(r.isMFA());
		}
	}



}
