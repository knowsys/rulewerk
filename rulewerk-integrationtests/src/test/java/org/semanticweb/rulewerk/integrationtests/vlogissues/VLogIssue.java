package org.semanticweb.rulewerk.integrationtests.vlogissues;

/*-
 * #%L
 * Rulewerk Integration Tests
 * %%
 * Copyright (C) 2018 - 2021 Rulewerk Developers
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
