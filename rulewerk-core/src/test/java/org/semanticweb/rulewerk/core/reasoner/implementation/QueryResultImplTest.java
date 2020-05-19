package org.semanticweb.rulewerk.core.reasoner.implementation;

/*-
 * #%L
 * Rulewerk Core Components
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.QueryResult;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class QueryResultImplTest {

	@Test
	public void testEquals() {
		final Constant c1 = Expressions.makeAbstractConstant("C");
		final Constant c2 = Expressions.makeAbstractConstant("ddd");
		final List<Term> constantList = Arrays.asList(c1, c1, c2);

		final QueryResult queryResult1 = new QueryResultImpl(constantList);
		final QueryResult queryResult2 = new QueryResultImpl(Arrays.asList(c1, c1, c2));
		final QueryResult queryResult3 = new QueryResultImpl(Arrays.asList(c1, c2, c1));

		assertEquals(queryResult1, queryResult1);
		assertEquals(queryResult2, queryResult1);
		assertEquals(queryResult2.hashCode(), queryResult1.hashCode());
		assertNotEquals(queryResult3, queryResult1);
		assertNotEquals(queryResult3.hashCode(), queryResult1.hashCode());
		assertNotEquals(new QueryResultImpl(null), queryResult1);
		assertEquals(new QueryResultImpl(null), new QueryResultImpl(null));
		assertFalse(queryResult1.equals(null));
		assertFalse(queryResult1.equals(constantList));
	}

}
