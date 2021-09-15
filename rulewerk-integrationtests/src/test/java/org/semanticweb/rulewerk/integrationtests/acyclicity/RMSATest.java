package org.semanticweb.rulewerk.integrationtests.acyclicity;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.rulewerk.core.reasoner.Acyclicity;
import org.semanticweb.rulewerk.parser.ParsingException;

public class RMSATest extends AcyclicityTest {
	private void checkIsRMSA(final String resourceName, boolean expected) throws ParsingException {
		this.checkHasProperty(resourceName, Acyclicity.MSA, expected);
	}

	@Test
	public void IsRMSA_datalog() throws ParsingException {
		this.checkIsRMSA("datalog.rls", true);
	}

	@Test
	public void IsRMSA_nonRecursive() throws ParsingException {
		this.checkIsRMSA("nonRecursive.rls", true);
	}

	@Test
	public void IsRMSA_JA_1() throws ParsingException {
		this.checkIsRMSA("JA_1.rls", true);
	}

	// TODO should be RMSA
	@Ignore
	@Test
	public void IsRMSA_RJA_1() throws ParsingException {
		this.checkIsRMSA("RJA_1.rls", true);
	}

	// TODO should be RMSA
	@Ignore
	@Test
	public void IsRMSA_RJA_2() throws ParsingException {
		this.checkIsRMSA("RJA_2.rls", true);
	}

	// TODO should be RMSA
	@Ignore
	@Test
	public void IsRMSA_RJA_3() throws ParsingException {
		this.checkIsRMSA("RJA_3.rls", true);
	}

	@Test
	public void IsNotRMSA_MFA_1() throws ParsingException {
		this.checkIsRMSA("MFA_1.rls", false);
	}

	@Test
	public void IsNotRMSA_RMFA_1() throws ParsingException {
		this.checkIsRMSA("RMFA_1.rls", false);
	}

	@Test
	public void IsNotRMSA_MFC_1() throws ParsingException {
		this.checkIsRMSA("MFC_1.rls", false);
	}

	@Test
	public void IsNotRMSA_RMFC_1() throws ParsingException {
		this.checkIsRMSA("RMFC_1.rls", false);
	}
}
