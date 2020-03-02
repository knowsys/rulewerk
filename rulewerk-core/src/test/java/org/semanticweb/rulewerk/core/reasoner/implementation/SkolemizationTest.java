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

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.NamedNull;

public class SkolemizationTest {
	private Skolemization skolemization;
	private final static String name1 = "_:1";
	private final static String name2 = "_:2";

	@Before
	public void init() {
		this.skolemization = new Skolemization();
	}

	@Test
	public void skolemizeNamedNull_sameName_mapsToSameNamedNull() throws IOException {
		NamedNull null1 = skolemization.skolemizeNamedNull(name1);
		NamedNull null2 = skolemization.skolemizeNamedNull(name1);

		assertEquals(null1.getName(), null2.getName());
	}

	@Test
	public void skolemizeNamedNull_differentName_mapsToDifferentNamedNull() throws IOException {
		NamedNull null1 = skolemization.skolemizeNamedNull(name1);
		NamedNull null2 = skolemization.skolemizeNamedNull(name2);

		assertNotEquals(null1.getName(), null2.getName());
	}

	@Test
	public void skolemizeNamedNull_differentInstances_mapsToDifferentNamedNull() throws IOException {
		NamedNull null1 = skolemization.skolemizeNamedNull(name1);
		Skolemization other = new Skolemization();
		NamedNull null2 = other.skolemizeNamedNull(name1);

		assertNotEquals(null1.getName(), null2.getName());
	}

	@Test
	public void skolemizeNamedNull_differentInstancesDifferentNames_mapsToDifferentNamedNull() throws IOException {
		NamedNull null1 = skolemization.skolemizeNamedNull(name1);
		Skolemization other = new Skolemization();
		NamedNull null2 = other.skolemizeNamedNull(name2);

		assertNotEquals(null1.getName(), null2.getName());
		assertEquals(null1.getName(), skolemization.skolemizeNamedNull(name1).getName());
		assertEquals(null2.getName(), other.skolemizeNamedNull(name2).getName());
	}
}
