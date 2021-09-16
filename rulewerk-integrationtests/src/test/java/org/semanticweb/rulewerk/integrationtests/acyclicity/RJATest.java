package org.semanticweb.rulewerk.integrationtests.acyclicity;

/*-
 * #%L
 * Rulewerk Integration Tests
 * %%
 * Copyright (C) 2018 - 2021 Rulewerk Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
	public void isNotRJA_MSA_1() throws ParsingException {
		this.checkIsRJA("MSA_1.rls", false);
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

	@Test
	public void isNotRJA_RMFC_1() throws ParsingException {
		this.checkIsRJA("RMFC_1.rls", false);
	}
}
