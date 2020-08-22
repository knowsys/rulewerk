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

import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.core.model.api.LanguageStringConstant;
import org.semanticweb.rulewerk.core.model.api.TermVisitor;

/**
 * Simple implementation of {@link LanguageStringConstant}.
 * 
 * @author Markus Kroetzsch
 *
 */
public class LanguageStringConstantImpl implements LanguageStringConstant {

	final String string;
	final String lang;

	public LanguageStringConstantImpl(String string, String languageTag) {
		Validate.notNull(string);
		Validate.notBlank(languageTag, "Language tags cannot be blank strings.");
		this.string = string;
		this.lang = languageTag;
	}

	@Override
	public String getName() {
		return toString();
	}

	@Override
	public <T> T accept(TermVisitor<T> termVisitor) {
		return termVisitor.visit(this);
	}

	@Override
	public String getString() {
		return this.string;
	}

	@Override
	public String getLanguageTag() {
		return this.lang;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = lang.hashCode();
		result = prime * result + string.hashCode();
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
		LanguageStringConstantImpl other = (LanguageStringConstantImpl) obj;
		return this.string.equals(other.getString()) && this.lang.equals(other.getLanguageTag());
	}

	@Override
	public String toString() {
		return Serializer.getSerialization(serializer -> serializer.writeLanguageStringConstant(this));
	}

}
