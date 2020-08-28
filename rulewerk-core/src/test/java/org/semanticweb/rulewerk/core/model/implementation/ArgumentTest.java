package org.semanticweb.rulewerk.core.model.implementation;

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

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Argument;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class ArgumentTest {
	private static final Term TERM = Expressions.makeDatatypeConstant("some string",
			PrefixDeclarationRegistry.XSD_STRING);
	private static final PositiveLiteral LITERAL = Expressions.makePositiveLiteral("p", TERM);
	private static final Rule RULE = Expressions.makeRule(LITERAL, LITERAL);

	private static final Argument TERM_ARGUMENT = Argument.term(TERM);
	private static final Argument LITERAL_ARGUMENT = Argument.positiveLiteral(LITERAL);
	private static final Argument RULE_ARGUMENT = Argument.rule(RULE);

	@Test
	public void equals_null_returnsFalse() {
		assertFalse(LITERAL_ARGUMENT.equals(null));
		assertFalse(RULE_ARGUMENT.equals(null));
		assertFalse(TERM_ARGUMENT.equals(null));
	}

	@Test
	public void equals_self_returnsTrue() {
		assertTrue(RULE_ARGUMENT.equals(RULE_ARGUMENT));
		assertTrue(LITERAL_ARGUMENT.equals(LITERAL_ARGUMENT));
		assertTrue(TERM_ARGUMENT.equals(TERM_ARGUMENT));
	}

	@Test
	public void equals_equal_returnsTrue() {
		assertTrue(RULE_ARGUMENT.equals(Argument.rule(RULE)));
		assertTrue(LITERAL_ARGUMENT.equals(Argument.positiveLiteral(LITERAL)));
		assertTrue(TERM_ARGUMENT.equals(Argument.term(TERM)));
	}

	@Test
	public void equals_notEqualButSameType_returnsFalse() {
		assertFalse(RULE_ARGUMENT.equals(Argument.rule(Expressions.makeRule(LITERAL, LITERAL, LITERAL))));
		assertFalse(LITERAL_ARGUMENT.equals(Argument.positiveLiteral(Expressions.makePositiveLiteral("q", TERM))));
		assertFalse(TERM_ARGUMENT
				.equals(Argument.term(Expressions.makeDatatypeConstant("another string", "https://example.com"))));
	}

	@Test
	public void equals_differentType_returnsFalse() {
		assertFalse(RULE_ARGUMENT.equals(LITERAL_ARGUMENT));
		assertFalse(RULE_ARGUMENT.equals(TERM_ARGUMENT));
		assertFalse(LITERAL_ARGUMENT.equals(RULE_ARGUMENT));
		assertFalse(LITERAL_ARGUMENT.equals(TERM_ARGUMENT));
		assertFalse(TERM_ARGUMENT.equals(RULE_ARGUMENT));
		assertFalse(TERM_ARGUMENT.equals(LITERAL_ARGUMENT));
	}

	@Test
	public void equals_String_returnsFalse() {
		assertFalse(RULE_ARGUMENT.equals((Object) "test"));
		assertFalse(LITERAL_ARGUMENT.equals((Object) "test"));
		assertFalse(TERM_ARGUMENT.equals((Object) "test"));
	}
}
