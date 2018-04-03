package org.semanticweb.vlog4j.owlapi;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;

import org.semanticweb.owlapi.model.OWLAnonymousIndividual;

/*-
 * #%L
 * VLog4j OWL API Support
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

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.implementation.AtomImpl;
import org.semanticweb.vlog4j.core.model.implementation.BlankImpl;
import org.semanticweb.vlog4j.core.model.implementation.ConstantImpl;
import org.semanticweb.vlog4j.core.model.implementation.PredicateImpl;
import org.semanticweb.vlog4j.owlapi.AbstractClassToRuleConverter.SimpleConjunction;

/**
 * Utility class for helper functions that are used to convert OWL API objects
 * to rules.
 * 
 * @author Markus Kroetzsch
 *
 */
public class OwlToRulesConversionHelper {

	/**
	 * Returns a {@link Term} to represent an {@link OWLIndividual} in rules.
	 * 
	 * @param owlIndividual
	 *            the individual to get a term for
	 * @return a suitable term
	 */
	public static Term getIndividualTerm(OWLIndividual owlIndividual) {
		if (owlIndividual instanceof OWLNamedIndividual) {
			return new ConstantImpl(((OWLNamedIndividual) owlIndividual).getIRI().toString());
		} else if (owlIndividual instanceof OWLAnonymousIndividual) {
			return new BlankImpl(((OWLAnonymousIndividual) owlIndividual).getID().toString());
		} else {
			throw new OwlFeatureNotSupportedException(
					"Could not convert OWL individual '" + owlIndividual.toString() + "' to a term.");
		}
	}

	/**
	 * Returns a {@link Predicate} to represent an {@link OWLClass} in rules.
	 * 
	 * @param owlClass
	 *            the atomic class to get a predicate for
	 * @return a suitable unary predicate
	 */
	public static Predicate getClassPredicate(OWLClass owlClass) {
		return new PredicateImpl(owlClass.getIRI().toString(), 1);
	}

	/**
	 * Returns a {@link Predicate} to represent an {@link OWLObjectProperty} in
	 * rules.
	 * 
	 * @param owlObjectProperty
	 *            the atomic property to get a predicate for
	 * @return a suitable binary predicate
	 */
	public static Predicate getObjectPropertyPredicate(OWLObjectProperty owlObjectProperty) {
		return new PredicateImpl(owlObjectProperty.getIRI().toString(), 2);
	}

	public static Predicate getAuxiliaryClassPredicate(Collection<OWLClassExpression> owlClassExpressions) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			for (OWLClassExpression owlClassExpression : owlClassExpressions) {
				messageDigest.update(owlClassExpression.toString().getBytes("UTF-8"));
			}
			byte[] digest = messageDigest.digest();
			BigInteger bigInt = new BigInteger(1, digest);
			String hashtext = bigInt.toString(16);
			return new PredicateImpl("aux-" + hashtext, 1);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			throw new RuntimeException("We are missing some core functionality of Java here", e);
		}
	}

	/**
	 * Adds a binary predicate for a given OWL object property expression to the
	 * given conjunction. If the expression is an inverse, source and target terms
	 * are swapped. If the expression is top or bottom, it is handled appropriately.
	 * 
	 * @param owlObjectPropertyExpression
	 *            the property expression
	 * @param sourceTerm
	 *            the term that should be in the first parameter position of the
	 *            original expression
	 * @param targetTerm
	 *            the term that should be in the second parameter position of the
	 *            original expression
	 */
	static void addConjunctForPropertyExpression(OWLObjectPropertyExpression owlObjectPropertyExpression,
			Term sourceTerm, Term targetTerm, SimpleConjunction conjuncts) {
		if (owlObjectPropertyExpression.isOWLTopObjectProperty()) {
			conjuncts.init();
		} else if (owlObjectPropertyExpression.isOWLBottomObjectProperty()) {
			conjuncts.makeFalse();
		} else {
			conjuncts.add(getObjectPropertyAtom(owlObjectPropertyExpression, sourceTerm, targetTerm));
		}
	}

	public static Atom getObjectPropertyAtom(OWLObjectPropertyExpression owlObjectPropertyExpression, Term sourceTerm,
			Term targetTerm) {
		if (owlObjectPropertyExpression.isAnonymous()) {
			Predicate predicate = OwlToRulesConversionHelper
					.getObjectPropertyPredicate(owlObjectPropertyExpression.getInverseProperty().asOWLObjectProperty());
			return new AtomImpl(predicate, Arrays.asList(targetTerm, sourceTerm));
		} else {
			Predicate predicate = OwlToRulesConversionHelper
					.getObjectPropertyPredicate(owlObjectPropertyExpression.asOWLObjectProperty());
			return new AtomImpl(predicate, Arrays.asList(sourceTerm, targetTerm));
		}
	}

	public static Atom getBottom(Term term) {
		Predicate predicate = new PredicateImpl("http://www.w3.org/2002/07/owl#Nothing", 1);
		return new AtomImpl(predicate, Arrays.asList(term));
	}

	public static Atom getTop(Term term) {
		Predicate predicate = new PredicateImpl("http://www.w3.org/2002/07/owl#Thing", 1);
		return new AtomImpl(predicate, Arrays.asList(term));
	}

}
