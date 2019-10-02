package org.semanticweb.vlog4j.owlapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.apibinding.OWLManager;

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

import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.Literal;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.ConjunctionImpl;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.model.implementation.FactImpl;
import org.semanticweb.vlog4j.core.model.implementation.UniversalVariableImpl;

/**
 * Class for converting OWL axioms to rules.
 *
 * @author Markus Kroetzsch
 *
 */
public class OwlAxiomToRulesConverter implements OWLAxiomVisitor {

	static OWLDataFactory owlDataFactory = OWLManager.getOWLDataFactory();

	final Set<Rule> rules = new HashSet<>();
	final Set<Fact> facts = new HashSet<>();
	final Variable frontierVariable = new UniversalVariableImpl("X");
	int freshVariableCounter = 0;

	/**
	 * Returns a fresh variable, which can be used as auxiliary variable in the
	 * current axiom's translation.
	 *
	 * @return a variable
	 */
	Variable getFreshVariable() {
		this.freshVariableCounter++;
		return new UniversalVariableImpl("Y" + this.freshVariableCounter);
	}

	void addRule(final AbstractClassToRuleConverter converter) {
		if (converter.isTautology()) {
			return;
		}
		Conjunction<PositiveLiteral> headConjunction;
		if (converter.head.isFalseOrEmpty()) {
			headConjunction = new ConjunctionImpl<>(
					Arrays.asList(OwlToRulesConversionHelper.getBottom(converter.mainTerm)));
		} else {
			headConjunction = new ConjunctionImpl<>(converter.head.getConjuncts());
		}

		Conjunction<PositiveLiteral> bodyConjunction;
		if (converter.body.isTrueOrEmpty()) {
			bodyConjunction = new ConjunctionImpl<>(
					Arrays.asList(OwlToRulesConversionHelper.getTop(converter.mainTerm)));
			if (headConjunction.getVariables().isEmpty()) {
				for (final PositiveLiteral conjunct : headConjunction.getLiterals()) {
					this.facts.add(new FactImpl(conjunct.getPredicate(), conjunct.getArguments()));
				}
				return;
			}
		} else {
			bodyConjunction = new ConjunctionImpl<>(converter.body.getConjuncts());
		}

		this.rules.add(Expressions.makePositiveLiteralsRule(headConjunction, bodyConjunction));
	}

	/**
	 * Resets the internal counter used for generating fresh variables.
	 */
	void startAxiomConversion() {
		this.freshVariableCounter = 0;
	}

	void addSubClassAxiom(final OWLClassExpression subClass, final OWLClassExpression superClass) {
		this.startAxiomConversion();

		final ClassToRuleHeadConverter headConverter = new ClassToRuleHeadConverter(this.frontierVariable, this);
		superClass.accept(headConverter);
		final ClassToRuleBodyConverter bodyConverter = new ClassToRuleBodyConverter(this.frontierVariable,
				headConverter.body, headConverter.head, this);
		bodyConverter.handleDisjunction(subClass, this.frontierVariable);
		this.addRule(bodyConverter);
	}

	@Override
	public void visit(final OWLSubClassOfAxiom axiom) {
		this.addSubClassAxiom(axiom.getSubClass(), axiom.getSuperClass());
	}

	@Override
	public void visit(final OWLNegativeObjectPropertyAssertionAxiom axiom) {
		final Term subject = OwlToRulesConversionHelper.getIndividualTerm(axiom.getSubject());
		final Term object = OwlToRulesConversionHelper.getIndividualTerm(axiom.getObject());
		final Literal atom = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getProperty(), subject, object);
		final PositiveLiteral bot = OwlToRulesConversionHelper.getBottom(subject);
		this.rules.add(Expressions.makeRule(bot, atom));
	}

	@Override
	public void visit(final OWLAsymmetricObjectPropertyAxiom axiom) {
		this.startAxiomConversion();
		final Variable secondVariable = this.getFreshVariable();
		final Literal atom1 = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getProperty(),
				this.frontierVariable, secondVariable);
		final Literal atom2 = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getProperty(), secondVariable,
				this.frontierVariable);
		this.rules.add(Expressions.makeRule(OwlToRulesConversionHelper.getBottom(this.frontierVariable), atom1, atom2));
	}

	@Override
	public void visit(final OWLReflexiveObjectPropertyAxiom axiom) {
		final PositiveLiteral atom1 = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getProperty(),
				this.frontierVariable, this.frontierVariable);
		this.rules.add(Expressions.makeRule(atom1, OwlToRulesConversionHelper.getTop(this.frontierVariable)));
	}

	@Override
	public void visit(final OWLDisjointClassesAxiom axiom) {
		// TODO Efficient implementation for lists of disjoint classes needed

	}

	@Override
	public void visit(final OWLDataPropertyDomainAxiom axiom) {
		throw new OwlFeatureNotSupportedException("OWL datatypes currently not supported in rules.");
	}

	@Override
	public void visit(final OWLObjectPropertyDomainAxiom axiom) {
		final OWLClassExpression existsProperty = owlDataFactory.getOWLObjectSomeValuesFrom(axiom.getProperty(),
				owlDataFactory.getOWLThing());
		this.addSubClassAxiom(existsProperty, axiom.getDomain());
	}

	@Override
	public void visit(final OWLEquivalentObjectPropertiesAxiom axiom) {
		this.startAxiomConversion();
		final Variable secondVariable = this.getFreshVariable();

		PositiveLiteral firstAtom = null;
		Literal previousAtom = null;
		PositiveLiteral currentAtom = null;

		for (final OWLObjectPropertyExpression owlObjectPropertyExpression : axiom.properties()
				.collect(Collectors.toList())) {
			currentAtom = OwlToRulesConversionHelper.getObjectPropertyAtom(owlObjectPropertyExpression,
					this.frontierVariable, secondVariable);
			if (previousAtom == null) {
				firstAtom = currentAtom;
			} else {
				this.rules.add(Expressions.makeRule(currentAtom, previousAtom));
			}
			previousAtom = currentAtom;
		}

		if (currentAtom != null) {
			this.rules.add(Expressions.makeRule(firstAtom, currentAtom));
		}
	}

	@Override
	public void visit(final OWLNegativeDataPropertyAssertionAxiom axiom) {
		throw new OwlFeatureNotSupportedException("OWL datatypes currently not supported in rules.");

	}

	@Override
	public void visit(final OWLDifferentIndividualsAxiom axiom) {
		throw new OwlFeatureNotSupportedException(
				"DifferentIndividuals currently not supported, due to lack of equality support.");
	}

	@Override
	public void visit(final OWLDisjointDataPropertiesAxiom axiom) {
		throw new OwlFeatureNotSupportedException("OWL datatypes currently not supported in rules.");
	}

	@Override
	public void visit(final OWLDisjointObjectPropertiesAxiom axiom) {
		// TODO Efficient implementation for lists of disjoint properties needed

	}

	@Override
	public void visit(final OWLObjectPropertyRangeAxiom axiom) {
		this.startAxiomConversion();
		final OWLClassExpression forallPropertyDomain = owlDataFactory.getOWLObjectAllValuesFrom(axiom.getProperty(),
				axiom.getRange());
		final ClassToRuleHeadConverter headConverter = new ClassToRuleHeadConverter(this.frontierVariable, this);
		forallPropertyDomain.accept(headConverter);
		this.addRule(headConverter);
	}

	@Override
	public void visit(final OWLObjectPropertyAssertionAxiom axiom) {
		final Term subject = OwlToRulesConversionHelper.getIndividualTerm(axiom.getSubject());
		final Term object = OwlToRulesConversionHelper.getIndividualTerm(axiom.getObject());
		this.facts.add(OwlToRulesConversionHelper.getObjectPropertyFact(axiom.getProperty(), subject, object));
	}

	@Override
	public void visit(final OWLFunctionalObjectPropertyAxiom axiom) {
		throw new OwlFeatureNotSupportedException(
				"FunctionalObjectProperty currently not supported, due to lack of equality support.");
	}

	@Override
	public void visit(final OWLSubObjectPropertyOfAxiom axiom) {
		this.startAxiomConversion();
		final Variable secondVariable = this.getFreshVariable();
		final Literal subRole = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getSubProperty(),
				this.frontierVariable, secondVariable);
		final PositiveLiteral superRole = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getSuperProperty(),
				this.frontierVariable, secondVariable);

		this.rules.add(Expressions.makeRule(superRole, subRole));
	}

	@Override
	public void visit(final OWLDisjointUnionAxiom axiom) {
		throw new OwlFeatureNotSupportedException(
				"OWL DisjointUnion not supported, since the cases where it would be expressible in disjunction-free rules are not useful.");
	}

	@Override
	public void visit(final OWLSymmetricObjectPropertyAxiom axiom) {
		this.startAxiomConversion();
		final Variable secondVariable = this.getFreshVariable();
		final Literal atom1 = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getProperty(),
				this.frontierVariable, secondVariable);
		final PositiveLiteral atom2 = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getProperty(),
				secondVariable, this.frontierVariable);

		this.rules.add(Expressions.makeRule(atom2, atom1));
	}

	@Override
	public void visit(final OWLDataPropertyRangeAxiom axiom) {
		throw new OwlFeatureNotSupportedException("OWL datatypes currently not supported in rules.");
	}

	@Override
	public void visit(final OWLFunctionalDataPropertyAxiom axiom) {
		throw new OwlFeatureNotSupportedException("OWL datatypes currently not supported in rules.");
	}

	@Override
	public void visit(final OWLEquivalentDataPropertiesAxiom axiom) {
		throw new OwlFeatureNotSupportedException("OWL datatypes currently not supported in rules.");
	}

	@Override
	public void visit(final OWLClassAssertionAxiom axiom) {
		this.startAxiomConversion();
		final Term term = OwlToRulesConversionHelper.getIndividualTerm(axiom.getIndividual());
		final ClassToRuleHeadConverter headConverter = new ClassToRuleHeadConverter(term, this);
		axiom.getClassExpression().accept(headConverter);
		this.addRule(headConverter);
	}

	@Override
	public void visit(final OWLEquivalentClassesAxiom axiom) {
		OWLClassExpression firstClass = null;
		OWLClassExpression previousClass = null;
		OWLClassExpression currentClass = null;
		for (final OWLClassExpression owlClassExpression : axiom.classExpressions().collect(Collectors.toList())) {
			currentClass = owlClassExpression;
			if (previousClass == null) {
				firstClass = currentClass;
			} else {
				this.addSubClassAxiom(previousClass, currentClass);
			}
			previousClass = currentClass;
		}

		if (currentClass != null) {
			this.addSubClassAxiom(currentClass, firstClass);
		}
	}

	@Override
	public void visit(final OWLDataPropertyAssertionAxiom axiom) {
		throw new OwlFeatureNotSupportedException("OWL datatypes currently not supported in rules.");
	}

	@Override
	public void visit(final OWLTransitiveObjectPropertyAxiom axiom) {
		this.startAxiomConversion();
		final Variable var1 = this.getFreshVariable();
		final Variable var2 = this.getFreshVariable();
		final Literal atom1 = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getProperty(),
				this.frontierVariable, var1);
		final Literal atom2 = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getProperty(), var1, var2);
		final PositiveLiteral atomHead = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getProperty(),
				this.frontierVariable, var2);

		this.rules.add(Expressions.makeRule(atomHead, atom1, atom2));
	}

	@Override
	public void visit(final OWLIrreflexiveObjectPropertyAxiom axiom) {
		final Literal atomSelf = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getProperty(),
				this.frontierVariable, this.frontierVariable);
		this.rules.add(Expressions.makeRule(OwlToRulesConversionHelper.getBottom(this.frontierVariable), atomSelf));
	}

	@Override
	public void visit(final OWLSubDataPropertyOfAxiom axiom) {
		throw new OwlFeatureNotSupportedException("OWL datatypes currently not supported in rules.");
	}

	@Override
	public void visit(final OWLInverseFunctionalObjectPropertyAxiom axiom) {
		throw new OwlFeatureNotSupportedException(
				"InverseFunctionalObjectProperty currently not supported, due to lack of equality support.");
	}

	@Override
	public void visit(final OWLSameIndividualAxiom axiom) {
		throw new OwlFeatureNotSupportedException(
				"SameIndividual currently not supported, due to lack of equality support.");
	}

	@Override
	public void visit(final OWLSubPropertyChainOfAxiom axiom) {
		this.startAxiomConversion();
		Variable previousVariable = this.frontierVariable;
		Variable currentVariable = null;
		final List<Literal> body = new ArrayList<>();

		for (final OWLObjectPropertyExpression owlObjectPropertyExpression : axiom.getPropertyChain()) {
			currentVariable = this.getFreshVariable();
			body.add(OwlToRulesConversionHelper.getObjectPropertyAtom(owlObjectPropertyExpression, previousVariable,
					currentVariable));
			previousVariable = currentVariable;
		}

		final PositiveLiteral headAtom = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getSuperProperty(),
				this.frontierVariable, currentVariable);

		this.rules.add(
				Expressions.makeRule(Expressions.makePositiveConjunction(headAtom), Expressions.makeConjunction(body)));
	}

	@Override
	public void visit(final OWLInverseObjectPropertiesAxiom axiom) {
		this.startAxiomConversion();
		final Variable secondVariable = this.getFreshVariable();
		final PositiveLiteral firstRole = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getFirstProperty(),
				this.frontierVariable, secondVariable);
		final PositiveLiteral secondRole = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getSecondProperty(),
				secondVariable, this.frontierVariable);

		this.rules.add(Expressions.makeRule(secondRole, firstRole));
		this.rules.add(Expressions.makeRule(firstRole, secondRole));
	}

	@Override
	public void visit(final OWLHasKeyAxiom axiom) {
		throw new OwlFeatureNotSupportedException("HasKey currently not supported, due to lack of equality support.");
	}

	@Override
	public void visit(final SWRLRule rule) {
		// TODO support SWRL rules

	}

}
