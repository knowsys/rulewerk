package org.semanticweb.vlog4j.rdf;

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

import static org.semanticweb.vlog4j.rdf.RdfValueToTermConverter.rdfValueToTerm;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.semanticweb.vlog4j.core.model.api.Blank;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Fact;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;

/**
 * Class for converting RDF {@link Model}s to {@link PositiveLiteral} sets.
 * Converts each {@code <subject, predicate, object>} triple statement of the
 * given {@code rdfModel} into an {@link PositiveLiteral} of the form
 * {@code TRIPLE(subject, predicate, object)}. The ternary predicate used for
 * all literals generated from RDF triples is
 * {@link RdfModelConverter#RDF_TRIPLE_PREDICATE}. Subject, predicate and object
 * {@link Value}s are converted to corresponding {@link Term}s:
 * <ul>
 * <li>{@link URI}s are converted to {@link Constant}s with the escaped URI
 * String as name.</li>
 * <li>{@link Literal}s are converted to {@link Constant}s with names containing
 * the canonical form of the literal label, the data type and the language.</li>
 * <li>{@link BNode}s are converted to {@link Blank}s with the generated blank
 * ID as name. {@link BNode}s have unique generated IDs in the context a
 * {@link Model}s. Blanks with the same name loaded from different models will
 * have different ids.</li>
 * </ul>
 *
 * @author Irina Dragoste
 *
 */
public final class RdfModelConverter {

	/**
	 * The name of the ternary predicate of literals generated from RDF triples:
	 * "TRIPLE".
	 */
	public static final String RDF_TRIPLE_PREDICATE_NAME = "TRIPLE";

	/**
	 * The ternary predicate of literals generated from RDF triples. It has
	 * {@code name}({@link Predicate#getName()}) "TRIPLE" and
	 * {@code arity}({@link Predicate#getArity()}) 3.
	 */
	public static final Predicate RDF_TRIPLE_PREDICATE = Expressions.makePredicate(RDF_TRIPLE_PREDICATE_NAME, 3);

	private RdfModelConverter() {
	}

	/**
	 * Converts each {@code <subject, predicate, object>} triple statement of the
	 * given {@code rdfModel} into a {@link PositiveLiteral} of the form
	 * {@code TRIPLE(subject, predicate, object)}. See
	 * {@link RdfModelConverter#RDF_TRIPLE_PREDICATE}, the ternary predicate used
	 * for all literals generated from RDF triples.
	 *
	 * @param rdfModel a {@link Model} of an RDF document, containing triple
	 *                 statements that will be converter to facts.
	 * @return a set of literals corresponding to the statements of given
	 *         {@code rdfModel}.
	 */
	public static Set<Fact> rdfModelToFacts(final Model rdfModel) {
		return rdfModel.stream().map(RdfModelConverter::rdfStatementToFact).collect(Collectors.toSet());
	}

	static Fact rdfStatementToFact(final Statement statement) {
		final Resource subject = statement.getSubject();
		final URI predicate = statement.getPredicate();
		final Value object = statement.getObject();

		return Expressions.makeFact(RDF_TRIPLE_PREDICATE,
				Arrays.asList(rdfValueToTerm(subject), rdfValueToTerm(predicate), rdfValueToTerm(object)));
	}

}
