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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClass;
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
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
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
import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.TermType;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.ConjunctionImpl;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.model.implementation.FactImpl;
import org.semanticweb.rulewerk.core.model.implementation.PositiveLiteralImpl;
import org.semanticweb.rulewerk.core.model.implementation.RuleImpl;
import org.semanticweb.rulewerk.core.model.implementation.UniversalVariableImpl;

/**
 * Class for converting OWL axioms to rules.
 *
 * @author Markus Kroetzsch
 *
 */
public class OwlAxiomToRulesConverter implements OWLAxiomVisitor {

	static OWLDataFactory owlDataFactory = OWLManager.getOWLDataFactory();

	final ConverterTermFactory termFactory = new ConverterTermFactory();

	final Set<Rule> rules = new HashSet<>();
	final Set<Fact> facts = new HashSet<>();

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
		if (!converter.isTautology()) {
			final Conjunction<PositiveLiteral> headConjunction = this.constructHeadConjunction(converter);

			if (converter.body.isTrueOrEmpty() && (headConjunction.getVariables().count() == 0)) {
				for (final PositiveLiteral conjunct : headConjunction.getLiterals()) {
					this.facts.add(new FactImpl(conjunct.getPredicate(), conjunct.getArguments()));
				}
			} else {
				final Conjunction<PositiveLiteral> bodyConjunction = this.constructBodyConjunction(converter);
				this.rules.add(Expressions.makePositiveLiteralsRule(headConjunction, bodyConjunction));
			}
		}
	}

	private Conjunction<PositiveLiteral> constructBodyConjunction(final AbstractClassToRuleConverter converter) {
		if (converter.body.isTrueOrEmpty()) {
			return new ConjunctionImpl<>(Arrays.asList(OwlToRulesConversionHelper.getTop(converter.mainTerm)));
		} else {
			return new ConjunctionImpl<>(converter.body.getConjuncts());
		}
	}

	private Conjunction<PositiveLiteral> constructHeadConjunction(final AbstractClassToRuleConverter converter) {
		if (converter.head.isFalseOrEmpty()) {
			return new ConjunctionImpl<>(Arrays.asList(OwlToRulesConversionHelper.getBottom(converter.mainTerm)));
		} else {
			return new ConjunctionImpl<>(converter.head.getConjuncts());
		}
	}

	Term replaceTerm(Term term, Term oldTerm, Term newTerm) {
		return term.equals(oldTerm) ? newTerm : term;
	}

	PositiveLiteralImpl makeTermReplacedLiteral(Literal literal, Term oldTerm, Term newTerm) {
		if (literal.isNegated()) {
			throw new OwlFeatureNotSupportedException(
					"Nonmonotonic negation of literals is not handled in OWL conversion.");
		}
		return new PositiveLiteralImpl(literal.getPredicate(),
				literal.getTerms().map(term -> this.replaceTerm(term, oldTerm, newTerm)).collect(Collectors.toList()));
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
			body.forEach(literal -> newBody.add(this.makeTermReplacedLiteral(literal, auxTerm, newVariable)));
			head.forEach(literal -> newHead.add(this.makeTermReplacedLiteral(literal, auxTerm, newVariable)));
			this.rules.add(new RuleImpl(new ConjunctionImpl<>(newHead), new ConjunctionImpl<>(newBody)));
		} else {
			this.rules.add(new RuleImpl(new ConjunctionImpl<>(head), new ConjunctionImpl<>(body)));
		}

	}

	/**
	 * Resets the internal counter used for generating fresh variables.
	 */
	void startAxiomConversion() {
		// this.freshVariableCounter = 0;
		this.termFactory.resetFreshVariableCounter();
	}

	/**
	 * Processes an OWL class inclusion axiom with the two class expressions as
	 * given, and adds the resulting rules. The method proceeds by first converting
	 * the superclass, then converting the subclass with the same body and head atom
	 * buffers, and finally creating a rule from the collected body and head. The
	 * conversions may lead to auxiliary rules being created during processing, so
	 * additional rules besides the one that is added here might be created.
	 *
	 * @param subClass
	 * @param superClass
	 */
	void addSubClassAxiom(final OWLClassExpression subClass, final OWLClassExpression superClass) {
		if (subClass instanceof OWLObjectOneOf) {
			final OWLObjectOneOf subClassObjectOneOf = (OWLObjectOneOf) subClass;
			subClassObjectOneOf.individuals()
					.forEach(individual -> this.visitClassAssertionAxiom(individual, superClass));
		} else {
			this.startAxiomConversion();

			final ClassToRuleHeadConverter headConverter = new ClassToRuleHeadConverter(
					this.termFactory.frontierVariable, this);
			superClass.accept(headConverter);
			final ClassToRuleBodyConverter bodyConverter = new ClassToRuleBodyConverter(
					this.termFactory.frontierVariable, headConverter.body, headConverter.head, this);
			bodyConverter.handleDisjunction(subClass, this.termFactory.frontierVariable);
			this.addRule(bodyConverter);
		}
	}

	@Override
	public void visit(final OWLSubClassOfAxiom axiom) {
		this.addSubClassAxiom(axiom.getSubClass(), axiom.getSuperClass());
	}

	@Override
	public void visit(final OWLNegativeObjectPropertyAssertionAxiom axiom) {
		final Term subject = OwlToRulesConversionHelper.getIndividualTerm(axiom.getSubject(), this.termFactory);
		final Term object = OwlToRulesConversionHelper.getIndividualTerm(axiom.getObject(), this.termFactory);
		final Literal atom = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getProperty(), subject, object);
		final PositiveLiteral bot = OwlToRulesConversionHelper.getBottom(subject);
		this.rules.add(Expressions.makeRule(bot, atom));
	}

	@Override
	public void visit(final OWLAsymmetricObjectPropertyAxiom axiom) {
		this.startAxiomConversion();
		final Variable secondVariable = this.termFactory.getFreshUniversalVariable();
		final Literal atom1 = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getProperty(),
				this.termFactory.frontierVariable, secondVariable);
		final Literal atom2 = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getProperty(), secondVariable,
				this.termFactory.frontierVariable);
		this.rules.add(Expressions.makeRule(OwlToRulesConversionHelper.getBottom(this.termFactory.frontierVariable),
				atom1, atom2));
	}

	@Override
	public void visit(final OWLReflexiveObjectPropertyAxiom axiom) {
		final PositiveLiteral atom1 = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getProperty(),
				this.termFactory.frontierVariable, this.termFactory.frontierVariable);
		this.rules
				.add(Expressions.makeRule(atom1, OwlToRulesConversionHelper.getTop(this.termFactory.frontierVariable)));
	}

	@Override
	public void visit(final OWLDisjointClassesAxiom axiom) {
		final List<OWLClassExpression> disjointClassExpressions = axiom.operands().collect(Collectors.toList());
		if (disjointClassExpressions.size() < 2) {
			throw new IllegalArgumentException(
					"OWLDisjointClassesAxiom " + axiom + " expected to have at least 2 operands!");
		}

		while (disjointClassExpressions.size() > 2) {
			final OWLClassExpression a = this.removeFirst(disjointClassExpressions);
			final OWLClassExpression b = this.removeFirst(disjointClassExpressions);

			final OWLClass disjunctionAB = this.disjointClassExpressionsToSubClassOfAuxiliaryDisjunction(a, b);
			disjointClassExpressions.add(disjunctionAB);
		}

		final OWLObjectIntersectionOf disjointIntersection = owlDataFactory
				.getOWLObjectIntersectionOf(disjointClassExpressions.get(0), disjointClassExpressions.get(1));
		this.addSubClassAxiom(disjointIntersection, owlDataFactory.getOWLNothing());
	}

	private <T> T removeFirst(final List<? extends T> list) {
		final T t = list.get(0);
		list.remove(t);
		return t;
	}

	private OWLClass disjointClassExpressionsToSubClassOfAuxiliaryDisjunction(final OWLClassExpression a,
			final OWLClassExpression b) {
		this.addSubClassAxiom(owlDataFactory.getOWLObjectIntersectionOf(a, b), owlDataFactory.getOWLNothing());

		final OWLClass auxiliaryClass = owlDataFactory
				.getOWLClass(OwlToRulesConversionHelper.getAuxiliaryClassNameDisjuncts((Arrays.asList(a, b))));
		this.addSubClassAxiom(a, auxiliaryClass);
		this.addSubClassAxiom(b, auxiliaryClass);

		return auxiliaryClass;
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
		final Variable secondVariable = this.termFactory.getFreshUniversalVariable();

		PositiveLiteral firstAtom = null;
		Literal previousAtom = null;
		PositiveLiteral currentAtom = null;

		for (final OWLObjectPropertyExpression owlObjectPropertyExpression : axiom.properties()
				.collect(Collectors.toList())) {
			currentAtom = OwlToRulesConversionHelper.getObjectPropertyAtom(owlObjectPropertyExpression,
					this.termFactory.frontierVariable, secondVariable);
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
		// FIXME How should we interpret axiom with 1 or 0 operands?
		if (axiom.operands().count() < 2) {
			throw new OwlFeatureNotSupportedException(
					"OWLDisjointObjectPropertiesAxiom " + axiom + " only supported for 2 or more operands!");
		}
		final Term sourceTerm = this.termFactory.frontierVariable;
		final Term targetTerm = this.termFactory.getFreshUniversalVariable();

		if (axiom.operands().anyMatch(prop -> prop.isOWLTopObjectProperty())) {
			this.toUnsatisfiableObjectProperties(axiom, sourceTerm, targetTerm);
		} else {

			final List<OWLObjectPropertyExpression> disjointPropertyExpressions = axiom.operands()
					.collect(Collectors.toList());

			while (disjointPropertyExpressions.size() > 2) {
				final OWLObjectPropertyExpression a = this.removeFirst(disjointPropertyExpressions);
				final OWLObjectPropertyExpression b = this.removeFirst(disjointPropertyExpressions);
				final OWLObjectProperty disjunctionAB = owlDataFactory.getOWLObjectProperty(
						OwlToRulesConversionHelper.getAuxiliaryPropertyNameDisjuncts(Arrays.asList(a, b)));

				final PositiveLiteral literalA = OwlToRulesConversionHelper.getObjectPropertyAtom(a, sourceTerm,
						targetTerm);
				final PositiveLiteral literalB = OwlToRulesConversionHelper.getObjectPropertyAtom(b, sourceTerm,
						targetTerm);
				this.addUnsatisfiableRule(Expressions.makeConjunction(literalA, literalB), sourceTerm);

				final PositiveLiteral disjunctionABLiteral = OwlToRulesConversionHelper
						.getObjectPropertyAtom(disjunctionAB, sourceTerm, targetTerm);
				this.rules.add(Expressions.makeRule(disjunctionABLiteral, literalA));
				this.rules.add(Expressions.makeRule(disjunctionABLiteral, literalB));

				disjointPropertyExpressions.add(disjunctionAB);
			}

			final PositiveLiteral literalA = OwlToRulesConversionHelper
					.getObjectPropertyAtom(disjointPropertyExpressions.get(0), sourceTerm, targetTerm);
			final PositiveLiteral literalB = OwlToRulesConversionHelper
					.getObjectPropertyAtom(disjointPropertyExpressions.get(1), sourceTerm, targetTerm);
			this.addUnsatisfiableRule(Expressions.makeConjunction(literalA, literalB), sourceTerm);
		}
	}

	private void toUnsatisfiableObjectProperties(final OWLDisjointObjectPropertiesAxiom axiom, final Term sourceTerm,
			final Term targetTerm) {
		axiom.operands().forEach(prop -> {
			if (!prop.isOWLTopObjectProperty()) {
				final Literal propertyLiteral = OwlToRulesConversionHelper.getObjectPropertyAtom(prop, sourceTerm,
						targetTerm);
				this.addUnsatisfiableRule(Expressions.makeConjunction(propertyLiteral), sourceTerm);
			}
		});
	}

	void addUnsatisfiableRule(final Conjunction<Literal> body, final Term term) {
		final Rule ruleConjunctionUnsatisfiable = Expressions
				.makeRule(Expressions.makePositiveConjunction(OwlToRulesConversionHelper.getBottom(term)), body);
		this.rules.add(ruleConjunctionUnsatisfiable);
	}

	@Override
	public void visit(final OWLObjectPropertyRangeAxiom axiom) {
		this.startAxiomConversion();
		final OWLClassExpression forallPropertyDomain = owlDataFactory.getOWLObjectAllValuesFrom(axiom.getProperty(),
				axiom.getRange());
		final ClassToRuleHeadConverter headConverter = new ClassToRuleHeadConverter(this.termFactory.frontierVariable,
				this);
		forallPropertyDomain.accept(headConverter);
		this.addRule(headConverter);
	}

	@Override
	public void visit(final OWLObjectPropertyAssertionAxiom axiom) {
		final Term subject = OwlToRulesConversionHelper.getIndividualTerm(axiom.getSubject(), this.termFactory);
		final Term object = OwlToRulesConversionHelper.getIndividualTerm(axiom.getObject(), this.termFactory);
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
		final Variable secondVariable = this.termFactory.getFreshUniversalVariable();
		final Literal subRole = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getSubProperty(),
				this.termFactory.frontierVariable, secondVariable);
		final PositiveLiteral superRole = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getSuperProperty(),
				this.termFactory.frontierVariable, secondVariable);

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
		final Variable secondVariable = this.termFactory.getFreshUniversalVariable();
		final Literal atom1 = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getProperty(),
				this.termFactory.frontierVariable, secondVariable);
		final PositiveLiteral atom2 = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getProperty(),
				secondVariable, this.termFactory.frontierVariable);

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
		this.visitClassAssertionAxiom(axiom.getIndividual(), axiom.getClassExpression());
	}

	void visitClassAssertionAxiom(final OWLIndividual individual, final OWLClassExpression classExpression) {
		this.startAxiomConversion();
		final Term term = OwlToRulesConversionHelper.getIndividualTerm(individual, this.termFactory);
		final ClassToRuleHeadConverter headConverter = new ClassToRuleHeadConverter(term, this);
		classExpression.accept(headConverter);
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
		final Variable var1 = this.termFactory.getFreshUniversalVariable();
		final Variable var2 = this.termFactory.getFreshUniversalVariable();
		final Literal atom1 = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getProperty(),
				this.termFactory.frontierVariable, var1);
		final Literal atom2 = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getProperty(), var1, var2);
		final PositiveLiteral atomHead = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getProperty(),
				this.termFactory.frontierVariable, var2);

		this.rules.add(Expressions.makeRule(atomHead, atom1, atom2));
	}

	@Override
	public void visit(final OWLIrreflexiveObjectPropertyAxiom axiom) {
		final Literal atomSelf = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getProperty(),
				this.termFactory.frontierVariable, this.termFactory.frontierVariable);
		this.rules.add(Expressions.makeRule(OwlToRulesConversionHelper.getBottom(this.termFactory.frontierVariable),
				atomSelf));
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
		Variable previousVariable = this.termFactory.frontierVariable;
		Variable currentVariable = null;
		final List<Literal> body = new ArrayList<>();

		for (final OWLObjectPropertyExpression owlObjectPropertyExpression : axiom.getPropertyChain()) {
			currentVariable = this.termFactory.getFreshUniversalVariable();
			body.add(OwlToRulesConversionHelper.getObjectPropertyAtom(owlObjectPropertyExpression, previousVariable,
					currentVariable));
			previousVariable = currentVariable;
		}

		final PositiveLiteral headAtom = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getSuperProperty(),
				this.termFactory.frontierVariable, currentVariable);

		this.rules.add(
				Expressions.makeRule(Expressions.makePositiveConjunction(headAtom), Expressions.makeConjunction(body)));
	}

	@Override
	public void visit(final OWLInverseObjectPropertiesAxiom axiom) {
		this.startAxiomConversion();
		final Variable secondVariable = this.termFactory.getFreshUniversalVariable();
		final PositiveLiteral firstRole = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getFirstProperty(),
				this.termFactory.frontierVariable, secondVariable);
		final PositiveLiteral secondRole = OwlToRulesConversionHelper.getObjectPropertyAtom(axiom.getSecondProperty(),
				secondVariable, this.termFactory.frontierVariable);

		this.rules.add(Expressions.makeRule(secondRole, firstRole));
		this.rules.add(Expressions.makeRule(firstRole, secondRole));
	}

	@Override
	public void visit(final OWLHasKeyAxiom axiom) {
		throw new OwlFeatureNotSupportedException("HasKey currently not supported, due to lack of equality support.");
	}

	@Override
	public void visit(final SWRLRule rule) {
		throw new OwlFeatureNotSupportedException("SWRLRule currently not supported.");

	}

	/**
	 * Changes the renaming function for blank node IDs. Blank nodes with the same
	 * local ID will be represented differently before and after this function is
	 * called, but will retain a constant interpretation otherwise.
	 */
	public void startNewBlankNodeContext() {
		this.termFactory.startNewBlankNodeContext();
	}

}
