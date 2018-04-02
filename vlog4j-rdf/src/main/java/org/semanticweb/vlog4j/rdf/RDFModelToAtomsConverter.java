package org.semanticweb.vlog4j.rdf;

import java.util.Set;
import java.util.stream.Collectors;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;

public class RDFModelToAtomsConverter {

	public static Set<Atom> rdfModelToAtoms(Model rdfModel) {
		// TODO do we need rdfModel.getNamespaces() ?
		return rdfModel.stream().map(RDFModelToAtomsConverter::rdfStatementToAtom).collect(Collectors.toSet());
	}

	public static Atom rdfStatementToAtom(final Statement statement) {
		// TODO do we need statement.getContext() ?
		final Resource subject = statement.getSubject();

		// FIXME should we enclose predicate value in "<" ">" ?
		final URI predicate = statement.getPredicate();

		final Value object = statement.getObject();

		return Expressions.makeAtom(predicate.stringValue(), RDFValueToTermConverter.rdfValueToTerm(subject),
				RDFValueToTermConverter.rdfValueToTerm(object));
	}

}
