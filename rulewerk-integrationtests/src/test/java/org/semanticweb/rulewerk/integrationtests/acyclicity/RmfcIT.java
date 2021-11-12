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

public class RmfcIT extends AcyclicityIT {

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
	public void isNotRMFC_MSA_1() throws ParsingException {
		this.checkIsRMFC("MSA_1.rls", false);
	}

	@Test
	public void isNotMFC_MFA_1() throws ParsingException {
		this.checkIsRMFC("MFA_1.rls", false);
	}

	@Test
	public void isNotRMFC_1_depth_RMFA_1() throws ParsingException {
		this.checkIsRMFC("1_depth_RMFA_1.rls", false);
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

	@Test
	public void isRMFC_MFC_1() throws ParsingException {
		this.checkIsRMFC("MFC_1.rls", true);
	}

	@Test
	public void isRMFC_RMFC_1() throws ParsingException {
		this.checkIsRMFC("RMFC_1.rls", true);
	}
	
	@Test
	public void isNotRMFC_MSA_constants() throws ParsingException {
		this.checkIsRMFC("MSA_constants.rls", false);
	}

}
