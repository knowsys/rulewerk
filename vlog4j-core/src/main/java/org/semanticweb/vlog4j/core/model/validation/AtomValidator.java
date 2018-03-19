package org.semanticweb.vlog4j.core.model.validation;

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

import java.util.List;

import org.semanticweb.vlog4j.core.model.api.Term;

public class AtomValidator {
	public static void validateArguments(final List<Term> arguments) throws AtomValidationException {
		if (arguments == null) {
			throw new AtomValidationException("Null Atom argument list");
		}
		if (arguments.isEmpty()) {
			throw new AtomValidationException("Empty Atom argument list");
		}
		for (final Term term : arguments) {
			if (term == null) {
				throw new AtomValidationException("Null argument in the Atom argument list");
			}
		}
	}

}
