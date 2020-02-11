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
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.vlog4j.core.exceptions.PrefixDeclarationException;
import org.semanticweb.vlog4j.core.model.implementation.MergeablePrefixDeclarations;

public class MergeablePrefixDeclarationsTest {
	private MergeablePrefixDeclarations prefixDeclarations;

	private static final String BASE = "https://example.org/";
	private static final String MORE_SPECIFIC = BASE + "example/";
	private static final String RELATIVE = "relative/test";


	@Before
	public void init() {
		prefixDeclarations = new MergeablePrefixDeclarations();
	}

	@Test
	public void setBase_changingBase_succeeds() {
		prefixDeclarations.setBase(BASE);
		assertEquals(prefixDeclarations.getBase(), BASE);
		prefixDeclarations.setBase(MORE_SPECIFIC);
		assertEquals(prefixDeclarations.getBase(), MORE_SPECIFIC);
	}

	@Test
	public void setBase_redeclareSameBase_succeeds() {
		prefixDeclarations.setBase(BASE);
		assertEquals(prefixDeclarations.getBase(), BASE);
		prefixDeclarations.setBase(BASE);
		assertEquals(prefixDeclarations.getBase(), BASE);
	}

	@Test
	public void absolutize_noBase_identical() {
		assertEquals(prefixDeclarations.absolutize(RELATIVE), RELATIVE);
	}

	@Test
	public void absolutize_base_absoluteIri() {
		prefixDeclarations.setBase(BASE);
		assertEquals(prefixDeclarations.absolutize(RELATIVE), BASE + RELATIVE);
	}

	@Test
	public void absolutize_absoluteIri_identical() {
		assertEquals(prefixDeclarations.absolutize(BASE), BASE);
	}

	@Test(expected = PrefixDeclarationException.class)
	public void resolvePrefixedName_undeclaredPrefix_throws() throws PrefixDeclarationException {
		prefixDeclarations.resolvePrefixedName("eg:" + RELATIVE);
	}

	@Test
	public void resolvePrefixedName_knownPrefix_succeeds() throws PrefixDeclarationException {
		prefixDeclarations.setPrefix("eg:", BASE);
		assertEquals(prefixDeclarations.resolvePrefixedName("eg:" + RELATIVE), BASE + RELATIVE);
	}

	@Test
	public void setPrefix_redeclarePrefix_succeeds() {
		prefixDeclarations.setPrefix("eg:", BASE);
		prefixDeclarations.setPrefix("eg:", MORE_SPECIFIC);
	}

	@Test
	public void getFreshPrefix_registeredPrefix_returnsFreshPrefix() throws PrefixDeclarationException {
		String prefix = "vlog4j_generated_";
		prefixDeclarations.setPrefix(prefix + "0:", BASE + "generated/");
		prefixDeclarations.setPrefix("eg:", BASE);
		prefixDeclarations.setPrefix("eg:", MORE_SPECIFIC);

		assertEquals(prefixDeclarations.getPrefix(prefix + "1:"), MORE_SPECIFIC);
	}


}
