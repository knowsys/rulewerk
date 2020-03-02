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

import java.util.Arrays;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.OWLClass;
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
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.PositiveLiteralImpl;

/**
 * Helper class for transforming OWL class expressions that occur as subclasses
 * into suitable body literals for rules. Auxiliary rules might be created to
 * capture the semantics of some constructs.
 *
 * @author Markus Krötzsch
 *
 */
public class ClassToRuleBodyConverter extends AbstractClassToRuleConverter implements OWLClassExpressionVisitor {

	public ClassToRuleBodyConverter(final Term mainTerm, final SimpleConjunction body, final SimpleConjunction head,
			final OwlAxiomToRulesConverter parent) {
		super(mainTerm, body, head, parent);
	}

	public ClassToRuleBodyConverter(final Term mainTerm, final OwlAxiomToRulesConverter parent) {
		this(mainTerm, new SimpleConjunction(), new SimpleConjunction(), parent);
	}

	@Override
	public AbstractClassToRuleConverter makeChildConverter(final Term mainTerm) {
		return new ClassToRuleBodyConverter(mainTerm, this.parent);
	}

	@Override
	public void visit(final OWLClass ce) {
		if (ce.isOWLNothing()) {
			this.body.makeFalse();
		} else if (ce.isOWLThing()) {
			this.body.init();
		} else {
			final Predicate predicate = OwlToRulesConversionHelper.getClassPredicate(ce);
			this.body.add(new PositiveLiteralImpl(predicate, Arrays.asList(this.mainTerm)));
		}
	}

	@Override
	public void visit(final OWLObjectIntersectionOf ce) {
		this.handleDisjunction(ce.operands().collect(Collectors.toList()));
	}

	@Override
	public void visit(final OWLObjectUnionOf ce) {
		this.handleConjunction(ce.operands().collect(Collectors.toList()), this.mainTerm);
	}

	@Override
	public void visit(final OWLObjectComplementOf ce) {
		final ClassToRuleHeadConverter converter = new ClassToRuleHeadConverter(this.mainTerm, this.body, this.head,
				this.parent);
		ce.getOperand().accept(converter);
	}

	@Override
	public void visit(final OWLObjectSomeValuesFrom ce) {
		this.handleObjectAllValues(ce.getProperty(), ce.getFiller());
	}

	@Override
	public void visit(final OWLObjectAllValuesFrom ce) {
		final Variable variable = this.parent.getFreshExistentialVariable();
		OwlToRulesConversionHelper.addConjunctForPropertyExpression(ce.getProperty(), this.mainTerm, variable,
				this.head);
		if (!this.head.isFalse()) {
			this.handleConjunction(Arrays.asList(ce.getFiller()), variable);
		}
	}

	@Override
	public void visit(final OWLObjectHasValue ce) {
		final Term term = OwlToRulesConversionHelper.getIndividualTerm(ce.getFiller(), parent.skolemization);
		OwlToRulesConversionHelper.addConjunctForPropertyExpression(ce.getProperty(), this.mainTerm, term, this.body);
	}

	@Override
	public void visit(final OWLObjectMinCardinality ce) {
		if (ce.getCardinality() == 0) {
			this.body.init(); // tautological
		} else if (ce.getCardinality() == 1) {
			this.handleObjectAllValues(ce.getProperty(), ce.getFiller());
		} else {
			throw new OwlFeatureNotSupportedException(
					"Min cardinality restrictions with values greater than 1 in subclass positions are not supported in rules.");
		}
	}

	@Override
	public void visit(final OWLObjectExactCardinality ce) {
		throw new OwlFeatureNotSupportedException(
				"Exact cardinality restrictions in subclass positions are not supported in rules.");
	}

	@Override
	public void visit(final OWLObjectMaxCardinality ce) {
		throw new OwlFeatureNotSupportedException(
				"Maximal cardinality restrictions in subclass positions are not supported in rules.");
	}

	@Override
	public void visit(final OWLObjectHasSelf ce) {
		OwlToRulesConversionHelper.addConjunctForPropertyExpression(ce.getProperty(), this.mainTerm, this.mainTerm,
				this.body);
	}

	// TODO support this feature
	@Override
	public void visit(final OWLObjectOneOf ce) {
		throw new OwlFeatureNotSupportedException(
				"OWLObjectOneOf in complex class expressions currently not supported!");
	}

	@Override
	public void visit(final OWLDataSomeValuesFrom ce) {
		throw new OwlFeatureNotSupportedException("OWL datatypes currently not supported in rules.");
	}

	@Override
	public void visit(final OWLDataAllValuesFrom ce) {
		throw new OwlFeatureNotSupportedException("OWL datatypes currently not supported in rules.");
	}

	@Override
	public void visit(final OWLDataHasValue ce) {
		throw new OwlFeatureNotSupportedException("OWL datatypes currently not supported in rules.");
	}

	@Override
	public void visit(final OWLDataMinCardinality ce) {
		throw new OwlFeatureNotSupportedException("OWL datatypes currently not supported in rules.");
	}

	@Override
	public void visit(final OWLDataExactCardinality ce) {
		throw new OwlFeatureNotSupportedException("OWL datatypes currently not supported in rules.");
	}

	@Override
	public void visit(final OWLDataMaxCardinality ce) {
		throw new OwlFeatureNotSupportedException("OWL datatypes currently not supported in rules.");
	}

}
