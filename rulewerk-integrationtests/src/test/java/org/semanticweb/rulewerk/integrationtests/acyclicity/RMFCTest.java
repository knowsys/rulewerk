package org.semanticweb.rulewerk.integrationtests.acyclicity;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.rulewerk.core.reasoner.Cyclicity;
import org.semanticweb.rulewerk.parser.ParsingException;

public class RMFCTest extends AcyclicityTest {

	private void checkIsRMFC(final String resourceName, boolean expected) throws ParsingException {
		this.checkHasProperty(resourceName, Cyclicity.RMFC, expected);
	}
	
	@Test
	public void isNotRMFC_datalog() throws ParsingException {
		this.checkIsRMFC("datalog.rls", false);
	}

	@Test
	public void isNotRMFC_nonRecursive() throws ParsingException {
		this.checkIsRMFC("nonRecursive.rls", false);
	}

	@Test
	public void isNotRMFC_JA_1() throws ParsingException {
		this.checkIsRMFC("JA_1.rls", false);
	}

	@Test
	public void isNotMFC_MFA_1() throws ParsingException {
		this.checkIsRMFC("MFA_1.rls", false);
	}

	//TODO should be RMFC
	@Ignore
	@Test
	public void isNotRMFC_RMFA_1() throws ParsingException {
		this.checkIsRMFC("RMFA_1.rls", false);
	}

	@Test
	public void isNotRMFC_RJA_1() throws ParsingException {
		this.checkIsRMFC("RJA_1.rls", false);
	}

	@Test
	public void isNotRMFC_RJA_2() throws ParsingException {
		this.checkIsRMFC("RJA_2.rls", false);
	}

	@Test
	public void isNotRMFC_RJA_3() throws ParsingException {
		this.checkIsRMFC("RJA_3.rls", false);
	}

	//TODO should be RMFC
	@Ignore
	@Test
	public void isRMFC_MFC_1() throws ParsingException {
		this.checkIsRMFC("MFC_1.rls", true);
	}

	@Test
	public void isRMFC_RMFC_1() throws ParsingException {
		this.checkIsRMFC("RMFC_1.rls", true);
	}

}
