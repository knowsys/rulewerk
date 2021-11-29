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

public class RmsaIT extends AcyclicityIT {
	private void checkIsRMSA(final String resourceName, boolean expected) throws ParsingException {
		this.checkHasProperty(resourceName, Acyclicity.RMSA, expected);
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

	@Test
	public void IsRMSA_RJA_1() throws ParsingException {
		this.checkIsRMSA("RJA_1.rls", true);
	}

	@Test
	public void IsRMSA_RJA_2() throws ParsingException {
		this.checkIsRMSA("RJA_2.rls", true);
	}

	@Test
	public void IsRMSA_RJA_3() throws ParsingException {
		this.checkIsRMSA("RJA_3.rls", true);
	}
	
	@Test
	public void isRMSA_MSA_1() throws ParsingException {
		this.checkIsRMSA("MSA_1.rls", true);
	}
	

	@Test
	public void IsNotRMSA_MFA_1() throws ParsingException {
		this.checkIsRMSA("MFA_1.rls", false);
	}
	
	@Test
	public void isNotRMSA_RMFA_1() throws ParsingException {
		this.checkIsRMSA("RMFA_1.rls", false);
	}
	
	@Test
	public void isNotRMSA_RMFA_2() throws ParsingException {
		this.checkIsRMSA("RMFA_2.rls", false);
	}

	@Test
	public void IsNotRMSA_1_depth_RMFA_1() throws ParsingException {
		this.checkIsRMSA("1_depth_RMFA_1.rls", false);
	}

	@Test
	public void IsNotRMSA_MFC_1() throws ParsingException {
		this.checkIsRMSA("MFC_1.rls", false);
	}

	@Test
	public void IsNotRMSA_RMFC_1() throws ParsingException {
		this.checkIsRMSA("RMFC_1.rls", false);
	}
	
	@Test
	public void isNotRMSA_bike_wheel_spike() throws ParsingException {
		this.checkIsRMSA("bike_wheel_spike.rls", false);
	}
}