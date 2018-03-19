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

import org.semanticweb.vlog4j.core.model.api.Atom;

public class RuleValidator {

	public static void bodyCheck(final List<Atom> body) {
		// TODO Auto-generated method stub

	}

	public static void headCheck(final List<Atom> head) {
		// TODO Auto-generated method stub

	}

	public static void ruleCheck(final List<Atom> body, final List<Atom> head) throws RuleValidationException {
		if (body == null) {
			throw new RuleValidationException("Null rule body");
		}
		if (body.isEmpty()) {
			throw new RuleValidationException("Empty rule body");
		}
		for (final Atom bodyAtom : body) {
			if (bodyAtom == null) {
				throw new RuleValidationException("Rule body contains null atoms");
			}
		}

		if (head == null) {
			throw new RuleValidationException("Null rule head");
		}
		if (head.isEmpty()) {
			throw new RuleValidationException("Empty rule head");
		}
		for (final Atom headAtom : head) {
			if (headAtom == null) {
				throw new RuleValidationException("Rule head contains null atoms");
			}
		}
	}

}
