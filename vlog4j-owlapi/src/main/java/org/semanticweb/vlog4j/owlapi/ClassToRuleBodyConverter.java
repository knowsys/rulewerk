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
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.impl.AtomImpl;
import org.semanticweb.vlog4j.core.model.impl.ConjunctionImpl;
import org.semanticweb.vlog4j.core.model.impl.RuleImpl;

public class ClassToRuleBodyConverter implements OWLClassExpressionVisitor {

	final List<Atom> bodyConjuncts;
	final Set<Rule> rules;
	final Variable frontierVariable;
	final OwlToRulesConverter parent;

	/**
	 * Set to true to indicate that an unsatisfiable body conjunct (owl:Nothing,
	 * owl:BottomObjectProperty, ...) occurred, so that the body cannot have any
	 * matches. In this case, no rules based on this body must be created.
	 */
	boolean unsatisfiable = false;

	public ClassToRuleBodyConverter(Variable frontierVariable, List<Atom> bodyConjuncts, Set<Rule> rules,
			OwlToRulesConverter parent) {
		this.frontierVariable = frontierVariable;
		this.bodyConjuncts = bodyConjuncts;
		this.rules = rules;
		this.parent = parent;
	}

	/**
	 * Returns true if an unsatisfiable body conjunct (owl:Nothing,
	 * owl:BottomObjectProperty, ...) has occurred, so that the body cannot have any
	 * matches. In this case, no rules based on this body must be created.
	 * 
	 * @return true if the body is unsatisfiable
	 */
	public boolean isUnsatisfiable() {
		return this.unsatisfiable;
	}

	@Override
	public void visit(OWLClass ce) {
		if (ce.isOWLNothing()) {
			this.unsatisfiable = true;
		} else if (ce.isOWLThing()) {
			// irrelevant in body; omit
		} else {
			Predicate predicate = OwlToRulesConversionHelper.getClassPredicate(ce);
			bodyConjuncts.add(new AtomImpl(predicate, Arrays.asList(this.frontierVariable)));
		}
	}

	@Override
	public void visit(OWLObjectIntersectionOf ce) {
		for (OWLClassExpression conjunct : ce.getOperands()) {
			conjunct.accept(this);
		}
	}

	@Override
	public void visit(OWLObjectUnionOf ce) {
		Predicate predicate = OwlToRulesConversionHelper.getAuxiliaryClassPredicate(ce);
		Atom headAtom = new AtomImpl(predicate, Arrays.asList(this.frontierVariable));
		this.bodyConjuncts.add(headAtom);
		Conjunction auxHead = new ConjunctionImpl(Arrays.asList(headAtom));
		for (OWLClassExpression conjunct : ce.getOperands()) {
			ClassToRuleBodyConverter converter = new ClassToRuleBodyConverter(this.frontierVariable, new ArrayList<>(),
					this.rules, this.parent);
			conjunct.accept(converter);
			if (!converter.isUnsatisfiable()) {
				// TODO handle case where no conjunctions were created (tautological subclass)
				this.rules.add(new RuleImpl(auxHead, new ConjunctionImpl(converter.bodyConjuncts)));
			}
		}
	}

	@Override
	public void visit(OWLObjectComplementOf ce) {
		throw new OwlFeatureNotSupportedException("Negation in subclass positions is not supported in rules.");
	}

	@Override
	public void visit(OWLObjectSomeValuesFrom ce) {
		Variable variable = this.parent.getFreshVariable();
		addConjunctForPropertyExpression(ce.getProperty(), this.frontierVariable, variable);
		if (!this.unsatisfiable) {
			ClassToRuleBodyConverter converter = new ClassToRuleBodyConverter(variable, this.bodyConjuncts, this.rules,
					this.parent);
			ce.getFiller().accept(converter);
		}
	}

	@Override
	public void visit(OWLObjectAllValuesFrom ce) {
		throw new OwlFeatureNotSupportedException(
				"Universal quantifiers (AllValuesFrom)  in subclass positions is not supported in rules.");
	}

	@Override
	public void visit(OWLObjectHasValue ce) {
		Term term = OwlToRulesConversionHelper.getIndividualTerm(ce.getFiller());
		addConjunctForPropertyExpression(ce.getProperty(), this.frontierVariable, term);
	}

	@Override
	public void visit(OWLObjectMinCardinality ce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLObjectExactCardinality ce) {
		throw new OwlFeatureNotSupportedException(
				"Exact cardinality restrictions  in subclass positions is not supported in rules.");
	}

	@Override
	public void visit(OWLObjectMaxCardinality ce) {
		throw new OwlFeatureNotSupportedException(
				"Maximal cardinality restrictions  in subclass positions is not supported in rules.");
	}

	@Override
	public void visit(OWLObjectHasSelf ce) {
		addConjunctForPropertyExpression(ce.getProperty(), this.frontierVariable, this.frontierVariable);
	}

	@Override
	public void visit(OWLObjectOneOf ce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLDataSomeValuesFrom ce) {
		throw new OwlFeatureNotSupportedException("OWL datatypes currently not supported in rules.");
	}

	@Override
	public void visit(OWLDataAllValuesFrom ce) {
		throw new OwlFeatureNotSupportedException("OWL datatypes currently not supported in rules.");
	}

	@Override
	public void visit(OWLDataHasValue ce) {
		throw new OwlFeatureNotSupportedException("OWL datatypes currently not supported in rules.");
	}

	@Override
	public void visit(OWLDataMinCardinality ce) {
		throw new OwlFeatureNotSupportedException("OWL datatypes currently not supported in rules.");
	}

	@Override
	public void visit(OWLDataExactCardinality ce) {
		throw new OwlFeatureNotSupportedException("OWL datatypes currently not supported in rules.");
	}

	@Override
	public void visit(OWLDataMaxCardinality ce) {
		throw new OwlFeatureNotSupportedException("OWL datatypes currently not supported in rules.");
	}

	/**
	 * Adds a binary predicate for a given OWL object property expression to the
	 * body. If the expression is an inverse, source and target terms are swapped.
	 * If the expression is top or bottom, it is handled appropriately.
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
	void addConjunctForPropertyExpression(OWLObjectPropertyExpression owlObjectPropertyExpression, Term sourceTerm,
			Term targetTerm) {
		if (owlObjectPropertyExpression.isOWLTopObjectProperty()) {
			// irrelevant in body; omit
		} else if (owlObjectPropertyExpression.isOWLBottomObjectProperty()) {
			this.unsatisfiable = true;
		} else {
			if (owlObjectPropertyExpression.isAnonymous()) {
				Predicate predicate = OwlToRulesConversionHelper.getObjectPropertyPredicate(
						owlObjectPropertyExpression.getInverseProperty().asOWLObjectProperty());
				bodyConjuncts.add(new AtomImpl(predicate, Arrays.asList(targetTerm, sourceTerm)));
			} else {
				Predicate predicate = OwlToRulesConversionHelper
						.getObjectPropertyPredicate(owlObjectPropertyExpression.asOWLObjectProperty());
				bodyConjuncts.add(new AtomImpl(predicate, Arrays.asList(sourceTerm, targetTerm)));
			}
		}
	}

}
