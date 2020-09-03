package org.semanticweb.rulewerk.core.model.api;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

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

	/**
	 * Returns the lexical value of a term that is an xsd:string constant, and
	 * throws an exception for all other cases.
	 * 
	 * @param term the term from which the string is to be extracted
	 * @return extracted string
	 * @throws IllegalArgumentException if the given term is not a constant of type
	 *                                  xsd:string
	 */
	public static String extractString(Term term) {
		if (term.getType() == TermType.DATATYPE_CONSTANT) {
			DatatypeConstant datatypeConstant = (DatatypeConstant) term;
			if (PrefixDeclarationRegistry.XSD_STRING.equals(datatypeConstant.getDatatype()))
				return datatypeConstant.getLexicalValue();
		}
		throw new IllegalArgumentException(
				"Term " + term.toString() + " is not a datatype constant of type xsd:string.");
	}

	/**
	 * Returns the name of an abstract term, and throws an exception for all other
	 * cases.
	 * 
	 * @param term the term from which the name is to be extracted
	 * @return extracted name
	 * @throws IllegalArgumentException if the given term is not an abstract
	 *                                  constant
	 */
	public static String extractName(Term term) {
		if (term.getType() == TermType.ABSTRACT_CONSTANT) {
			return term.getName();
		} else {
			throw new IllegalArgumentException("Term " + term.toString() + " is not an abstract constant.");
		}
	}

	/**
	 * Returns the IRI representation of an abstract term, and throws an exception
	 * for all other cases.
	 * 
	 * @param term the term from which the IRI is to be extracted
	 * @return extracted IRI
	 * @throws IllegalArgumentException if the given term is not an abstract
	 *                                  constant or cannot be parsed as an IRI
	 */
	public static URI extractIri(Term term) {
		try {
			return new URI(extractName(term));
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Returns the URL representation of an abstract term, and throws an exception
	 * for all other cases.
	 * 
	 * @param term the term from which the URL is to be extracted
	 * @return extracted URL
	 * @throws IllegalArgumentException if the given term is not an abstract
	 *                                  constant or cannot be parsed as a URL
	 */
	public static URL extractUrl(Term term) {
		try {
			return new URL(extractName(term));
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Returns the numeric value of a term that is an xsd:integer (or supported
	 * subtype) constant, and throws an exception for all other cases.
	 * 
	 * @param term the term from which the integer is to be extracted
	 * @return extracted integer
	 * @throws IllegalArgumentException if the given term is not a constant of an
	 *                                  integer type, or if the lexical
	 *                                  representation could not be parsed into a
	 *                                  Java int
	 */
	public static int extractInt(Term term) {
		if (term.getType() == TermType.DATATYPE_CONSTANT) {
			DatatypeConstant datatypeConstant = (DatatypeConstant) term;
			if (PrefixDeclarationRegistry.XSD_INTEGER.equals(datatypeConstant.getDatatype())
					|| PrefixDeclarationRegistry.XSD_LONG.equals(datatypeConstant.getDatatype())
					|| PrefixDeclarationRegistry.XSD_INT.equals(datatypeConstant.getDatatype())
					|| PrefixDeclarationRegistry.XSD_SHORT.equals(datatypeConstant.getDatatype())
					|| PrefixDeclarationRegistry.XSD_BYTE.equals(datatypeConstant.getDatatype()))
				return Integer.parseInt(datatypeConstant.getLexicalValue());
		}
		throw new IllegalArgumentException(
				"Term " + term.toString() + " is not a datatype constant of a supported integer type.");
	}

}
