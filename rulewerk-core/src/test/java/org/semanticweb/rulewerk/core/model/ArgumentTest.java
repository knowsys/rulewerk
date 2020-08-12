package org.semanticweb.rulewerk.core.model;

/*-
 * #%L
 * Rulewerk Parser
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
import static org.junit.Assert.*;

import java.net.URI;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Argument;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class ArgumentTest {
	private static final String STRING = "src/test/resources/facts.rls";
	private static final URI IRI = URI.create("https://example.org");
	private static final Term TERM = Expressions.makeDatatypeConstant(STRING, IRI.toString());

	private static final Argument STRING_ARGUMENT = Argument.string(STRING);
	private static final Argument IRI_ARGUMENT = Argument.iri(IRI);
	private static final Argument TERM_ARGUMENT = Argument.term(TERM);

	@Test
	public void equals_null_returnsFalse() {
		assertFalse(STRING_ARGUMENT.equals(null));
		assertFalse(IRI_ARGUMENT.equals(null));
		assertFalse(TERM_ARGUMENT.equals(null));
	}

	@Test
	public void equals_self_returnsTrue() {
		assertTrue(STRING_ARGUMENT.equals(STRING_ARGUMENT));
		assertTrue(IRI_ARGUMENT.equals(IRI_ARGUMENT));
		assertTrue(TERM_ARGUMENT.equals(TERM_ARGUMENT));
	}

	@Test
	public void equals_equal_returnsTrue() {
		assertTrue(STRING_ARGUMENT.equals(Argument.string(STRING)));
		assertTrue(IRI_ARGUMENT.equals(Argument.iri(IRI)));
		assertTrue(TERM_ARGUMENT.equals(Argument.term(TERM)));
	}

	@Test
	public void equals_notEqualButSameType_returnsFalse() {
		assertFalse(STRING_ARGUMENT.equals(Argument.string(STRING + "test")));
		assertFalse(IRI_ARGUMENT.equals(Argument.iri(URI.create("https://example.com"))));
		assertFalse(TERM_ARGUMENT
				.equals(Argument.term(Expressions.makeDatatypeConstant(STRING, "https://example.com"))));
	}

	@Test
	public void equals_differentType_returnsFalse() {
		assertFalse(STRING_ARGUMENT.equals(IRI_ARGUMENT));
		assertFalse(STRING_ARGUMENT.equals(TERM_ARGUMENT));
		assertFalse(IRI_ARGUMENT.equals(STRING_ARGUMENT));
		assertFalse(IRI_ARGUMENT.equals(TERM_ARGUMENT));
		assertFalse(TERM_ARGUMENT.equals(STRING_ARGUMENT));
		assertFalse(TERM_ARGUMENT.equals(IRI_ARGUMENT));
	}

	@Test
	public void equals_String_returnsFalse() {
		assertFalse(STRING_ARGUMENT.equals((Object) "test"));
		assertFalse(IRI_ARGUMENT.equals((Object) "test"));
		assertFalse(TERM_ARGUMENT.equals((Object) "test"));
	}
}
