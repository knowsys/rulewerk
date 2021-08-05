package org.semanticweb.rulewerk.integrationtests.acyclicity;

import org.semanticweb.rulewerk.integrationtests.IntegrationTest;

public abstract class AcyclicityTest extends IntegrationTest {

	@Override
	protected String getResourcePrefix() {
		return "/acyclicity/";
	}

}
