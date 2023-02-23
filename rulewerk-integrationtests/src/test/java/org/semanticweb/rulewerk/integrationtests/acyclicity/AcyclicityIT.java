package org.semanticweb.rulewerk.integrationtests.acyclicity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.RulesCyclicityProperty;

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

import org.semanticweb.rulewerk.integrationtests.AbstractRulewerkIT;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.reasoner.vlog.VLogRulesAnalyser;

public abstract class AcyclicityIT extends AbstractRulewerkIT {

	@Override
	protected String getResourcePrefix() {
		return "/acyclicity/";
	}

	protected abstract RulesCyclicityProperty getPropertyToCheck();
	
	protected void checkHasProperty(final String resourceName, boolean expected) throws ParsingException {
		checkHasProperty(resourceName, this.getPropertyToCheck(), expected);
	}
	
	protected void checkHasProperty(final String resourceName, RulesCyclicityProperty property, boolean expected)
			throws ParsingException {
		final KnowledgeBase knowledgeBase = this.parseKbFromResource(resourceName);
		try (VLogRulesAnalyser staticAnalyser = new VLogRulesAnalyser()) {

			boolean hasProperty = staticAnalyser.checkProperty(property, knowledgeBase.getRules());
			if (expected) {
				assertTrue(hasProperty);
			} else {
				assertFalse(hasProperty);
			}
		}
	}


}
