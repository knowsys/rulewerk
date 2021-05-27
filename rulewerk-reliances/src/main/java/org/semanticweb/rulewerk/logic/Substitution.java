package org.semanticweb.rulewerk.logic;

/*-
 * #%L
 * Rulewerk Reliances
 * %%
 * Copyright (C) 2018 - 2021 Rulewerk Developers
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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.Variable;

public class Substitution {
	final private Map<Variable, Term> substitution;

	public Substitution() {
		substitution = new HashMap<>();
	}

	public void add(Variable key, Term value) {
		if (key.equals(value)) {
			throw new IllegalArgumentException("Pairs key-value should be different in Substutions");
		}
		substitution.put(key, value);
	}

	public boolean contains(Term key) {
		return substitution.containsKey(key);
	}

	public Term getValue(Term key) {
		return substitution.containsKey(key) ? getValue(substitution.get(key)) : key;
	}

	@Override
	public String toString() {
		return substitution.keySet().stream().map(k -> k + ":" + substitution.get(k)).collect(Collectors.joining(","));
	}

}
