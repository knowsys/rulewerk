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
	
	//TODO are the RJA, MFA and RMFA ones MSA? 

	@Test
	public void isNotMSA_MFC_1() throws ParsingException {
		this.checkIsMSA("MFC_1.rls", false);
	}

	@Test
	public void isNotMSA_RMFC_1() throws ParsingException {
		this.checkIsMSA("RMFC_1.rls", false);
	}

}
