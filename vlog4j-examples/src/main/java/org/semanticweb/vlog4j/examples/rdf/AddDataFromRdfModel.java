package org.semanticweb.vlog4j.examples.rdf;

/*-
 * #%L
 * VLog4j Examples
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;
import org.semanticweb.vlog4j.core.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;
import org.semanticweb.vlog4j.core.reasoner.implementation.VLogKnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.implementation.VLogReasoner;
import org.semanticweb.vlog4j.examples.ExamplesUtils;
import org.semanticweb.vlog4j.rdf.RdfModelConverter;
import org.semanticweb.vlog4j.parser.ParsingException;
import org.semanticweb.vlog4j.parser.RuleParser;

/**
 * This example shows how <b>vlog4j-rdf</b> library's utility class
 * {@link RdfModelConverter} can be used to convert RDF {@link Model}s from
 * various types of RDF resources to <b>vlog4j-core</b> {@code Atom} sets.
 * 
 * @author Irina Dragoste
 *
 */
public class AddDataFromRdfModel {

	public static void main(final String[] args) throws IOException, RDFParseException, RDFHandlerException,
			URISyntaxException, ReasonerStateException, EdbIdbSeparationException, IncompatiblePredicateArityException {

		ExamplesUtils.configureLogging();

		/*
		 * Local file containing metadata of publications from ISWC'16 conference, in
		 * RDF/XML format.
		 */
		final File rdfXMLResourceFile = new File(ExamplesUtils.INPUT_FOLDER + "rdf/iswc-2016-complete-alignments.rdf");
		final FileInputStream inputStreamISWC2016 = new FileInputStream(rdfXMLResourceFile);
		/* An RDF Model is obtained from parsing the RDF/XML resource. */
		final Model rdfModelISWC2016 = parseRdfResource(inputStreamISWC2016, rdfXMLResourceFile.toURI(),
				RDFFormat.RDFXML);
		
		/*
		 * Using vlog4j-rdf library, we convert RDF Model triples to facts, each having
		 * the ternary predicate "TRIPLE".
		 */
		final Set<PositiveLiteral> tripleFactsISWC2016 = RdfModelConverter.rdfModelToPositiveLiterals(rdfModelISWC2016);
		System.out.println("Example triple fact from iswc-2016 dataset:");
		System.out.println(" - " + tripleFactsISWC2016.iterator().next());

		/*
		 * URL of online resource containing metadata of publications from ISWC'17
		 * conference, in TURTLE format.
		 */
		final URL turtleResourceURL = new URL(
				"http://www.scholarlydata.org/dumps/conferences/alignments/iswc-2017-complete-alignments.ttl");
		final InputStream inputStreamISWC2017 = turtleResourceURL.openStream();
		/* An RDF Model is obtained from parsing the TURTLE resource. */
		final Model rdfModelISWC2017 = parseRdfResource(inputStreamISWC2017, turtleResourceURL.toURI(),
				RDFFormat.TURTLE);

		/*
		 * Using vlog4j-rdf library, we convert RDF Model triples to facts, each having
		 * the ternary predicate "TRIPLE".
		 */
		final Set<PositiveLiteral> tripleFactsISWC2017 = RdfModelConverter.rdfModelToPositiveLiterals(rdfModelISWC2017);
		System.out.println("Example triple fact from iswc-2017 dataset:");
		System.out.println(" - " + tripleFactsISWC2017.iterator().next());

		/**
		 * We wish to combine triples about a person's affiliation, an affiliation's
		 * organization and an organization's name, to find a person's organization
		 * name.
		 */

		/* Predicate names of the triples found in both RDF files. */
		final Variable varPerson = Expressions.makeVariable("person");
		final Predicate predicateHasOrganizationName = Expressions.makePredicate("hasOrganizationName", 2);

		/*
		 * Rule that retrieves pairs of persons and their organization name:
		 */
		final String rules = "%%%% We specify the rules syntactically for convenience %%%\n"
				+ "@prefix cnf: <https://w3id.org/scholarlydata/ontology/conference-ontology.owl#> ."
				+ "hasOrganizationName(?Person, ?OrgName) :- "
				+ "  TRIPLE(?Person, cnf:hasAffiliation, ?Aff), TRIPLE(?Aff, cnf:withOrganisation, ?Org),"
				+ "  TRIPLE(?Org, cnf:name, ?OrgName) .";
		RuleParser ruleParser = new RuleParser();
		try {
			ruleParser.parse(rules);
		} catch (ParsingException e) {
			System.out.println("Failed to parse rules: " + e.getMessage());
			return;
		}

		final VLogKnowledgeBase kb = new VLogKnowledgeBase();
		/*
		 * The rule that maps people to their organization name based on facts extracted
		 * from RDF triples is added to the Reasoner's knowledge base.
		 */
		kb.addRules(ruleParser.getRules());
		/*
		 * Facts extracted from the RDF resources are added to the Reasoner's knowledge
		 * base.
		 */
		kb.addFacts(tripleFactsISWC2016);
		kb.addFacts(tripleFactsISWC2017);

		try (VLogReasoner reasoner = new VLogReasoner(kb)) {
			reasoner.load();
			reasoner.reason();

			/* We query for persons whose organization name is "TU Dresden" . */
			final Constant constantTuDresden = Expressions.makeDatatypeConstant("TU Dresden",
					"http://www.w3.org/2001/XMLSchema#string");
			/* hasOrganizationName(?person, "TU Dresden") */
			final PositiveLiteral queryTUDresdenParticipantsAtISWC = Expressions
					.makePositiveLiteral(predicateHasOrganizationName, varPerson, constantTuDresden);

			System.out.println("\nParticipants at ISWC'16 and '17 from Organization 'TU Dresden':");
			System.out.println("(Answers to query " + queryTUDresdenParticipantsAtISWC + ")\n");
			try (QueryResultIterator queryResultIterator = reasoner.answerQuery(queryTUDresdenParticipantsAtISWC,
					false)) {
				queryResultIterator.forEachRemaining(answer -> System.out
						.println(" - " + answer.getTerms().get(0) + ", organization " + answer.getTerms().get(1)));
			}

		}

	}

	/**
	 * Parses the data from the supplied InputStream, using the supplied baseURI to
	 * resolve any relative URI references.
	 * 
	 * @param inputStream The content to be parsed, expected to be in the given
	 *                    {@code rdfFormat}.
	 * @param baseURI     The URI associated with the data in the InputStream.
	 * @param rdfFormat   The expected RDFformat of the inputStream resource that is
	 *                    to be parsed.
	 * @return A Model containing the RDF triples. Blanks have unique ids across
	 *         different models.
	 * @throws IOException         If an I/O error occurred while data was read from
	 *                             the InputStream.
	 * @throws RDFParseException   If the parser has found an unrecoverable parse
	 *                             error.
	 * @throws RDFHandlerException If the configured statement handler has
	 *                             encountered an unrecoverable error.
	 */
	private static Model parseRdfResource(final InputStream inputStream, final URI baseURI, final RDFFormat rdfFormat)
			throws IOException, RDFParseException, RDFHandlerException {
		final Model model = new LinkedHashModel();
		final RDFParser rdfParser = Rio.createParser(rdfFormat);
		rdfParser.setRDFHandler(new StatementCollector(model));
		rdfParser.parse(inputStream, baseURI.toString());

		return model;
	}

}
