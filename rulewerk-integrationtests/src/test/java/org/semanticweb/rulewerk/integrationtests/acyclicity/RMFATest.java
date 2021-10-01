package org.semanticweb.rulewerk.integrationtests.acyclicity;

import org.junit.Test;
import org.semanticweb.rulewerk.core.reasoner.Acyclicity;
import org.semanticweb.rulewerk.parser.ParsingException;

public class RMFATest extends AcyclicityTest {

	private void checkIsRMFA(final String resourceName, boolean expected) throws ParsingException {
		this.checkHasProperty(resourceName, Acyclicity.RMFA, expected);
	}

	@Test
	public void isRMFA_datalog() throws ParsingException {
		this.checkIsRMFA("datalog.rls", true);
	}

	@Test
	public void isRMFA_nonRecursive() throws ParsingException {
		this.checkIsRMFA("nonRecursive.rls", true);
	}

	@Test
	public void isRMFA_JA_1() throws ParsingException {
		this.checkIsRMFA("JA_1.rls", true);
	}

	@Test
	public void isRMFA_RJA_1() throws ParsingException {
		this.checkIsRMFA("RJA_1.rls", true);
	}

	@Test
	public void isRMFA_RJA_2() throws ParsingException {
		this.checkIsRMFA("RJA_2.rls", true);
	}

	@Test
	public void isRMFA_RJA_3() throws ParsingException {
		this.checkIsRMFA("RJA_3.rls", true);
	}
	
	@Test
	public void isRMFA_MSA_1() throws ParsingException {
		this.checkIsRMFA("MSA_1.rls", true);
	}

	@Test
	public void isRMFA_MFA_1() throws ParsingException {
		this.checkIsRMFA("MFA_1.rls", true);
	}

	@Test
	public void isRMFA_RMFA_1() throws ParsingException {
		this.checkIsRMFA("RMFA_1.rls", true);
	}

	@Test
	public void isNotRMFA_MFC_1() throws ParsingException {
		this.checkIsRMFA("MFC_1.rls", false);
	}

	@Test
	public void isNotRMFA_RMFC_1() throws ParsingException {
		this.checkIsRMFA("RMFC_1.rls", false);
	}
}
