package org.semanticweb.vlog4j.rdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Set;

import org.junit.Test;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.semanticweb.vlog4j.core.model.api.Atom;

public class TestTurtleToFacts {

	public static final String TURTLE_TEST_FILE_PATH = "src/test/data/exampleFacts.ttl";

	@Test
	public void testRDFFileToAtomsConverter() throws RDFParseException, RDFHandlerException, IOException {
		final File turtleTestFile = new File(TURTLE_TEST_FILE_PATH);
		final URI uri = turtleTestFile.toURI();
		final InputStream fileInputStream = new FileInputStream(turtleTestFile);
		final Set<Atom> facts = RDFFileToAtomsConverter.fromFormatToFacts(fileInputStream, uri.toString(),
				RDFFormat.TURTLE);

		System.out.println(facts);
		// TODO test literal of all literal datatypes
		// TODO test with/without language
		// TODO asserts
		// TODO test if reasoning is possible with this predicate / fact names
	}

}
