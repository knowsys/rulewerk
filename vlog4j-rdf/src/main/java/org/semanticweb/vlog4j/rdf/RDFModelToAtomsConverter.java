package org.semanticweb.vlog4j.rdf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.ParserConfig;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFParserFactory;
import org.openrdf.rio.RDFParserRegistry;
import org.openrdf.rio.helpers.StatementCollector;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;

public class RDFModelToAtomsConverter {

	public static Set<Atom> rdfModelToAtoms(Model rdfModel) {
		// TODO do we need this?
		// rdfModel.getNamespaces();
		return rdfModel.stream().map(RDFModelToAtomsConverter::rdfStatementToAtom).collect(Collectors.toSet());
	}

	public static Atom rdfStatementToAtom(final Statement statement) {
		// TODO do we need it?
		// final Resource context = statement.getContext();
		final Resource subject = statement.getSubject();
		final URI predicate = statement.getPredicate();
		// object is a resource or a literal
		final Value object = statement.getObject();

		// TODO constant or blank?
		return Expressions.makeAtom(predicate.stringValue(), RDFValueToTermConverter.rdfValueToTerm(subject),
				RDFValueToTermConverter.rdfValueToTerm(object));
	}

	public static Set<Atom> fromFormatToFacts(RDFFormat rdfFormat, InputStream in, String baseURI, ParserConfig config)
			throws RDFParseException, RDFHandlerException, IOException {
		// RDFFormat format =
		// Rio.getParserFormatForFileName(documentURL.toString()).orElse(RDFFormat.RDFXML);

		final RDFParserRegistry rdfParserRegistry = RDFParserRegistry.getInstance();
		final RDFParserFactory rdfParserFactory = rdfParserRegistry.get(rdfFormat);
		final RDFParser parser = rdfParserFactory.getParser();
		// TODO or like this
		// final RDFParser rdfParser = Rio.createParser(rdfFormat);
		parser.setParserConfig(config);
		// TODO parser config
		// TODO perhaps have parser as parameter?
		// TODO which implementation of model should we chose
		final Model model = new LinkedHashModel();
		parser.setRDFHandler(new StatementCollector(model));
		parser.parse(in, baseURI);

		// 1
		// 2
		// java.net.URL documentUrl = new URL("http://example.org/example.ttl");
		// InputStream inputStream = documentUrl.openStream();
		// try {
		// rdfParser.parse(inputStream, documentURL.toString());
		// }
		// catch (IOException e) {
		// // handle IO problems (e.g. the file could not be read)
		// }
		// catch (RDFParseException e) {
		// // handle unrecoverable parse error
		// }
		// catch (RDFHandlerException e) {
		// // handle a problem encountered by the RDFHandler
		// }
		// finally {
		// inputStream.close();
		// }
		//
		// The Rio utility class provides additional helper methods, to make parsing to
		// a Model a single API call:
		//
		// 1
		// Model results = Rio.parse(inputStream, documentUrl.toString(),
		// RDFFormat.TURTLE);

		return rdfModelToAtoms(model);
	}

}
