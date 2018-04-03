package org.semanticweb.vlog4j.rdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.openrdf.model.BNode;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;
import org.semanticweb.vlog4j.core.model.api.Atom;

public class TestTurtleToFacts {

	public static final String TURTLE_TEST_FILES_PATH = "src/test/data/";

	@Test
	public void testRDFFileToAtomsConverter() throws RDFParseException, RDFHandlerException, IOException {
		// FIXME only provide the parser dependencies for testing

		final Model model = parseFile(new File(TURTLE_TEST_FILES_PATH + "exampleFacts.ttl"), RDFFormat.TURTLE);
		final Set<Atom> facts = RDFModelToAtomsConverter.rdfModelToAtoms(model);

		System.out.println(facts);
		// TODO asserts: url long name for constants and literal datatypes
		// TODO asserts: normalized literal label
		// TODO asserts: escaped " characters in literal
		// FIXME test builtin datatypes?
		// TODO test literal of all literal datatypes
		// TODO test with/without language
		// TODO test if reasoning is possible with this predicate / fact names
	}

	@Test
	public void testBlanksHaveDifferentIdsInDifferentModelContexts()
			throws RDFParseException, RDFHandlerException, IOException {

		final String blanksTurtleFile1 = TURTLE_TEST_FILES_PATH + "blanks_context1.ttl";
		final Model model1 = parseFile(new File(blanksTurtleFile1), RDFFormat.TURTLE);
		final Set<String> blankNodeIdsForModel1File1 = collectBlankNodeIds(model1);
		assertEquals(2, blankNodeIdsForModel1File1.size());

		final Model model2 = parseFile(new File(blanksTurtleFile1), RDFFormat.TURTLE);
		final Set<String> blankNodeIdsForModel2File1 = collectBlankNodeIds(model2);
		assertEquals(2, blankNodeIdsForModel2File1.size());
		assertNotEquals(blankNodeIdsForModel2File1, blankNodeIdsForModel1File1);

		final String blanksTurtleFile2SameContentAsFile1 = TURTLE_TEST_FILES_PATH + "blanks_context2.ttl";
		final Model model3 = parseFile(new File(blanksTurtleFile2SameContentAsFile1), RDFFormat.TURTLE);
		final Set<String> blankNodeIdsForModel3File2 = collectBlankNodeIds(model3);
		assertEquals(2, blankNodeIdsForModel3File2.size());
		assertNotEquals(blankNodeIdsForModel3File2, blankNodeIdsForModel1File1);
		assertNotEquals(blankNodeIdsForModel3File2, blankNodeIdsForModel2File1);
	}

	private Set<String> collectBlankNodeIds(Model model) {
		final HashSet<String> blankNodeIds = new HashSet<>();
		model.forEach(statement -> {
			final Resource subject = statement.getSubject();
			if (subject instanceof BNode) {
				blankNodeIds.add(((BNode) subject).getID());
			}
			final Value object = statement.getObject();
			if (object instanceof BNode) {
				blankNodeIds.add(((BNode) object).getID());
			}
		});
		return blankNodeIds;
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
