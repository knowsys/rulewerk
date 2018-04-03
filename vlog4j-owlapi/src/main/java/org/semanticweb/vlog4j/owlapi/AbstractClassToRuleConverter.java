package org.semanticweb.vlog4j.owlapi;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.AtomImpl;
import org.semanticweb.vlog4j.core.model.implementation.ConjunctionImpl;
import org.semanticweb.vlog4j.core.model.implementation.RuleImpl;

/**
 * Abstract base class for converters that create rules from OWL class
 * expressions.
 * 
 * @author Markus Krötzsch
 */
public abstract class AbstractClassToRuleConverter implements OWLClassExpressionVisitor {

	/**
	 * Helper class to represent a list of atoms, interpreted as a conjunction of
	 * (positive) atoms. An empty conjunction is "true" (the neutral element of
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

		private List<Atom> conjuncts;
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

		public void add(Atom atom) {
			if (this.unsatisfiable) {
				return;
			}
			init();
			this.conjuncts.add(atom);
		}

		public void add(List<Atom> atoms) {
			if (this.unsatisfiable) {
				return;
			}
			init();
			this.conjuncts.addAll(atoms);
		}

		public void makeFalse() {
			this.unsatisfiable = true;
		}

		/**
		 * Returns true if this conjunction is true, i.e., if it is an empty conjunction
		 * (assuming that tautological atoms are never added). A true conjunction can
		 * become refutable when more atoms are added.
		 * 
		 * @return
		 */
		public boolean isTrue() {
			return !this.unsatisfiable && (this.conjuncts != null) && this.conjuncts.isEmpty();
		}

		/**
		 * Returns true if this conjunction is strongly false, i.e., if it contains an
		 * unsatisfiable atom. In this case, the actual atoms stored are not relevant. A
		 * false conjunction can not become true again.
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
			return !this.unsatisfiable && this.conjuncts != null && !this.conjuncts.isEmpty();
		}

		public List<Atom> getConjuncts() {
			return this.conjuncts;
		}

		/**
		 * Returns true if the conjunction is false or empty.
		 * 
		 * @return
		 */
		public boolean isFalseOrEmpty() {
			return this.conjuncts == null || this.unsatisfiable;
		}

		/**
		 * Returns true if the conjunction is true or empty.
		 * 
		 * @return
		 */
		public boolean isTrueOrEmpty() {
			return this.conjuncts == null || (this.conjuncts.isEmpty() && !this.unsatisfiable);
		}

	}

	SimpleConjunction body;
	SimpleConjunction head;

	/**
	 * Current frontier variable used as the main variable for creating atoms.
	 */
	final Term mainTerm;

	/**
	 * Parent converter object, used to create fresh names, e.g., for variables..
	 */
	final OwlAxiomToRulesConverter parent;

	public AbstractClassToRuleConverter(Term mainTerm, SimpleConjunction body, SimpleConjunction head,
			OwlAxiomToRulesConverter parent) {
		this.mainTerm = mainTerm;
		this.body = body;
		this.head = head;
		this.parent = parent;
	}

	/**
	 * Returns true if the current rule is a tautology, i.e., has an unsatisfiable
	 * body or a tautological head.
	 * 
	 * @return
	 */
	public boolean isTautology() {
		return this.body.isFalse() || this.head.isTrue();
	}

	/**
	 * Returns true if the current rule represents a falsity, i.e., has a
	 * tautological (or non-existent) body and an unsatisfiable (or no-existent)
	 * head.
	 * 
	 * @return
	 */
	public boolean isFalsity() {
		return this.body.isTrueOrEmpty() && this.head.isFalseOrEmpty();
	}

	void handleDisjunction(OWLClassExpression disjunct, Term term) {
		if (this.isTautology()) {
			return;
		}
		AbstractClassToRuleConverter converter = makeChildConverter(term);
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

	void handleDisjunction(Collection<OWLClassExpression> disjuncts) {
		OwlFeatureNotSupportedException owlFeatureNotSupportedException = null;
		for (OWLClassExpression disjunct : disjuncts) {
			try {
				handleDisjunction(disjunct, this.mainTerm);
			} catch (OwlFeatureNotSupportedException e) {
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

	void handleConjunction(Collection<OWLClassExpression> conjuncts, Term term) {
		List<AbstractClassToRuleConverter> converters = new ArrayList<>();
		OwlFeatureNotSupportedException owlFeatureNotSupportedException = null;
		boolean hasPositiveConjuncts = false;
		for (OWLClassExpression conjunct : conjuncts) {
			AbstractClassToRuleConverter converter = makeChildConverter(term);
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
			} catch (OwlFeatureNotSupportedException e) {
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

		Atom auxAtom = null;
		if (hasPositiveConjuncts || this.head.hasPositiveAtoms()) { // make positive (head) auxiliary atom
			for (AbstractClassToRuleConverter converter : converters) {
				auxAtom = handlePositiveConjunct(converter, conjuncts, term, auxAtom);
			}
		} else { // make negative (body) auxiliary atom
			auxAtom = new AtomImpl(OwlToRulesConversionHelper.getAuxiliaryClassPredicate(conjuncts),
					Arrays.asList(term));
			this.body.add(auxAtom);
			Conjunction auxHead = new ConjunctionImpl(Arrays.asList(auxAtom));
			for (AbstractClassToRuleConverter converter : converters) {
				assert (converter.body.exists()); // else: falsity (empty body true, empty head false)
				this.parent.rules.add(new RuleImpl(auxHead, new ConjunctionImpl(converter.body.getConjuncts())));
			}
		}
	}

	private Atom handlePositiveConjunct(AbstractClassToRuleConverter converter,
			Collection<OWLClassExpression> auxiliaryExpressions, Term term, Atom auxiliaryAtom) {
		assert (!converter.isFalsity());
		assert (!converter.isTautology());
		if (converter.body.isTrueOrEmpty()) {
			assert (converter.head.exists()); // else: falsity (empty body true, empty head false)
			this.head.add(converter.head.getConjuncts());
		} else {
			assert (converter.body.exists()); // checked in if-branch
			List<Atom> newBody = new ArrayList<>(converter.body.getConjuncts().size() + 1);
			if (auxiliaryAtom == null) {
				auxiliaryAtom = new AtomImpl(
						OwlToRulesConversionHelper.getAuxiliaryClassPredicate(auxiliaryExpressions),
						Arrays.asList(term));
				this.head.add(auxiliaryAtom);
			}
			newBody.add(auxiliaryAtom);
			newBody.addAll(converter.body.getConjuncts());
			List<Atom> newHead;
			if (converter.head.hasPositiveAtoms()) {
				newHead = converter.head.getConjuncts();
			} else {
				newHead = Arrays.asList(OwlToRulesConversionHelper.getBottom(term));
			}
			this.parent.rules.add(new RuleImpl(new ConjunctionImpl(newHead), new ConjunctionImpl(newBody)));
		}
		return auxiliaryAtom;
	}

	/**
	 * Handles a OWLObjectAllValues expression.
	 * 
	 * @param property
	 *            the OWL property of the expression
	 * @param filler
	 *            the filler class of the expression
	 */
	void handleObjectAllValues(OWLObjectPropertyExpression property, OWLClassExpression filler) {
		Variable variable = this.parent.getFreshVariable();
		OwlToRulesConversionHelper.addConjunctForPropertyExpression(property, this.mainTerm, variable,
				this.body);
		if (!this.body.isFalse()) {
			handleDisjunction(filler, variable);
		}
	}

	/**
	 * Handles a OWLObjectSomeValues expression.
	 * 
	 * @param property
	 *            the OWL property of the expression
	 * @param filler
	 *            the filler class of the expression
	 */
	void handleObjectSomeValues(OWLObjectPropertyExpression property, OWLClassExpression filler) {
		Variable variable = this.parent.getFreshVariable();
		OwlToRulesConversionHelper.addConjunctForPropertyExpression(property, this.mainTerm, variable,
				this.head);
		if (!this.head.isFalse()) {
			handleConjunction(Arrays.asList(filler), variable);
		}
	}

	/**
	 * Creates a new converter object of the same polarity, using the given frontier
	 * variable.
	 * 
	 * @param mainTerm
	 *            a variable to use
	 */
	public abstract AbstractClassToRuleConverter makeChildConverter(Term mainTerm);

}
