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
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.impl.AtomImpl;
import org.semanticweb.vlog4j.core.model.impl.ConjunctionImpl;
import org.semanticweb.vlog4j.core.model.impl.RuleImpl;

public class ClassToRuleBodyConverter implements OWLClassExpressionVisitor {

	final List<Atom> bodyConjuncts;
	final Set<Rule> rules;
	final Variable frontierVariable;

	public ClassToRuleBodyConverter(Variable frontierVariable, List<Atom> bodyConjuncts, Set<Rule> rules) {
		this.frontierVariable = frontierVariable;
		this.bodyConjuncts = bodyConjuncts;
		this.rules = rules;
	}

	@Override
	public void visit(OWLClass ce) {
		Predicate predicate = OwlToRulesConversionHelper.getClassPredicate(ce);
		bodyConjuncts.add(new AtomImpl(predicate, Arrays.asList(this.frontierVariable)));
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
					this.rules);
			conjunct.accept(converter);
			this.rules.add(new RuleImpl(auxHead, new ConjunctionImpl(converter.bodyConjuncts)));
		}
	}

	@Override
	public void visit(OWLObjectComplementOf ce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLObjectSomeValuesFrom ce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLObjectAllValuesFrom ce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLObjectHasValue ce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLObjectMinCardinality ce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLObjectExactCardinality ce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLObjectMaxCardinality ce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLObjectHasSelf ce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLObjectOneOf ce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLDataSomeValuesFrom ce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLDataAllValuesFrom ce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLDataHasValue ce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLDataMinCardinality ce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLDataExactCardinality ce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLDataMaxCardinality ce) {
		// TODO Auto-generated method stub

	}

}
