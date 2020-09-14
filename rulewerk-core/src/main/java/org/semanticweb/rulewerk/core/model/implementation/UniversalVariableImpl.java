package org.semanticweb.rulewerk.core.model.implementation;

import org.semanticweb.rulewerk.core.model.api.TermVisitor;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;

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

/**
 * Simple implementation of {@link UniversalVariable}.
 *
 * @author david.carral@tu-dresden.de
 */
public class UniversalVariableImpl extends AbstractTermImpl implements UniversalVariable {

	/**
	 * Constructor.
	 *
	 * @param name cannot be a blank String (null, empty or whitespace).
	 */
	public UniversalVariableImpl(final String name) {
		super(name);
	}

	@Override
	public <T> T accept(TermVisitor<T> termVisitor) {
		return termVisitor.visit(this);
	}

	@Override
	public String toString() {
		return Serializer.getSerialization(serializer -> serializer.writeUniversalVariable(this));
	}
}
