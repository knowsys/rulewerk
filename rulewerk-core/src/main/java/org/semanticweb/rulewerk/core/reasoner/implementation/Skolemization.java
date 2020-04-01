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

import org.semanticweb.rulewerk.core.model.api.NamedNull;
import org.semanticweb.rulewerk.core.model.implementation.RenamedNamedNull;

/**
 * A class that implements skolemization of named null names. The same name
 * should be skolemized to the same {@link NamedNull} when skolemized using the
 * same instance, but to two different instances of {@link NamedNull} when
 * skolemized using different instances of {@link Skolemization}.
 *
 * @author Maximilian Marx
 */
public class Skolemization {
	/**
	 * The namespace to use for skolemizing named null names.
	 */
	private final byte[] namedNullNamespace = UUID.randomUUID().toString().getBytes();

	/**
	 * Skolemize a named null name. The same {@code name} will map to a
	 * {@link RenamedNamedNull} instance with the same name when called on the same
	 * instance.
	 *
	 * @return a {@link RenamedNamedNull} instance with a new name that is specific
	 *         to this instance and {@code name}.
	 */
	public RenamedNamedNull skolemizeNamedNull(String name) {
		byte[] nameBytes = name.getBytes();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		stream.write(namedNullNamespace, 0, namedNullNamespace.length);
		stream.write(nameBytes, 0, nameBytes.length);
		return new RenamedNamedNull(UUID.nameUUIDFromBytes(stream.toByteArray()));
	}
}
