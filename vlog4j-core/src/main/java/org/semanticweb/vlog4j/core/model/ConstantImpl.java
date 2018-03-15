package org.semanticweb.vlog4j.core.model;

import org.semanticweb.vlog4j.core.model.validation.VLog4jTermValidationException;

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

public class ConstantImpl extends AbstractTerm implements Constant {

	public ConstantImpl(final String name) throws VLog4jTermValidationException {
		super(name);
	}

	@Override
	public TermType getType() {
		return TermType.CONSTANT;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (super.getName() == null ? 0 : super.getName().hashCode());
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
		final ConstantImpl other = (ConstantImpl) obj;
		if (super.getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!super.getName().equals(other.getName())) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Constant [name=" + super.getName() + "]";
	}

}
