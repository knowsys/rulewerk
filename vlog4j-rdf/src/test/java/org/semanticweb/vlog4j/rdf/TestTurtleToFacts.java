package org.semanticweb.vlog4j.rdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Set;

import org.junit.Test;
import org.openrdf.model.Model;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;
import org.semanticweb.vlog4j.core.model.api.Atom;

public class TestTurtleToFacts {

	public static final String TURTLE_TEST_FILE_PATH = "src/test/data/exampleFacts.ttl";

	@Test
	public void testRDFFileToAtomsConverter() throws RDFParseException, RDFHandlerException, IOException {
		// FIXME only provide the parser dependencies for testing

		final Model model = parseFile(new File(TURTLE_TEST_FILE_PATH), RDFFormat.TURTLE);
		final Set<Atom> facts = RDFModelToAtomsConverter.rdfModelToAtoms(model);

		System.out.println(facts);
		// TODO asserts: url long name for constants and literal datatypes
		// TODO asserts: normalized literal label
		// TODO asserts: escaped " characters in literal
		// TODO asserts: boolean names should be unique per model
		// TODO test literal of all literal datatypes
		// TODO test with/without language
		// TODO test if reasoning is possible with this predicate / fact names
	}

	private Model parseFile(File file, RDFFormat rdfFormat) throws RDFParseException, RDFHandlerException, IOException {

		final URI baseURI = file.toURI();
		final InputStream inputStream = new FileInputStream(file);
		final RDFParser rdfParser = Rio.createParser(rdfFormat);

		final Model model = new LinkedHashModel();
		rdfParser.setRDFHandler(new StatementCollector(model));
		rdfParser.parse(inputStream, baseURI.toString());

		return model;
	}

}
