package org.semanticweb.rulewerk.integrationtests.acyclicity;

import org.junit.Test;
import org.semanticweb.rulewerk.core.reasoner.Acyclicity;
import org.semanticweb.rulewerk.parser.ParsingException;

public class JATest extends AcyclicityTest {

	private void checkIsJA(final String resourceName, boolean expected) throws ParsingException {
		this.checkHasProperty(resourceName, Acyclicity.JA, expected);
	}

	@Test
	public void isJA_datalog() throws ParsingException {
		this.checkIsJA("datalog.rls", true);
	}

	@Test
	public void isJA_nonRecursive() throws ParsingException {
		this.checkIsJA("nonRecursive.rls", true);
	}

	@Test
	public void isJA_JA_1() throws ParsingException {
		this.checkIsJA("JA_1.rls", true);
	}

	@Test
	public void isNotJA_RJA_1() throws ParsingException {
		this.checkIsJA("RJA_1.rls", false);
	}

	@Test
	public void isNotJA_RJA_2() throws ParsingException {
		this.checkIsJA("RJA_2.rls", false);
	}

	@Test
	public void isNotJA_RJA_3() throws ParsingException {
		this.checkIsJA("RJA_3.rls", false);
	}
	
	@Test
	public void isNotJA_MSA_1() throws ParsingException {
		this.checkIsJA("MSA_1.rls", false);
	}

	@Test
	public void isNotJA_MFA_1() throws ParsingException {
		this.checkIsJA("MFA_1.rls", false);
	}

	@Test
	public void isNotJA_RMFA_1() throws ParsingException {
		this.checkIsJA("RMFA_1.rls", false);
	}

	@Test
	public void isNotJA_MFC_1() throws ParsingException {
		this.checkIsJA("MFC_1.rls", false);
	}

	@Test
	public void isNotJA_RMFC_1() throws ParsingException {
		this.checkIsJA("RMFC_1.rls", false);
	}
}
