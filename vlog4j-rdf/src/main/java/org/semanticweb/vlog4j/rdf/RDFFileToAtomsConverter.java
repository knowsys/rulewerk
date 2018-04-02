package org.semanticweb.vlog4j.rdf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.ParserConfig;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;
import org.semanticweb.vlog4j.core.model.api.Atom;

public class RDFFileToAtomsConverter {

	// TODO get parser for mime type.orElse
	// TODO get parser for file name.orElse
	// TODO get parser for mime type
	// TODO get parser for file name

	// TODO maybe also provide parser from reader instead of inputStream
	// parse(Reader reader, String baseURI)

	// FIXME perhaps we only need sesame-rio-api, not the implementation of
	// particular parsers. Or do we need to expose more methods that would ease
	// parsing?

	public static Set<Atom> fromFormatToFacts(InputStream inputStream, String baseURI, RDFFormat rdfFormat)
			throws RDFParseException, RDFHandlerException, IOException {
		final RDFParser rdfParser = Rio.createParser(rdfFormat);

		return parseRDFToAtoms(inputStream, baseURI, rdfParser);
	}

	public static Set<Atom> fromFormatToFacts(InputStream inputStream, String baseURI, RDFFormat rdfFormat,
			ParserConfig config) throws RDFParseException, RDFHandlerException, IOException {
		final RDFParser rdfParser = Rio.createParser(rdfFormat);
		rdfParser.setParserConfig(config);

		return parseRDFToAtoms(inputStream, baseURI, rdfParser);
	}

	private static Set<Atom> parseRDFToAtoms(InputStream inputStream, String baseURI, final RDFParser rdfParser)
			throws IOException, RDFParseException, RDFHandlerException {
		final Model model = new LinkedHashModel();
		rdfParser.setRDFHandler(new StatementCollector(model));
		rdfParser.parse(inputStream, baseURI);
		// TODO perhaps return model?
		return RDFModelToAtomsConverter.rdfModelToAtoms(model);
	}

}
