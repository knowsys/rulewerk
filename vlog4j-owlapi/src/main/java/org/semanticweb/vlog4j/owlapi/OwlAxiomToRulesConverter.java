package org.semanticweb.vlog4j.owlapi;

import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
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
import org.semanticweb.vlog4j.core.model.api.ExistentialVariable;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.Literal;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.TermType;
import org.semanticweb.vlog4j.core.model.api.UniversalVariable;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.ConjunctionImpl;
import org.semanticweb.vlog4j.core.model.implementation.ExistentialVariableImpl;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.model.implementation.FactImpl;
import org.semanticweb.vlog4j.core.model.implementation.PositiveLiteralImpl;
import org.semanticweb.vlog4j.core.model.implementation.RuleImpl;
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
	final Variable frontierVariable2 = new UniversalVariableImpl("Y");
	int freshVariableCounter = 0;

	/**
	 * Returns a fresh universal variable, which can be used as auxiliary variable
	 * in the current axiom's translation.
	 *
	 * @return a variable
	 */
	UniversalVariable getFreshUniversalVariable() {
		this.freshVariableCounter++;
		return new UniversalVariableImpl("Z" + this.freshVariableCounter);
	}

	/**
	 * Returns a fresh existential variable, which can be used as auxiliary variable
	 * in the current axiom's translation.
	 *
	 * @return a variable
	 */
	ExistentialVariable getFreshExistentialVariable() {
		this.freshVariableCounter++;
		return new ExistentialVariableImpl("W" + this.freshVariableCounter);
	}

	/**
	 * Resets the internal counter used for generating fresh variables.
	 * 
	 * @return
	 */
	private void startAxiomConversion() {
		this.freshVariableCounter = 0;
	}

	/**
	 * Processes the output of an {@link AbstractClassToRuleConverter} and
	 * transforms it into a statement that is added. Tautologies are not added but
	 * simply dropped. Formulas that have only positive atoms (empty body) are
	 * transformed into one or more facts. All other cases lead to a single rule
	 * being added.
	 * 
	 * @param converter
	 */
	void addRule(final AbstractClassToRuleConverter converter) {
		if (converter.isTautology()) {
			return;
		}
		Conjunction<PositiveLiteral> headConjunction;
		if (converter.head.isFalseOrEmpty()) {
			headConjunction = new ConjunctionImpl<>(
					Arrays.asList(OwlToRulesConversionHelper.getBottomAtom(converter.mainTerm)));
		} else {
			headConjunction = new ConjunctionImpl<>(converter.head.getConjuncts());
		}

		Conjunction<PositiveLiteral> bodyConjunction;
		if (converter.body.isTrueOrEmpty()) {
			bodyConjunction = new ConjunctionImpl<>(
					Arrays.asList(OwlToRulesConversionHelper.getTopAtom(converter.mainTerm)));
			if (headConjunction.getVariables().count() == 0) {
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

	Term replaceTerm(Term term, Term oldTerm, Term newTerm) {
		return term.equals(oldTerm) ? newTerm : term;
	}

	PositiveLiteralImpl makeTermReplacedLiteral(Literal literal, Term oldTerm, Term newTerm) {
		if (literal.isNegated()) {
			throw new RuntimeException("Nonmonotonic negation of literals is not handled in OWL conversion.");
		} else {
			return new PositiveLiteralImpl(literal.getPredicate(),
					literal.getTerms().map(term -> replaceTerm(term, oldTerm, newTerm)).collect(Collectors.toList()));
		}
	}

	/**
	 * Creates and adds an auxiliary rule for the given body and head. All auxiliary
	 * rules are renamings of class expressions, based on auxiliary class names
	 * (unary predicates). The given term is the term used in this auxiliary
	 * predicate.
	 * 
	 * Variables used in auxiliary atoms can be existentially quantified, but the
	 * corresponding variable in auxiliary rules must always be universally
	 * quantified. Therefore, if the given term is an existential variable, the
	 * method will replace it by a universal one of the same name.
	 * 
	 * @param head
	 * @param body
	 * @param auxTerm
	 */
	void addAuxiliaryRule(List<PositiveLiteral> head, List<? extends Literal> body, Term auxTerm) {
		if (auxTerm.getType() == TermType.EXISTENTIAL_VARIABLE) {
			Term newVariable = new UniversalVariableImpl(auxTerm.getName());
			List<Literal> newBody = new ArrayList<>();
			List<PositiveLiteral> newHead = new ArrayList<>();
			body.forEach(literal -> newBody.add(makeTermReplacedLiteral(literal, auxTerm, newVariable)));
			head.forEach(literal -> newHead.add(makeTermReplacedLiteral(literal, auxTerm, newVariable)));
			this.rules.add(new RuleImpl(new ConjunctionImpl<>(newHead), new ConjunctionImpl<>(newBody)));
		} else {
			this.rules.add(new RuleImpl(new ConjunctionImpl<>(head), new ConjunctionImpl<>(body)));
		}

	}

	/**
	 * Processes an OWL class inclusion axiom with the two class expressions as
	 * give, and adds the resulting rules. The method proceeds by first converting
	 * the superclass, then converting the subclass with the same body and head atom
	 * buffers, and finally creating a rule from the collected body and head. The
	 * conversions may lead to auxiliary rules being created during processing, so
	 * additional rules besides the one that is added here might be created.
	 * 
	 * @param subClass
	 * @param superClass
	 */
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
	public void visit(final OWLEquivalentClassesAxiom axiom) {
		List<OWLClassExpression> equivalentClasses = axiom.classExpressions().collect(Collectors.toList());
		if (equivalentClasses.size() > 1) {
			int indexOfLast = equivalentClasses.size() - 1;
			for (int i = 0; i < indexOfLast; i++) {
				this.addSubClassAxiom(equivalentClasses.get(i), equivalentClasses.get(i + 1));
			}
			this.addSubClassAxiom(equivalentClasses.get(indexOfLast), equivalentClasses.get(0));
		}
	}

	@Override
	public void visit(final OWLDisjointClassesAxiom axiom) {
		List<OWLClassExpression> pairwiseDisjointClasses = axiom.classExpressions().collect(Collectors.toList());
		for (int i = 0; i < pairwiseDisjointClasses.size(); i++) {
			for (int j = i + 1; j < pairwiseDisjointClasses.size(); j++) {
				Stream<OWLClassExpression> conjuncts = Stream.of(pairwiseDisjointClasses.get(i),
						pairwiseDisjointClasses.get(j));
				OWLObjectIntersectionOf conjunction = owlDataFactory.getOWLObjectIntersectionOf(conjuncts);
				this.addSubClassAxiom(conjunction, owlDataFactory.getOWLNothing());
			}
		}
	}

	@Override
	public void visit(final OWLObjectPropertyDomainAxiom axiom) {
		final OWLClassExpression existsObjectSubclass = owlDataFactory.getOWLObjectSomeValuesFrom(axiom.getProperty(),
				owlDataFactory.getOWLThing());
		this.addSubClassAxiom(existsObjectSubclass, axiom.getDomain());
	}

	@Override
	public void visit(final OWLDataPropertyDomainAxiom axiom) {
		final OWLClassExpression existsDataSubClass = owlDataFactory.getOWLDataSomeValuesFrom(axiom.getProperty(),
				owlDataFactory.getTopDatatype());
		this.addSubClassAxiom(existsDataSubClass, axiom.getDomain());
	}

	@Override
	public void visit(final OWLObjectPropertyRangeAxiom axiom) {
		final OWLClassExpression forallObjectSuperclass = owlDataFactory.getOWLObjectAllValuesFrom(axiom.getProperty(),
				axiom.getRange());
		this.addSubClassAxiom(owlDataFactory.getOWLThing(), forallObjectSuperclass);
	}

	@Override
	public void visit(final OWLDataPropertyRangeAxiom axiom) {
		final OWLClassExpression forallObjectSuperclass = owlDataFactory.getOWLDataAllValuesFrom(axiom.getProperty(),
				axiom.getRange());
		this.addSubClassAxiom(owlDataFactory.getOWLThing(), forallObjectSuperclass);
	}

	@Override
	public void visit(final OWLReflexiveObjectPropertyAxiom axiom) {
		final OWLClassExpression hasSelfObjectSuperclass = owlDataFactory.getOWLObjectHasSelf(axiom.getProperty());
		this.addSubClassAxiom(owlDataFactory.getOWLThing(), hasSelfObjectSuperclass);
	}

	@Override
	public void visit(final OWLIrreflexiveObjectPropertyAxiom axiom) {
		final OWLClassExpression hasSelfObjectSubclass = owlDataFactory.getOWLObjectHasSelf(axiom.getProperty());
		this.addSubClassAxiom(hasSelfObjectSubclass, owlDataFactory.getOWLNothing());
	}

	@Override
	public void visit(final OWLFunctionalObjectPropertyAxiom axiom) {
		throw new OwlFeatureNotSupportedException(
				"OWLFunctionalObjectPropertyAxiom currently not supported, due to lack of equality support.");
	}

	@Override
	public void visit(final OWLFunctionalDataPropertyAxiom axiom) {
		throw new OwlFeatureNotSupportedException(
				"OWLFunctionalDataPropertyAxiom currently not supported, due to lack of equality support.");
	}

	@Override
	public void visit(final OWLInverseFunctionalObjectPropertyAxiom axiom) {
		throw new OwlFeatureNotSupportedException(
				"OWLInverseFunctionalObjectPropertyAxiom currently not supported, due to lack of equality support.");
	}

	@Override
	public void visit(final OWLHasKeyAxiom axiom) {
		throw new OwlFeatureNotSupportedException(
				"OWLHasKeyAxiom currently not supported, due to lack of equality support.");
	}

	@Override
	public void visit(final OWLDisjointUnionAxiom axiom) {
		throw new OwlFeatureNotSupportedException(
				"OWLDisjointUnionAxiom not supported, since the cases where it would be expressible in disjunction-free rules are not useful.");
	}

	// RBox Axioms
	void addSubroleAxiom(final OWLPropertyExpression subrole, final OWLPropertyExpression superrole) {
		final Literal subroleAtom = OwlToRulesConversionHelper.getPropertyAtom(subrole, this.frontierVariable,
				this.frontierVariable2);
		final PositiveLiteral superroleAtom = OwlToRulesConversionHelper.getPropertyAtom(superrole,
				this.frontierVariable, this.frontierVariable2);
		this.rules.add(Expressions.makeRule(superroleAtom, subroleAtom));
	}

	@Override
	public void visit(final OWLSubObjectPropertyOfAxiom axiom) {
		this.addSubroleAxiom(axiom.getSubProperty(), axiom.getSuperProperty());
	}

	@Override
	public void visit(final OWLSubDataPropertyOfAxiom axiom) {
		this.addSubroleAxiom(axiom.getSubProperty(), axiom.getSuperProperty());
	}

	@Override
	public void visit(final OWLInverseObjectPropertiesAxiom axiom) {
		this.addSubroleAxiom(axiom.getFirstProperty(), axiom.getSecondProperty().getInverseProperty());
		this.addSubroleAxiom(axiom.getSecondProperty().getInverseProperty(), axiom.getFirstProperty());
	}

	@Override
	public void visit(final OWLSymmetricObjectPropertyAxiom axiom) {
		this.addSubroleAxiom(axiom.getProperty().getInverseProperty(), axiom.getProperty());
	}

	@Override
	public void visit(final OWLEquivalentDataPropertiesAxiom axiom) {
		createRulesEquivalentPropertiesAxiom(axiom.properties().collect(Collectors.toList()));
	}

	@Override
	public void visit(final OWLEquivalentObjectPropertiesAxiom axiom) {
		createRulesEquivalentPropertiesAxiom(axiom.properties().collect(Collectors.toList()));
	}

	private void createRulesEquivalentPropertiesAxiom(final List<OWLPropertyExpression> equivalentProperties) {
		if (equivalentProperties.size() > 1) {
			int indexOfLast = equivalentProperties.size() - 1;
			for (int i = 0; i < indexOfLast; i++) {
				this.addSubroleAxiom(equivalentProperties.get(i), equivalentProperties.get(i + 1));
			}
			this.addSubroleAxiom(equivalentProperties.get(indexOfLast), equivalentProperties.get(0));
		}
	}

	@Override
	public void visit(final OWLDisjointDataPropertiesAxiom axiom) {
		createRulesDisjointPropertiesAxiom(axiom.properties().collect(Collectors.toList()));
	}

	@Override
	public void visit(final OWLDisjointObjectPropertiesAxiom axiom) {
		createRulesDisjointPropertiesAxiom(axiom.properties().collect(Collectors.toList()));
	}

	@Override
	public void visit(final OWLAsymmetricObjectPropertyAxiom axiom) {
		List<OWLPropertyExpression> disjointProperties = new ArrayList<OWLPropertyExpression>();
		disjointProperties.add(axiom.getProperty());
		disjointProperties.add(axiom.getProperty().getInverseProperty());
		createRulesDisjointPropertiesAxiom(disjointProperties);
	}

	private void createRulesDisjointPropertiesAxiom(final List<OWLPropertyExpression> disjointPairwiseProperties) {
		final PositiveLiteral bottomLiteral = OwlToRulesConversionHelper.getBottomAtom(this.frontierVariable);

		for (int i = 0; i < disjointPairwiseProperties.size(); i++) {
			for (int j = i + 1; j < disjointPairwiseProperties.size(); j++) {
				OWLPropertyExpression firstDisjointProperty = disjointPairwiseProperties.get(i);
				OWLPropertyExpression secondDisjointProperty = disjointPairwiseProperties.get(j);
				PositiveLiteral firstBodyAtom = OwlToRulesConversionHelper.getPropertyAtom(firstDisjointProperty,
						this.frontierVariable, this.frontierVariable2);
				PositiveLiteral secondBodyAtom = OwlToRulesConversionHelper.getPropertyAtom(secondDisjointProperty,
						this.frontierVariable, this.frontierVariable2);
				this.rules.add(Expressions.makeRule(bottomLiteral, firstBodyAtom, secondBodyAtom));
			}
		}
	}

	@Override
	public void visit(final OWLSubPropertyChainOfAxiom axiom) {
		this.startAxiomConversion();
		Variable previousVariable = this.frontierVariable;
		Variable currentVariable = null;
		final List<Literal> body = new ArrayList<>();

		for (final OWLObjectPropertyExpression owlObjectPropertyExpression : axiom.getPropertyChain()) {
			currentVariable = this.getFreshUniversalVariable();
			body.add(OwlToRulesConversionHelper.getPropertyAtom(owlObjectPropertyExpression, previousVariable,
					currentVariable));
			previousVariable = currentVariable;
		}

		final PositiveLiteral headAtom = OwlToRulesConversionHelper.getPropertyAtom(axiom.getSuperProperty(),
				this.frontierVariable, currentVariable);

		this.rules.add(
				Expressions.makeRule(Expressions.makePositiveConjunction(headAtom), Expressions.makeConjunction(body)));
	}

	@Override
	public void visit(final OWLTransitiveObjectPropertyAxiom axiom) {
		this.startAxiomConversion();
		final Variable var1 = this.getFreshUniversalVariable();
		final Variable var2 = this.getFreshUniversalVariable();
		final Literal atom1 = OwlToRulesConversionHelper.getPropertyAtom(axiom.getProperty(), this.frontierVariable,
				var1);
		final Literal atom2 = OwlToRulesConversionHelper.getPropertyAtom(axiom.getProperty(), var1, var2);
		final PositiveLiteral atomHead = OwlToRulesConversionHelper.getPropertyAtom(axiom.getProperty(),
				this.frontierVariable, var2);

		this.rules.add(Expressions.makeRule(atomHead, atom1, atom2));
	}

	// SWRL Rules
	@Override
	public void visit(final SWRLRule rule) {
		// TODO support SWRL rules
	}

	// Assertions
	@Override
	public void visit(final OWLClassAssertionAxiom axiom) {
		this.startAxiomConversion();
		final Term term = OwlToRulesConversionHelper.getIndividualTerm(axiom.getIndividual());
		final ClassToRuleHeadConverter headConverter = new ClassToRuleHeadConverter(term, this);
		axiom.getClassExpression().accept(headConverter);
		this.addRule(headConverter);
	}

	@Override
	public void visit(final OWLObjectPropertyAssertionAxiom axiom) {
		final Term subject = OwlToRulesConversionHelper.getIndividualTerm(axiom.getSubject());
		final Term object = OwlToRulesConversionHelper.getIndividualTerm(axiom.getObject());
		this.facts.add(OwlToRulesConversionHelper.getPropertyFact(axiom.getProperty(), subject, object));
	}

	@Override
	public void visit(final OWLDataPropertyAssertionAxiom axiom) {
		final Term subject = OwlToRulesConversionHelper.getIndividualTerm(axiom.getSubject());
		final Term object = OwlToRulesConversionHelper.getLiteralTerm(axiom.getObject());
		this.facts.add(OwlToRulesConversionHelper.getPropertyFact(axiom.getProperty(), subject, object));
	}

	@Override
	public void visit(final OWLNegativeObjectPropertyAssertionAxiom axiom) {
		final Term subject = OwlToRulesConversionHelper.getIndividualTerm(axiom.getSubject());
		final Term object = OwlToRulesConversionHelper.getIndividualTerm(axiom.getObject());
		addNegativePropertyAssertion(axiom.getProperty(), subject, object);
	}

	@Override
	public void visit(final OWLNegativeDataPropertyAssertionAxiom axiom) {
		final Term subject = OwlToRulesConversionHelper.getIndividualTerm(axiom.getSubject());
		final Term object = OwlToRulesConversionHelper.getLiteralTerm(axiom.getObject());
		addNegativePropertyAssertion(axiom.getProperty(), subject, object);
	}

	private void addNegativePropertyAssertion(OWLPropertyExpression property, Term subject, Term object) {
		final Literal atom = OwlToRulesConversionHelper.getPropertyAtom(property, subject, object);
		final PositiveLiteral bot = OwlToRulesConversionHelper.getBottomAtom(subject);
		this.rules.add(Expressions.makeRule(bot, atom));
	}

	@Override
	public void visit(final OWLSameIndividualAxiom axiom) {
		throw new OwlFeatureNotSupportedException(
				"OWLSameIndividualAxiom currently not supported, due to lack of equality support.");
	}

	@Override
	public void visit(final OWLDifferentIndividualsAxiom axiom) {
		throw new OwlFeatureNotSupportedException(
				"OWLDifferentIndividualsAxiom currently not supported, due to lack of equality support.");
	}

}
