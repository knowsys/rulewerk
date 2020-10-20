package org.semanticweb.rulewerk.asp;

/*-
 * #%L
 * Rulewerk ASP Components
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

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.rulewerk.asp.implementation.KnowledgeBaseAnalyser;
import org.semanticweb.rulewerk.core.model.api.*;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class KnowledgeBaseAnalyserTest {

	private KnowledgeBase knowledgeBase;
	private KnowledgeBaseAnalyser analyser;

	private final Variable x = Expressions.makeUniversalVariable("X");
	private final Constant c = Expressions.makeAbstractConstant("c");

	private final PositiveLiteral atomP1 = Expressions.makePositiveLiteral("P1", x);
	private final PositiveLiteral atomP2 = Expressions.makePositiveLiteral("P2", x);

	private final PositiveLiteral atomQ1 = Expressions.makePositiveLiteral("Q1", x);
	private final PositiveLiteral atomQ2 = Expressions.makePositiveLiteral("Q2", x);
	private final PositiveLiteral atomQ3 = Expressions.makePositiveLiteral("Q3", x);
	private final NegativeLiteral atomNegQ2 = Expressions.makeNegativeLiteral("Q2", x);

	private final PositiveLiteral atomR1 = Expressions.makePositiveLiteral("R1", x);

	private final PositiveLiteral atomS1 = Expressions.makePositiveLiteral("S1", x);
	private final PositiveLiteral atomS2 = Expressions.makePositiveLiteral("S2", x);
	private final NegativeLiteral atomNegS2 = Expressions.makeNegativeLiteral("S2", x);

	@Before
	public void initKB() {
		knowledgeBase = new KnowledgeBase();

		knowledgeBase.addStatement(Expressions.makeFact("T1", c));

		knowledgeBase.addStatement(Expressions.makeRule(atomP1, atomP2));
		knowledgeBase.addStatement(Expressions.makeRule(atomP2, atomP1));

		knowledgeBase.addStatement(Expressions.makeRule(atomQ1, atomQ3, atomNegQ2));
		knowledgeBase.addStatement(Expressions.makeRule(atomQ2, atomQ3));
		knowledgeBase.addStatement(Expressions.makeRule(atomQ3, atomQ1, atomQ2));

		knowledgeBase.addStatement(Expressions.makeRule(atomR1, atomP1, atomP2, atomQ3));

		knowledgeBase.addStatement(Expressions.makeRule(atomS1, atomNegS2, atomS1));
		knowledgeBase.addStatement(Expressions.makeRule(atomS2, atomS1));

		analyser = new KnowledgeBaseAnalyser(knowledgeBase);
	}


	@Test
	public void getOverApproximatedPredicates() {
		Set<Predicate> expectedPredicats = new HashSet<>();
		for (String name : Arrays.asList("Q1", "Q2", "Q3", "R1", "S1", "S2")) {
			expectedPredicats.add(Expressions.makePredicate(name, 1));
		}
		assertEquals(expectedPredicats, analyser.getOverApproximatedPredicates());
	}

	@Test
	public void getConnectedComponents() {
		Set<Set<String>> stringComponents = new HashSet<>();
		stringComponents.add(new HashSet<>(Arrays.asList("P1", "P2")));
		stringComponents.add(new HashSet<>(Arrays.asList("Q1", "Q2", "Q3")));
		stringComponents.add(new HashSet<>(Collections.singletonList("R1")));
		stringComponents.add(new HashSet<>(Arrays.asList("S1", "S2")));
		stringComponents.add(new HashSet<>(Collections.singletonList("T1")));

		Set<Set<Predicate>> expectedComponents = new HashSet<>();
		for (Set<String> stringComponent : stringComponents) {
			Set<Predicate> component = new HashSet<>();
			for (String name : stringComponent) {
				component.add(Expressions.makePredicate(name, 1));
			}
			expectedComponents.add(component);
		}

		assertEquals(expectedComponents, new HashSet<>(analyser.getStronglyConnectedComponents().values()));
	}

}
