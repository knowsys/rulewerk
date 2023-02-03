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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.parser.ParsingException;

public class JAIT extends AcyclicityTest {

	@Test
	public void isJA_datalog() throws ParsingException {
		try (Reasoner r = this.getReasonerWithKbFromResource("datalog.rls")) {
			assertTrue(r.isJA());
		}
	}

	@Test
	public void isJA_nonRecursive() throws ParsingException {
		try (Reasoner r = this.getReasonerWithKbFromResource("non-recursive.rls")) {
			assertTrue(r.isJA());
		}
	}

	@Test
	public void isJA_JA_1() throws ParsingException {
		try (Reasoner r = this.getReasonerWithKbFromResource("JA-1.rls")) {
			assertTrue(r.isJA());
		}
	}

	@Test
	public void isNotJA_RJA_1() throws ParsingException {
		try (Reasoner r = this.getReasonerWithKbFromResource("RJA-1.rls")) {
			assertFalse(r.isJA());
		}
	}

	@Test
	public void isNotJA_RJA_2() throws ParsingException {
		try (Reasoner r = this.getReasonerWithKbFromResource("RJA-2.rls")) {
			assertFalse(r.isJA());
		}
	}

	@Test
	public void isNotJA_RJA_3() throws ParsingException {
		try (Reasoner r = this.getReasonerWithKbFromResource("RJA-3.rls")) {
			assertFalse(r.isJA());
		}
	}

	@Test
	public void isNotJA_MFA_1() throws ParsingException {
		try (Reasoner r = this.getReasonerWithKbFromResource("MFA-1.rls")) {
			assertFalse(r.isJA());
		}
	}

	@Test
	public void isNotJA_RMFA_1() throws ParsingException {
		try (Reasoner r = this.getReasonerWithKbFromResource("RMFA-1.rls")) {
			assertFalse(r.isJA());
		}
	}

	@Test
	public void isNotJA_MFC_1() throws ParsingException {
		try (Reasoner r = this.getReasonerWithKbFromResource("MFC-1.rls")) {
			assertFalse(r.isJA());
		}
	}

	@Test
	public void isNotJA_RMFC_1() throws ParsingException {
		try (Reasoner r = this.getReasonerWithKbFromResource("RMFC-1.rls")) {
			assertFalse(r.isJA());
		}
	}
}
