package org.semanticweb.vlog4j.core.model;

import org.apache.commons.lang3.StringUtils;
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

public abstract class AbstractTerm implements Term {

	private final String name;

	public AbstractTerm(final String name) throws VLog4jTermValidationException {
		if (StringUtils.isBlank(name)) {
			// TODO use string formatter
			throw new VLog4jTermValidationException("Invalid blank Term name: " + name);
		}
		// TODO: other name validations
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public abstract TermType getType();

}
