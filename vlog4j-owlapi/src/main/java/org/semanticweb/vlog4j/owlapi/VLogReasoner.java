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

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.FreshEntityPolicy;
import org.semanticweb.owlapi.reasoner.IndividualNodeSetPolicy;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.Version;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;

public class VLogReasoner implements OWLReasoner {

	private final Reasoner reasoner;

	private final OWLOntology owlOntology;

	public VLogReasoner(Reasoner reasoner, OWLOntology owlOntology) {
		super();
		this.reasoner = reasoner;
		this.owlOntology = owlOntology;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub

	}

	@Override
	public Node<OWLClass> getBottomClassNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node<OWLDataProperty> getBottomDataPropertyNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BufferingMode getBufferingMode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeSet<OWLClass> getDataPropertyDomains(OWLDataProperty arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLLiteral> getDataPropertyValues(OWLNamedIndividual arg0, OWLDataProperty arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeSet<OWLNamedIndividual> getDifferentIndividuals(OWLNamedIndividual arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeSet<OWLClass> getDisjointClasses(OWLClassExpression arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeSet<OWLDataProperty> getDisjointDataProperties(OWLDataPropertyExpression arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeSet<OWLObjectPropertyExpression> getDisjointObjectProperties(OWLObjectPropertyExpression arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node<OWLClass> getEquivalentClasses(OWLClassExpression arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node<OWLDataProperty> getEquivalentDataProperties(OWLDataProperty arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node<OWLObjectPropertyExpression> getEquivalentObjectProperties(OWLObjectPropertyExpression arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FreshEntityPolicy getFreshEntityPolicy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression classExpression, boolean direct) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node<OWLObjectPropertyExpression> getInverseObjectProperties(OWLObjectPropertyExpression arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeSet<OWLClass> getObjectPropertyDomains(OWLObjectPropertyExpression arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeSet<OWLClass> getObjectPropertyRanges(OWLObjectPropertyExpression arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeSet<OWLNamedIndividual> getObjectPropertyValues(OWLNamedIndividual arg0,
			OWLObjectPropertyExpression arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLAxiom> getPendingAxiomAdditions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLAxiom> getPendingAxiomRemovals() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OWLOntologyChange> getPendingChanges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<InferenceType> getPrecomputableInferenceTypes() {
		return new HashSet<InferenceType>(
				Arrays.asList(InferenceType.CLASS_ASSERTIONS, InferenceType.OBJECT_PROPERTY_ASSERTIONS));
	}

	@Override
	public String getReasonerName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Version getReasonerVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLOntology getRootOntology() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node<OWLNamedIndividual> getSameIndividuals(OWLNamedIndividual arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeSet<OWLClass> getSubClasses(OWLClassExpression arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeSet<OWLDataProperty> getSubDataProperties(OWLDataProperty arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeSet<OWLObjectPropertyExpression> getSubObjectProperties(OWLObjectPropertyExpression arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeSet<OWLClass> getSuperClasses(OWLClassExpression arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeSet<OWLDataProperty> getSuperDataProperties(OWLDataProperty arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeSet<OWLObjectPropertyExpression> getSuperObjectProperties(OWLObjectPropertyExpression arg0,
			boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getTimeOut() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Node<OWLClass> getTopClassNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node<OWLDataProperty> getTopDataPropertyNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node<OWLObjectPropertyExpression> getTopObjectPropertyNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeSet<OWLClass> getTypes(OWLNamedIndividual arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node<OWLClass> getUnsatisfiableClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void interrupt() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isConsistent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEntailed(OWLAxiom arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEntailed(Set<? extends OWLAxiom> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEntailmentCheckingSupported(AxiomType<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPrecomputed(InferenceType arg0) {
		// FIXME how do we know if reasoner completed materialization?
		return false;
	}

	@Override
	public boolean isSatisfiable(OWLClassExpression arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void precomputeInferences(InferenceType... inferenceTypes) {
		{
			for (final InferenceType inferenceType : inferenceTypes) {
				if (inferenceType.equals(InferenceType.CLASS_ASSERTIONS)) {
					// TODO set precompted CLASS_ASSERTIONS
					materialize();
				} else if (inferenceType.equals(InferenceType.OBJECT_PROPERTY_ASSERTIONS)) {
					// TODO set precompted OBJECT_PROPERTY_ASSERTIONS
					materialize();
				}

			}
		}

	}

	private void materialize() {
		// TODO convert owlOntology to rules and facts
		try {
			// TODO set reasoner configuration (algorithm, rule rewriting strategy, log
			// level)
			reasoner.load();
			reasoner.reason();
		} catch (EdbIdbSeparationException | IOException | ReasonerStateException e) {
			// TODO convert properly
			throw new OWLRuntimeException("Unexpected exception during reasoning", e);
		}
	}

}
