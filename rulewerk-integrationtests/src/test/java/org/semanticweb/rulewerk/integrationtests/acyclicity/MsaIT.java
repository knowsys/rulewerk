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

public class MsaIT extends AcyclicityIT {
	
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
	
	@Test
	public void isNotMSA_RJA_1() throws ParsingException {
		this.checkIsMSA("RJA_1.rls", false);
	}
	
	@Test
	public void isNotMSA_RJA_2() throws ParsingException {
		this.checkIsMSA("RJA_2.rls", false);
	}
	
	@Test
	public void isNotMSA_RJA_3() throws ParsingException {
		this.checkIsMSA("RJA_3.rls", false);
	}
	
	@Test
	public void isMSA_MSA_1() throws ParsingException {
		this.checkIsMSA("MSA_1.rls", true);
	}
	
	@Test
	public void isNotMSA_MFA_1() throws ParsingException {
		this.checkIsMSA("MFA_1.rls", false);
	}
	
	@Test
	public void isNotMSA_RMFA_1() throws ParsingException {
		this.checkIsMSA("RMFA_1.rls", false);
	}
	
	@Test
	public void isNotMSA_RMFA_2() throws ParsingException {
		this.checkIsMSA("RMFA_2.rls", false);
	}
	
	@Test
	public void isNotMSA_1_depth_RMFA_1() throws ParsingException {
		this.checkIsMSA("1_depth_RMFA_1.rls", false);
	}

	@Test
	public void isNotMSA_MFC_1() throws ParsingException {
		this.checkIsMSA("MFC_1.rls", false);
	}

	@Test
	public void isNotMSA_RMFC_1() throws ParsingException {
		this.checkIsMSA("RMFC_1.rls", false);
	}
	
	@Test
	public void isNotMSA_bike_wheel_spike() throws ParsingException {
		this.checkIsMSA("bike_wheel_spike.rls", false);
	}

}