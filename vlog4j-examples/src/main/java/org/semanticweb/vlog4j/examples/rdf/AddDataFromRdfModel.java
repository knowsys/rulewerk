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

import org.eclipse.jdt.annotation.NonNull;
import org.openrdf.model.Model;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;
import org.semanticweb.vlog4j.examples.ExamplesUtils;
import org.semanticweb.vlog4j.rdf.RdfModelConverter;

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

		/*
		 * Local file containing metadata of publications from ISWC'16 conference, in
		 * RDF/XML format.
		 */
		final File rdfXMLResourceFile = new File(ExamplesUtils.INPUT_FOLDER + "rdf/iswc-2016-complete-alignments.rdf");
		final FileInputStream inputStreamISWC2016 = new FileInputStream(rdfXMLResourceFile);
		/* An RDF Model is obtained from parsing the RDF/XML resource. */
		final Model rdfModelISWC2016 = parseRDFResource(inputStreamISWC2016, rdfXMLResourceFile.toURI(),
				RDFFormat.RDFXML);

		/*
		 * Using vlog4j-rdf library, we convert RDF Model triples to facts, each having
		 * the ternary predicate "TRIPLE".
		 */
		final Set<PositiveLiteral> tripleFactsISWC2016 = RdfModelConverter.rdfModelToPositiveLiterals(rdfModelISWC2016);
		System.out.println("Example triple fact from iswc-2016");
		System.out.println(" - " + tripleFactsISWC2016.iterator().next());

		/*
		 * URL of online resource containing metadata of publications from ISWC'17
		 * conference, in TURTLE format.
		 */
		final URL turtleResourceURL = new URL(
				"http://www.scholarlydata.org/dumps/conferences/alignments/iswc-2017-complete-alignments.ttl");
		final InputStream inputStreamISWC2017 = turtleResourceURL.openStream();
		/* An RDF Model is obtained from parsing the TURTLE resource. */
		final Model rdfModelISWC2017 = parseRDFResource(inputStreamISWC2017, turtleResourceURL.toURI(),
				RDFFormat.TURTLE);

		/*
		 * Using vlog4j-rdf library, we convert RDF Model triples to facts, each having
		 * the ternary predicate "TRIPLE".
		 */
		final Set<PositiveLiteral> tripleFactsISWC2017 = RdfModelConverter.rdfModelToPositiveLiterals(rdfModelISWC2017);
		System.out.println("Example triple fact from iswc-2017");
		System.out.println(" - " + tripleFactsISWC2017.iterator().next());

		/**
		 * We wish to combine triples about a person's affiliation, an affiliation's
		 * organization and an organization's name, to find a person's organization
		 * name.
		 */

		/* Predicate names of the triples found in both RDF files. */
		final Constant constHasAffiiation = Expressions
				.makeConstant("https://w3id.org/scholarlydata/ontology/conference-ontology.owl#hasAffiliation");
		final Constant constWithOrganization = Expressions
				.makeConstant("https://w3id.org/scholarlydata/ontology/conference-ontology.owl#withOrganisation");
		final Constant constName = Expressions
				.makeConstant("https://w3id.org/scholarlydata/ontology/conference-ontology.owl#name");

		final Variable varOganization = Expressions.makeVariable("organization");
		final Variable varOganizationName = Expressions.makeVariable("organizationName");
		final Variable varPerson = Expressions.makeVariable("person");
		final Variable varAfiliation = Expressions.makeVariable("affiliation");

		/* Patterns for facts extracted from RDF triples. */
		final PositiveLiteral personHasAffiliation = Expressions.makePositiveLiteral(
				RdfModelConverter.RDF_TRIPLE_PREDICATE, varPerson, constHasAffiiation, varAfiliation);
		final PositiveLiteral affiliationWithOrganization = Expressions.makePositiveLiteral(
				RdfModelConverter.RDF_TRIPLE_PREDICATE, varAfiliation, constWithOrganization, varOganization);
		final PositiveLiteral organizationHasName = Expressions.makePositiveLiteral(
				RdfModelConverter.RDF_TRIPLE_PREDICATE, varOganization, constName, varOganizationName);

		/*
		 * We create a Rule that retrieves pairs of persons and their organization name,
		 * from facts extracted from RDF triples.
		 */
		final Predicate predicateHasOrganizationName = Expressions.makePredicate("hasOrganizationName", 2);
		final PositiveLiteral creatorOrganizationName = Expressions.makePositiveLiteral(predicateHasOrganizationName,
				varPerson, varOganizationName);

		/*
		 * hasOrganizationName(?person, ?organizationName) :- TRIPLE(?person,
		 * <hasAffiliation>, ?affiliation), TRIPLE(?affiliation, <withOrganisation>,
		 * ?organization), TRIPLE(?organization, <name>, ?organizationName) .
		 */
		final Rule organizationRule = Expressions.makeRule(creatorOrganizationName, personHasAffiliation,
				affiliationWithOrganization, organizationHasName);

		try (final Reasoner reasoner = Reasoner.getInstance();) {
			/*
			 * Facts extracted from the RDF resources are added to the Reasoner's knowledge
			 * base.
			 */
			reasoner.addFacts(tripleFactsISWC2016);
			reasoner.addFacts(tripleFactsISWC2017);
			/*
			 * The rule that maps people to their organization name based on facts extracted
			 * from RDF triples is added to the Reasoner's knowledge base.
			 */
			reasoner.addRules(organizationRule);

			reasoner.load();
			reasoner.reason();

			/* We query for persons whose organization name is "TU Dresden" . */
			final Constant constantTuDresdenOrganization = Expressions.makeConstant("\"TU Dresden\"");
			/* hasOrganizationName(?person, "TU Dresden") */
			@NonNull
			final PositiveLiteral queryTUDresdenParticipantsAtISWC = Expressions
					.makePositiveLiteral(predicateHasOrganizationName, varPerson, constantTuDresdenOrganization);

			System.out.println("Participants at ISWC'16 and '17 from Organization 'TU Dresden':");
			System.out.println("( Answers to query " + queryTUDresdenParticipantsAtISWC + " )");
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
	private static Model parseRDFResource(final InputStream inputStream, final URI baseURI, final RDFFormat rdfFormat)
			throws IOException, RDFParseException, RDFHandlerException {
		final Model model = new LinkedHashModel();

		final RDFParser rdfParser = Rio.createParser(rdfFormat);
		rdfParser.setRDFHandler(new StatementCollector(model));
		rdfParser.parse(inputStream, baseURI.toString());

		return model;
	}

}
