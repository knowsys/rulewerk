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
import static org.junit.Assert.assertNotEquals;

import java.util.stream.StreamSupport;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.rulewerk.core.exceptions.PrefixDeclarationException;
import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.implementation.MergingPrefixDeclarationRegistry;

public class MergingPrefixDeclarationRegistryTest {
	private MergingPrefixDeclarationRegistry prefixDeclarations;

	private static final String BASE = "https://example.org/";
	private static final String UNRELATED = "https://example.com/";
	private static final String MORE_SPECIFIC = BASE + "example/";
	private static final String EVEN_MORE_SPECIFIC = MORE_SPECIFIC + "relative/";
	private static final String RELATIVE = "relative/test";

	@Before
	public void init() {
		prefixDeclarations = new MergingPrefixDeclarationRegistry();
	}

	@Test
	public void setBaseIri_changingBase_succeeds() {
		prefixDeclarations.setBaseIri(BASE);
		assertEquals(BASE, prefixDeclarations.getBaseIri());
		prefixDeclarations.setBaseIri(MORE_SPECIFIC);
		assertEquals(MORE_SPECIFIC, prefixDeclarations.getBaseIri());
	}

	@Test
	public void setBaseIri_redeclareSameBase_succeeds() {
		prefixDeclarations.setBaseIri(BASE);
		assertEquals(BASE, prefixDeclarations.getBaseIri());
		prefixDeclarations.setBaseIri(BASE);
		assertEquals(BASE, prefixDeclarations.getBaseIri());
	}

	@Test
	public void absolutizeIri_noBase_identical() throws PrefixDeclarationException {
		assertEquals(RELATIVE, prefixDeclarations.absolutizeIri(RELATIVE));
	}

	@Test
	public void absolutizeIri_base_absoluteIri() throws PrefixDeclarationException {
		prefixDeclarations.setBaseIri(BASE);
		assertEquals(BASE + RELATIVE, prefixDeclarations.absolutizeIri(RELATIVE));
	}

	@Test
	public void absolutizeIri_absoluteIri_identical() throws PrefixDeclarationException {
		assertEquals(BASE, prefixDeclarations.absolutizeIri(BASE));
	}

	@Test(expected = PrefixDeclarationException.class)
	public void resolvePrefixedName_undeclaredPrefix_throws() throws PrefixDeclarationException {
		prefixDeclarations.resolvePrefixedName("eg:" + RELATIVE);
	}

	@Test
	public void resolvePrefixedName_knownPrefix_succeeds() throws PrefixDeclarationException {
		prefixDeclarations.setPrefixIri("eg:", BASE);
		assertEquals(BASE + RELATIVE, prefixDeclarations.resolvePrefixedName("eg:" + RELATIVE));
	}

	@Test
	public void resolvePrefixedName_unresolveAbsoluteIri_doesRoundTrip() throws PrefixDeclarationException {
		String prefix = "eg:";
		prefixDeclarations.setPrefixIri(prefix, BASE);
		String resolved = BASE + RELATIVE;
		String unresolved = prefixDeclarations.unresolveAbsoluteIri(resolved, false);
		assertEquals(resolved, prefixDeclarations.resolvePrefixedName(unresolved));
	}

	@Test
	public void setPrefixIri_redeclarePrefix_succeeds() throws PrefixDeclarationException {
		prefixDeclarations.setPrefixIri("eg:", BASE);
		prefixDeclarations.setPrefixIri("eg:", MORE_SPECIFIC);
		assertEquals(BASE, prefixDeclarations.getPrefixIri("eg:"));
		assertEquals(2, StreamSupport.stream(prefixDeclarations.spliterator(), false).count());
	}
	
	@Test
	public void clearPrefix_succeeds() throws PrefixDeclarationException {
		prefixDeclarations.setPrefixIri("eg:", BASE);
		prefixDeclarations.setPrefixIri("another:", MORE_SPECIFIC);
		prefixDeclarations.clear();
		assertEquals(0, StreamSupport.stream(prefixDeclarations.spliterator(), false).count());
	}
	
	@Test
	public void setPrefixIri_setSamePrefix_succeeds() throws PrefixDeclarationException {
		prefixDeclarations.setPrefixIri("eg:", BASE);
		prefixDeclarations.setPrefixIri("eg:", BASE);
		assertEquals(BASE, prefixDeclarations.getPrefixIri("eg:"));
		assertEquals(1, StreamSupport.stream(prefixDeclarations.spliterator(), false).count());
	}

	@Test
	public void getFreshPrefix_registeredPrefix_returnsFreshPrefix() throws PrefixDeclarationException {
		String prefix = "rw_gen";
		prefixDeclarations.setPrefixIri(prefix + "0:", BASE + "generated/");
		prefixDeclarations.setPrefixIri("eg:", BASE);
		prefixDeclarations.setPrefixIri("eg:", MORE_SPECIFIC);

		assertEquals(MORE_SPECIFIC, prefixDeclarations.getPrefixIri(prefix + "1:"));
	}

	@Test
	public void mergingPrefixDeclarationRegistry_constructor_succeeds() throws PrefixDeclarationException {
		this.prefixDeclarations.setPrefixIri("eg:", MORE_SPECIFIC);
		MergingPrefixDeclarationRegistry prefixDeclarations = new MergingPrefixDeclarationRegistry(
				this.prefixDeclarations);
		assertEquals(MORE_SPECIFIC, prefixDeclarations.getPrefixIri("eg:"));
	}

	@Test
	public void mergePrefixDeclarations_conflictingPrefixName_renamesConflictingPrefixName()
			throws PrefixDeclarationException {
		this.prefixDeclarations.setPrefixIri("eg:", BASE);
		PrefixDeclarationRegistry prefixDeclarations = new MergingPrefixDeclarationRegistry();
		prefixDeclarations.setPrefixIri("eg:", MORE_SPECIFIC);
		this.prefixDeclarations.mergePrefixDeclarations(prefixDeclarations);
		assertEquals(BASE, this.prefixDeclarations.getPrefixIri("eg:"));
		assertEquals(MORE_SPECIFIC, this.prefixDeclarations.getPrefixIri("rw_gen0:"));
	}

	@Test
	public void unresolveAbsoluteIri_default_identical() {
		assertEquals(BASE, prefixDeclarations.unresolveAbsoluteIri(BASE, false));
	}

	@Test
	public void unresolveAbsoluteIri_declaredPrefix_succeeds() {
		assertEquals(MORE_SPECIFIC, prefixDeclarations.unresolveAbsoluteIri(MORE_SPECIFIC, false));
		prefixDeclarations.setPrefixIri("eg:", BASE);
		assertEquals("eg:example/", prefixDeclarations.unresolveAbsoluteIri(MORE_SPECIFIC, false));
	}

	@Test
	public void unresolveAbsoluteIri_unrelatedPrefix_identical() {
		prefixDeclarations.setPrefixIri("eg:", UNRELATED);
		assertEquals(MORE_SPECIFIC, prefixDeclarations.unresolveAbsoluteIri(MORE_SPECIFIC, false));
	}

	@Test
	public void unresolveAbsoluteIri_unrelatedAndRelatedPrefixes_succeeds() {
		prefixDeclarations.setPrefixIri("ex:", UNRELATED);
		prefixDeclarations.setPrefixIri("eg:", BASE);
		assertEquals("eg:example/", prefixDeclarations.unresolveAbsoluteIri(MORE_SPECIFIC, false));
	}

	@Test
	public void unresolveAbsoluteIri_multipleMatchingPrefixes_longestMatchWins() {
		prefixDeclarations.setPrefixIri("eg:", BASE);
		prefixDeclarations.setPrefixIri("ex:", MORE_SPECIFIC);
		assertEquals("ex:" + RELATIVE, prefixDeclarations.unresolveAbsoluteIri(MORE_SPECIFIC + RELATIVE, false));
		prefixDeclarations.setPrefixIri("er:", EVEN_MORE_SPECIFIC);
		assertEquals("er:test", prefixDeclarations.unresolveAbsoluteIri(MORE_SPECIFIC + RELATIVE, false));
	}

	@Test
	public void unresolveAbsoluteIri_exactPrefixMatch_identical() {
		prefixDeclarations.setPrefixIri("eg:", BASE);
		assertEquals(BASE, prefixDeclarations.unresolveAbsoluteIri(BASE, false));
	}

	@Test
	public void unresolveAbsoluteIri_baseIsMoreSpecific_baseWins() {
		prefixDeclarations.setBaseIri(MORE_SPECIFIC);
		prefixDeclarations.setPrefixIri("eg:", BASE);
		assertEquals(RELATIVE, prefixDeclarations.unresolveAbsoluteIri(MORE_SPECIFIC + RELATIVE, false));
	}

	@Test
	public void unresolveAbsoluteIri_resolvePrefixedName_doesRoundTrip() throws PrefixDeclarationException {
		String prefix = "eg:";
		prefixDeclarations.setPrefixIri(prefix, BASE);
		String unresolved = prefix + RELATIVE;
		String resolved = prefixDeclarations.resolvePrefixedName(unresolved);
		assertEquals(unresolved, prefixDeclarations.unresolveAbsoluteIri(resolved, false));
	}

	@Test
	public void unresolveAbsoluteIri_relativeIriAfterMergeWithNewBase_staysRelative()
			throws PrefixDeclarationException {
		String relativeIri = this.prefixDeclarations.absolutizeIri(RELATIVE);
		PrefixDeclarationRegistry prefixDeclarations = new MergingPrefixDeclarationRegistry();
		prefixDeclarations.setBaseIri(BASE);
		this.prefixDeclarations.mergePrefixDeclarations(prefixDeclarations);
		assertEquals(relativeIri, this.prefixDeclarations.unresolveAbsoluteIri(relativeIri, false));
	}

	@Test
	public void unresolveAbsoluteIri_absoluteIriMergedOntoEmptyBase_staysAbsolute() throws PrefixDeclarationException {
		assertEquals("", this.prefixDeclarations.getBaseIri()); // FIXME: why test this?

		PrefixDeclarationRegistry prefixDeclarations = new MergingPrefixDeclarationRegistry();
		prefixDeclarations.setBaseIri(BASE);
		String absoluteIri = prefixDeclarations.absolutizeIri(RELATIVE);
		this.prefixDeclarations.mergePrefixDeclarations(prefixDeclarations);
		String resolvedIri = this.prefixDeclarations.unresolveAbsoluteIri(absoluteIri, false);

		assertNotEquals(RELATIVE, resolvedIri);
		assertEquals("rw_gen0:" + RELATIVE, resolvedIri);
	}
}
