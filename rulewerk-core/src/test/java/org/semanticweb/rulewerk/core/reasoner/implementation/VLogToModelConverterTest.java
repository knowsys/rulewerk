package org.semanticweb.rulewerk.core.reasoner.implementation;

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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.AbstractConstant;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.AbstractConstantImpl;
import org.semanticweb.rulewerk.core.model.implementation.DatatypeConstantImpl;
import org.semanticweb.rulewerk.core.model.implementation.LanguageStringConstantImpl;
import org.semanticweb.rulewerk.core.model.implementation.NamedNullImpl;

public class VLogToModelConverterTest {

	@Test
	public void testAbstractConstantConversion() {
		final karmaresearch.vlog.Term vLogTerm = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT, "c");
		final Term vLog4jTerm = new AbstractConstantImpl("c");
		final Term convertedTerm = VLogToModelConverter.toTerm(vLogTerm);
		assertEquals(vLog4jTerm, convertedTerm);
	}

	@Test
	public void testAbstractConstantIriConversion() {
		final karmaresearch.vlog.Term vLogTerm = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT,
				"<http://example.org/test>");
		final Term vLog4jTerm = new AbstractConstantImpl("http://example.org/test");
		final Term convertedTerm = VLogToModelConverter.toTerm(vLogTerm);
		assertEquals(vLog4jTerm, convertedTerm);
	}

	@Test
	public void testDatatypeConstantConversion() {
		final karmaresearch.vlog.Term vLogTerm = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT,
				"\"a\"^^<http://example.org/test>");
		final Term vLog4jTerm = new DatatypeConstantImpl("a", "http://example.org/test");
		final Term convertedTerm = VLogToModelConverter.toTerm(vLogTerm);
		assertEquals(vLog4jTerm, convertedTerm);
	}

	@Test
	public void testLanguageStringConversion() {
		final karmaresearch.vlog.Term vLogTerm = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT,
				"\"Test\"@en");
		final Term vLog4jTerm = new LanguageStringConstantImpl("Test", "en");
		final Term convertedTerm = VLogToModelConverter.toTerm(vLogTerm);
		assertEquals(vLog4jTerm, convertedTerm);
	}

	@Test
	public void testNamedNullConversion() {
		final karmaresearch.vlog.Term vLogTerm = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.BLANK, "_123");
		final Term vLog4jTerm = new NamedNullImpl("_123");
		final Term convertedTerm = VLogToModelConverter.toTerm(vLogTerm);
		assertEquals(vLog4jTerm, convertedTerm);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testVariableConversion() {
		final karmaresearch.vlog.Term vLogTerm = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE, "X");
		VLogToModelConverter.toTerm(vLogTerm);
	}

	@Test
	public void testAbstractConstantContainingQuoteExpression() {
		final String constName = "\"";
		final Term convertedTerm = VLogToModelConverter
				.toTerm(new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT, constName));
		assertTrue(convertedTerm.isConstant());
		assertTrue(convertedTerm instanceof AbstractConstant);
		assertEquals(constName, convertedTerm.getName());
	}

}
