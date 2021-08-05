package org.semanticweb.rulewerk.integrationtests.vlogissues;

import org.semanticweb.rulewerk.integrationtests.IntegrationTest;

abstract class VLogIssue extends IntegrationTest {
	@Override
	protected String getResourcePrefix() {
		return "/vlogissues/";
	}

}
