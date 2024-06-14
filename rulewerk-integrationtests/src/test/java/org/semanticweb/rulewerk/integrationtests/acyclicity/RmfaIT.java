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
import org.semanticweb.rulewerk.core.reasoner.RulesCyclicityProperty;
import org.semanticweb.rulewerk.parser.ParsingException;

public class RmfaIT extends AcyclicityIT {

	@Override
	protected RulesCyclicityProperty getPropertyToCheck() {
		return Acyclicity.RMFA;
	}

	@Test
	public void isRMFA_datalog() throws ParsingException {
		this.checkHasProperty("datalog.rls", true);
	}

	@Test
	public void isRMFA_nonRecursive() throws ParsingException {
		this.checkHasProperty("nonRecursive.rls", true);
	}

	@Test
	public void isRMFA_JA_1() throws ParsingException {
		this.checkHasProperty("JA_1.rls", true);
	}

	@Test
	public void isRMFA_RJA_1() throws ParsingException {
		this.checkHasProperty("RJA_1.rls", true);
	}

	@Test
	public void isRMFA_RJA_2() throws ParsingException {
		this.checkHasProperty("RJA_2.rls", true);
	}

	@Test
	public void isRMFA_RJA_3() throws ParsingException {
		this.checkHasProperty("RJA_3.rls", true);
	}

	@Test
	public void isRMFA_MSA_1() throws ParsingException {
		this.checkHasProperty("MSA_1.rls", true);
	}

	@Test
	public void isRMFA_MFA_1() throws ParsingException {
		this.checkHasProperty("MFA_1.rls", true);
	}

	@Test
	public void isRMFA_RMFA_1() throws ParsingException {
		this.checkHasProperty("RMFA_1.rls", true);
	}

	@Test
	public void isRMFA_RMFA_2() throws ParsingException {
		this.checkHasProperty("RMFA_2.rls", true);
	}

	@Test
	public void isNotRMFA_1_depth_RMFA_1() throws ParsingException {
		this.checkHasProperty("1_depth_RMFA_1.rls", false);
	}

	@Test
	public void isNotRMFA_MFC_1() throws ParsingException {
		this.checkHasProperty("MFC_1.rls", false);
	}

	@Test
	public void isNotRMFA_RMFC_1() throws ParsingException {
		this.checkHasProperty("RMFC_1.rls", false);
	}

	@Test
	public void isNotRMFA_bike_wheel_spike() throws ParsingException {
		this.checkHasProperty("bike_wheel_spike.rls", false);
	}

	// TODO check correctness
//	@Test
//	public void isNotRMFA_constants_1() throws ParsingException {
//		// only RMFA if the critical instance is built using all rule set constants
//		this.checkHasProperty("constants_1.rls", false);
//	}
}