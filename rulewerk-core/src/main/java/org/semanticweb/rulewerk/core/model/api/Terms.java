package org.semanticweb.rulewerk.core.model.api;

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

import java.util.stream.Stream;

/**
 * Collection of utility methods for handling {@link Term}s.
 * 
 * @author Markus Kroetzsch
 *
 */
public class Terms {

	/**
	 * Returns a stream of variables found in the given stream of terms. Ordering
	 * and duplicates are not affected.
	 * 
	 * @param terms stream of all terms
	 * @return stream of results
	 */
	public static Stream<Variable> getVariables(Stream<? extends Term> terms) {
		return terms.filter(term -> term.isVariable()).map(Variable.class::cast);
	}

	/**
	 * Returns a stream of constants found in the given stream of terms. Ordering
	 * and duplicates are not affected.
	 * 
	 * @param terms stream of all terms
	 * @return stream of results
	 */
	public static Stream<Constant> getConstants(Stream<? extends Term> terms) {
		return terms.filter(term -> term.isConstant()).map(Constant.class::cast);
	}

	/**
	 * Returns a stream of named nulls found in the given stream of terms. Ordering
	 * and duplicates are not affected.
	 * 
	 * @param terms stream of all terms
	 * @return stream of results
	 */
	public static Stream<NamedNull> getNamedNulls(Stream<? extends Term> terms) {
		return terms.filter(term -> term.getType() == TermType.NAMED_NULL).map(NamedNull.class::cast);
	}

	/**
	 * Returns a stream of universal variables found in the given stream of terms.
	 * Ordering and duplicates are not affected.
	 * 
	 * @param terms stream of all terms
	 * @return stream of results
	 */
	public static Stream<UniversalVariable> getUniversalVariables(Stream<? extends Term> terms) {
		return terms.filter(term -> term.getType() == TermType.UNIVERSAL_VARIABLE).map(UniversalVariable.class::cast);
	}

	/**
	 * Returns a stream of existential variables found in the given stream of terms.
	 * Ordering and duplicates are not affected.
	 * 
	 * @param terms stream of all terms
	 * @return stream of results
	 */
	public static Stream<ExistentialVariable> getExistentialVariables(Stream<? extends Term> terms) {
		return terms.filter(term -> term.getType() == TermType.EXISTENTIAL_VARIABLE)
				.map(ExistentialVariable.class::cast);
	}

	/**
	 * Returns a stream of abstract constants found in the given stream of terms.
	 * Ordering and duplicates are not affected.
	 * 
	 * @param terms stream of all terms
	 * @return stream of results
	 */
	public static Stream<AbstractConstant> getAbstractConstants(Stream<Term> terms) {
		return terms.filter(term -> term.getType() == TermType.ABSTRACT_CONSTANT).map(AbstractConstant.class::cast);
	}

	/**
	 * Returns a stream of datatype constants found in the given stream of terms.
	 * Ordering and duplicates are not affected.
	 * 
	 * @param terms stream of all terms
	 * @return stream of results
	 */
	public static Stream<DatatypeConstant> getDatatypeConstants(Stream<Term> terms) {
		return terms.filter(term -> term.getType() == TermType.DATATYPE_CONSTANT).map(DatatypeConstant.class::cast);
	}

}
