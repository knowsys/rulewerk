package org.semanticweb.vlog4j.core.reasoner;

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

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.impl.Expressions;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.impl.QueryResultIterator;
import org.semanticweb.vlog4j.core.reasoner.impl.Reasoner;

import karmaresearch.vlog.AlreadyStartedException;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.NotStartedException;

public class QueryDataFromMemoryTest {
	
	@Test
	public void queryEmptyKnowledgeBase() throws AlreadyStartedException, EDBConfigurationException, IOException,
			NotStartedException, EdbIdbSeparationException, ReasonerStateException {
		ReasonerInterface reasoner = new Reasoner();

		reasoner.load();

		Atom queryAtom = Expressions.makeAtom("P", Expressions.makeVariable("?x"));
		QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtom);
		Assert.assertFalse(queryResultIterator.hasNext());
		queryResultIterator.close();

		reasoner.reason();

		QueryResultIterator queryResultIteratorAfterReason = reasoner.answerQuery(queryAtom);
		Assert.assertFalse(queryResultIteratorAfterReason.hasNext());
		queryResultIteratorAfterReason.close();

		reasoner.dispose();
	}

	@Test
	public void queryEmptyFacts() throws AlreadyStartedException, EDBConfigurationException, IOException,
			NotStartedException, EdbIdbSeparationException, ReasonerStateException {
		Variable vx = Expressions.makeVariable("x");
		Rule rule = Expressions.makeRule(Expressions.makeAtom("q", vx), Expressions.makeAtom("p", vx));

		ReasonerInterface reasoner = new Reasoner();
		reasoner.addRules(rule);
		reasoner.load();

		Atom queryAtom = Expressions.makeAtom("P", Expressions.makeVariable("?x"));
		QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtom);
		Assert.assertFalse(queryResultIterator.hasNext());
		queryResultIterator.close();

		reasoner.reason();

		QueryResultIterator queryResultIteratorAfterReason = reasoner.answerQuery(queryAtom);
		Assert.assertFalse(queryResultIteratorAfterReason.hasNext());
		queryResultIteratorAfterReason.close();

		reasoner.dispose();
	}

}
