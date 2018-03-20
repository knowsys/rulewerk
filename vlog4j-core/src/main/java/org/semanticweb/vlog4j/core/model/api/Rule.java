package org.semanticweb.vlog4j.core.model.api;

import java.util.List;
import java.util.Set;

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

//TODO rule definition
//TODO specify body and head are non-empty
public interface Rule {

	/**
	 * The Atoms representing the Rule body conjuncts as an unmodifiableList. An {@link UnsupportedOperationException} is thrown, when an attempt to modify the
	 * list occurs.
	 *
	 * @return an unmodifiableList representing the Rule body conjuncts.
	 */
	public List<Atom> getBody();

	/**
	 * The Atoms representing the Rule head conjuncts as an unmodifiableList. An {@link UnsupportedOperationException} is thrown, when an attempt to modify the
	 * list occurs.
	 *
	 * @return an unmodifiableList representing the Rule head conjuncts.
	 */
	public List<Atom> getHead();

	/**
	 * The existentially quantified variables, which are variables that only occur in the Rule head, and not in the Rule Body. They are returned as an
	 * unmodifiableList. An {@link UnsupportedOperationException} is thrown, when an attempt to modify the list occurs.
	 *
	 * @return an unmodifiableList representing the Rule existentially quantified variables.
	 */
	public Set<Variable> getExistentiallyQuantifiedVariables();

	/**
	 * The universally quantified variables, which are variables that occur in the rule and are not existentially quantified. They are returned as an
	 * unmodifiableList. An {@link UnsupportedOperationException} is thrown, when an attempt to modify the list occurs.
	 *
	 * @return an unmodifiableList representing the Rule universally quantified variables.
	 */
	public Set<Variable> getUniversallyQuantifiedVariables();

	/**
	 * All Variables occurring in the Rule body, returned as an unmodifiableList. An {@link UnsupportedOperationException} is thrown, when an attempt to modify
	 * the list occurs.
	 *
	 * @return an unmodifiableList representing all Variables in the Rule body.
	 */
	public Set<Variable> getBodyVariables();

	/**
	 * All Variables occurring in the Rule head, returned as an unmodifiableList. An {@link UnsupportedOperationException} is thrown, when an attempt to modify
	 * the list occurs.
	 *
	 * @return an unmodifiableList representing all Variables in the Rule head.
	 */
	public Set<Variable> getHeadVariables();

	/**
	 * All Variables occurring in the Rule head and body atoms, returned as an unmodifiableList. An {@link UnsupportedOperationException} is thrown, when an
	 * attempt to modify the list occurs.
	 *
	 * @return an unmodifiableList representing all Variables in the Rule.
	 */
	public Set<Variable> getVariables();

	/**
	 * All Constants occurring in the Rule body, returned as an unmodifiableList. An {@link UnsupportedOperationException} is thrown, when an attempt to modify
	 * the list occurs.
	 *
	 * @return an unmodifiableList representing all Constants in the Rule body.
	 */
	public Set<Constant> getBodyConstants();

	/**
	 * All Constants occurring in the Rule head, returned as an unmodifiableList. An {@link UnsupportedOperationException} is thrown, when an attempt to modify
	 * the list occurs.
	 *
	 * @return an unmodifiableList representing all Constants in the Rule head.
	 */
	public Set<Constant> getHeadConstants();

	/**
	 * All Constant occurring in the Rule head and body atoms, returned as an unmodifiableList. An {@link UnsupportedOperationException} is thrown, when an
	 * attempt to modify the list occurs.
	 *
	 * @return an unmodifiableList representing all Constant in the Rule.
	 */
	public Set<Constant> getConstants();

	/**
	 * All Terms (Variables and Constants) occurring in the Rule head and body atoms, returned as an unmodifiableList. An {@link UnsupportedOperationException}
	 * is thrown, when an attempt to modify the list occurs.
	 *
	 * @return an unmodifiableList representing all Terms in the Rule.
	 */
	public Set<Term> getTerms();

	public Set<Term> getBodyTerms();

	public Set<Term> getHeadTerms();

}
