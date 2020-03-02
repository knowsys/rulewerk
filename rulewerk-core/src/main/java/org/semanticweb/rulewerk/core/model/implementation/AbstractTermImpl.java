package org.semanticweb.rulewerk.core.model.implementation;

import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.core.model.api.Term;

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
 * Abstract class implementing all methods used by all types of terms.
 *
 * @author david.carral@tu-dresden.de
 */
public abstract class AbstractTermImpl implements Term {

	private final String name;

	public AbstractTermImpl(final String name) {
		Validate.notBlank(name, "Terms cannot be named by blank strings");
		this.name = new String(name);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = this.name.hashCode();
		result = prime * result + this.getType().hashCode();
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
		if (!(obj instanceof Term)) {
			return false;
		}
		final Term other = (Term) obj;

		return (this.getType() == other.getType()) && this.name.equals(other.getName());
	}

}
