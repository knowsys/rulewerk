package org.semanticweb.rulewerk.rdf;

/*-
 * #%L
 * Rulewerk RDF Support
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
import java.util.Set;
import java.util.stream.Collectors;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Namespace;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.semanticweb.rulewerk.core.model.api.NamedNull;
import org.semanticweb.rulewerk.core.exceptions.PrefixDeclarationException;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * <li>{@link BNode}s are converted to {@link NamedNull}s with the generated
 * blank ID as name. {@link BNode}s have unique generated IDs in the context a
 * {@link Model}s. Blanks with the same name loaded from different models will
 * have different ids.</li>
 * </ul>
 *
 * @author Irina Dragoste
 * @author Markus Kroetzsch
 *
 */
public final class RdfModelConverter {

	private static Logger LOGGER = LoggerFactory.getLogger(RdfModelConverter.class);

	/**
	 * The name of the ternary predicate of literals generated from RDF triples by
	 * default.
	 */
	public static final String RDF_TRIPLE_PREDICATE_NAME = "TRIPLE";

	final RdfValueToTermConverter rdfValueToTermConverter;
	final Predicate triplePredicate;

	/**
	 * Construct an object that does not skolemize blank nodes and that uses a
	 * ternary predicate named {@link RdfModelConverter#RDF_TRIPLE_PREDICATE_NAME}
	 * for storing triples.
	 */
	public RdfModelConverter() {
		this(false, RDF_TRIPLE_PREDICATE_NAME);
	}

	/**
	 * Constructor. If {@code triplePredicateName} is a string, then RDF triples
	 * will be represented as ternary facts with a predicate of that name. If it is
	 * {@code null}, then triples will be converted to binary facts where the
	 * predicate is the RDF predicate; moreover, triples with rdf:rype as predicate
	 * will be converted to unary facts.
	 * 
	 * @param skolemize           if true, blank nodes are translated to constants
	 *                            with generated IRIs; otherwise they are replanced
	 *                            by named nulls with generated ids
	 * @param triplePredicateName name of the ternary predicate that should be used
	 *                            to store RDF triples; or null to generate binary
	 *                            predicates from the predicates of RDF triples
	 */
	public RdfModelConverter(boolean skolemize, String triplePredicateName) {
		this.rdfValueToTermConverter = new RdfValueToTermConverter(skolemize);
		if (triplePredicateName != null) {
			this.triplePredicate = Expressions.makePredicate(triplePredicateName, 3);
		} else {
			this.triplePredicate = null;
		}
	}

	/**
	 * Converts each {@code <subject, predicate, object>} triple statement of the
	 * given {@code rdfModel} into a {@link Fact} of the form
	 * {@code TRIPLE(subject, predicate, object)}. See
	 * {@link RdfModelConverter#RDF_TRIPLE_PREDICATE}, the ternary predicate used
	 * for all literals generated from RDF triples.
	 *
	 * @param model a {@link Model} of an RDF document, containing triple statements
	 *              that will be converter to facts.
	 * @return a set of facts corresponding to the statements of given
	 *         {@code rdfModel}.
	 */
	public Set<Fact> rdfModelToFacts(final Model model) {
		return model.stream().map((statement) -> rdfStatementToFact(statement)).collect(Collectors.toSet());
	}

	/**
	 * Adds data and prefix declarations from a given RDF {@link Model} to a given
	 * {@link KnowledgeBase}.
	 * 
	 * @param knowledgeBase the {@link KnowledgeBase} to add to
	 * @param model         the {@link Model} with the RDF data
	 */
	public void addAll(KnowledgeBase knowledgeBase, Model model) {
		addPrefixes(knowledgeBase, model);
		addFacts(knowledgeBase, model);
	}

	/**
	 * Adds the data from a given RDF {@link Model} as {@link Fact}s to the given
	 * {@link KnowledgeBase}.
	 * 
	 * @param knowledgeBase the {@link KnowledgeBase} to add {@link Fact}s to
	 * @param model         the {@link Model} with the RDF data
	 */
	public void addFacts(KnowledgeBase knowledgeBase, Model model) {
		model.stream().forEach((statement) -> {
			knowledgeBase.addStatement(rdfStatementToFact(statement));
		});
	}

	/**
	 * Adds the prefixes declared for a given RDF {@link Model} to the given
	 * {@link KnowledgeBase}. If a prefix cannot be added for some reason, it is
	 * ignored and a warning is logged.
	 * 
	 * @param knowledgeBase the {@link KnowledgeBase} to add prefix declarations to
	 * @param model         the {@link Model} with the RDF data
	 */
	public void addPrefixes(KnowledgeBase knowledgeBase, Model model) {
		for (Namespace namespace : model.getNamespaces()) {
			try {
				knowledgeBase.getPrefixDeclarationRegistry().setPrefixIri(namespace.getPrefix() + ":",
						namespace.getName());
			} catch (PrefixDeclarationException e) {
				LOGGER.warn("Failed to set prefix \"" + namespace.getPrefix() + "\" from RDF model: " + e.getMessage());
			}
		}
	}

	/**
	 * Converts an RDF statement (triple) to a Rulewerk {@link Fact}.
	 * 
	 * @param statement
	 * @return
	 */
	Fact rdfStatementToFact(final Statement statement) {
		final Term subject = rdfValueToTermConverter.convertValue(statement.getSubject());
		final Term object = rdfValueToTermConverter.convertValue(statement.getObject());

		if (triplePredicate != null) {
			final Term predicate = rdfValueToTermConverter.convertUri(statement.getPredicate());
			return Expressions.makeFact(triplePredicate, Arrays.asList(subject, predicate, object));
		} else {
			if (PrefixDeclarationRegistry.RDF_TYPE.equals(statement.getPredicate().stringValue())
					&& statement.getObject() instanceof URI) {
				Predicate classPredicate = rdfValueToTermConverter.convertUriToPredicate((URI) statement.getObject(), 1);
				return Expressions.makeFact(classPredicate, Arrays.asList(subject));
			} else {
				Predicate factPredicate = rdfValueToTermConverter.convertUriToPredicate(statement.getPredicate(), 2);
				return Expressions.makeFact(factPredicate, Arrays.asList(subject, object));
			}
		}
	}

}
