package org.semanticweb.vlog4j.owlapi.reasoner;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.vlog4j.core.model.api.Blank;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.TermVisitor;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.owlapi.OwlFeatureNotSupportedException;

public class TermsToOWLIndividualsConversionHelper implements TermVisitor<OWLIndividual> {

	@Override
	public OWLIndividual visit(Constant term) {
		// TODO OWLNamedIndividual
		return null;
	}

	@Override
	public OWLIndividual visit(Variable term) {
		throw new OwlFeatureNotSupportedException(
				"Could not convert VLog Variable '" + term + "' to an OWLIndividual.");
	}

	@Override
	public OWLIndividual visit(Blank term) {
		// TODO OWLAnonymousIndividual
		return null;
	}

}
