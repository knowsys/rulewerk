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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.PositiveLiteralImpl;

/**
 * Abstract base class for converters that create rules from OWL class
 * expressions.
 *
 * @author Markus Krötzsch
 */
public abstract class AbstractClassToRuleConverter implements OWLClassExpressionVisitor {

	/**
	 * Helper class to represent a list of literals, interpreted as a conjunction of
	 * (positive) literals. An empty conjunction is "true" (the neutral element of
	 * conjunction). If the conjunction would become false due to some unsatisfiable
	 * atom, this is recorded in {@link SimpleConjunction#unsatisfiable}. In this
	 * case, the conjuncts should be ignored. A third relevant option for the head
	 * is that the conjunction is not present at all, which in a disjunctive (head)
	 * context amounts to it being false (the neutral element of disjunction), while
	 * in a conjunctive (body) context it amounts to being true.
	 */
	/**
	 * @author Markus Krötzsch
	 *
	 */
	static class SimpleConjunction {

		private List<PositiveLiteral> conjuncts;
		private boolean unsatisfiable;

		/**
		 * Initialises the conjunction, so it is no longer considered empty. This
		 * corresponds to adding a tautological atom to the conjunction.
		 */
		public void init() {
			if (this.conjuncts == null) {
				this.conjuncts = new ArrayList<>();
			}
		}

		public void add(final PositiveLiteral atom) {
			if (this.unsatisfiable) {
				return;
			}
			this.init();
			this.conjuncts.add(atom);
		}

		public void add(final List<PositiveLiteral> atoms) {
			if (this.unsatisfiable) {
				return;
			}
			this.init();
			this.conjuncts.addAll(atoms);
		}

		public void makeFalse() {
			this.unsatisfiable = true;
		}

		/**
		 * Returns true if this conjunction is true, i.e., if it is an empty conjunction
		 * (assuming that tautological literals are never added). A true conjunction can
		 * become refutable when more literals are added.
		 *
		 * @return
		 */
		public boolean isTrue() {
			return !this.unsatisfiable && (this.conjuncts != null) && this.conjuncts.isEmpty();
		}

		/**
		 * Returns true if this conjunction is strongly false, i.e., if it contains an
		 * unsatisfiable atom. In this case, the actual literals stored are not
		 * relevant. A false conjunction can not become true again.
		 *
		 * @return
		 */
		public boolean isFalse() {
			return this.unsatisfiable;
		}

		/**
		 * Returns true if this object represents a conjunction at all (even an empty
		 * one).
		 *
		 * @return
		 */
		public boolean exists() {
			return this.conjuncts != null;
		}

		/**
		 * Returns true if this object represents a conjunction that contains at least
		 * one atom. For this it should be neither empty, nor false, nor true.
		 *
		 * @return
		 */
		public boolean hasPositiveAtoms() {
			return !this.unsatisfiable && (this.conjuncts != null) && !this.conjuncts.isEmpty();
		}

		public List<PositiveLiteral> getConjuncts() {
			return this.conjuncts;
		}

		/**
		 * Returns true if the conjunction is false or empty.
		 *
		 * @return
		 */
		public boolean isFalseOrEmpty() {
			return (this.conjuncts == null) || this.unsatisfiable;
		}

		/**
		 * Returns true if the conjunction is true or empty.
		 *
		 * @return
		 */
		public boolean isTrueOrEmpty() {
			return (this.conjuncts == null) || (this.conjuncts.isEmpty() && !this.unsatisfiable);
		}

	}

	SimpleConjunction body;
	SimpleConjunction head;

	/**
	 * Current frontier variable used as the main variable for creating literals.
	 */
	final Term mainTerm;

	/**
	 * Parent converter object, used to create fresh names, e.g., for variables..
	 */
	final OwlAxiomToRulesConverter parent;

	public AbstractClassToRuleConverter(final Term mainTerm, final SimpleConjunction body, final SimpleConjunction head,
			final OwlAxiomToRulesConverter parent) {
		this.mainTerm = mainTerm;
		this.body = body;
		this.head = head;
		this.parent = parent;
	}

	/**
	 * Check whether the current rule is a tautology.
	 *
	 * @return true if the current rule is a tautology, i.e., has an
	 * unsatisfiable body or a tautological head.
	 */
	public boolean isTautology() {
		return this.body.isFalse() || this.head.isTrue();
	}

	/**
	 * Checks whether the current rule is a falsity.
	 *
	 * @return true if the current rule represents a falsity, i.e.,
	 * has a tautological (or non-existent) body and an unsatisfiable
	 * (or no-existent) head.
	 */
	public boolean isFalsity() {
		return this.body.isTrueOrEmpty() && this.head.isFalseOrEmpty();
	}

	void handleDisjunction(final OWLClassExpression disjunct, final Term term) {
		if (this.isTautology()) {
			return;
		}
		final AbstractClassToRuleConverter converter = this.makeChildConverter(term);
		disjunct.accept(converter);
		if (converter.isTautology()) {
			this.body.makeFalse();
			return;
		}
		if (converter.isFalsity()) {
			return;
		}
		if (converter.head.hasPositiveAtoms()) {
			if (this.head.hasPositiveAtoms()) {
				throw new OwlFeatureNotSupportedException("Union in superclass positions is not supported in rules.");
			} else {
				this.head = converter.head;
			}
		}
		if (converter.body.exists()) {
			this.body.add(converter.body.getConjuncts());
		}
	}

	void handleDisjunction(final Collection<OWLClassExpression> disjuncts) {
		OwlFeatureNotSupportedException owlFeatureNotSupportedException = null;
		for (final OWLClassExpression disjunct : disjuncts) {
			try {
				this.handleDisjunction(disjunct, this.mainTerm);
			} catch (final OwlFeatureNotSupportedException e) {
				owlFeatureNotSupportedException = e;
			}
			if (this.isTautology()) {
				return;
			}
		}

		if (owlFeatureNotSupportedException != null) {
			throw owlFeatureNotSupportedException;
		}
	}

	void handleConjunction(final Collection<OWLClassExpression> conjuncts, final Term term) {
		final List<AbstractClassToRuleConverter> converters = new ArrayList<>();
		OwlFeatureNotSupportedException owlFeatureNotSupportedException = null;
		boolean hasPositiveConjuncts = false;

		for (final OWLClassExpression conjunct : conjuncts) {
			final AbstractClassToRuleConverter converter = this.makeChildConverter(term);
			try {
				conjunct.accept(converter);
				if (converter.isTautology()) {
					continue; // ignore tautologies
				}
				if (converter.isFalsity()) {
					this.head.makeFalse(); // overwrites even prior exceptions
					return;
				}
				hasPositiveConjuncts = hasPositiveConjuncts || converter.head.hasPositiveAtoms();
				converters.add(converter);
			} catch (final OwlFeatureNotSupportedException e) {
				owlFeatureNotSupportedException = e;
			}
		}

		if (owlFeatureNotSupportedException != null) {
			throw owlFeatureNotSupportedException;
		}

		if (converters.isEmpty()) {
			this.head.init();
			return;
		}

		PositiveLiteral auxAtom = null;
		if (hasPositiveConjuncts || this.head.hasPositiveAtoms()) { // make positive (head) auxiliary atom
			for (final AbstractClassToRuleConverter converter : converters) {
				auxAtom = this.handlePositiveConjunct(converter, conjuncts, term, auxAtom);
			}
		} else { // make negative (body) auxiliary atom
			auxAtom = new PositiveLiteralImpl(OwlToRulesConversionHelper.getAuxiliaryClassPredicate(conjuncts),
					Arrays.asList(term));
			this.body.add(auxAtom);
			final List<PositiveLiteral> auxHead = Collections.singletonList(auxAtom);
			for (final AbstractClassToRuleConverter converter : converters) {
				assert (converter.body.exists()); // else: falsity (empty body true, empty head false)
				this.parent.addAuxiliaryRule(auxHead, converter.body.getConjuncts(), term);
			}
		}
	}

	private PositiveLiteral handlePositiveConjunct(final AbstractClassToRuleConverter converter,
			final Collection<OWLClassExpression> auxiliaryExpressions, final Term term, PositiveLiteral auxiliaryAtom) {
		assert (!converter.isFalsity());
		assert (!converter.isTautology());
		if (converter.body.isTrueOrEmpty()) {
			assert (converter.head.exists()); // else: falsity (empty body true, empty head false)
			this.head.add(converter.head.getConjuncts());
		} else {
			assert (converter.body.exists()); // checked in if-branch
			final List<Literal> newBody = new ArrayList<>(converter.body.getConjuncts().size() + 1);
			if (auxiliaryAtom == null) {
				auxiliaryAtom = new PositiveLiteralImpl(
						OwlToRulesConversionHelper.getAuxiliaryClassPredicate(auxiliaryExpressions),
						Arrays.asList(term));
				this.head.add(auxiliaryAtom);
			}
			newBody.add(auxiliaryAtom);
			newBody.addAll(converter.body.getConjuncts());
			List<PositiveLiteral> newHead;
			if (converter.head.hasPositiveAtoms()) {
				newHead = converter.head.getConjuncts();
			} else {
				newHead = Arrays.asList(OwlToRulesConversionHelper.getBottom(term));
			}
			this.parent.addAuxiliaryRule(newHead, newBody, term);
		}
		return auxiliaryAtom;
	}

	/**
	 * Handles a OWLObjectAllValues expression.
	 *
	 * @param property the OWL property of the expression
	 * @param filler   the filler class of the expression
	 */
	void handleObjectAllValues(final OWLObjectPropertyExpression property, final OWLClassExpression filler) {
		final Variable variable = this.parent.getFreshUniversalVariable();
		OwlToRulesConversionHelper.addConjunctForPropertyExpression(property, this.mainTerm, variable, this.body);
		if (!this.body.isFalse()) {
			this.handleDisjunction(filler, variable);
		}
	}

	/**
	 * Handles a OWLObjectSomeValues expression.
	 *
	 * @param property the OWL property of the expression
	 * @param filler   the filler class of the expression
	 */
	void handleObjectSomeValues(final OWLObjectPropertyExpression property, final OWLClassExpression filler) {
		final Variable variable = this.parent.getFreshExistentialVariable();
		OwlToRulesConversionHelper.addConjunctForPropertyExpression(property, this.mainTerm, variable, this.head);
		if (!this.head.isFalse()) {
			this.handleConjunction(Arrays.asList(filler), variable);
		}
	}

	/**
	 * Creates a new converter object of the same polarity, using the given frontier
	 * variable.
	 *
	 * @param mainTerm a variable to use
	 */
	public abstract AbstractClassToRuleConverter makeChildConverter(Term mainTerm);

}
