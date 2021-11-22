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

public class MfaIT extends AcyclicityIT {

	private void checkIsMFA(final String resourceName, boolean expected) throws ParsingException {
		this.checkHasProperty(resourceName, Acyclicity.MFA, expected);
	}

	@Test
	public void isMFA_datalog() throws ParsingException {
		this.checkIsMFA("datalog.rls", true);
	}

	@Test
	public void isMFA_nonRecursive() throws ParsingException {
		this.checkIsMFA("nonRecursive.rls", true);
	}

	@Test
	public void isMFA_JA_1() throws ParsingException {
		this.checkIsMFA("JA_1.rls", true);
	}
	
	@Test
	public void isMFA_MSA_1() throws ParsingException {
		this.checkIsMFA("MSA_1.rls", true);
	}

	@Test
	public void isMFA_MFA_1() throws ParsingException {
		this.checkIsMFA("MFA_1.rls", true);
	}

	@Test
	public void isNotMFA_RJA_1() throws ParsingException {
		this.checkIsMFA("RJA_1.rls", false);
	}

	@Test
	public void isNotMFA_RJA_2() throws ParsingException {
		this.checkIsMFA("RJA_2.rls", false);
	}

	@Test
	public void isNotMFA_RJA_3() throws ParsingException {
		this.checkIsMFA("RJA_3.rls", false);
	}
	
	@Test
	public void isNotMFA_RMFA_1() throws ParsingException {
		this.checkIsMFA("RMFA_1.rls", false);
	}
	
	@Test
	public void isNotMFA_RMFA_2() throws ParsingException {
		this.checkIsMFA("RMFA_2.rls", false);
	}

	@Test
	public void isNotMFA_1_depth_RMFA_1() throws ParsingException {
		this.checkIsMFA("1_depth_RMFA_1.rls", false);
	}

	@Test
	public void isNotMFA_MFC_1() throws ParsingException {
		this.checkIsMFA("MFC_1.rls", false);
	}

	@Test
	public void isNotMFA_RMFC_1() throws ParsingException {
		this.checkIsMFA("RMFC_1.rls", false);
	}
	
	@Test
	public void isNotRMFA_bike_wheel_spike() throws ParsingException {
		this.checkIsMFA("bike_wheel_spike.rls", false);
	}
}
