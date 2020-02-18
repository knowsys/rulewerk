package org.semanticweb.vlog4j.core.reasoner.implementation;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.vlog4j.core.exceptions.PrefixDeclarationException;
import org.semanticweb.vlog4j.core.model.api.AbstractConstant;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.PrefixDeclarations;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.UniversalVariable;
import org.semanticweb.vlog4j.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;

/*-
 * #%L
 * VLog4j Core Components
 * %%
 * Copyright (C) 2018 VLog4j Developers
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

public class VLogReasonerWriteInferencesTest {
	private final Constant c = Expressions.makeAbstractConstant("http://example.org/c");
	private final Fact fact = Expressions.makeFact("http://example.org/s", c);
	private final AbstractConstant dresdenConst = Expressions.makeAbstractConstant("dresden");
	private final Predicate locatedInPred = Expressions.makePredicate("LocatedIn", 2);
	private final Predicate addressPred = Expressions.makePredicate("address", 4);
	private final Predicate universityPred = Expressions.makePredicate("university", 2);
	private final UniversalVariable varX = Expressions.makeUniversalVariable("X");
	private final UniversalVariable varY = Expressions.makeUniversalVariable("Y");
	private final PositiveLiteral pl1 = Expressions.makePositiveLiteral(locatedInPred, varX, varY);
	private final PositiveLiteral pl2 = Expressions.makePositiveLiteral("location", varX, varY);
	private final PositiveLiteral pl3 = Expressions.makePositiveLiteral(addressPred, varX,
			Expressions.makeExistentialVariable("Y"), Expressions.makeExistentialVariable("Z"),
			Expressions.makeExistentialVariable("Q"));
	private final PositiveLiteral pl4 = Expressions.makePositiveLiteral(locatedInPred,
			Expressions.makeExistentialVariable("Q"), Expressions.makeUniversalVariable("F"));
	private final PositiveLiteral pl5 = Expressions.makePositiveLiteral(universityPred, varX,
			Expressions.makeUniversalVariable("F"));
	private final Conjunction<PositiveLiteral> conjunction = Expressions.makePositiveConjunction(pl3, pl4);
	private final Rule rule1 = Expressions.makeRule(pl1, pl2);
	private final Rule rule2 = Expressions.makeRule(conjunction, Expressions.makeConjunction(pl5));
	private final Fact f1 = Expressions.makeFact(locatedInPred, Expressions.makeAbstractConstant("Egypt"),
			Expressions.makeAbstractConstant("Africa"));
	private final Fact f2 = Expressions.makeFact(addressPred, Expressions.makeAbstractConstant("TSH"),
			Expressions.makeAbstractConstant("Pragerstraße13"), Expressions.makeAbstractConstant("01069"),
			dresdenConst);
	private final Fact f3 = Expressions.makeFact("city", dresdenConst);
	private final Fact f4 = Expressions.makeFact("country", Expressions.makeAbstractConstant("germany"));
	private final Fact f5 = Expressions.makeFact(universityPred, Expressions.makeAbstractConstant("tudresden"),
			Expressions.makeAbstractConstant("germany"));
	private final InMemoryDataSource locations = new InMemoryDataSource(2, 1);
	private KnowledgeBase kb;

	@Before
	public void initKb() {
		kb = new KnowledgeBase();
		kb.addStatement(fact);
		kb.addStatements(rule1, rule2, f1, f2, f3, f4, f5);
		locations.addTuple("dresden", "germany");
		kb.addStatement(new DataSourceDeclarationImpl(Expressions.makePredicate("location", 2), locations));
	}

	@Test
	public void writeInferences_example_succeeds() throws IOException {
		assertEquals(10, getInferences().size());
	}

	@Test
	public void writeInferences_withPrefixDeclarations_abbreviatesIris()
			throws IOException, PrefixDeclarationException {
		PrefixDeclarations prefixDeclarations = mock(PrefixDeclarations.class);
		when(prefixDeclarations.getBase()).thenReturn("");
		when(prefixDeclarations.getPrefix(eq("eg:"))).thenReturn("http://example.org/");
		when(prefixDeclarations.iterator()).thenReturn(Arrays.asList("eg:").iterator());
		kb.mergePrefixDeclarations(prefixDeclarations);

		assertEquals(11, getInferences().size());
		assertTrue("the abbreviated fact is present", getInferences().contains("eg:s(eg:c) ."));
	}

	@Test
	public void writeInferences_withBase_writesBase() throws IOException, PrefixDeclarationException {
		PrefixDeclarations prefixDeclarations = mock(PrefixDeclarations.class);
		when(prefixDeclarations.getBase()).thenReturn("http://example.org/");
		when(prefixDeclarations.iterator()).thenReturn(Arrays.<String>asList().iterator());
		kb.mergePrefixDeclarations(prefixDeclarations);

		assertEquals(11, getInferences().size());
		assertTrue("the base declaration is present", getInferences().contains("@base <http://example.org/> ."));
	}

	private List<String> getInferences() throws IOException {
		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			reasoner.writeInferences(stream);
			stream.flush();

			Stream<String> inferences = Arrays.stream(stream.toString().split("(?<=[>)]\\s*)\\.\\s*"));

			return inferences.map((String inference) -> inference + ".").collect(Collectors.toList());
		}
	}
}
