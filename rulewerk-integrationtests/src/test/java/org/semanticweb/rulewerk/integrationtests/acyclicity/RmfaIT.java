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

import org.junit.Test;
import org.semanticweb.rulewerk.core.reasoner.Acyclicity;
import org.semanticweb.rulewerk.parser.ParsingException;

public class RmfaIT extends AcyclicityIT {

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
	public void isRMFA_RMFA_2() throws ParsingException {
		this.checkIsRMFA("RMFA_2.rls", true);
	}

	@Test
	public void isNotRMFA_1_depth_RMFA_1() throws ParsingException {
		this.checkIsRMFA("1_depth_RMFA_1.rls", false);
	}

	@Test
	public void isNotRMFA_MFC_1() throws ParsingException {
		this.checkIsRMFA("MFC_1.rls", false);
	}

	@Test
	public void isNotRMFA_RMFC_1() throws ParsingException {
		this.checkIsRMFA("RMFC_1.rls", false);
	}

	@Test
	public void isNotRMFA_bike_wheel_spike() throws ParsingException {
		this.checkIsRMFA("bike_wheel_spike.rls", false);
	}
}