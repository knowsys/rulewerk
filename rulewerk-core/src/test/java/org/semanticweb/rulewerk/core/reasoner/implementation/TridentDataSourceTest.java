package org.semanticweb.rulewerk.core.reasoner.implementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

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

import java.io.IOException;

import org.junit.Test;
import org.mockito.Mockito;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class TridentDataSourceTest {

	@Test(expected = NullPointerException.class)
	public void nullFile_fails() throws IOException {
		new TridentDataSource(null);
	}

	@Test
	public void get_succeeds() throws IOException {
		final TridentDataSource tridentDataSource = new TridentDataSource("trident/path");
		assertEquals("trident/path", tridentDataSource.getPath());
	}

	@Test
	public void getDeclarationFact_succeeds() throws IOException {
		final TridentDataSource tridentDataSource = new TridentDataSource("trident/path");
		Fact fact = tridentDataSource.getDeclarationFact();
		assertEquals(TridentDataSource.declarationPredicateName, fact.getPredicate().getName());
		assertEquals(1, fact.getPredicate().getArity());
		assertEquals(Expressions.makeDatatypeConstant("trident/path", PrefixDeclarationRegistry.XSD_STRING),
				fact.getArguments().get(0));
	}

	@Test
	public void visit_succeeds() throws IOException {
		final DataSourceConfigurationVisitor visitor = Mockito.spy(DataSourceConfigurationVisitor.class);
		final TridentDataSource tridentDataSource = new TridentDataSource("trident/path");

		tridentDataSource.accept(visitor);

		Mockito.verify(visitor).visit(tridentDataSource);
	}

	@Test
	public void hashEquals_succeed() throws IOException {
		final TridentDataSource tridentDataSource1 = new TridentDataSource("trident/path");
		final TridentDataSource tridentDataSource2 = new TridentDataSource("trident/path");
		final TridentDataSource tridentDataSource3 = new TridentDataSource("trident/anotherpath");

		assertEquals(tridentDataSource1, tridentDataSource2);
		assertEquals(tridentDataSource1.hashCode(), tridentDataSource2.hashCode());
		assertNotEquals(tridentDataSource1, tridentDataSource3);
		assertEquals(tridentDataSource1, tridentDataSource1);
		assertFalse(tridentDataSource1.equals(null));
		assertFalse(tridentDataSource1.equals("trident/path"));
	}
}
