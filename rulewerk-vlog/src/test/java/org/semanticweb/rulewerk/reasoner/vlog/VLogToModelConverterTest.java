package org.semanticweb.rulewerk.reasoner.vlog;

/*-
 * #%L
 * Rulewerk VLog Reasoner Support
 * %%
 * Copyright (C) 2018 - 2020 Rulewerk Developers
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

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.AbstractConstantImpl;
import org.semanticweb.rulewerk.core.model.implementation.DatatypeConstantImpl;
import org.semanticweb.rulewerk.core.model.implementation.LanguageStringConstantImpl;
import org.semanticweb.rulewerk.core.model.implementation.NamedNullImpl;

public class VLogToModelConverterTest {

	@Test
	public void testAbstractConstantConversion() {
		final karmaresearch.vlog.Term vLogTerm = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT,
				"c");
		final Term rulewerkTerm = new AbstractConstantImpl("c");
		final Term convertedTerm = VLogToModelConverter.toTerm(vLogTerm);
		assertEquals(rulewerkTerm, convertedTerm);
	}

	@Test
	public void testAbstractConstantIriConversion() {
		final karmaresearch.vlog.Term vLogTerm = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT,
				"<http://example.org/test>");
		final Term rulewerkTerm = new AbstractConstantImpl("http://example.org/test");
		final Term convertedTerm = VLogToModelConverter.toTerm(vLogTerm);
		assertEquals(rulewerkTerm, convertedTerm);
	}

	@Test
	public void testDatatypeConstantConversion() {
		final karmaresearch.vlog.Term vLogTerm = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT,
				"\"a\"^^<http://example.org/test>");
		final Term rulewerkTerm = new DatatypeConstantImpl("a", "http://example.org/test");
		final Term convertedTerm = VLogToModelConverter.toTerm(vLogTerm);
		assertEquals(rulewerkTerm, convertedTerm);
	}

	@Test
	public void testLanguageStringConversion() {
		final karmaresearch.vlog.Term vLogTerm = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT,
				"\"Test\"@en");
		final Term rulewerkTerm = new LanguageStringConstantImpl("Test", "en");
		final Term convertedTerm = VLogToModelConverter.toTerm(vLogTerm);
		assertEquals(rulewerkTerm, convertedTerm);
	}

	@Test
	public void testNamedNullConversion() {
		final karmaresearch.vlog.Term vLogTerm = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.BLANK,
				"_123");
		final Term rulewerkTerm = new NamedNullImpl("_123");
		final Term convertedTerm = VLogToModelConverter.toTerm(vLogTerm);
		assertEquals(rulewerkTerm, convertedTerm);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testVariableConversion() {
		final karmaresearch.vlog.Term vLogTerm = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE,
				"X");
		VLogToModelConverter.toTerm(vLogTerm);
	}

	@Test(expected = RuntimeException.class)
	public void testAbstractConstantContainingQuoteExpression() {
		final String constName = "\"";
		VLogToModelConverter.toTerm(new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT, constName));
	}

}
