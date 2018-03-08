package org.semanticweb.vlog4j.core.model;

import org.semanticweb.vlog4j.core.validation.VLog4jTermValidationException;

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

public class VariableImpl  extends AbstractTerm implements Variable {

	public VariableImpl(String name) throws VLog4jTermValidationException {
		super(name);
	}

	@Override
	public TermType getType() {
		return TermType.VARIABLE;
	}

	@Override
	public boolean isVariable() {
		return true;
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((super.getName() == null) ? 0 : super.getName().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VariableImpl other = (VariableImpl) obj;
		if (super.getName() == null) {
			if (other.getType() != null)
				return false;
		} else if (!super.getName().equals(other.getName()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Variable [name=" + super.getName() + "]";
	}

}
