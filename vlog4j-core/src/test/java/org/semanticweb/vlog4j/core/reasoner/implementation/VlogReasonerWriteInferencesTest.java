package org.semanticweb.vlog4j.core.reasoner.implementation;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.AbstractConstant;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
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

public class VlogReasonerWriteInferencesTest {
	final Constant c = Expressions.makeAbstractConstant("http://example.org/c");
	final Fact fact = Expressions.makeFact("http://example.org/s", c);
	final AbstractConstant dresdenConst = Expressions.makeAbstractConstant("dresden");
	final Predicate locatedInPred = Expressions.makePredicate("LocatedIn", 2);
	final Predicate addressPred = Expressions.makePredicate("address", 4);
	final Predicate universityPred = Expressions.makePredicate("university", 2);
	final UniversalVariable varX = Expressions.makeUniversalVariable("X");
	final UniversalVariable varY = Expressions.makeUniversalVariable("Y");
	final PositiveLiteral pl1 = Expressions.makePositiveLiteral(locatedInPred, varX, varY);
	final PositiveLiteral pl2 = Expressions.makePositiveLiteral("location", varX, varY);
	final PositiveLiteral pl3 = Expressions.makePositiveLiteral(addressPred, varX,
			Expressions.makeExistentialVariable("Y"), Expressions.makeExistentialVariable("Z"),
			Expressions.makeExistentialVariable("Q"));
	final PositiveLiteral pl4 = Expressions.makePositiveLiteral(locatedInPred, Expressions.makeExistentialVariable("Q"),
			Expressions.makeUniversalVariable("F"));
	final PositiveLiteral pl5 = Expressions.makePositiveLiteral(universityPred, varX,
			Expressions.makeUniversalVariable("F"));
	final Conjunction<PositiveLiteral> conjunction = Expressions.makePositiveConjunction(pl3, pl4);
	final Rule rule1 = Expressions.makeRule(pl1, pl2);
	final Rule rule2 = Expressions.makeRule(conjunction, Expressions.makeConjunction(pl5));
	final Fact f1 = Expressions.makeFact(locatedInPred, Expressions.makeAbstractConstant("Egypt"),
			Expressions.makeAbstractConstant("Africa"));
	final Fact f2 = Expressions.makeFact(addressPred, Expressions.makeAbstractConstant("TSH"),
			Expressions.makeAbstractConstant("Pragerstraße13"), Expressions.makeAbstractConstant("01069"),
			dresdenConst);
	final Fact f3 = Expressions.makeFact("city", dresdenConst);
	final Fact f4 = Expressions.makeFact("country", Expressions.makeAbstractConstant("germany"));
	final Fact f5 = Expressions.makeFact(universityPred, Expressions.makeAbstractConstant("tudresden"),
			Expressions.makeAbstractConstant("germany"));
	final InMemoryDataSource locations = new InMemoryDataSource(2, 1);

	@Test
	public void testWriteInferences() throws IOException {
		KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(fact);
		kb.addStatements(rule1, rule2, f1, f2, f3, f4, f5);
		locations.addTuple("dresden", "germany");
		kb.addStatement(new DataSourceDeclarationImpl(Expressions.makePredicate("location", 2), locations));
		List<String> inferences = new ArrayList<String>();
		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			reasoner.writeInferences(stream);
			stream.flush();
			try (BufferedReader input = new BufferedReader(new StringReader(stream.toString()))) {
				String factString = "";
				while ((factString = input.readLine()) != null) {
					inferences.add(factString);
				}

			}
			assertEquals(10, inferences.size());
		}

	}
}
