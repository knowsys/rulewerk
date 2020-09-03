package org.semanticweb.rulewerk.core.model.implementation;

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

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.DatatypeConstant;
import org.semanticweb.rulewerk.core.model.api.LanguageStringConstant;
import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.TermType;
import org.semanticweb.rulewerk.core.model.implementation.AbstractConstantImpl;
import org.semanticweb.rulewerk.core.model.implementation.DatatypeConstantImpl;
import org.semanticweb.rulewerk.core.model.implementation.ExistentialVariableImpl;
import org.semanticweb.rulewerk.core.model.implementation.LanguageStringConstantImpl;
import org.semanticweb.rulewerk.core.model.implementation.NamedNullImpl;
import org.semanticweb.rulewerk.core.model.implementation.UniversalVariableImpl;

public class TermImplTest {

	@Test
	public void abstractConstantImplEqualityTest() {
		Term c = new AbstractConstantImpl("c");
		Term ctoo = new AbstractConstantImpl("c");
		Term a = new AbstractConstantImpl("a");
		Term v = new UniversalVariableImpl("c");

		assertEquals(c, c);
		assertEquals(ctoo, c);
		assertNotEquals(a, c);
		assertNotEquals(v, c);
		assertEquals(c.hashCode(), ctoo.hashCode());
		assertFalse(c.equals(null)); // written like this for recording coverage properly
	}

	@Test
	public void datatypeConstantImplEqualityTest() {
		Term c = new DatatypeConstantImpl("c", "http://example.org/mystring");
		Term ctoo = new DatatypeConstantImpl("c", "http://example.org/mystring");
		Term a = new DatatypeConstantImpl("a", "http://example.org/mystring");
		Term b = new DatatypeConstantImpl("c", "http://example.org/mystring2");
		Term v = new UniversalVariableImpl("c");

		assertEquals(c, c);
		assertEquals(ctoo, c);
		assertNotEquals(a, c);
		assertNotEquals(b, c);
		assertNotEquals(v, c);
		assertEquals(c.hashCode(), ctoo.hashCode());
		assertFalse(c.equals(null)); // written like this for recording coverage properly
	}

	@Test
	public void languageStringConstantImplEqualityTest() {
		Term c = new LanguageStringConstantImpl("Test", "en");
		Term ctoo = new LanguageStringConstantImpl("Test", "en");
		Term a = new LanguageStringConstantImpl("Test2", "en");
		Term b = new LanguageStringConstantImpl("Test", "de");
		Term v = new UniversalVariableImpl("c");

		assertEquals(c, c);
		assertEquals(ctoo, c);
		assertNotEquals(a, c);
		assertNotEquals(b, c);
		assertNotEquals(v, c);
		assertEquals(c.hashCode(), ctoo.hashCode());
		assertFalse(c.equals(null)); // written like this for recording coverage properly
		assertFalse(c.equals("c")); // written like this for recording coverage properly
	}

	@Test
	public void abstractConstantGetterTest() {
		Term c = new AbstractConstantImpl("c");
		assertEquals("c", c.getName());
		assertEquals(TermType.ABSTRACT_CONSTANT, c.getType());
	}

	@Test
	public void datatypeConstantGetterTest() {
		DatatypeConstant c = new DatatypeConstantImpl("c", "http://example.org/type");
		assertEquals("c", c.getLexicalValue());
		assertEquals("http://example.org/type", c.getDatatype());
		assertEquals("\"c\"^^<http://example.org/type>", c.getName());
		assertEquals(TermType.DATATYPE_CONSTANT, c.getType());
	}

	@Test
	public void languageStringConstantGetterTest() {
		LanguageStringConstant c = new LanguageStringConstantImpl("Test", "en");
		assertEquals("Test", c.getString());
		assertEquals("en", c.getLanguageTag());
		assertEquals("\"Test\"@en", c.getName());
		assertEquals(TermType.LANGSTRING_CONSTANT, c.getType());
	}

	@Test
	public void universalVariableGetterTest() {
		Term v = new UniversalVariableImpl("v");
		assertEquals("v", v.getName());
		assertEquals(TermType.UNIVERSAL_VARIABLE, v.getType());
	}

	@Test
	public void existentialVariableGetterTest() {
		Term v = new ExistentialVariableImpl("v");
		assertEquals("v", v.getName());
		assertEquals(TermType.EXISTENTIAL_VARIABLE, v.getType());
	}

	@Test
	public void namedNullGetterTest() {
		Term n = new NamedNullImpl("123");
		assertEquals("123", n.getName());
		assertEquals(TermType.NAMED_NULL, n.getType());
	}

	@Test
	public void abstractConstantToStringTest() {
		AbstractConstantImpl c = new AbstractConstantImpl("c");
		assertEquals("c", c.toString());
	}

	@Test
	public void datatypeConstantToStringTest() {
		DatatypeConstantImpl c = new DatatypeConstantImpl("c", PrefixDeclarationRegistry.XSD_STRING);
		assertEquals("\"c\"", c.toString());
	}

	@Test
	public void languageStringConstantToStringTest() {
		LanguageStringConstantImpl c = new LanguageStringConstantImpl("Test", "en");
		assertEquals("\"Test\"@en", c.toString());
	}

	@Test
	public void universalVariableToStringTest() {
		UniversalVariableImpl v = new UniversalVariableImpl("v");
		assertEquals("?v", v.toString());
	}

	@Test
	public void existentialVariableToStringTest() {
		ExistentialVariableImpl v = new ExistentialVariableImpl("v");
		assertEquals("!v", v.toString());
	}

	@Test
	public void namedNullToStringTest() {
		NamedNullImpl n = new NamedNullImpl("123");
		assertEquals("_:123", n.toString());
	}

	@Test(expected = NullPointerException.class)
	public void constantNameNonNullTest() {
		new AbstractConstantImpl((String) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constantNameNonEmptyTest() {
		new AbstractConstantImpl("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void constantNameNonWhitespaceTest() {
		new AbstractConstantImpl(" ");
	}

	@Test(expected = IllegalArgumentException.class)
	public void languageTagNonEmptyTest() {
		new LanguageStringConstantImpl("test", "");
	}

	@Test(expected = NullPointerException.class)
	public void languageStringNameNonNull() {
		new LanguageStringConstantImpl(null, "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void datatypeNonEmptyTest() {
		new DatatypeConstantImpl("test", "");
	}

	@Test(expected = NullPointerException.class)
	public void lexicalValueNonNull() {
		new DatatypeConstantImpl(null, "");
	}

}
