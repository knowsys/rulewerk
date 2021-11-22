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
import org.semanticweb.rulewerk.core.reasoner.Cyclicity;
import org.semanticweb.rulewerk.parser.ParsingException;

public class MfcIT extends AcyclicityIT {

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
	public void isNotMFC_MSA_1() throws ParsingException {
		this.checkIsMFC("MSA_1.rls", false);
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
	public void isMFC_RMFA_2() throws ParsingException {
		this.checkIsMFC("RMFA_2.rls", true);
	}

	@Test
	public void isMFC_1_depth_RMFA_1() throws ParsingException {
		this.checkIsMFC("1_depth_RMFA_1.rls", true);
	}

	@Test
	public void isMFC_MFC_1() throws ParsingException {
		this.checkIsMFC("MFC_1.rls", true);
	}

	@Test
	public void isMFC_RMFC_1() throws ParsingException {
		this.checkIsMFC("RMFC_1.rls", true);
	}
	
	@Test
	public void isMFC_bike_wheel_spike() throws ParsingException {
		this.checkIsMFC("bike_wheel_spike.rls", true);
	}
}
