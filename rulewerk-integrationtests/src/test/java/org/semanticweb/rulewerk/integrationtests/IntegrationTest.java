package org.semanticweb.rulewerk.integrationtests;

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

import java.io.InputStream;

import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;

public abstract class IntegrationTest {
	/**
	 * Returns the prefix to use for resource names
	 *
	 * @return the prefix to use when turning resource names into paths
	 *
	 *         This needs to be overriden in subpackages for loading to work
	 *         correctly.
	 */
	protected String getResourcePrefix() {
		return "/";
	}

	/**
	 * Obtain an input stream for a resource name
	 *
	 * @param resourceName the resource name to load
	 * @return an {@link InputStream} pointing to the resource
	 */
	protected InputStream getResourceAsStream(final String resourceName) {
		String prefix = this.getResourcePrefix();

		if (resourceName.startsWith(prefix)) {
			prefix = "";
		} else if (resourceName.startsWith("/") && prefix.endsWith("/")) {
			prefix = prefix.substring(0, prefix.length() - 1);
		}

		return this.getClass().getResourceAsStream(prefix + resourceName);
	}

	/**
	 * Load a Knowledge Base from a resource name
	 *
	 * @param resourceName the name of the resource to parse into a Knowledge Base
	 *
	 * @throws ParsingException when there is an error during parsing
	 *
	 * @return a {@link KnowledgeBase} containing the parsed contents of the named
	 *         resource
	 */
	protected KnowledgeBase parseKbFromResource(final String resourceName) throws ParsingException {
		final KnowledgeBase kb = new KnowledgeBase();

		RuleParser.parseInto(kb, this.getResourceAsStream(resourceName));

		return kb;
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
	protected Reasoner getReasonerWithKbFromResource(final String resourceName) throws ParsingException {
		return new VLogReasoner(this.parseKbFromResource(resourceName));
	}
}
