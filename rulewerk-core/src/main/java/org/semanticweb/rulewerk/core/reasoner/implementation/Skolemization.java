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

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import org.semanticweb.rulewerk.core.model.api.AbstractConstant;
import org.semanticweb.rulewerk.core.model.api.NamedNull;
import org.semanticweb.rulewerk.core.model.implementation.RenamedNamedNull;
import org.semanticweb.rulewerk.core.model.implementation.TermFactory;

/**
 * A class that implements skolemization and collision-free renaming of named
 * nulls. The same name will always be renamed in the same way when using the
 * same instance of {@link Skolemization}, but it is extremely unlikely that
 * different names or different instances will ever produce the same name.
 * 
 * This can be used to rename apart named nulls from different input sources to
 * avoid clashes. There is also code for creating skolem constants with
 * appropriate absolute IRIs.
 *
 * @author Maximilian Marx
 */
public class Skolemization {

	/**
	 * IRI prefix used for IRIs skolem constants in Rulewerk.
	 */
	public final static String SKOLEM_IRI_PREFIX = "https://rulewerk.semantic-web.org/.well-known/genid/";
	/**
	 * Prefix used to ensure that UUID-based local names do not start with a number.
	 */
	private final static String SKOLEM_UUID_START = "B-";

	/**
	 * The namespace to use for skolemizing named null names.
	 */
	private final byte[] namedNullNamespace = UUID.randomUUID().toString().getBytes();

	/**
	 * Creates a named null with a renamed name that is determined by the given
	 * original name. The result is a {@link RenamedNamedNull} to allow other code
	 * to recognise that no further renaming is necessary.
	 *
	 * @param name the name of the {@link NamedNull} to be renamed here (or any
	 *             other string for which to create a unique renaming)
	 * @return a {@link RenamedNamedNull} with a new name that is specific to this
	 *         instance and {@code name}.
	 */
	public RenamedNamedNull getRenamedNamedNull(String name) {
		return new RenamedNamedNull(getFreshName(name));
	}

	/**
	 * Creates a skolem constant that is determined by the given original name.
	 * 
	 * @param name        the name of the {@link NamedNull} to skolemize (or any
	 *                    other string for which to create a unique renaming)
	 * @param termFactory the {@link TermFactory} that is used to create the
	 *                    constant
	 * @return a {@link AbstractConstant} with an IRI that is specific to this
	 *         instance and {@code name}.
	 */
	public AbstractConstant getSkolemConstant(String name, TermFactory termFactory) {
		return termFactory.makeAbstractConstant(getSkolemConstantName(name));
	}

	/**
	 * Creates a skolem constant that is determined by the given {@link NamedNull}.
	 * The method ensures that a new unique name is generated unless the given
	 * object is already a {@link RenamedNamedNull}.
	 * 
	 * @param namedNull   the {@link NamedNull} to skolemize
	 * @param termFactory the {@link TermFactory} that is used to create the
	 *                    constant
	 * @return a {@link AbstractConstant} with an IRI that is specific to this
	 *         instance and {@code namedNull}.
	 */
	public AbstractConstant getSkolemConstant(NamedNull namedNull, TermFactory termFactory) {
		return termFactory.makeAbstractConstant(getSkolemConstantName(namedNull));

	}

	/**
	 * Returns the name (IRI string) of a skolem constant for skolemising a named
	 * null of the given name.
	 * 
	 * @param name the name of the {@link NamedNull} to be renamed here (or any
	 *             other string for which to create a unique renaming)
	 * @return string that is an IRI for a skolem constant
	 */
	public String getSkolemConstantName(String name) {
		return getSkolemConstantNameFromUniqueName(getFreshName(name).toString());
	}

	/**
	 * Returns the name (IRI string) of a skolem constant for skolemising the given
	 * named {@link NamedNull}. The method ensures that a new unique name is
	 * generated unless the given object is already a {@link RenamedNamedNull}.
	 * 
	 * @param name the name of the {@link NamedNull} to be renamed here (or any
	 *             other string for which to create a unique renaming)
	 * @return string that is an IRI for a skolem constant
	 */
	public String getSkolemConstantName(NamedNull namedNull) {
		if (namedNull instanceof RenamedNamedNull) {
			return getSkolemConstantNameFromUniqueName(namedNull.getName());
		} else {
			return getSkolemConstantName(namedNull.getName());
		}
	}

	/**
	 * Returns a full skolem constant IRI string from its local id part.
	 * 
	 * @param name local id of skolem constant
	 * @return IRI string
	 */
	private String getSkolemConstantNameFromUniqueName(String name) {
		return SKOLEM_IRI_PREFIX + SKOLEM_UUID_START + name;
	}

	/**
	 * Creates a fresh UUID based on the given string. The UUID is determined by the
	 * string and the instance of {@link Skolemization}. Other strings or instances
	 * are extremely unlikely to produce the same string.
	 * 
	 * @param name the string to be renamed
	 * @return a UUID for the new name
	 */
	public UUID getFreshName(String name) {
		byte[] nameBytes = name.getBytes();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		stream.write(namedNullNamespace, 0, namedNullNamespace.length);
		stream.write(nameBytes, 0, nameBytes.length);
		return UUID.nameUUIDFromBytes(stream.toByteArray());
	}
}
