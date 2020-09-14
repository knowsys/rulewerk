package org.semanticweb.rulewerk.core.model.implementation;

import org.apache.commons.lang3.Validate;

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

import org.semanticweb.rulewerk.core.model.api.DatatypeConstant;
import org.semanticweb.rulewerk.core.model.api.TermVisitor;

/**
 * Simple implementation of {@link DatatypeConstant}.
 * 
 * @author Markus Kroetzsch
 *
 */
public class DatatypeConstantImpl implements DatatypeConstant {

	final String datatype;
	final String lexicalValue;

	public DatatypeConstantImpl(String lexicalValue, String datatype) {
		Validate.notNull(lexicalValue);
		Validate.notBlank(datatype, "Datatype IRIs cannot be blank strings.");
		this.lexicalValue = lexicalValue;
		this.datatype = datatype;
	}

	@Override
	public <T> T accept(TermVisitor<T> termVisitor) {
		return termVisitor.visit(this);
	}

	@Override
	public String getDatatype() {
		return this.datatype;
	}

	@Override
	public String getLexicalValue() {
		return this.lexicalValue;
	}

	@Override
	public String toString() {
		return Serializer.getSerialization(serializer -> serializer.writeDatatypeConstant(this));
	}

	@Override
	public String getName() {
		return toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = datatype.hashCode();
		result = prime * result + lexicalValue.hashCode();
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
		DatatypeConstantImpl other = (DatatypeConstantImpl) obj;

		return this.lexicalValue.equals(other.getLexicalValue()) && this.datatype.equals(other.getDatatype());
	}

}
