package org.semanticweb.rulewerk.integrationtests.vlogissues;

import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.integrationtests.IntegrationTest;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;

abstract class VLogIssue extends IntegrationTest {
	@Override
	protected String getResourcePrefix() {
		return "/vlogissues/";
	}

	/**
	 * Obtain a reasoner loaded with the Knowledge Base read from the resource name
	 *
	 * @param resourceName the name of the resource to load into the Reasoner
	 *
	 * @throws ParsingException when there is an error during parsing
	 *
	 * @return a {@link VLogReasoner} containing the parsed contents of the named
	 *         resource
	 */
	protected Reasoner getReasonerWithKbFromResource(String resourceName) throws ParsingException {
		return new VLogReasoner(parseKbFromResource(resourceName));
	}
}
