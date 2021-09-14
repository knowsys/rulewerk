package org.semanticweb.rulewerk.integrationtests.acyclicity;

import org.junit.Test;
import org.semanticweb.rulewerk.core.reasoner.Acyclicity;
import org.semanticweb.rulewerk.parser.ParsingException;

public class MSATest extends AcyclicityTest {
	
	private void checkIsMSA(final String resourceName, boolean expected) throws ParsingException {
		this.checkHasProperty(resourceName, Acyclicity.MSA, expected);
	}

	@Test
	public void isMSA_datalog() throws ParsingException {
		this.checkIsMSA("datalog.rls", true);
	}

	@Test
	public void isMSA_nonRecursive() throws ParsingException {
		this.checkIsMSA("nonRecursive.rls", true);
	}

	@Test
	public void isMSA_JA_1() throws ParsingException {
		this.checkIsMSA("JA_1.rls", true);
	}
	
	@Test
	public void isNotMSA_RJA_1() throws ParsingException {
		this.checkIsMSA("RJA_1.rls", false);
	}
	
	@Test
	public void isNotMSA_RJA_2() throws ParsingException {
		this.checkIsMSA("RJA_2.rls", false);
	}
	
	@Test
	public void isNotMSA_RJA_3() throws ParsingException {
		this.checkIsMSA("RJA_3.rls", false);
	}
	
	@Test
	public void isNotMSA_MFA_1() throws ParsingException {
		this.checkIsMSA("MFA_1.rls", false);
	}
	
	@Test
	public void isNotMSA_RMFA_1() throws ParsingException {
		this.checkIsMSA("RMFA_1.rls", false);
	}

	@Test
	public void isNotMSA_MFC_1() throws ParsingException {
		this.checkIsMSA("MFC_1.rls", false);
	}

	@Test
	public void isNotMSA_RMFC_1() throws ParsingException {
		this.checkIsMSA("RMFC_1.rls", false);
	}

}
