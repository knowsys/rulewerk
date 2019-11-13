package org.semanticweb.vlog4j.core.reasoner.implementation;

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

import java.io.IOException;

import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.Literal;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;

public class ExtensionSizeTest {

	private static final Predicate predP = Expressions.makePredicate("P", 1);
	private static final Predicate predQ = Expressions.makePredicate("Q", 1);
	private static final Predicate predR = Expressions.makePredicate("R", 2);
	private static final Variable x = Expressions.makeUniversalVariable("x");
	private static final Variable y = Expressions.makeExistentialVariable("y");
	private static final Constant c = Expressions.makeAbstractConstant("c");
	private static final Constant d = Expressions.makeAbstractConstant("d");
	private static final Constant e = Expressions.makeAbstractConstant("e");
	private static final Constant f = Expressions.makeAbstractConstant("f");

	private static final PositiveLiteral Px = Expressions.makePositiveLiteral(predP, x);
	private static final PositiveLiteral Qx = Expressions.makePositiveLiteral(predQ, x);
	private static final PositiveLiteral Qy = Expressions.makePositiveLiteral(predQ, y);
	private static final PositiveLiteral Rxy = Expressions.makePositiveLiteral(predR, x, y);
	private static final Conjunction<PositiveLiteral> conRxyQy = Expressions.makePositiveConjunction(Rxy, Qy);
	private static final Conjunction<Literal> conPx = Expressions.makeConjunction(Px);

	private static final Rule QxPx = Expressions.makeRule(Qx, Px);
	private static final Rule RxyQyPx = Expressions.makeRule(conRxyQy, conPx);

	private static final Fact factPc = Expressions.makeFact(predP, c);
	private static final Fact factPd = Expressions.makeFact(predP, d);

	private static final Fact factQe = Expressions.makeFact(predQ, e);
	private static final Fact factQf = Expressions.makeFact(predQ, f);

	private static final PositiveLiteral Rdy = Expressions.makePositiveLiteral(predR, d, y);
	private static final PositiveLiteral Rxd = Expressions.makePositiveLiteral(predR, x, d);
	private static final PositiveLiteral Rxe = Expressions.makePositiveLiteral(predR, x, e);

	@Test
	public void noFactsnoRules() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		try (VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			assertEquals(0, reasoner.getExtensionSize(Px));
		}
	}

	@Test
	public void noFactsUniversalRule() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(QxPx);
		try (VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			assertEquals(0, reasoner.getExtensionSize(Px));
			assertEquals(0, reasoner.getExtensionSize(Qx));
			assertEquals(0, reasoner.getExtensionSize(Rxy));
		}
	}

	@Test
	public void noFactsExistentialRule() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(RxyQyPx);
		try (VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			assertEquals(0, reasoner.getExtensionSize(Px));
			assertEquals(0, reasoner.getExtensionSize(Qx));
			assertEquals(0, reasoner.getExtensionSize(Rxy));
		}
	}

	@Test
	public void pFactsNoRules() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(factPc, factPd);
		try (VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			assertEquals(2, reasoner.getExtensionSize(Px));
			assertEquals(0, reasoner.getExtensionSize(Qx));
			assertEquals(0, reasoner.getExtensionSize(Rxy));
		}
	}

	@Test
	public void pFactsUniversalRule() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(factPc, factPd, QxPx);
		try (VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			assertEquals(2, reasoner.getExtensionSize(Px));
			assertEquals(2, reasoner.getExtensionSize(Qx));
			assertEquals(0, reasoner.getExtensionSize(Rxy));
		}
	}

	@Test
	public void pFactsExistentialRule() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(factPc, factPd, RxyQyPx);
		try (VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			assertEquals(2, reasoner.getExtensionSize(Px));
			assertEquals(2, reasoner.getExtensionSize(Qx));
			assertEquals(2, reasoner.getExtensionSize(Qx));
			assertEquals(2, reasoner.getExtensionSize(Rxy));
		}
	}

	@Test
	public void qFactsUniversalRule() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(factQe, factQf, RxyQyPx);
		try (VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			assertEquals(0, reasoner.getExtensionSize(Px));
			assertEquals(2, reasoner.getExtensionSize(Qx));
			assertEquals(0, reasoner.getExtensionSize(Rxy));
		}
	}

	@Test
	public void qFactsExistentialRule() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(factQe, factQf, RxyQyPx);
		try (VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			assertEquals(0, reasoner.getExtensionSize(Px));
			assertEquals(2, reasoner.getExtensionSize(Qx));
			assertEquals(0, reasoner.getExtensionSize(Rxy));
		}
	}

	@Test
	public void pFactsQFactsUniversalRule() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(factPc, factPd, factQe, factQf, QxPx);
		try (VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			assertEquals(2, reasoner.getExtensionSize(Px));
			assertEquals(4, reasoner.getExtensionSize(Qx));
			assertEquals(0, reasoner.getExtensionSize(Rxy));
		}
	}

	@Test
	public void pFactsQFactsExistentialRule() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(factPc, factPd, factQe, factQf, RxyQyPx);
		try (VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			assertEquals(2, reasoner.getExtensionSize(Px));
			assertEquals(4, reasoner.getExtensionSize(Qx));
			assertEquals(2, reasoner.getExtensionSize(Rxy));

			assertEquals(2, reasoner.getExtensionSize(Rdy));
			assertEquals(2, reasoner.getExtensionSize(Rxe));
		}
	}

	@Test
	public void pFactsQFactsExistentialAndUniversalRule() throws IOException {
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(factPc, factPd, factQe, factQf, QxPx, RxyQyPx);
		try (VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			assertEquals(2, reasoner.getExtensionSize(Px));
			assertEquals(6, reasoner.getExtensionSize(Qx));
			assertEquals(2, reasoner.getExtensionSize(Rxy));

			assertEquals(2, reasoner.getExtensionSize(Rdy));
			assertEquals(2, reasoner.getExtensionSize(Rxd));
		}
	}
}
