package org.semanticweb.rulewerk.core.model.implementation;

import java.util.UUID;

/*
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

import org.semanticweb.rulewerk.core.model.api.NamedNull;
import org.semanticweb.rulewerk.core.model.implementation.NamedNullImpl;

/**
 * A {@link NamedNull} term that has been renamed during parsing.
 *
 * @author Maximilian Marx
 */
public class RenamedNamedNull extends NamedNullImpl {
	/**
	 * Construct a new renamed named null, with the given UUID as a name.
	 *
	 * @param name the name of the named null.
	 */
	public RenamedNamedNull(UUID name) {
		super(name.toString());
	}
}
