package org.semanticweb.vlog4j.core.model.impl;

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

import java.util.Arrays;
import java.util.List;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.Variable;

/**
 * This class provides static methods for creating terms and formulas in vlog4j.
 * 
 * @author Markus Krötzsch
 *
 */
public class Expressions {

	/**
	 * Creates a {@link Variable}.
	 * 
	 * @param name
	 *            name of the variable
	 * @return a {@link Variable} corresponding to the input
	 */
	public static Variable makeVariable(String name) {
		return new VariableImpl(name);
	}

	/**
	 * Creates a {@link Constant}.
	 * 
	 * @param name
	 *            name of the constant
	 * @return a {@link Constant} corresponding to the input
	 */
	public static Constant makeConstant(String name) {
		return new ConstantImpl(name);
	}

	/**
	 * Creates an {@code Atom}.
	 *
	 * @param predicateName
	 *            non-blank predicate name
	 * @param arguments
	 *            non-empty list of non-null terms
	 * @return an {@link Atom} corresponding to the input
	 */
	public static Atom makeAtom(final String predicateName, final List<Term> arguments) {
		return new AtomImpl(predicateName, arguments);
	}

	/**
	 * Creates an {@code Atom}.
	 *
	 * @param predicateName
	 *            non-blank predicate name
	 * @param arguments
	 *            non-empty array of non-null terms
	 * @return an {@link Atom} corresponding to the input
	 */
	public static Atom makeAtom(final String predicateName, final Term... arguments) {
		return new AtomImpl(predicateName, Arrays.asList(arguments));
	}

}
