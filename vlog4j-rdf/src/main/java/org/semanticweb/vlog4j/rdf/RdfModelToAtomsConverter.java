package org.semanticweb.vlog4j.rdf;

import static org.semanticweb.vlog4j.rdf.RdfValueToTermConverter.rdfValueToTerm;

/*-
 * #%L
 * VLog4j RDF Support
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

import java.util.Set;
import java.util.stream.Collectors;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;

public final class RdfModelToAtomsConverter {

	public static final String RDF_TRIPLE_PREDICATE_NAME = "TRIPLE";
	public static final Predicate RDF_TRIPLE_PREDICATE = Expressions.makePredicate(RDF_TRIPLE_PREDICATE_NAME, 3);

	private RdfModelToAtomsConverter() {
	}

	public static Set<Atom> rdfModelToAtoms(Model rdfModel) {
		return rdfModel.stream().map(RdfModelToAtomsConverter::rdfStatementToAtom).collect(Collectors.toSet());
	}

	static Atom rdfStatementToAtom(final Statement statement) {
		final Resource subject = statement.getSubject();

		final URI predicate = statement.getPredicate();

		final Value object = statement.getObject();

		return Expressions.makeAtom(RDF_TRIPLE_PREDICATE, rdfValueToTerm(subject), rdfValueToTerm(predicate),
				rdfValueToTerm(object));
	}

}
