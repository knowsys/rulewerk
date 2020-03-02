package org.semanticweb.rulewerk.parser;

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
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.NamedNullImpl;
import org.semanticweb.rulewerk.core.model.implementation.RenamedNamedNull;

public interface ParserTestUtils {
	public default void assertUuid(String uuidLike) {
		try {
			UUID.fromString(uuidLike);
		} catch (IllegalArgumentException e) {
			throw new AssertionError("expected a valid UUID, but got \"" + uuidLike + "\"", e);
		}
	}

	public default void assertArgumentIsNamedNull(Literal literal, int argument) {
		List<Term> arguments = literal.getArguments();
		assertTrue("argument is positive", argument >= 1);
		assertTrue("argument is a valid position", argument <= arguments.size());
		Term term = arguments.get(argument - 1);
		assertTrue("argument is a named null", term instanceof NamedNullImpl);

		if (term instanceof RenamedNamedNull) {
			assertUuid(term.getName());
		}
	}
}
