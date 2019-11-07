package org.semanticweb.vlog4j.core.reasoner.implementation;

/*-
 * #%L
 * VLog4j Core Components
 * %%
 * Copyright (C) 2018 - 2019 VLog4j Developers
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

import static org.junit.Assert.*;

import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.implementation.AbstractConstantImpl;
import org.semanticweb.vlog4j.core.model.implementation.DatatypeConstantImpl;
import org.semanticweb.vlog4j.core.model.implementation.LanguageStringConstantImpl;
import org.semanticweb.vlog4j.core.model.implementation.NamedNullImpl;

public class VLogToModelConverterTest {

	@Test
	public void testAbstractConstantConversion() {
		karmaresearch.vlog.Term vLogTerm = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT, "c");
		Term vLog4jTerm = new AbstractConstantImpl("c");
		Term convertedTerm = VLogToModelConverter.toTerm(vLogTerm);
		assertEquals(vLog4jTerm, convertedTerm);
	}

	@Test
	public void testAbstractConstantIriConversion() {
		karmaresearch.vlog.Term vLogTerm = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT,
				"<http://example.org/test>");
		Term vLog4jTerm = new AbstractConstantImpl("http://example.org/test");
		Term convertedTerm = VLogToModelConverter.toTerm(vLogTerm);
		assertEquals(vLog4jTerm, convertedTerm);
	}

	@Test
	public void testDatatypeConstantConversion() {
		karmaresearch.vlog.Term vLogTerm = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT,
				"\"a\"^^<http://example.org/test>");
		Term vLog4jTerm = new DatatypeConstantImpl("a", "http://example.org/test");
		Term convertedTerm = VLogToModelConverter.toTerm(vLogTerm);
		assertEquals(vLog4jTerm, convertedTerm);
	}

	@Test
	public void testLanguageStringConversion() {
		karmaresearch.vlog.Term vLogTerm = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT,
				"\"Test\"@en");
		Term vLog4jTerm = new LanguageStringConstantImpl("Test", "en");
		Term convertedTerm = VLogToModelConverter.toTerm(vLogTerm);
		assertEquals(vLog4jTerm, convertedTerm);
	}

	@Test
	public void testNamedNullConversion() {
		karmaresearch.vlog.Term vLogTerm = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.BLANK, "_123");
		Term vLog4jTerm = new NamedNullImpl("_123");
		Term convertedTerm = VLogToModelConverter.toTerm(vLogTerm);
		assertEquals(vLog4jTerm, convertedTerm);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testVariableConversion() {
		karmaresearch.vlog.Term vLogTerm = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE, "X");
		VLogToModelConverter.toTerm(vLogTerm);
	}

}
