package org.semanticweb.vlog4j.core.model;

import org.semanticweb.vlog4j.core.model.validation.BlankNameValidationException;

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

import org.semanticweb.vlog4j.core.model.validation.EntityNameValidator;

public class BlankImpl extends AbstractTerm implements Blank {

	public BlankImpl(final String name) throws BlankNameValidationException {
		super(name);
		EntityNameValidator.blankNameCheck(name);
	}

	public BlankImpl(final Blank copyBlank) throws BlankNameValidationException {
		super(new String(copyBlank.getName()));
	}

	@Override
	public TermType getType() {
		return TermType.BLANK;
	}
}
