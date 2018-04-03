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

import java.util.Arrays;
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
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.implementation.AtomImpl;

/**
 * Helper class for transforming OWL class expressions that occur as
 * superclasses into suitable head atoms for rules.
 * 
 * @author Markus Krötzsch
 *
 */
public class ClassToRuleHeadConverter extends AbstractClassToRuleConverter implements OWLClassExpressionVisitor {

	boolean currentIsExistential = false;

	public ClassToRuleHeadConverter(Term mainTerm, SimpleConjunction body, SimpleConjunction head,
			OwlAxiomToRulesConverter parent) {
		super(mainTerm, body, head, parent);
	}

	public ClassToRuleHeadConverter(Term mainTerm, OwlAxiomToRulesConverter parent) {
		this(mainTerm, new SimpleConjunction(), new SimpleConjunction(), parent);
	}

	@Override
	public AbstractClassToRuleConverter makeChildConverter(Term mainTerm) {
		return new ClassToRuleHeadConverter(mainTerm, this.parent);
	}

	@Override
	public void visit(OWLClass ce) {
		if (ce.isOWLNothing()) {
			this.head.makeFalse();
		} else if (ce.isOWLThing()) {
			this.head.init();
		} else {
			Predicate predicate = OwlToRulesConversionHelper.getClassPredicate(ce);
			this.head.add(new AtomImpl(predicate, Arrays.asList(this.mainTerm)));
		}
	}

	@Override
	public void visit(OWLObjectIntersectionOf ce) {
		handleConjunction(ce.getOperands(), this.mainTerm);
	}

	@Override
	public void visit(OWLObjectUnionOf ce) {
		handleDisjunction(ce.getOperands());
	}

	@Override
	public void visit(OWLObjectComplementOf ce) {
		ClassToRuleBodyConverter converter = new ClassToRuleBodyConverter(this.mainTerm, this.body, this.head,
				this.parent);
		ce.getOperand().accept(converter);
	}

	@Override
	public void visit(OWLObjectSomeValuesFrom ce) {
		handleObjectSomeValues(ce.getProperty(), ce.getFiller());
	}

	@Override
	public void visit(OWLObjectAllValuesFrom ce) {
		handleObjectAllValues(ce.getProperty(), ce.getFiller());
	}

	@Override
	public void visit(OWLObjectHasValue ce) {
		Term term = OwlToRulesConversionHelper.getIndividualTerm(ce.getFiller());
		OwlToRulesConversionHelper.addConjunctForPropertyExpression(ce.getProperty(), this.mainTerm, term, this.head);
	}

	@Override
	public void visit(OWLObjectMinCardinality ce) {
		if (ce.getCardinality() == 0) {
			this.head.init(); // tautological
		} else if (ce.getCardinality() == 1) {
			handleObjectSomeValues(ce.getProperty(), ce.getFiller());
		} else {
			throw new OwlFeatureNotSupportedException(
					"Min cardinality restrictions with values greater than 1 in superclass positions are not supported in rules.");
		}
	}

	@Override
	public void visit(OWLObjectExactCardinality ce) {
		throw new OwlFeatureNotSupportedException(
				"Exact cardinality restrictions in superclass positions are not supported in rules.");
	}

	@Override
	public void visit(OWLObjectMaxCardinality ce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLObjectHasSelf ce) {
		OwlToRulesConversionHelper.addConjunctForPropertyExpression(ce.getProperty(), this.mainTerm, this.mainTerm,
				this.head);
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

}
