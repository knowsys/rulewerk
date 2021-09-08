package org.semanticweb.rulewerk.integrationtests.acyclicity;

import org.junit.Test;
import org.semanticweb.rulewerk.core.reasoner.Acyclicity;
import org.semanticweb.rulewerk.parser.ParsingException;

public class MFATest extends AcyclicityTest {

	private void checkIsMFA(final String resourceName, boolean expected) throws ParsingException {
		this.checkHasProperty(resourceName, Acyclicity.MFA, expected);
	}

	@Test
	public void isMFA_datalog() throws ParsingException {
		this.checkIsMFA("datalog.rls", true);
	}

	@Test
	public void isMFA_nonRecursive() throws ParsingException {
		this.checkIsMFA("nonRecursive.rls", true);
	}

	@Test
	public void isMFA_JA_1() throws ParsingException {
		this.checkIsMFA("JA_1.rls", true);
	}

	@Test
	public void isMFA_MFA_1() throws ParsingException {
		this.checkIsMFA("MFA_1.rls", true);
	}

	@Test
	public void isNotMFA_RJA_1() throws ParsingException {
		this.checkIsMFA("RJA_1.rls", false);
	}

	@Test
	public void isNotMFA_RJA_2() throws ParsingException {
		this.checkIsMFA("RJA_2.rls", false);
	}

	@Test
	public void isNotMFA_RJA_3() throws ParsingException {
		this.checkIsMFA("RJA_3.rls", false);
	}

	@Test
	public void isNotMFA_RMFA_1() throws ParsingException {
		this.checkIsMFA("RMFA_1.rls", false);
	}

	@Test
	public void isNotMFA_MFC_1() throws ParsingException {
		this.checkIsMFA("MFC_1.rls", false);
	}

	@Test
	public void isNotMFA_RMFC_1() throws ParsingException {
		this.checkIsMFA("RMFC_1.rls", false);
	}
}
