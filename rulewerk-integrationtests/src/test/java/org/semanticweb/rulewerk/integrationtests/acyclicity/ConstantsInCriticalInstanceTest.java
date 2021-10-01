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

public class ConstantsInCriticalInstanceTest extends AcyclicityTest {

	private void checkIsMFA(final String resourceName, boolean expected) throws ParsingException {
		this.checkHasProperty(resourceName, Acyclicity.MFA, expected);
	}
	
	private void checkIsMSA(final String resourceName, boolean expected) throws ParsingException {
		this.checkHasProperty(resourceName, Acyclicity.MSA, expected);
	}
	
	@Test
	public void isMSA_MSA_constants() throws ParsingException {
		// only true if the critical instance is built using all rule set constants
		this.checkIsMSA("MSA_constants.rls", true);
	}
	
	@Test
	public void isRMSA_MSA_constants() throws ParsingException {
		// only true if the critical instance is built using all rule set constants
		this.checkHasProperty("MSA_constants.rls", Acyclicity.RMSA, true);
	}

	@Test
	public void isMFA_MSA_constants() throws ParsingException {
		// only true if the critical instance is built using all rule set constants
		this.checkIsMFA("MSA_constants.rls", true);
	}

	@Test
	public void isRMFA_MSA_constants() throws ParsingException {
		// only true if the critical instance is built using all rule set constants
		this.checkHasProperty("MSA_constants.rls", Acyclicity.RMFA, true);
	}

}
