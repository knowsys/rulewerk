package org.semanticweb.vlog4j.owlapi;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.impl.BlankImpl;
import org.semanticweb.vlog4j.core.model.impl.ConstantImpl;
import org.semanticweb.vlog4j.core.model.impl.PredicateImpl;

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

	public static Predicate getAuxiliaryClassPredicate(OWLClassExpression owlClassExpression) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			byte[] digest = messageDigest.digest(owlClassExpression.toString().getBytes("UTF-8"));
			BigInteger bigInt = new BigInteger(1, digest);
			String hashtext = bigInt.toString(16);
			return new PredicateImpl("aux-" + hashtext, 1);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			throw new RuntimeException("We are missing some core functionality of Java here", e);
		}
	}

}
