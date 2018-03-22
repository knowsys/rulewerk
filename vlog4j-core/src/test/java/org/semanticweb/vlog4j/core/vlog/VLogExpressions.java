package org.semanticweb.vlog4j.core.vlog;

/*-
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

/**
 * Utility class with static methods for creating {@code karmaresearch.vlog}
 * objects.
 * 
 * @author Irina.Dragoste
 *
 */
public final class VLogExpressions {

	private VLogExpressions() {
	}

	public static karmaresearch.vlog.Term makeVariable(final String name) {
		final karmaresearch.vlog.Term variable = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE,
				name);
		return variable;
	}

	public static karmaresearch.vlog.Term makeConstant(final String name) {
		final karmaresearch.vlog.Term constant = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT,
				name);
		return constant;
	}

	public static karmaresearch.vlog.Rule makeRule(final karmaresearch.vlog.Atom headAtom,
			final karmaresearch.vlog.Atom... bodyAtoms) {
		return new karmaresearch.vlog.Rule(new karmaresearch.vlog.Atom[] { headAtom }, bodyAtoms);
	}

}
