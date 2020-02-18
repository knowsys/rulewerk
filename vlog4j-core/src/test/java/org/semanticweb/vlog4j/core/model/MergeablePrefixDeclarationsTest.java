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

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.vlog4j.core.exceptions.PrefixDeclarationException;
import org.semanticweb.vlog4j.core.model.api.PrefixDeclarations;
import org.semanticweb.vlog4j.core.model.implementation.MergeablePrefixDeclarations;

public class MergeablePrefixDeclarationsTest {
	private MergeablePrefixDeclarations prefixDeclarations;

	private static final String BASE = "https://example.org/";
	private static final String UNRELATED = "https://example.com/";
	private static final String MORE_SPECIFIC = BASE + "example/";
	private static final String EVEN_MORE_SPECIFIC = MORE_SPECIFIC + "relative/";
	private static final String RELATIVE = "relative/test";

	@Before
	public void init() {
		prefixDeclarations = new MergeablePrefixDeclarations();
	}

	@Test
	public void setBase_changingBase_succeeds() {
		prefixDeclarations.setBase(BASE);
		assertEquals(BASE, prefixDeclarations.getBase());
		prefixDeclarations.setBase(MORE_SPECIFIC);
		assertEquals(MORE_SPECIFIC, prefixDeclarations.getBase());
	}

	@Test
	public void setBase_redeclareSameBase_succeeds() {
		prefixDeclarations.setBase(BASE);
		assertEquals(BASE, prefixDeclarations.getBase());
		prefixDeclarations.setBase(BASE);
		assertEquals(BASE, prefixDeclarations.getBase());
	}

	@Test
	public void absolutize_noBase_identical() {
		assertEquals(RELATIVE, prefixDeclarations.absolutize(RELATIVE));
	}

	@Test
	public void absolutize_base_absoluteIri() {
		prefixDeclarations.setBase(BASE);
		assertEquals(BASE + RELATIVE, prefixDeclarations.absolutize(RELATIVE));
	}

	@Test
	public void absolutize_absoluteIri_identical() {
		assertEquals(BASE, prefixDeclarations.absolutize(BASE));
	}

	@Test(expected = PrefixDeclarationException.class)
	public void resolvePrefixedName_undeclaredPrefix_throws() throws PrefixDeclarationException {
		prefixDeclarations.resolvePrefixedName("eg:" + RELATIVE);
	}

	@Test
	public void resolvePrefixedName_knownPrefix_succeeds() throws PrefixDeclarationException {
		prefixDeclarations.setPrefix("eg:", BASE);
		assertEquals(BASE + RELATIVE, prefixDeclarations.resolvePrefixedName("eg:" + RELATIVE));
	}

	@Test
	public void resolvePrefixedName_unresolveAbsoluteIri_doesRoundTrip() throws PrefixDeclarationException {
		String prefix = "eg:";
		prefixDeclarations.setPrefix(prefix, BASE);
		String resolved = BASE + RELATIVE;
		String unresolved = prefixDeclarations.unresolveAbsoluteIri(resolved);
		assertEquals(resolved, prefixDeclarations.resolvePrefixedName(unresolved));
	}

	@Test
	public void setPrefix_redeclarePrefix_succeeds() throws PrefixDeclarationException {
		prefixDeclarations.setPrefix("eg:", BASE);
		prefixDeclarations.setPrefix("eg:", MORE_SPECIFIC);
		assertEquals(BASE, prefixDeclarations.getPrefix("eg:"));
	}

	@Test
	public void getFreshPrefix_registeredPrefix_returnsFreshPrefix() throws PrefixDeclarationException {
		String prefix = "vlog4j_generated_";
		prefixDeclarations.setPrefix(prefix + "0:", BASE + "generated/");
		prefixDeclarations.setPrefix("eg:", BASE);
		prefixDeclarations.setPrefix("eg:", MORE_SPECIFIC);

		assertEquals(MORE_SPECIFIC, prefixDeclarations.getPrefix(prefix + "1:"));
	}

	@Test
	public void mergeablePrefixDeclarations_constructor_succeeds() throws PrefixDeclarationException {
		this.prefixDeclarations.setPrefix("eg:", MORE_SPECIFIC);
		MergeablePrefixDeclarations prefixDeclarations = new MergeablePrefixDeclarations(this.prefixDeclarations);
		assertEquals(MORE_SPECIFIC, prefixDeclarations.getPrefix("eg:"));
	}

	@Test(expected = RuntimeException.class)
	public void mergePrefixDeclarations_getPrefixUnexpectedlyThrows_throws() throws PrefixDeclarationException {
		PrefixDeclarations prefixDeclarations = mock(PrefixDeclarations.class);

		when(prefixDeclarations.iterator()).thenReturn(Arrays.asList("eg:", "ex:").iterator());
		when(prefixDeclarations.getPrefix(anyString())).thenThrow(PrefixDeclarationException.class);

		this.prefixDeclarations.mergePrefixDeclarations(prefixDeclarations);
	}

	@Test
	public void unresolveAbsoluteIri_default_identical() {
		assertEquals(BASE, prefixDeclarations.unresolveAbsoluteIri(BASE));
	}

	@Test
	public void unresolveAbsoluteIri_declaredPrefix_succeeds() {
		assertEquals(MORE_SPECIFIC, prefixDeclarations.unresolveAbsoluteIri(MORE_SPECIFIC));
		prefixDeclarations.setPrefix("eg:", BASE);
		assertEquals("eg:example/", prefixDeclarations.unresolveAbsoluteIri(MORE_SPECIFIC));
	}

	@Test
	public void unresolveAbsoluteIri_unrelatedPrefix_identical() {
		prefixDeclarations.setPrefix("eg:", UNRELATED);
		assertEquals(MORE_SPECIFIC, prefixDeclarations.unresolveAbsoluteIri(MORE_SPECIFIC));
	}

	@Test
	public void unresolveAbsoluteIri_unrelatedAndRelatedPrefixes_succeeds() {
		prefixDeclarations.setPrefix("ex:", UNRELATED);
		prefixDeclarations.setPrefix("eg:", BASE);
		assertEquals("eg:example/", prefixDeclarations.unresolveAbsoluteIri(MORE_SPECIFIC));
	}

	@Test
	public void unresolveAbsoluteIri_multipleMatchingPrefixes_longestMatchWins() {
		prefixDeclarations.setPrefix("eg:", BASE);
		prefixDeclarations.setPrefix("ex:", MORE_SPECIFIC);
		assertEquals("ex:" + RELATIVE, prefixDeclarations.unresolveAbsoluteIri(MORE_SPECIFIC + RELATIVE));
		prefixDeclarations.setPrefix("er:", EVEN_MORE_SPECIFIC);
		assertEquals("er:test", prefixDeclarations.unresolveAbsoluteIri(MORE_SPECIFIC + RELATIVE));
	}

	@Test
	public void unresolveAbsoluteIri_exactPrefixMatch_identical() {
		prefixDeclarations.setPrefix("eg:", BASE);
		assertEquals(BASE, prefixDeclarations.unresolveAbsoluteIri(BASE));
	}

	@Test
	public void unresolveAbsoluteIri_baseIsMoreSpecific_baseWins() {
		prefixDeclarations.setBase(MORE_SPECIFIC);
		prefixDeclarations.setPrefix("eg:", BASE);
		assertEquals(RELATIVE, prefixDeclarations.unresolveAbsoluteIri(MORE_SPECIFIC + RELATIVE));
	}

	@Test
	public void unresolveAbsoluteIri_resolvePrefixedName_doesRoundTrip() throws PrefixDeclarationException {
		String prefix = "eg:";
		prefixDeclarations.setPrefix(prefix, BASE);
		String unresolved = prefix + RELATIVE;
		String resolved = prefixDeclarations.resolvePrefixedName(unresolved);
		assertEquals(unresolved, prefixDeclarations.unresolveAbsoluteIri(resolved));
	}
}
