package org.semanticweb.vlog4j.core.model;

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

import static org.junit.Assert.*;

import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.TermType;
import org.semanticweb.vlog4j.core.model.implementation.AbstractConstantImpl;
import org.semanticweb.vlog4j.core.model.implementation.UniversalVariableImpl;

public class TermImplTest {

	@Test
	public void constantImplEqualityTest() {
		Term c = new AbstractConstantImpl("c");
		Term ctoo = new AbstractConstantImpl("c");
		Term a = new AbstractConstantImpl("a");
		Term v = new UniversalVariableImpl("c");

		assertEquals(c, c);
		assertEquals(ctoo, c);
		assertNotEquals(a, c);
		assertNotEquals(v, c);
		assertNotEquals(a.hashCode(), c.hashCode());
		assertNotEquals(v.hashCode(), c.hashCode());
		assertFalse(c.equals(null)); // written like this for recording coverage properly
		assertFalse(c.equals("c")); // written like this for recording coverage properly
	}

	@Test
	public void termGetterTest() {
		Term c = new AbstractConstantImpl("c");
		assertEquals("c", c.getName());
		assertEquals(TermType.CONSTANT, c.getType());

		Term v = new UniversalVariableImpl("v");
		assertEquals("v", v.getName());
		assertEquals(TermType.VARIABLE, v.getType());
	}
	
	@Test(expected = NullPointerException.class)
	public void constantNameNonNullTest() {
		new AbstractConstantImpl((String)null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constantNameNonEmptyTest() {
		new AbstractConstantImpl("");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void constantNameNonWhitespaceTest() {
		new AbstractConstantImpl(" ");
	}

	

}
