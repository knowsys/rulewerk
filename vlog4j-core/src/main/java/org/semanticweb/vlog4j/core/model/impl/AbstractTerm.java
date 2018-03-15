package org.semanticweb.vlog4j.core.model.impl;

import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.TermType;
import org.semanticweb.vlog4j.core.model.validation.EntityNameValidator;
import org.semanticweb.vlog4j.core.model.validation.IllegalEntityNameException;

/*
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

/**
 * Abstract class implementing all methods used by all types of terms ({@link TermType##CONSTANT}, {@link TermType##BLANK}, and {@link TermType##VARIABLE}).
 *
 * @author david.carral@tu-dresden.de
 */
public abstract class AbstractTerm implements Term {

	private final String name;

	public AbstractTerm(final String name) throws IllegalEntityNameException {
		EntityNameValidator.validateNonEmptyString(name);
		this.name = new String(name);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public abstract TermType getType();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.name == null ? 0 : this.name.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AbstractTerm other = (AbstractTerm) obj;

		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getType() + " [name=" + this.name + "]";
	}

}
