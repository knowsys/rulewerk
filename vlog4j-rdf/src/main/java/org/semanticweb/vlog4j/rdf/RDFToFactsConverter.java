package org.semanticweb.vlog4j.rdf;

import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.semanticweb.vlog4j.core.model.api.Atom;

public class RDFToFactsConverter {

	public static Set<Atom> rdfModelToFacts(Model rdfModel) {
		// TODO do we need this?
		// rdfModel.getNamespaces();
		// rdfModel.forEach(rdfStatementToFact);
		// collect
		// / rdfModel.stream().map(mapper);
		return null;

	}

	public static Atom rdfStatementToFact(final Statement statement) {
		// TODO do we need it?
		// final Resource context = statement.getContext();
		final Resource subject = statement.getSubject();
		final URI predicate = statement.getPredicate();
		final Value object = statement.getObject();

		return null;
	}

	// public void fromFormatToFacts(RDFFormat key, InputStream in, String baseURI)
	// throws RDFParseException, RDFHandlerException, IOException {
	//
	// final Model statements = new LinkedHashModel();
	//
	// final RDFParserRegistry rdfParserRegistry = RDFParserRegistry.getInstance();
	// final RDFParserFactory rdfParserFactory = rdfParserRegistry.get(key);
	// final RDFParser parser = rdfParserFactory.getParser();
	// // TODO parser config
	// // TODO perhaps have parser as parameter?
	// parser.setRDFHandler(new StatementCollector(statements));
	// parser.parse(in, baseURI);
	//
	// statements.forEach(action);
	// }

}
