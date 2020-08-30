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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.AbstractConstant;
import org.semanticweb.rulewerk.core.model.api.NamedNull;
import org.semanticweb.rulewerk.core.model.implementation.NamedNullImpl;
import org.semanticweb.rulewerk.core.model.implementation.TermFactory;

public class SkolemizationTest {
	private Skolemization skolemization;
	private final static String name1 = "_:1";
	private final static String name2 = "_:2";

	@Before
	public void init() {
		this.skolemization = new Skolemization();
	}

	@Test
	public void skolemizeNamedNull_sameName_mapsToSameNamedNull() {
		NamedNull null1 = skolemization.getRenamedNamedNull(name1);
		NamedNull null2 = skolemization.getRenamedNamedNull(name1);

		assertEquals(null1.getName(), null2.getName());
	}

	@Test
	public void skolemizeNamedNull_differentName_mapsToDifferentNamedNull() {
		NamedNull null1 = skolemization.getRenamedNamedNull(name1);
		NamedNull null2 = skolemization.getRenamedNamedNull(name2);

		assertNotEquals(null1.getName(), null2.getName());
	}

	@Test
	public void skolemizeNamedNull_differentInstances_mapsToDifferentNamedNull() {
		NamedNull null1 = skolemization.getRenamedNamedNull(name1);
		Skolemization other = new Skolemization();
		NamedNull null2 = other.getRenamedNamedNull(name1);

		assertNotEquals(null1.getName(), null2.getName());
	}

	@Test
	public void skolemizeNamedNull_differentInstancesDifferentNames_mapsToDifferentNamedNull() {
		NamedNull null1 = skolemization.getRenamedNamedNull(name1);
		Skolemization other = new Skolemization();
		NamedNull null2 = other.getRenamedNamedNull(name2);

		assertNotEquals(null1.getName(), null2.getName());
		assertEquals(null1.getName(), skolemization.getRenamedNamedNull(name1).getName());
		assertEquals(null2.getName(), other.getRenamedNamedNull(name2).getName());
	}

	@Test
	public void skolemConstant_succeeds() {
		TermFactory termFactory = new TermFactory();
		AbstractConstant skolem = skolemization.getSkolemConstant(name1, termFactory);
		assertTrue(skolem.getName().startsWith(Skolemization.SKOLEM_IRI_PREFIX));
	}

	@Test
	public void skolemConstantFromNamedNull_succeeds() {
		TermFactory termFactory = new TermFactory();
		NamedNull null1 = new NamedNullImpl(name1);
		AbstractConstant skolem1 = skolemization.getSkolemConstant(null1, termFactory);
		AbstractConstant skolem2 = skolemization.getSkolemConstant(name1, termFactory);
		assertEquals(skolem2, skolem1);
	}

	@Test
	public void skolemConstantFromRenamedNamedNull_succeeds() {
		TermFactory termFactory = new TermFactory();
		NamedNull null1 = skolemization.getRenamedNamedNull(name1);
		AbstractConstant skolem1 = skolemization.getSkolemConstant(null1, termFactory);
		AbstractConstant skolem2 = skolemization.getSkolemConstant(name1, termFactory);
		assertEquals(skolem2, skolem1);
	}
}
