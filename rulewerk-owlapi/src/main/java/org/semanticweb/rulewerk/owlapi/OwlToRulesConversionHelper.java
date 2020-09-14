package org.semanticweb.rulewerk.owlapi;

/*-
 * #%L
 * Rulewerk OWL API Support
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
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;

import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.rulewerk.core.exceptions.RulewerkRuntimeException;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.AbstractConstantImpl;
import org.semanticweb.rulewerk.core.model.implementation.FactImpl;
import org.semanticweb.rulewerk.core.model.implementation.PositiveLiteralImpl;
import org.semanticweb.rulewerk.core.model.implementation.PredicateImpl;
import org.semanticweb.rulewerk.core.reasoner.implementation.Skolemization;
import org.semanticweb.rulewerk.owlapi.AbstractClassToRuleConverter.SimpleConjunction;

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
	 * @param owlIndividual the individual to get a term for
	 * @return a suitable term
	 */
	public static Term getIndividualTerm(final OWLIndividual owlIndividual, Skolemization skolemization) {
		if (owlIndividual instanceof OWLNamedIndividual) {
			return new AbstractConstantImpl(((OWLNamedIndividual) owlIndividual).getIRI().toString());
		} else if (owlIndividual instanceof OWLAnonymousIndividual) {
			return skolemization.getRenamedNamedNull(((OWLAnonymousIndividual) owlIndividual).getID().toString());
		} else {
			throw new OwlFeatureNotSupportedException(
					"Could not convert OWL individual '" + owlIndividual.toString() + "' to a term.");
		}
	}

	/**
	 * Returns a {@link Predicate} to represent an {@link OWLClass} in rules.
	 *
	 * @param owlClass the atomic class to get a predicate for
	 * @return a suitable unary predicate
	 */
	public static Predicate getClassPredicate(final OWLClass owlClass) {
		return new PredicateImpl(owlClass.getIRI().toString(), 1);
	}

	/**
	 * Returns a {@link Predicate} to represent an {@link OWLObjectProperty} in
	 * rules.
	 *
	 * @param owlObjectProperty the atomic property to get a predicate for
	 * @return a suitable binary predicate
	 */
	public static Predicate getObjectPropertyPredicate(final OWLObjectProperty owlObjectProperty) {
		return new PredicateImpl(owlObjectProperty.getIRI().toString(), 2);
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
			throw new RulewerkRuntimeException("We are missing some core functionality of Java here", e);
		}
	}

	/**
	 * Adds a binary predicate for a given OWL object property expression to the
	 * given conjunction. If the expression is an inverse, source and target terms
	 * are swapped. If the expression is top or bottom, it is handled appropriately.
	 *
	 * @param owlObjectPropertyExpression the property expression
	 * @param sourceTerm                  the term that should be in the first
	 *                                    parameter position of the original
	 *                                    expression
	 * @param targetTerm                  the term that should be in the second
	 *                                    parameter position of the original
	 *                                    expression
	 */
	static void addConjunctForPropertyExpression(final OWLObjectPropertyExpression owlObjectPropertyExpression,
			final Term sourceTerm, final Term targetTerm, final SimpleConjunction conjuncts) {
		if (owlObjectPropertyExpression.isOWLTopObjectProperty()) {
			conjuncts.init();
		} else if (owlObjectPropertyExpression.isOWLBottomObjectProperty()) {
			conjuncts.makeFalse();
		} else {
			conjuncts.add(getObjectPropertyAtom(owlObjectPropertyExpression, sourceTerm, targetTerm));
		}
	}

	public static PositiveLiteral getObjectPropertyAtom(final OWLObjectPropertyExpression owlObjectPropertyExpression,
			final Term sourceTerm, final Term targetTerm) {
		if (owlObjectPropertyExpression.isAnonymous()) {
			final Predicate predicate = OwlToRulesConversionHelper
					.getObjectPropertyPredicate(owlObjectPropertyExpression.getInverseProperty().asOWLObjectProperty());
			return new PositiveLiteralImpl(predicate, Arrays.asList(targetTerm, sourceTerm));
		} else {
			final Predicate predicate = OwlToRulesConversionHelper
					.getObjectPropertyPredicate(owlObjectPropertyExpression.asOWLObjectProperty());
			return new PositiveLiteralImpl(predicate, Arrays.asList(sourceTerm, targetTerm));
		}
	}

	public static Fact getObjectPropertyFact(final OWLObjectPropertyExpression owlObjectPropertyExpression,
			final Term sourceTerm, final Term targetTerm) {
		if (owlObjectPropertyExpression.isAnonymous()) {
			final Predicate predicate = OwlToRulesConversionHelper
					.getObjectPropertyPredicate(owlObjectPropertyExpression.getInverseProperty().asOWLObjectProperty());
			return new FactImpl(predicate, Arrays.asList(targetTerm, sourceTerm));
		} else {
			final Predicate predicate = OwlToRulesConversionHelper
					.getObjectPropertyPredicate(owlObjectPropertyExpression.asOWLObjectProperty());
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
