package org.semanticweb.rulewerk.parser;

/*-
 * #%L
 * vlog4j-parser
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

import java.net.URI;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class DirectiveArgumentTest {
	private static final String STRING = "src/test/resources/facts.rls";
	private static final URI IRI = URI.create("https://example.org");
	private static final Term TERM = Expressions.makeDatatypeConstant(STRING, IRI.toString());

	private static final DirectiveArgument STRING_ARGUMENT = DirectiveArgument.string(STRING);
	private static final DirectiveArgument IRI_ARGUMENT = DirectiveArgument.iri(IRI);
	private static final DirectiveArgument TERM_ARGUMENT = DirectiveArgument.term(TERM);

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
		assertTrue(STRING_ARGUMENT.equals(DirectiveArgument.string(STRING)));
		assertTrue(IRI_ARGUMENT.equals(DirectiveArgument.iri(IRI)));
		assertTrue(TERM_ARGUMENT.equals(DirectiveArgument.term(TERM)));
	}

	@Test
	public void equals_notEqualButSameType_returnsFalse() {
		assertFalse(STRING_ARGUMENT.equals(DirectiveArgument.string(STRING + "test")));
		assertFalse(IRI_ARGUMENT.equals(DirectiveArgument.iri(URI.create("https://example.com"))));
		assertFalse(TERM_ARGUMENT
				.equals(DirectiveArgument.term(Expressions.makeDatatypeConstant(STRING, "https://example.com"))));
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
