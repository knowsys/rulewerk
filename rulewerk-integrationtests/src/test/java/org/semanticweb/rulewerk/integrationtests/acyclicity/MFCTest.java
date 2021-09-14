package org.semanticweb.rulewerk.integrationtests.acyclicity;

import org.junit.Test;
import org.semanticweb.rulewerk.core.reasoner.Cyclicity;
import org.semanticweb.rulewerk.parser.ParsingException;

public class MFCTest extends AcyclicityTest {

	private void checkIsMFC(final String resourceName, boolean expected) throws ParsingException {
		this.checkHasProperty(resourceName, Cyclicity.MFC, expected);
	}

	@Test
	public void isNotMFC_datalog() throws ParsingException {
		this.checkIsMFC("datalog.rls", false);
	}

	@Test
	public void isNotMFC_nonRecursive() throws ParsingException {
		this.checkIsMFC("nonRecursive.rls", false);
	}

	@Test
	public void isNotMFC_JA_1() throws ParsingException {
		this.checkIsMFC("JA_1.rls", false);
	}

	@Test
	public void isMFC_RJA_1() throws ParsingException {
		this.checkIsMFC("RJA_1.rls", true);
	}

	@Test
	public void isMFC_RJA_2() throws ParsingException {
		this.checkIsMFC("RJA_2.rls", true);
	}

	@Test
	public void isMFC_RJA_3() throws ParsingException {
		this.checkIsMFC("RJA_3.rls", true);
	}

	@Test
	public void isNotMFC_MFA_1() throws ParsingException {
		this.checkIsMFC("MFA_1.rls", false);
	}

	@Test
	public void isMFC_RMFA_1() throws ParsingException {
		this.checkIsMFC("RMFA_1.rls", true);
	}

	@Test
	public void isMFC_MFC_1() throws ParsingException {
		this.checkIsMFC("MFC_1.rls", true);
	}

	@Test
	public void isMFC_RMFC_1() throws ParsingException {
		this.checkIsMFC("RMFC_1.rls", true);
	}
}
