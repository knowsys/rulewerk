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
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.implementation.AbstractConstantImpl;
import org.semanticweb.vlog4j.core.model.implementation.DatatypeConstantImpl;
import org.semanticweb.vlog4j.core.model.implementation.FactImpl;
import org.semanticweb.vlog4j.core.model.implementation.NamedNullImpl;
import org.semanticweb.vlog4j.core.model.implementation.PositiveLiteralImpl;
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
	public static Term getIndividualTerm(final OWLIndividual owlIndividual) {
		if (owlIndividual instanceof OWLNamedIndividual) {
			return new AbstractConstantImpl(((OWLNamedIndividual) owlIndividual).getIRI().toString());
		} else if (owlIndividual instanceof OWLAnonymousIndividual) {
			return new NamedNullImpl(((OWLAnonymousIndividual) owlIndividual).getID().toString());
		} else {
			throw new OwlFeatureNotSupportedException(
					"Could not convert OWL individual '" + owlIndividual.toString() + "' to a term.");
		}
	}

	/**
	 * Returns a {@link Term} to represent an {@link OWLLiteral} in rules.
	 * 
	 * @param owlLiteral
	 *            the literal to get a term for
	 * @return a suitable term
	 */
	public static Term getLiteralTerm(final OWLLiteral owlLiteral) {
		return new DatatypeConstantImpl(owlLiteral.getDatatype().toString(), owlLiteral.getLiteral());
	}

	/**
	 * Returns a {@link Predicate} to represent an {@link OWLClass} in rules.
	 * 
	 * @param owlClass
	 *            the atomic class to get a predicate for
	 * @return a suitable unary predicate
	 */
	public static Predicate getClassPredicate(final OWLClass owlClass) {
		return new PredicateImpl(owlClass.getIRI().toString(), 1);
	}

	/**
	 * Returns a {@link Predicate} to represent an {@link OWLDatatype} in rules.
	 * 
	 * @param owlClass
	 *            the atomic class to get a predicate for
	 * @return a suitable unary predicate
	 */
	public static Predicate getDatatypePredicate(final OWLDatatype owlDatatype) {
		return new PredicateImpl(owlDatatype.getIRI().toString(), 1);
	}

	/**
	 * Returns a {@link Predicate} to represent an {@link OWLProperty} in rules.
	 * 
	 * @param owlProperty
	 *            the atomic object property to get a predicate for
	 * @return a suitable binary predicate
	 */
	public static Predicate getPropertyPredicate(final OWLProperty owlProperty) {
		return new PredicateImpl(owlProperty.getIRI().toString(), 2);
	}

	public static Predicate getAuxiliaryClassPredicate(final Collection<OWLClassExpression> owlClassExpressions) {
		try {
			final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			for (final OWLClassExpression owlClassExpression : owlClassExpressions) {
				messageDigest.update(owlClassExpression.toString().getBytes("UTF-8"));
			}
			final byte[] digest = messageDigest.digest();
			final BigInteger bigInt = new BigInteger(1, digest);
			final String hashtext = bigInt.toString(16);
			return new PredicateImpl("aux-" + hashtext, 1);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			throw new RuntimeException("We are missing some core functionality of Java here", e);
		}
	}

	/**
	 * Adds a binary atom for a given OWL property expression to the given
	 * conjunction.
	 * 
	 * @param owlPropertyExpression
	 *            the property expression
	 * @param sourceTerm
	 *            the term that should be in the first parameter position of the
	 *            original expression
	 * @param targetTerm
	 *            the term that should be in the second parameter position of the
	 *            original expression
	 * @param conjuncts
	 *            the conjunction to which we add unary atom
	 */
	static void addConjunctForPropertyExpression(final OWLPropertyExpression owlPropertyExpression,
			final Term sourceTerm, final Term targetTerm, final SimpleConjunction conjuncts) {
		if (owlPropertyExpression.isTopEntity()) {
			conjuncts.init();
		} else if (owlPropertyExpression.isBottomEntity()) {
			conjuncts.makeFalse();
		} else {
			conjuncts.add(getPropertyAtom(owlPropertyExpression, sourceTerm, targetTerm));
		}
	}

	/**
	 * Adds a unary atom for a given OWL data range expression to the given
	 * conjunction.
	 * 
	 * @param owlDataRange
	 *            the property expression
	 * @param sourceTerm
	 *            the term that should be in the first parameter position of the
	 *            original expression
	 * @param targetTerm
	 *            the term that should be in the second parameter position of the
	 *            original expression
	 */
	public static void addConjunctForOWLDataRange(final OWLDataRange owlDataRange, final Term term,
			final SimpleConjunction conjuncts) {
		if (owlDataRange.isTopDatatype()) {
			conjuncts.init();
		} else if (owlDataRange.isOWLDatatype()) {
			OWLDatatype owlDatatype = (OWLDatatype) owlDataRange;
			conjuncts.add(new PositiveLiteralImpl(getDatatypePredicate(owlDatatype), Arrays.asList(term)));
		} else {
			throw new OwlFeatureNotSupportedException(
					"OWL data ranges that are not of type OWLDatatype are currently not supported in rules.");
		}
	}

	public static PositiveLiteral getPropertyAtom(final OWLPropertyExpression owlPropertyExpression,
			final Term sourceTerm, final Term targetTerm) {
		if (owlPropertyExpression.isObjectPropertyExpression()) {
			return getObjectPropertyAtom((OWLObjectPropertyExpression) owlPropertyExpression, sourceTerm, targetTerm);
		} else if (owlPropertyExpression.isDataPropertyExpression()) {
			return getDataPropertyAtom((OWLDataPropertyExpression) owlPropertyExpression, sourceTerm, targetTerm);
		} else {
			throw new OwlFeatureNotSupportedException(
					"We only support binary atoms defined over properties of type OWLObjectPropertyExpression and OWLDataPropertyExpression.");
		}
	}

	public static PositiveLiteral getObjectPropertyAtom(final OWLObjectPropertyExpression owlObjectPropertyExpression,
			final Term sourceTerm, final Term targetTerm) {
		if (owlObjectPropertyExpression.isAnonymous()) {
			final Predicate predicate = OwlToRulesConversionHelper
					.getPropertyPredicate(owlObjectPropertyExpression.getInverseProperty().asOWLObjectProperty());
			return new PositiveLiteralImpl(predicate, Arrays.asList(targetTerm, sourceTerm));
		} else {
			final Predicate predicate = OwlToRulesConversionHelper
					.getPropertyPredicate(owlObjectPropertyExpression.asOWLObjectProperty());
			return new PositiveLiteralImpl(predicate, Arrays.asList(sourceTerm, targetTerm));
		}
	}

	public static PositiveLiteral getDataPropertyAtom(final OWLDataPropertyExpression owlDataPropertyExpression,
			final Term sourceTerm, final Term targetTerm) {
		final Predicate predicate = OwlToRulesConversionHelper
				.getPropertyPredicate(owlDataPropertyExpression.asOWLDataProperty());
		return new PositiveLiteralImpl(predicate, Arrays.asList(sourceTerm, targetTerm));
	}

	public static Fact getObjectPropertyFact(final OWLObjectPropertyExpression owlObjectPropertyExpression,
			final Term sourceTerm, final Term targetTerm) {
		if (owlObjectPropertyExpression.isAnonymous()) {
			final Predicate predicate = OwlToRulesConversionHelper
					.getPropertyPredicate(owlObjectPropertyExpression.getInverseProperty().asOWLObjectProperty());
			return new FactImpl(predicate, Arrays.asList(targetTerm, sourceTerm));
		} else {
			final Predicate predicate = OwlToRulesConversionHelper
					.getPropertyPredicate(owlObjectPropertyExpression.asOWLObjectProperty());
			return new FactImpl(predicate, Arrays.asList(sourceTerm, targetTerm));
		}
	}

	public static PositiveLiteral getBottom(final Term term) {
		final Predicate predicate = new PredicateImpl("http://www.w3.org/2002/07/owl#Nothing", 1);
		return new PositiveLiteralImpl(predicate, Arrays.asList(term));
	}

	public static PositiveLiteral getTop(final Term term) {
		final Predicate predicate = new PredicateImpl("http://www.w3.org/2002/07/owl#Thing", 1);
		return new PositiveLiteralImpl(predicate, Arrays.asList(term));
	}

}
