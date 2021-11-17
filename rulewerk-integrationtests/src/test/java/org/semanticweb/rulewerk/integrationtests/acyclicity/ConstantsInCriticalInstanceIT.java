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

public class ConstantsInCriticalInstanceIT extends AcyclicityIT {

	@Test
	public void isNotMSA_constants_1() throws ParsingException {
		// only MSA if the critical instance is built using all rule set constants
		this.checkHasProperty("constants_1.rls", Acyclicity.MSA, false);
	}

	@Test
	public void isNotRMSA_constants_1() throws ParsingException {
		// only RMSA if the critical instance is built using all rule set constants
		this.checkHasProperty("constants_1.rls", Acyclicity.RMSA, false);
	}

	@Test
	public void isNotMFA_constants_1() throws ParsingException {
		// only MFA if the critical instance is built using all rule set constants
		this.checkHasProperty("constants_1.rls", Acyclicity.MFA, false);
	}

	@Test
	public void isNotRMFA_constants_1() throws ParsingException {
		// only RMFA if the critical instance is built using all rule set constants
		this.checkHasProperty("constants_1.rls", Acyclicity.RMFA, false);
	}

}
