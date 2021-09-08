package org.semanticweb.rulewerk.integrationtests.acyclicity;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.rulewerk.core.reasoner.Acyclicity;
import org.semanticweb.rulewerk.parser.ParsingException;

public class RJATest extends AcyclicityTest {

	private void checkIsRJA(final String resourceName, boolean expected) throws ParsingException {
		this.checkHasProperty(resourceName, Acyclicity.RJA, expected);
	}

	@Test
	public void isRJA_datalog() throws ParsingException {
		this.checkIsRJA("datalog.rls", true);
	}

	@Test
	public void isRJA_nonRecursive() throws ParsingException {
		this.checkIsRJA("nonRecursive.rls", true);
	}

	@Test
	public void isJRA_JA_1() throws ParsingException {
		this.checkIsRJA("JA_1.rls", true);
	}

	// FIXME should be RJA
	@Ignore
	@Test
	public void isRJA_RJA_1() throws ParsingException {
		this.checkIsRJA("RJA_1.rls", true);
	}

	// FIXME should be RJA
	@Ignore
	@Test
	public void isRJA_RJA_2() throws ParsingException {
		this.checkIsRJA("RJA_2.rls", true);
	}

	// FIXME should be RJA
	@Ignore
	@Test
	public void isRJA_RJA_3() throws ParsingException {
		this.checkIsRJA("RJA_3.rls", true);
	}

	@Test
	public void isNotRJA_MFA_1() throws ParsingException {
		this.checkIsRJA("MFA_1.rls", false);
	}

	@Test
	public void isNotRJA_RMFA_1() throws ParsingException {
		this.checkIsRJA("RMFA_1.rls", false);
	}

	@Test
	public void isNotRJA_MFC_1() throws ParsingException {
		this.checkIsRJA("MFC_1.rls", false);
	}

	// FIXME: https://github.com/karmaresearch/vlog/issues/77
	@Ignore
	@Test
	public void isNotRJA_RMFC_1() throws ParsingException {
		this.checkIsRJA("RMFC_1.rls", false);
	}
}
